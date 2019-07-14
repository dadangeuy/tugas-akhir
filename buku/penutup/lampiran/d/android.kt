class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseInstanceId.getInstance()
            .instanceId
            .addOnCompleteListener { task ->
                run { TokenTextView.text = task.result?.token }
            }
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val toast = Toast.makeText(this, "Token Copied!", Toast.LENGTH_SHORT)
        CopyButton.setOnClickListener {
            run {
                clipboard.primaryClip = ClipData.newPlainText("token", TokenTextView.text)
                toast.show()
            }
        }
    }
}

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
