package com.example.lab03

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class RemindersAdapter(private val context: Context, private val reminders: List<Reminder>) : BaseAdapter() {

    override fun getCount(): Int {
        return reminders.size
    }

    override fun getItem(position: Int): Any {
        return reminders[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        // Get the reminder for the current position
        val reminder = reminders[position]

        // Set the reminder details to the TextViews
        val titleView = view.findViewById<TextView>(android.R.id.text1)
        val timeView = view.findViewById<TextView>(android.R.id.text2)

        titleView.text = reminder.title
        timeView.text = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", reminder.time)

        return view
    }
}
