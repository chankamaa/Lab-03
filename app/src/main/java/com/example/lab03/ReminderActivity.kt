package com.example.lab03

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var reminderTitle: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var btnSetReminder: Button
    private lateinit var btnShowReminders: Button
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        reminderTitle = findViewById(R.id.reminderTitle)
        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)
        btnSetReminder = findViewById(R.id.btnSetReminder)
        btnShowReminders = findViewById(R.id.btnShowReminders)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the 12-hour or 24-hour mode programmatically
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setIs24HourView(false) // 12-hour view
        }

        // Set the reminder when the button is clicked
        btnSetReminder.setOnClickListener {
            setReminder()
        }

        // Show reminders when the button is clicked
        btnShowReminders.setOnClickListener {
            val intent = Intent(this, ShowRemindersActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setReminder() {
        val title = reminderTitle.text.toString()

        // Get the selected date and time from DatePicker and TimePicker
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, datePicker.year)
            set(Calendar.MONTH, datePicker.month)
            set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
            if (Build.VERSION.SDK_INT >= 23) {
                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
            } else {
                set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
                set(Calendar.MINUTE, timePicker.currentMinute)
            }
            set(Calendar.SECOND, 0)
        }

        // Log the reminder being set
        Log.d("ReminderActivity", "Setting reminder: $title at ${calendar.time}")

        // Save the reminder in SharedPreferences
        saveReminderToSharedPreferences(title, calendar.timeInMillis)

        // Create an intent to trigger the ReminderReceiver
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("reminderTitle", title)
        }

        // Create a pending intent for the alarm
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm to trigger at the selected time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun saveReminderToSharedPreferences(title: String, timeInMillis: Long) {
        val sharedPreferences = getSharedPreferences("Reminders", Context.MODE_PRIVATE)
        val gson = Gson()

        // Retrieve the existing reminders from SharedPreferences
        val existingRemindersJson = sharedPreferences.getString("reminders", "[]")
        val reminderListType = object : TypeToken<MutableList<Reminder>>() {}.type
        val reminderList: MutableList<Reminder> = gson.fromJson(existingRemindersJson, reminderListType)

        // Add the new reminder to the list
        reminderList.add(Reminder(title, timeInMillis))

        // Save the updated list back to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("reminders", gson.toJson(reminderList))
        editor.apply()

        // Log the saved reminders for debugging
        Log.d("ReminderActivity", "Saved reminders: ${gson.toJson(reminderList)}")
    }
}
