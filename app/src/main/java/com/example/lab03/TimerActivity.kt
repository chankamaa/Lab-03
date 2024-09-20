package com.example.lab03

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class TimerActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnReset: Button
    private lateinit var inputHours: EditText
    private lateinit var inputMinutes: EditText
    private lateinit var inputSeconds: EditText
    private lateinit var modeSwitch: RadioGroup

    private var countDownTimer: CountDownTimer? = null
    private var handler = Handler(Looper.getMainLooper()) // For stopwatch mode
    private var timeInMillis: Long = 0L
    private var startTime = 0L
    private var timeSwapBuff = 0L
    private var updateTime = 0L
    private var isRunning = false
    private var isStopwatchMode = false // Toggle between stopwatch and timer modes
    private val CHANNEL_ID = "timer_notifications"
    private val NOTIFICATION_ID = 100

    private val runnable = object : Runnable {
        override fun run() {
            val timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updateTime = timeSwapBuff + timeInMilliseconds

            val secs = (updateTime / 1000).toInt()
            val mins = secs / 60
            val hours = mins / 60
            val displaySecs = secs % 60
            val displayMins = mins % 60

            timerText.text = String.format("%02d:%02d:%02d", hours, displayMins, displaySecs)
            handler.postDelayed(this, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        timerText = findViewById(R.id.timerText)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnReset = findViewById(R.id.btnReset)
        inputHours = findViewById(R.id.inputHours)
        inputMinutes = findViewById(R.id.inputMinutes)
        inputSeconds = findViewById(R.id.inputSeconds)
        modeSwitch = findViewById(R.id.modeSwitch)

        // Check for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101 // Request code for notification permission
                )
            }
        }

        // Create notification channel for Android O and above
        createNotificationChannel()

        // Handle mode switch (Timer or Stopwatch)
        modeSwitch.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioTimer -> {
                    isStopwatchMode = false
                    inputHours.visibility = EditText.VISIBLE
                    inputMinutes.visibility = EditText.VISIBLE
                    inputSeconds.visibility = EditText.VISIBLE
                }
                R.id.radioStopwatch -> {
                    isStopwatchMode = true
                    inputHours.visibility = EditText.GONE
                    inputMinutes.visibility = EditText.GONE
                    inputSeconds.visibility = EditText.GONE
                }
            }
        }

        // Start button logic
        btnStart.setOnClickListener {
            if (!isRunning) {
                if (isStopwatchMode) {
                    startStopwatch()
                } else {
                    startTimer()
                }
            }
        }

        // Stop button logic
        btnStop.setOnClickListener {
            if (isRunning) {
                if (isStopwatchMode) {
                    stopStopwatch()
                } else {
                    stopTimer()
                }
            }
        }

        // Reset button logic
        btnReset.setOnClickListener {
            resetTimerOrStopwatch()
        }
    }

    // Create a notification channel (Android O and above)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Notifications"
            val descriptionText = "Notification when timer is about to finish"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        // Check if notification permission is granted before sending notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return // Do not send notification if permission is not granted
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // or your custom icon
            .setContentTitle("Timer Countdown")
            .setContentText("Only 10 seconds left!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun startStopwatch() {
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 0)
        isRunning = true
    }

    private fun stopStopwatch() {
        timeSwapBuff += SystemClock.uptimeMillis() - startTime
        handler.removeCallbacks(runnable)
        isRunning = false
    }

    private fun startTimer() {
        val hours = inputHours.text.toString().toLongOrNull() ?: 0L
        val minutes = inputMinutes.text.toString().toLongOrNull() ?: 0L
        val seconds = inputSeconds.text.toString().toLongOrNull() ?: 0L

        // Convert hours, minutes, seconds to milliseconds
        timeInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000

        // Set up CountDownTimer
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secs = (millisUntilFinished / 1000).toInt()
                val mins = secs / 60
                val hours = mins / 60
                val displaySecs = secs % 60
                val displayMins = mins % 60

                timerText.text = String.format("%02d:%02d:%02d", hours, displayMins, displaySecs)

                // Send notification when there are 10 seconds left
                if (millisUntilFinished <= 10000 && millisUntilFinished > 9000) {
                    sendNotification()
                }
            }

            override fun onFinish() {
                timerText.text = "00:00:00"
                isRunning = false
            }
        }.start()

        isRunning = true
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        isRunning = false
    }

    private fun resetTimerOrStopwatch() {
        if (isStopwatchMode) {
            handler.removeCallbacks(runnable)
            timerText.text = "00:00:00"
            timeSwapBuff = 0L
            updateTime = 0L
        } else {
            countDownTimer?.cancel()
            timerText.text = "00:00:00"
        }
        isRunning = false
    }
}
