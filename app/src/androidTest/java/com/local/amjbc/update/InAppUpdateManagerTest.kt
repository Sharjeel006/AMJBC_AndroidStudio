package com.local.amjbc.update

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test validating the Google Play In-App Updates Flexible update flow
 * using FakeAppUpdateManager (Update Available -> User Accepts -> Download -> Complete Update).
 */
@RunWith(AndroidJUnit4::class)
class InAppUpdateManagerTest {

    @Test
    fun testFlexibleUpdateFlowWithFakeAppUpdateManager() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fakeAppUpdateManager = FakeAppUpdateManager(context)

        // 1. Setup fake update: version 203 available with FLEXIBLE update type allowed
        fakeAppUpdateManager.setUpdateAvailable(203, AppUpdateType.FLEXIBLE)
        fakeAppUpdateManager.setUpdatePriority(0)

        // 2. Verify update availability
        val updateInfoTask = fakeAppUpdateManager.appUpdateInfo
        assertTrue(updateInfoTask.isSuccessful)

        // 3. User accepts flexible update
        fakeAppUpdateManager.userAcceptsUpdate()

        // 4. Download completes -> InstallStatus becomes DOWNLOADED
        fakeAppUpdateManager.downloadCompletes()

        // 5. Complete update
        val completeTask = fakeAppUpdateManager.completeUpdate()
        assertTrue(completeTask.isSuccessful)
    }
}