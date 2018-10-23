package de.adorsys.android.summerparty.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object PermissionManager {
    fun permissionPending(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
                context,
                permission) != PackageManager.PERMISSION_GRANTED
    }


    fun requestPermission(activity: AppCompatActivity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                requestCode)
    }

    fun handlePermissionsResult(activity: AppCompatActivity,
                                grantResults: IntArray,
                                permissions: Array<out String>,
                                action: ((Boolean) -> Unit)) {
        action.let {
            it(grantResults.isNotEmpty()
                    && permissions.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0]))
        }
    }
}
