package com.filippoengidashet.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * @author Filippo Engidashet
 * @version 1.0.0
 * @since Tue, 2019-12-24 at 16:47.
 */
object PermissionUtils {

    @JvmStatic
    fun isRuntimePermissionRequired() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    @JvmStatic
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun shouldShowPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}
