package com.filippoengidashet.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

/**
 * @author Filippo Engidashet
 * @version 1.0.0
 * @since Sun, 2019-12-15 at 21:08.
 */
internal class PermissionActivity : AppCompatActivity() {

    private var handledSuccessfully = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissions = intent.getStringArrayListExtra(EXTRA_PERMISSIONS)
        ActivityCompat.requestPermissions(this,
            permissions.toTypedArray(),
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {

            val results = arrayListOf<PermissionResult>()
            for (permissionsWithIndex in permissions.withIndex()) {

                val index = permissionsWithIndex.index
                val permission = permissionsWithIndex.value

                val result = if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    PermissionResult(permission, PermissionResult.State.GRANTED)
                } else {
                    if (PermissionUtils.shouldShowPermissionRationale(this, permission)) {
                        PermissionResult(permission, PermissionResult.State.DENIED_SHOW_RATIONALE)
                    } else {
                        PermissionResult(permission, PermissionResult.State.PERMANENTLY_DENIED)
                    }
                }
                results.add(result)
            }

            val resultIntent = Intent(LightPermission.ACTION_PERMISSION_RESULT)
            resultIntent.putExtra(EXTRA_REQUEST_HANDLED, true)
            resultIntent.putExtra(EXTRA_PERMISSION_RESULTS, results)
            sendBroadcast(resultIntent)

            handledSuccessfully = true
            finish()
        }
    }

    override fun onDestroy() {
        if (!handledSuccessfully) {
            val intent = Intent(LightPermission.ACTION_PERMISSION_RESULT)
            intent.putExtra(EXTRA_REQUEST_HANDLED, false)
            sendBroadcast(intent)
        }
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_CODE_PERMISSIONS: Int = 0x0001
        private const val EXTRA_PERMISSIONS: String =
            BuildConfig.LIBRARY_PACKAGE_NAME + ".extra.PERMISSIONS"

        const val EXTRA_PERMISSION_RESULTS: String =
            BuildConfig.LIBRARY_PACKAGE_NAME + ".extra.PERMISSION_RESULTS"
        const val EXTRA_REQUEST_HANDLED: String =
            BuildConfig.LIBRARY_PACKAGE_NAME + ".extra.REQUEST_HANDLED"

        fun start(context: Context, permissions: ArrayList<String>) {
            val intent = Intent(context, PermissionActivity::class.java)
            intent.putExtra(EXTRA_PERMISSIONS, permissions)
            context.startActivity(intent)
        }
    }
}
