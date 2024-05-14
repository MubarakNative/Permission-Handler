package com.example.cpexample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cpexample.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            binding.tvState.text = (resources.getString(R.string.permission_granted_msg))
            binding.requestPhoto.visibility = View.GONE

        }

        binding.btnOpenNewActivity.setOnClickListener {
            startActivity(
                Intent(this, RequestMultiPermissionActivity::class.java)
            )
        }


        binding.requestPhoto.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                binding.tvState.text = (resources.getString(R.string.permission_granted_msg))
                binding.requestPhoto.visibility = View.GONE

            } else this.createDialog()
        }
    }

    private fun Activity.createDialog() {
        MaterialAlertDialogBuilder(this).setTitle(R.string.permission_required_title)
            .setMessage(resources.getString(R.string.permission_rationale))
            .setPositiveButton(R.string.allow) { _, _ ->
                // Respond to positive button press
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    ), CAMERA_REQ_CODE
                )
            }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted! execute your task
                    binding.tvState.text = (resources.getString(R.string.permission_granted_msg))
                    binding.requestPhoto.visibility = View.GONE
                } else {
                    // permission is not-granted show education UI to explain why we need this permission.
                    Snackbar.make(
                        binding.parent, R.string.education_msg,
                        // this is a example message change it to appropriate to your need
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        R.string.allow
                    ) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.CAMERA
                            ), DENIED_CAMERA_PERMISSION_REQ_CODE
                        )
                    }.show()

                }
            }

            DENIED_CAMERA_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted!
                    binding.tvState.text = "Permission granted successfully"
                } else {
                    Toast.makeText(
                        this, R.string.permission_denied_title, Toast.LENGTH_SHORT
                    ).show()
                    MaterialAlertDialogBuilder(this).setTitle(R.string.permanent_permission_denied_title)
                        .setIcon(android.R.drawable.stat_notify_error).setCancelable(false)
                        .setMessage(R.string.permanent_permission_deniel_msg)
                        .setNegativeButton(R.string.not_now) { dialog, _ ->
                            // Respond to negative button press
                            dialog.dismiss()
                        }.setPositiveButton(R.string.settings) { _, _ ->
                            // Respond to positive button press
                            goToAppSettings()
                        }.show()

                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission is granted!
                        binding.tvState.text = (resources.getString(R.string.permission_granted_msg))
                    }
                }
            }
        }
    }

    private fun goToAppSettings() { // navigate the user to our app settings page to allow the permission if they denied it permanently
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
            it.data = Uri.fromParts("package", packageName, null)
            startActivity(it)
        }
    }

    companion object {
        const val CAMERA_REQ_CODE = 1
        const val DENIED_CAMERA_PERMISSION_REQ_CODE = 2
    }
}
