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

package android.window;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.app.IApplicationThread;
import android.os.IBinder;
import android.os.Parcelable;

import com.android.internal.util.DataClass;

/**
 * Represents a remote transition animation and information required to run it (eg. the app thread
 * that needs to be boosted).
 * @hide
 */
@DataClass(genToString = true, genSetters = true, genAidl = true)
public class RemoteTransition implements Parcelable {

    /** The actual remote-transition interface used to run the transition animation. */
    private @NonNull IRemoteTransition mRemoteTransition;

    /** The application thread that will be running the remote transition. */
    private @Nullable IApplicationThread mAppThread;

    /** A name for this that can be used for debugging. */
    private @Nullable String mDebugName;

    /** Constructs with no app thread (animation runs in shell). */
    public RemoteTransition(@NonNull IRemoteTransition remoteTransition) {
        this(remoteTransition, null /* appThread */, null /* debugName */);
    }

    /** Constructs with no app thread (animation runs in shell). */
    public RemoteTransition(@NonNull IRemoteTransition remoteTransition,
            @Nullable String debugName) {
        this(remoteTransition, null /* appThread */, debugName);
    }

    /** Get the IBinder associated with the underlying IRemoteTransition. */
    public @Nullable IBinder asBinder() {
        return mRemoteTransition.asBinder();
    }



    // Code below generated by codegen v1.0.23.
    //
    // DO NOT MODIFY!
    // CHECKSTYLE:OFF Generated code
    //
    // To regenerate run:
    // $ codegen $ANDROID_BUILD_TOP/frameworks/base/core/java/android/window/RemoteTransition.java
    //
    // To exclude the generated code from IntelliJ auto-formatting enable (one-time):
    //   Settings > Editor > Code Style > Formatter Control
    //@formatter:off


    /**
     * Creates a new RemoteTransition.
     *
     * @param remoteTransition
     *   The actual remote-transition interface used to run the transition animation.
     * @param appThread
     *   The application thread that will be running the remote transition.
     * @param debugName
     *   A name for this that can be used for debugging.
     */
    @DataClass.Generated.Member
    public RemoteTransition(
            @NonNull IRemoteTransition remoteTransition,
            @Nullable IApplicationThread appThread,
            @Nullable String debugName) {
        this.mRemoteTransition = remoteTransition;
        com.android.internal.util.AnnotationValidations.validate(
                NonNull.class, null, mRemoteTransition);
        this.mAppThread = appThread;
        this.mDebugName = debugName;

        // onConstructed(); // You can define this method to get a callback
    }

    /**
     * The actual remote-transition interface used to run the transition animation.
     */
    @DataClass.Generated.Member
    public @NonNull IRemoteTransition getRemoteTransition() {
        return mRemoteTransition;
    }

    /**
     * The application thread that will be running the remote transition.
     */
    @DataClass.Generated.Member
    public @Nullable IApplicationThread getAppThread() {
        return mAppThread;
    }

    /**
     * A name for this that can be used for debugging.
     */
    @DataClass.Generated.Member
    public @Nullable String getDebugName() {
        return mDebugName;
    }

    /**
     * The actual remote-transition interface used to run the transition animation.
     */
    @DataClass.Generated.Member
    public @NonNull RemoteTransition setRemoteTransition(@NonNull IRemoteTransition value) {
        mRemoteTransition = value;
        com.android.internal.util.AnnotationValidations.validate(
                NonNull.class, null, mRemoteTransition);
        return this;
    }

    /**
     * The application thread that will be running the remote transition.
     */
    @DataClass.Generated.Member
    public @NonNull RemoteTransition setAppThread(@NonNull IApplicationThread value) {
        mAppThread = value;
        return this;
    }

    /**
     * A name for this that can be used for debugging.
     */
    @DataClass.Generated.Member
    public @NonNull RemoteTransition setDebugName(@NonNull String value) {
        mDebugName = value;
        return this;
    }

    @Override
    @DataClass.Generated.Member
    public String toString() {
        // You can override field toString logic by defining methods like:
        // String fieldNameToString() { ... }

        return "RemoteTransition { " +
                "remoteTransition = " + mRemoteTransition + ", " +
                "appThread = " + mAppThread + ", " +
                "debugName = " + mDebugName +
        " }";
    }

    @Override
    @DataClass.Generated.Member
    public void writeToParcel(@NonNull android.os.Parcel dest, int flags) {
        // You can override field parcelling by defining methods like:
        // void parcelFieldName(Parcel dest, int flags) { ... }

        byte flg = 0;
        if (mAppThread != null) flg |= 0x2;
        if (mDebugName != null) flg |= 0x4;
        dest.writeByte(flg);
        dest.writeStrongInterface(mRemoteTransition);
        if (mAppThread != null) dest.writeStrongInterface(mAppThread);
        if (mDebugName != null) dest.writeString(mDebugName);
    }

    @Override
    @DataClass.Generated.Member
    public int describeContents() { return 0; }

    /** @hide */
    @SuppressWarnings({"unchecked", "RedundantCast"})
    @DataClass.Generated.Member
    protected RemoteTransition(@NonNull android.os.Parcel in) {
        // You can override field unparcelling by defining methods like:
        // static FieldType unparcelFieldName(Parcel in) { ... }

        byte flg = in.readByte();
        IRemoteTransition remoteTransition = IRemoteTransition.Stub.asInterface(in.readStrongBinder());
        IApplicationThread appThread = (flg & 0x2) == 0 ? null : IApplicationThread.Stub.asInterface(in.readStrongBinder());
        String debugName = (flg & 0x4) == 0 ? null : in.readString();

        this.mRemoteTransition = remoteTransition;
        com.android.internal.util.AnnotationValidations.validate(
                NonNull.class, null, mRemoteTransition);
        this.mAppThread = appThread;
        this.mDebugName = debugName;

        // onConstructed(); // You can define this method to get a callback
    }

    @DataClass.Generated.Member
    public static final @NonNull Parcelable.Creator<RemoteTransition> CREATOR
            = new Parcelable.Creator<RemoteTransition>() {
        @Override
        public RemoteTransition[] newArray(int size) {
            return new RemoteTransition[size];
        }

        @Override
        public RemoteTransition createFromParcel(@NonNull android.os.Parcel in) {
            return new RemoteTransition(in);
        }
    };

    @DataClass.Generated(
            time = 1678926409863L,
            codegenVersion = "1.0.23",
            sourceFile = "frameworks/base/core/java/android/window/RemoteTransition.java",
            inputSignatures = "private @android.annotation.NonNull android.window.IRemoteTransition mRemoteTransition\nprivate @android.annotation.Nullable android.app.IApplicationThread mAppThread\nprivate @android.annotation.Nullable java.lang.String mDebugName\npublic @android.annotation.Nullable android.os.IBinder asBinder()\nclass RemoteTransition extends java.lang.Object implements [android.os.Parcelable]\n@com.android.internal.util.DataClass(genToString=true, genSetters=true, genAidl=true)")
    @Deprecated
    private void __metadata() {}


    //@formatter:on
    // End of generated code

}
