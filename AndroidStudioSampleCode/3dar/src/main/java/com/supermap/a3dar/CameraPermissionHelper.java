/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supermap.a3dar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/** Helper to ask camera permission. */
public final class CameraPermissionHelper {
  private static final int CAMERA_PERMISSION_CODE = 0;

  private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
  private static final String READ_PHONE_STATE_PERMISSION = Manifest.permission.READ_PHONE_STATE;
  private static final String CHANGE_WIFI_STATE = Manifest.permission.CHANGE_WIFI_STATE;
  private static final String MOUNT_UNMOUNT_FILESYSTEMS = Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS;
  private static final String ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
  private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
  private static final String ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
  private static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;

  /** Check to see we have the necessary permissions for this app. */
  public static boolean hasCameraPermission(Activity activity) {
    return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION)
        == PackageManager.PERMISSION_GRANTED;
  }

  /** Check to see we have the necessary permissions for this app, and ask for them if we don't. */
  public static void requestCameraPermission(Activity activity) {
    ActivityCompat.requestPermissions(
        activity, new String[] {CAMERA_PERMISSION,READ_PHONE_STATE_PERMISSION,CHANGE_WIFI_STATE,MOUNT_UNMOUNT_FILESYSTEMS,ACCESS_WIFI_STATE,WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION,ACCESS_NETWORK_STATE,RECORD_AUDIO}, CAMERA_PERMISSION_CODE);
  }
  public static void requestCameraPermission1(Activity activity) {
    ActivityCompat.requestPermissions(
            activity, new String[] {"Manifest.permission.READ_PHONE_STATE"},0 );
  }

  /** Check to see if we need to show the rationale for this permission. */
  public static boolean shouldShowRequestPermissionRationale(Activity activity) {
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION);
  }

  /** Launch Application Setting to grant permission. */
  public static void launchPermissionSettings(Activity activity) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
    activity.startActivity(intent);
  }
}
