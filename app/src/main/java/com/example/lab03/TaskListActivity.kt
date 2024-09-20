package com.example.lab03

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

class TaskListActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        val recyclerView: RecyclerView = findViewById(R.id.taskRecyclerView)
        val inputTask: EditText = findViewById(R.id.inputTask)
        val btnAddTask: Button = findViewById(R.id.btnAddTask)

        // Initialize SharedPreferences and Gson
        val sharedPreferences = getSharedPreferences("TaskApp", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        // Load saved task list from SharedPreferences
        val savedTaskListJson = sharedPreferences.getString("task_list", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        taskList = if (savedTaskListJson != null) {
            Log.d("TaskListActivity", "Loaded Task List: $savedTaskListJson")
            gson.fromJson(savedTaskListJson, type)
        } else {
            Log.d("TaskListActivity", "No saved task list found, initializing empty list.")
            mutableListOf()
        }

        // Initialize task adapter and RecyclerView
        taskAdapter = TaskAdapter(taskList, ::deleteTask, ::editTask)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add task logic
        btnAddTask.setOnClickListener {
            val taskTitle = inputTask.text.toString()
            if (taskTitle.isNotEmpty()) {
                // Add task to the list and update adapter
                taskList.add(Task(taskTitle))
                taskAdapter.notifyDataSetChanged()

                // Save the task list to SharedPreferences
                val taskListJson = gson.toJson(taskList)
                editor.putString("task_list", taskListJson)
                editor.putString("latest_task", taskTitle)
                editor.apply()

                inputTask.text.clear()

                // Broadcast widget update intent
                val intent = Intent(this, WidgetProvider::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                sendBroadcast(intent)
            }
        }
    }

    // Function to delete a task
    private fun deleteTask(position: Int) {
        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)

        // Update the saved task list in SharedPreferences
        val sharedPreferences = getSharedPreferences("TaskApp", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val taskListJson = gson.toJson(taskList)
        editor.putString("task_list", taskListJson)
        editor.apply()

        // Log task list after deletion
        Log.d("TaskListActivity", "Task List After Deletion: $taskListJson")
    }

    // Function to edit a task
    private fun editTask(position: Int, newTitle: String) {
        taskList[position].title = newTitle
        taskAdapter.notifyItemChanged(position)

        // Update the saved task list in SharedPreferences
        val sharedPreferences = getSharedPreferences("TaskApp", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val taskListJson = gson.toJson(taskList)
        editor.putString("task_list", taskListJson)
        editor.apply()

        // Log task list after edit
        Log.d("TaskListActivity", "Task List After Edit: $taskListJson")
    }
}
