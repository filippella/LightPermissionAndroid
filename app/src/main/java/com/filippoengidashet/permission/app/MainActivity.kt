package com.filippoengidashet.permission.app

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.filippoengidashet.permission.LightPermission
import com.filippoengidashet.permission.PermissionResult

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            handlePermissionRequest()
        }
    }

    private fun handlePermissionRequest() {

        val readContacts = Manifest.permission.READ_CONTACTS
        val sendSms = Manifest.permission.SEND_SMS

        val lightPermission = LightPermission(this, object : LightPermission.Callback {

            override fun onResult(alreadyGranted: Boolean, results: Set<PermissionResult>) {
                if (alreadyGranted) {
                    showToast("Already Granted!")
                }
                //handle results
                handleResults(results, readContacts, sendSms)
            }
        })
        lightPermission.check()
        lightPermission.check(readContacts, sendSms)
        //lightPermission.checkAllFromManifest() // from manifest
    }

    private fun handleResults(
        results: Set<PermissionResult>,
        readContacts: String,
        sendSms: String
    ) {
        val txtReadSmsPermission = findViewById<TextView>(R.id.textReadSms)
        val txtReadContactsPermission = findViewById<TextView>(R.id.textReadContacts)

        results.forEach { pr ->
            if (TextUtils.equals(pr.permission, readContacts)) {
                txtReadSmsPermission.text = PermissionResult.State.from(pr.state)
            } else if (TextUtils.equals(pr.permission, sendSms)) {
                txtReadContactsPermission.text = PermissionResult.State.from(pr.state)
            }
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
