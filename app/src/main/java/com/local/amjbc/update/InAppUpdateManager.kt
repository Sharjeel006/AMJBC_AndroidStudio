package com.local.amjbc.update

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.local.amjbc.R

/**
 * Encapsulates Google Play In-App Update checks, options, and installation callbacks.
 *
 * This class is lifecycle-aware and manages registration of the ActivityResultLauncher
 * and InstallStateUpdatedListener automatically.
 *
 * @param activity The host AppCompatActivity context.
 * @param appUpdateManager An instance of AppUpdateManager (injectable for testing, e.g. FakeAppUpdateManager).
 */
class InAppUpdateManager(
    private val activity: AppCompatActivity,
    val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
) : DefaultLifecycleObserver {

    companion object {
        private const val TAG = "InAppUpdateManager"
        private const val HIGH_PRIORITY_THRESHOLD = 4
        private const val STALENESS_DAYS_THRESHOLD = 7
    }

    private var isListenerRegistered = false

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showUpdateDownloadedSnackbar()
        }
    }

    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                Log.w(TAG, "Update flow failed or cancelled by user. Result code: ${result.resultCode}")
            } else {
                Log.i(TAG, "Update flow completed successfully.")
            }
        }

    init {
        activity.lifecycle.addObserver(this)
    }

    /**
     * Checks for app update availability on launch and triggers immediate or flexible update flow.
     */
    fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { updateInfo ->
                if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    val priority = updateInfo.updatePriority()
                    val stalenessDays = updateInfo.clientVersionStalenessDays() ?: 0

                    val isHighPriority = priority >= HIGH_PRIORITY_THRESHOLD
                    val isStale = stalenessDays >= STALENESS_DAYS_THRESHOLD

                    if ((isHighPriority || isStale) && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startUpdateFlow(updateInfo, AppUpdateType.IMMEDIATE)
                    } else if (updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        registerInstallListener()
                        startUpdateFlow(updateInfo, AppUpdateType.FLEXIBLE)
                    }
                } else if (updateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showUpdateDownloadedSnackbar()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to check for app update", e)
            }
    }

    /**
     * Resumes in-progress immediate updates or prompts for downloaded flexible updates when resuming the activity.
     */
    fun resumeUpdateCheck() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { updateInfo ->
                if (updateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showUpdateDownloadedSnackbar()
                } else if (updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    if (updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startUpdateFlow(updateInfo, AppUpdateType.IMMEDIATE)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to resume update check", e)
            }
    }

    private fun startUpdateFlow(updateInfo: AppUpdateInfo, updateType: Int) {
        try {
            val options = AppUpdateOptions.defaultOptions(updateType)
            appUpdateManager.startUpdateFlowForResult(
                updateInfo,
                updateLauncher,
                options
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start update flow for type: $updateType", e)
        }
    }

    private fun registerInstallListener() {
        if (!isListenerRegistered) {
            appUpdateManager.registerListener(installStateUpdatedListener)
            isListenerRegistered = true
        }
    }

    fun unregisterInstallListener() {
        if (isListenerRegistered) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
            isListenerRegistered = false
        }
    }

    private fun showUpdateDownloadedSnackbar() {
        val rootView = activity.findViewById<View>(android.R.id.content) ?: return
        Snackbar.make(
            rootView,
            activity.getString(R.string.update_downloaded_message),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(activity.getString(R.string.update_restart_action)) {
                appUpdateManager.completeUpdate()
            }
            show()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        resumeUpdateCheck()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unregisterInstallListener()
    }
}