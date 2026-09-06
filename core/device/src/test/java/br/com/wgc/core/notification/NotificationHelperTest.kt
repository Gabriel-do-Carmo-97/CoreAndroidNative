package br.com.wgc.core.notification

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NotificationHelperTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationHelper: NotificationHelper

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationHelper = NotificationHelper(context)
    }

    @Test
    fun createNotificationChannelsDefault_createsAllExpectedChannels() {
        val appName = "TestApp"
        notificationHelper.createNotificationChannelsDefault(appName)

        val urgentChannel = notificationManager.getNotificationChannel(
            "${NotificationHelper.URGENT_CHANNEL_ID}-$appName"
        )
        val defaultChannel = notificationManager.getNotificationChannel(
            "${NotificationHelper.DEFAULT_CHANNEL_ID}-$appName"
        )
        val lowChannel = notificationManager.getNotificationChannel(
            "${NotificationHelper.LOW_PRIORITY_CHANNEL_ID}-$appName"
        )
        val downloadChannel = notificationManager.getNotificationChannel(
            "${NotificationHelper.DOWNLOAD_CHANNEL_ID}-$appName"
        )

        assertNotNull(urgentChannel)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, urgentChannel.importance)

        assertNotNull(defaultChannel)
        assertEquals(NotificationManager.IMPORTANCE_DEFAULT, defaultChannel.importance)

        assertNotNull(lowChannel)
        assertEquals(NotificationManager.IMPORTANCE_LOW, lowChannel.importance)

        assertNotNull(downloadChannel)
        assertEquals(NotificationManager.IMPORTANCE_MIN, downloadChannel.importance)
    }

    @Test
    fun createNotification_buildsValidNotification() {
        val notification = CreateNotification(context)
            .with(channelId = "test_channel", textTitle = "Título Teste")
            .withContent(android.R.drawable.ic_dialog_info, "Conteúdo da notificação")
            .autoCancel(true)
            .build()

        assertNotNull(notification)
        assertEquals("test_channel", notification.channelId)
    }
}
