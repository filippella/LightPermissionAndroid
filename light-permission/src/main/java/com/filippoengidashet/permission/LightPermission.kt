package com.filippoengidashet.permission

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.text.TextUtils

/**
 * @author Filippo Engidashet
 * @version 1.0.0
 * @since Sun, 2019-12-15 at 20:58.
 */
class LightPermission constructor(private val context: Context, private val callback: Callback) {

    private val resultReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (TextUtils.equals(intent.action, ACTION_PERMISSION_RESULT)) {
                checking = false
                unsubscribeReceiver(context)
                if (intent.getBooleanExtra(PermissionActivity.EXTRA_REQUEST_HANDLED, false)) {
                    val results =
                        intent.getParcelableArrayListExtra<PermissionResult>(PermissionActivity.EXTRA_PERMISSION_RESULTS)
                    permissionResults.addAll(results)
                    callback.onResult(false, permissionResults)
                }
            }
        }
    }

    private var checking: Boolean = false
    private val permissionResults = mutableSetOf<PermissionResult>()
    private var isRegistered: Boolean = false

    private fun subscribeReceiver(context: Context) {
        context.registerReceiver(resultReceiver, IntentFilter(ACTION_PERMISSION_RESULT))
        isRegistered = true
    }

    fun getAllManifestPermission(): Array<out String> {
        return context.packageManager
            .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
    }

    @Synchronized
    fun checkAllFromManifest(): Boolean {
        return check(*getAllManifestPermission())
    }

    @Synchronized
    fun check(vararg permissions: String): Boolean {
        if (checking || permissions.isEmpty()) return false
        permissionResults.clear()
        if (PermissionUtils.isRuntimePermissionRequired()) {
            val permissionSet = permissions.toSet()
            val permissionList = arrayListOf<String>()
            for (permission in permissionSet) {
                if (PermissionUtils.isPermissionGranted(context, permission)) {
                    permissionResults.add(PermissionResult(permission,
                        PermissionResult.State.GRANTED
                    )
                    )
                } else {
                    permissionList.add(permission)
                }
            }
            if (permissionList.isNotEmpty()) {
                checking = true
                subscribeReceiver(context)
                PermissionActivity.start(context, permissionList)
            } else {
                notifyResults(permissions)
            }
        } else {
            notifyResults(permissions)
        }
        return checking
    }

    private fun notifyResults(permissions: Array<out String>) {
        for (permission in permissions) {
            permissionResults.add(PermissionResult(permission, PermissionResult.State.GRANTED))
        }
        callback.onResult(true, permissionResults)
    }

    private fun unsubscribeReceiver(context: Context) {
        if (isRegistered) context.unregisterReceiver(resultReceiver)
        checking = false
        isRegistered = false
    }

    fun cleanup() {
        unsubscribeReceiver(context)
    }

    companion object {

        internal const val ACTION_PERMISSION_RESULT: String =
            BuildConfig.LIBRARY_PACKAGE_NAME + ".action.PERMISSION_RESULT"
    }

    interface Callback {

        fun onResult(alreadyGranted: Boolean, results: Set<PermissionResult>)
    }
}
