package com.jdw.random_lotto.common.util.permission

import android.Manifest
import android.os.Build

enum class AppPermission(
    val permissions: Array<String>,
) {
    CAMERA(
        permissions = arrayOf(
            Manifest.permission.CAMERA
        )
    ),

     GALLERY(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
     )
}