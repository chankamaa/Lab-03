package com.example.lab03

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onDeleteTask: (Int) -> Unit,  // Function for delete action
    private val onEditTask: (Int, String) -> Unit  // Function for edit action
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitle.text = task.title

        // Delete button logic
        holder.btnDelete.setOnClickListener {
            onDeleteTask(position)
        }

        // Edit button logic
        holder.btnEdit.setOnClickListener {
            showEditDialog(holder.itemView.context, task.title) { newTitle ->
                onEditTask(position, newTitle)
                holder.taskTitle.text = newTitle  // Update UI
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    // Function to show an AlertDialog for editing a task
    private fun showEditDialog(context: Context, currentTitle: String, onTaskEdited: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Edit Task")

        // Create an EditText programmatically
        val input = EditText(context)
        input.setText(currentTitle)  // Set current task title in the EditText

        // Set the EditText as the custom view in the AlertDialog
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val newTitle = input.text.toString()
            if (newTitle.isNotEmpty()) {
                onTaskEdited(newTitle)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
