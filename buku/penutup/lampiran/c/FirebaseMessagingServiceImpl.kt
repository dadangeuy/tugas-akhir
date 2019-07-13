class FirebaseMessagingServiceImpl : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        val remoteNotification = message?.notification
        val notification = Notification.Builder(this)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle(remoteNotification?.title)
            .setContentText(remoteNotification?.body)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, notification)
    }
}

