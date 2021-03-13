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

package com.android.systemui.biometrics;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.android.systemui.R;

/**
 * View corresponding with udfps_keyguard_view.xml
 */
public class UdfpsKeyguardView extends UdfpsAnimationView {
    private final UdfpsKeyguardDrawable mFingerprintDrawable;
    private ImageView mFingerprintView;

    public UdfpsKeyguardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mFingerprintDrawable = new UdfpsKeyguardDrawable(mContext);
    }

    @Override
    protected void onFinishInflate() {
        mFingerprintView = findViewById(R.id.udfps_keyguard_animation_fp_view);
        mFingerprintView.setImageDrawable(mFingerprintDrawable);
    }

    @Override
    public UdfpsDrawable getDrawable() {
        return mFingerprintDrawable;
    }

    @Override
    public boolean dozeTimeTick() {
        // TODO: burnin
        mFingerprintDrawable.dozeTimeTick();
        return true;
    }

    void onDozeAmountChanged(float linear, float eased) {
        mFingerprintDrawable.onDozeAmountChanged(linear, eased);
    }
}
