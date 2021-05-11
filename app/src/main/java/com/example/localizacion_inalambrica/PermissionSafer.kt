package com.example.localizacion_inalambrica

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment


class PermissionSafer(
    activity: Fragment,
    private val permission: String,
    onDenied: () -> Unit = {},
    onShowRationale: () -> Unit = {}
    ) {
    private var onGranted: () -> Unit = {}

    private val launcher =

        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> onGranted()
                activity.shouldShowRequestPermissionRationale(permission) -> onShowRationale()
                else -> onDenied()
            }
        }

    fun runWithPermission(onGranted: () -> Unit) {
        this.onGranted = onGranted
        launcher.launch(permission)
    }

}
