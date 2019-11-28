package com.xl.gccpage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

public class ActivityCompat {
    public ActivityCompat() {
    }

    @SuppressLint("NewApi")
    public static void requestPermissions23(Activity activity, String[] permissions, int requestCode) {
        if (activity instanceof ActivityCompat.RequestPermissionsRequestCodeValidator) {
            ((ActivityCompat.RequestPermissionsRequestCodeValidator)activity).validateRequestPermissionsRequestCode(requestCode);
        }

        activity.requestPermissions(permissions, requestCode);
    }

    @SuppressLint("NewApi")
    public static boolean shouldShowRequestPermissionRationale23(Activity activity, String permission) {
        return activity.shouldShowRequestPermissionRationale(permission);
    }

    //继承这个监听权限返回
    public interface RequestPermissionsRequestCodeValidator {
        void validateRequestPermissionsRequestCode(int var1);
    }

    //检测权限
    public static int checkSelfPermission(Context context, String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        } else {
            return context.checkPermission(permission, Process.myPid(), Process.myUid());
        }
    }


    public static void requestPermissions(final Activity activity, final String[] permissions, final int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions23(activity, permissions, requestCode);
        } else if (activity instanceof ActivityCompat.OnRequestPermissionsResultCallback) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    int[] grantResults = new int[permissions.length];
                    PackageManager packageManager = activity.getPackageManager();
                    String packageName = activity.getPackageName();
                    int permissionCount = permissions.length;

                    for(int i = 0; i < permissionCount; ++i) {
                        grantResults[i] = packageManager.checkPermission(permissions[i], packageName);
                    }

                    ((ActivityCompat.OnRequestPermissionsResultCallback)activity).onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            });
        }

    }

    //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return Build.VERSION.SDK_INT >= 23 && ActivityCompat.shouldShowRequestPermissionRationale23(activity, permission);
    }



    //请求结果回调
    public interface OnRequestPermissionsResultCallback {
        void onRequestPermissionsResult(int var1, String[] var2, int[] var3);
    }

}
