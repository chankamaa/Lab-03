package com.example.lab03

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            try {
                val sharedPreferences = context.getSharedPreferences("TaskApp", Context.MODE_PRIVATE)
                val gson = Gson()

                // Retrieve the saved task list from SharedPreferences
                val savedTaskListJson = sharedPreferences.getString("task_list", null)

                // Handle the case where there are no saved tasks
                val taskList: MutableList<Task> = if (savedTaskListJson != null) {
                    val type = object : TypeToken<MutableList<Task>>() {}.type
                    gson.fromJson(savedTaskListJson, type)
                } else {
                    mutableListOf()  // Create an empty list if no tasks are found
                }

                // Get the latest task or show "No Task" if the list is empty
                val latestTask = if (taskList.isNotEmpty()) taskList.last().title else "No Task"

                // Create an Intent to launch the MainActivity when the widget is clicked
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                // Update the widget layout with the task
                val views = RemoteViews(context.packageName, R.layout.widget_layout)
                views.setTextViewText(R.id.widgetTask, "Upcoming Task: $latestTask")
                views.setOnClickPendingIntent(R.id.widgetTaskTitle, pendingIntent)

                // Update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
