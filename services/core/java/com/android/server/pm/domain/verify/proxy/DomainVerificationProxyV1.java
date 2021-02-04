/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.pm.domain.verify.proxy;

import android.Manifest;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.app.BroadcastOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.domain.verify.DomainVerificationManager;
import android.content.pm.domain.verify.DomainVerificationSet;
import android.content.pm.domain.verify.DomainVerificationState;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;

import com.android.internal.annotations.GuardedBy;
import com.android.server.pm.domain.verify.DomainVerificationCollector;
import com.android.server.pm.domain.verify.DomainVerificationManagerInternal;
import com.android.server.pm.domain.verify.DomainVerificationMessageCodes;
import com.android.server.pm.parsing.pkg.AndroidPackage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class DomainVerificationProxyV1 implements DomainVerificationProxy {

    private static final String TAG = "DomainVerificationProxyV1";

    private static final boolean DEBUG_BROADCASTS = false;

    @NonNull
    private final Context mContext;

    @NonNull
    private final Connection mConnection;

    @NonNull
    private final ComponentName mVerifierComponent;

    @NonNull
    private final DomainVerificationManagerInternal mManager;

    @NonNull
    private final DomainVerificationCollector mCollector;

    @NonNull
    private final Object mLock = new Object();

    @NonNull
    @GuardedBy("mLock")
    private final ArrayMap<Integer, Pair<UUID, String>> mRequests = new ArrayMap<>();

    // TODO(b/159952358): For now, IDs start at a really high number to avoid conflict with the
    //  legacy manager, which is still active in code. Should be set to 0 once
    //  IntentFilterVerificationManager is removed.
    @GuardedBy("mLock")
    private int mVerificationToken = Integer.MAX_VALUE / 2;

    public DomainVerificationProxyV1(@NonNull Context context,
            @NonNull DomainVerificationManagerInternal manager,
            @NonNull DomainVerificationCollector collector, @NonNull Connection connection,
            @NonNull ComponentName verifierComponent) {
        mContext = context;
        mConnection = connection;
        mVerifierComponent = verifierComponent;
        mManager = manager;
        mCollector = collector;
    }

    public static void queueLegacyVerifyResult(@NonNull Context context,
            @NonNull DomainVerificationProxyV1.Connection connection, int verificationId,
            int verificationCode, @Nullable List<String> failedDomains, int callingUid) {
        context.enforceCallingOrSelfPermission(
                Manifest.permission.INTENT_FILTER_VERIFICATION_AGENT,
                "Only the intent filter verification agent can verify applications");

        connection.schedule(DomainVerificationMessageCodes.LEGACY_ON_INTENT_FILTER_VERIFIED,
                new Response(callingUid, verificationId, verificationCode, failedDomains));
    }

    @Override
    public void sendBroadcastForPackages(@NonNull Set<String> packageNames) {
        synchronized (mLock) {
            int size = mRequests.size();
            for (int index = size - 1; index >= 0; index--) {
                Pair<UUID, String> pair = mRequests.valueAt(index);
                if (packageNames.contains(pair.second)) {
                    mRequests.removeAt(index);
                }
            }
        }
        mConnection.schedule(DomainVerificationMessageCodes.SEND_REQUEST, packageNames);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean runMessage(int messageCode, Object object) {
        switch (messageCode) {
            case DomainVerificationMessageCodes.SEND_REQUEST:
                @SuppressWarnings("unchecked") Set<String> packageNames = (Set<String>) object;
                if (DEBUG_BROADCASTS) {
                    Slog.d(TAG, "Requesting domain verification for " + packageNames);
                }

                ArrayMap<Integer, Pair<UUID, String>> newRequests = new ArrayMap<>(
                        packageNames.size());
                synchronized (mLock) {
                    for (String packageName : packageNames) {
                        UUID domainSetId = mManager.getDomainVerificationSetId(packageName);
                        if (domainSetId == null) {
                            continue;
                        }

                        newRequests.put(mVerificationToken++,
                                Pair.create(domainSetId, packageName));
                    }
                    mRequests.putAll(newRequests);
                }

                sendBroadcasts(newRequests);
                return true;
            case DomainVerificationMessageCodes.LEGACY_ON_INTENT_FILTER_VERIFIED:
                Response response = (Response) object;

                Pair<UUID, String> pair = mRequests.get(response.verificationId);
                if (pair == null) {
                    return true;
                }

                UUID domainSetId = pair.first;
                String packageName = pair.second;
                DomainVerificationSet set;
                try {
                    set = mManager.getDomainVerificationSet(packageName);
                } catch (PackageManager.NameNotFoundException ignored) {
                    return true;
                }

                if (!Objects.equals(domainSetId, set.getIdentifier())) {
                    return true;
                }

                Set<String> successfulDomains = new ArraySet<>(set.getHostToStateMap().keySet());
                successfulDomains.removeAll(response.failedDomains);

                int callingUid = response.callingUid;
                try {
                    mManager.setDomainVerificationStatusInternal(callingUid, domainSetId,
                            successfulDomains, DomainVerificationState.STATE_SUCCESS);
                } catch (DomainVerificationManager.InvalidDomainSetException
                        | PackageManager.NameNotFoundException ignored) {
                }
                try {
                    mManager.setDomainVerificationStatusInternal(callingUid, domainSetId,
                            new ArraySet<>(response.failedDomains),
                            DomainVerificationState.STATE_LEGACY_FAILURE);
                } catch (DomainVerificationManager.InvalidDomainSetException
                        | PackageManager.NameNotFoundException ignored) {
                }

                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isCallerVerifier(int callingUid) {
        return mConnection.isCallerPackage(callingUid, mVerifierComponent.getPackageName());
    }

    @SuppressWarnings("deprecation")
    private void sendBroadcasts(@NonNull ArrayMap<Integer, Pair<UUID, String>> verifications) {
        final long allowListTimeout = mConnection.getPowerSaveTempWhitelistAppDuration();
        mConnection.getDeviceIdleInternal().addPowerSaveTempWhitelistApp(Process.myUid(),
                mVerifierComponent.getPackageName(), allowListTimeout,
                UserHandle.USER_SYSTEM, true, "domain verification agent");

        int size = verifications.size();
        for (int index = 0; index < size; index++) {
            int verificationId = verifications.keyAt(index);
            String packageName = verifications.valueAt(index).second;
            AndroidPackage pkg = mConnection.getPackage(packageName);

            String hostsString = buildHostsString(pkg);

            Intent intent = new Intent(Intent.ACTION_INTENT_FILTER_NEEDS_VERIFICATION)
                    .setComponent(mVerifierComponent)
                    .putExtra(PackageManager.EXTRA_INTENT_FILTER_VERIFICATION_ID,
                            verificationId)
                    .putExtra(PackageManager.EXTRA_INTENT_FILTER_VERIFICATION_URI_SCHEME,
                            IntentFilter.SCHEME_HTTPS)
                    .putExtra(PackageManager.EXTRA_INTENT_FILTER_VERIFICATION_HOSTS,
                            hostsString)
                    .putExtra(PackageManager.EXTRA_INTENT_FILTER_VERIFICATION_PACKAGE_NAME,
                            packageName)
                    .addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

            final BroadcastOptions options = BroadcastOptions.makeBasic();
            options.setTemporaryAppWhitelistDuration(allowListTimeout);
            mContext.sendBroadcastAsUser(intent, UserHandle.SYSTEM, null, options.toBundle());
        }
    }

    @NonNull
    private String buildHostsString(@NonNull AndroidPackage pkg) {
        // The collector itself handles the v1 vs v2 behavior, which is based on targetSdkVersion,
        // not the version of the verification agent on device.
        ArraySet<String> domains = mCollector.collectAutoVerifyDomains(pkg);
        return TextUtils.join(" ", domains);
    }

    private static class Response {
        public final int callingUid;
        public final int verificationId;
        public final int verificationCode;
        @NonNull
        public final List<String> failedDomains;

        private Response(int callingUid, int verificationId, int verificationCode,
                @Nullable List<String> failedDomains) {
            this.callingUid = callingUid;
            this.verificationId = verificationId;
            this.verificationCode = verificationCode;
            this.failedDomains = failedDomains == null ? Collections.emptyList() : failedDomains;
        }
    }

    public interface Connection extends BaseConnection {

        @Nullable
        AndroidPackage getPackage(@NonNull String packageName);
    }
}
