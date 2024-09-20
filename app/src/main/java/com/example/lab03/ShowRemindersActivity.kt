package com.example.lab03

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShowRemindersActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var remindersAdapter: RemindersAdapter
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_reminders)

        listView = findViewById(R.id.remindersListView)
        emptyView = findViewById(R.id.emptyView)

        // Load reminders from SharedPreferences
        val reminders = loadReminders()

        // Debug: Print the loaded reminders
        Log.d("ShowRemindersActivity", "Loaded Reminders: $reminders")

        // If the list is empty, show the emptyView
        if (reminders.isEmpty()) {
            emptyView.visibility = TextView.VISIBLE
            listView.visibility = ListView.GONE
        } else {
            emptyView.visibility = TextView.GONE
            listView.visibility = ListView.VISIBLE

            // Set the adapter with the loaded reminders
            remindersAdapter = RemindersAdapter(this, reminders)
            listView.adapter = remindersAdapter
        }
    }

    private fun loadReminders(): List<Reminder> {
        val sharedPreferences = getSharedPreferences("Reminders", Context.MODE_PRIVATE)
        val gson = Gson()
        val remindersJson = sharedPreferences.getString("reminders", null)

        return if (remindersJson != null) {
            val reminderListType = object : TypeToken<MutableList<Reminder>>() {}.type
            gson.fromJson(remindersJson, reminderListType)
        } else {
            // Return an empty list if there are no saved reminders
            mutableListOf()
        }
    }
}
