package br.com.wgc.core.device

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Represents the authorization status of an Android runtime permission.
 */
sealed interface PermissionState {
    /** Permission is granted by the user. */
    data object Granted : PermissionState

    /**
     * Permission was denied by the user.
     *
     * @param shouldShowRationale True if UI should display explanatory rationale to the user.
     */
    data class Denied(val shouldShowRationale: Boolean) : PermissionState
}

/**
 * Interface contract for querying permission status and guiding users to system settings.
 */
interface PermissionManager {
    /**
     * Checks if the given Android permission is currently granted.
     *
     * @param permission Manifest permission string (e.g., Manifest.permission.POST_NOTIFICATIONS).
     * @return True if granted.
     */
    fun isGranted(permission: String): Boolean

    /**
     * Inspects permission state relative to an [Activity] to determine whether rationale is warranted.
     *
     * @param activity Hosting Activity.
     * @param permission Manifest permission string.
     * @return [PermissionState] indicating status.
     */
    fun checkPermissionState(activity: Activity, permission: String): PermissionState

    /**
     * Directs the user to this application's system App Settings screen to grant revoked permissions.
     */
    fun openAppSettings()
}

/**
 * Default production implementation of [PermissionManager].
 *
 * @param context Android context used for checking permissions and launching settings.
 */
class DefaultPermissionManager(
    private val context: Context
) : PermissionManager {

    override fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun checkPermissionState(activity: Activity, permission: String): PermissionState {
        return if (isGranted(permission)) {
            PermissionState.Granted
        } else {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission
            )
            PermissionState.Denied(shouldShowRationale = shouldShowRationale)
        }
    }

    override fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
