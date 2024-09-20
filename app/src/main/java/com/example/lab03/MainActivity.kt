package com.example.lab03

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get references to the buttons
        val btnTaskList: Button = findViewById(R.id.btnTaskList)
        val btnTimer: Button = findViewById(R.id.btnTimer)
        val btnReminder: Button = findViewById(R.id.btnReminder)

        // Set onClickListeners for the buttons
        btnTaskList.setOnClickListener {
            // Navigate to the Task List Activity
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
        }

        btnTimer.setOnClickListener {
            // Navigate to the Timer/Stopwatch Activity (We'll create this later)
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        btnReminder.setOnClickListener {
            // Navigate to the Reminder System Activity (We'll create this later)
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
        }
    }
}
