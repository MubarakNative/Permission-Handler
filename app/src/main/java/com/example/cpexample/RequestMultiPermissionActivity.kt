package com.example.cpexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cpexample.databinding.ActivityRequestMultiPermissionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RequestMultiPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestMultiPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestMultiPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * There a two ways to request a runtime permission
         * TODO 1.)The request code was managed by system itself using registerForActivityResult api
         * TODO 2.) Request code managed by ourself using onRequestPermissionsResult **/

        binding.btnRequestMultiPermission.setOnClickListener {

            if (allPermissionsGranted()) {
                // execute your task when all permission is granted
                Toast.makeText(this, "All Permission was granted", Toast.LENGTH_LONG).show()
            } else { // request permission
                activityResultLauncher.launch(permissions)
            }

        }

    }

    private fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private val permissions =
        mutableListOf(
            Manifest.permission.CAMERA, // replace it with your multiple permission
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).toTypedArray()

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        var isPermissionGranted: Boolean = true
        permissions.entries.forEach {
            if (it.key in this.permissions && !it.value) {
                isPermissionGranted = false
            }
        }
        if (!isPermissionGranted) {
            showEducationalDialog()
        } else {
            // execute your task when permission is granted
            Toast.makeText(this, "All Permission was granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun showEducationalDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_denied_title)
            .setMessage(R.string.permission_denied_msg)
            .setNegativeButton(R.string.close) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setPositiveButton(R.string.settings) { dialog, _ ->
               goToAppSettings()
                dialog.dismiss()
            }
            .setCancelable(false)
        dialog.show()
    }


    private fun goToAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
            it.data = Uri.fromParts("package", packageName, null)
            startActivity(it)
        }
    }
}