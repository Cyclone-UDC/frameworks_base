/*
 * Copyright 2020 The Android Open Source Project
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

package android.uwb;

import android.annotation.NonNull;
import android.annotation.Nullable;

/**
 * Represents an angle of arrival measurement between two devices using Ultra Wideband
 *
 * @hide
 */
public final class AngleOfArrivalMeasurement {
    /**
     * Azimuth angle measurement
     * <p>Azimuth {@link AngleMeasurement} of remote device in horizontal coordinate system, this is
     * the angle clockwise from the meridian when viewing above the north pole.
     *
     * <p>See: https://en.wikipedia.org/wiki/Horizontal_coordinate_system
     *
     * <p>On an Android device, azimuth north is defined as the angle perpendicular away from the
     * back of the device when holding it in portrait mode upright.
     *
     * <p>Azimuth angle must be supported when Angle of Arrival is supported
     *
     * @return the azimuth {@link AngleMeasurement}
     */
    @NonNull
    public AngleMeasurement getAzimuth() {
        throw new UnsupportedOperationException();
    }

    /**
     * Altitude angle measurement
     * <p>Altitude {@link AngleMeasurement} of remote device in horizontal coordinate system, this
     * is the angle above the equator when the north pole is up.
     *
     * <p>See: https://en.wikipedia.org/wiki/Horizontal_coordinate_system
     *
     * <p>On an Android device, altitude is defined as the angle vertical from ground when holding
     * the device in portrait mode upright.
     *
     * @return altitude {@link AngleMeasurement} or null when this is not available
     */
    @Nullable
    public AngleMeasurement getAltitude() {
        throw new UnsupportedOperationException();
    }
}
