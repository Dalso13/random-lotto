package com.jdw.random_lotto.common.util.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * 권한 상태 관리 객체
 * - 권한이 GRANTED / DENIED / BLOCKED 상태인지 판단
 * - 권한 요청 이력 기록
 * - 권한 요청 처리
 */
object PermissionManager {

    private const val PREF_NAME = "permission_prefs"
    private const val KEY_PREFIX_REQUESTED = "requested_"

    /**
     * 권한 처리 함수
     * @param permission 체크할 AppPermission
     * @param active 권한이 GRANTED 상태일 때 실행할 람다
     * @param lancher 권한이 DENIED 상태일 때 실제 권한 요청을 수행하는 람다
     */
    fun checkAppPermission(
        context: Context,
        activity: Activity,
        permission: AppPermission,
        active: () -> Unit,
        lancher : (Array<String>) -> Unit
    ) {
        when (getStatus(activity, permission)) {
            PermissionStatus.GRANTED -> {
                active()
            }

            PermissionStatus.DENIED -> {
                // 요청 가능 → 실제 요청 전에 markRequested 호출
                val notGranted = getNotGrantedPermissions(context, permission)
                if (notGranted.isNotEmpty()) {
                    markRequested(context, permission)
                    lancher(notGranted)
                }
            }

            PermissionStatus.BLOCKED -> {
                // 설정창으로 유도 (다이얼로그 → 설정 Intent)
                openAppSettings(context)
            }
        }
    }

    private fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    // 모든 권한이 허용되었는지 여부
    private fun isGranted(
        context: Context,
        appPermission: AppPermission
    ): Boolean {
        return appPermission.permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 허용되지 않은 권한 리스트 반환
    private fun getNotGrantedPermissions(
        context: Context,
        appPermission: AppPermission
    ): Array<String> {
        return appPermission.permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }

    /**
     * 이 AppPermission 묶음에 대해 GRANTED / DENIED / BLOCKED 상태 반환
     */
    private fun getStatus(
        activity: Activity,
        appPermission: AppPermission
    ): PermissionStatus {
        val context = activity as Context

        // 전부 허용된 상태
        if (isGranted(context, appPermission)) {
            return PermissionStatus.GRANTED
        }

        val notGranted = getNotGrantedPermissions(context, appPermission)

        // 하나라도 BLOCKED로 판단되면 전체를 BLOCKED로 본다
        val anyBlocked = notGranted.any { permission ->
            isPermissionBlocked(activity, permission)
        }

        return if (anyBlocked) {
            PermissionStatus.BLOCKED
        } else {
            PermissionStatus.DENIED
        }
    }

    /**
     * 실제로 시스템 권한 요청 다이얼로그를 띄울 때
     * 반드시 함께 호출해서 "이 권한은 한 번 이상 요청한 적 있다"는 걸 기록.
     */
    private fun markRequested(
        context: Context,
        appPermission: AppPermission
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            appPermission.permissions.forEach { permission ->
                putBoolean("$KEY_PREFIX_REQUESTED$permission", true)
            }
        }.apply()
    }

    /**
     * 특정 개별 permission 이 이전에 요청된 적 있는지 판단
     */
    private fun wasRequestedBefore(
        context: Context,
        permission: String
    ): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("$KEY_PREFIX_REQUESTED$permission", false)
    }

    /**
     * 특정 개별 permission 이 BLOCKED 상태인지 판단
     */
    private fun isPermissionBlocked(
        activity: Activity,
        permission: String
    ): Boolean {
        val context = activity as Context

        val granted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) return false

        val requestedBefore = wasRequestedBefore(context, permission)

        val shouldShowRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

        // 이전에 요청한 적 있고, 지금은 설명도 보여줄 필요가 없다고 하면 BLOCKED
        return requestedBefore && !shouldShowRationale
    }
}
