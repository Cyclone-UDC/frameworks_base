/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.android.server.hdmi;

import android.annotation.CallbackExecutor;
import android.annotation.NonNull;
import android.content.Context;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceVolumeManager;
import android.media.VolumeInfo;

import java.util.concurrent.Executor;

/**
 * "Default" wrapper for {@link AudioDeviceVolumeManager}, as opposed to a "Fake" wrapper for
 * testing - see {@link FakeAudioFramework.FakeAudioDeviceVolumeManagerWrapper}.
 *
 * Creates an instance of {@link AudioDeviceVolumeManager} and directly passes method calls
 * to that instance.
 */
public class DefaultAudioDeviceVolumeManagerWrapper
        implements AudioDeviceVolumeManagerWrapper {

    private static final String TAG = "AudioDeviceVolumeManagerWrapper";

    private final AudioDeviceVolumeManager mAudioDeviceVolumeManager;

    public DefaultAudioDeviceVolumeManagerWrapper(Context context) {
        mAudioDeviceVolumeManager = new AudioDeviceVolumeManager(context);
    }

    @Override
    public void addOnDeviceVolumeBehaviorChangedListener(
            @NonNull @CallbackExecutor Executor executor,
            @NonNull AudioDeviceVolumeManager.OnDeviceVolumeBehaviorChangedListener listener)
            throws SecurityException {
        mAudioDeviceVolumeManager.addOnDeviceVolumeBehaviorChangedListener(executor, listener);
    }

    @Override
    public void removeOnDeviceVolumeBehaviorChangedListener(
            @NonNull AudioDeviceVolumeManager.OnDeviceVolumeBehaviorChangedListener listener) {
        mAudioDeviceVolumeManager.removeOnDeviceVolumeBehaviorChangedListener(listener);
    }

    @Override
    public void setDeviceAbsoluteVolumeBehavior(
            @NonNull AudioDeviceAttributes device,
            @NonNull VolumeInfo volume,
            @NonNull @CallbackExecutor Executor executor,
            @NonNull AudioDeviceVolumeManager.OnAudioDeviceVolumeChangedListener vclistener,
            boolean handlesVolumeAdjustment) {
        mAudioDeviceVolumeManager.setDeviceAbsoluteVolumeBehavior(device, volume, executor,
                vclistener, handlesVolumeAdjustment);
    }

    @Override
    public void setDeviceAbsoluteVolumeAdjustOnlyBehavior(
            @NonNull AudioDeviceAttributes device,
            @NonNull VolumeInfo volume,
            @NonNull @CallbackExecutor Executor executor,
            @NonNull AudioDeviceVolumeManager.OnAudioDeviceVolumeChangedListener vclistener,
            boolean handlesVolumeAdjustment) {
        mAudioDeviceVolumeManager.setDeviceAbsoluteVolumeAdjustOnlyBehavior(device, volume,
                executor, vclistener, handlesVolumeAdjustment);
    }
}
