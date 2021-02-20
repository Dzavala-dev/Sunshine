package com.example.to_do_list_2

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.todolist.R
import com.example.to_do_list_2.data.TaskEntry
import java.text.SimpleDateFormat
import java.util.*

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
class TaskAdapter(
    private val mContext: Context,
    listener: TaskAdapter.ItemClickListener
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder?>() {
    // Member variable to handle item clicks
    private val mItemClickListener: TaskAdapter.ItemClickListener

    // Class variables for the List that holds task data and the Context
    private var mTaskEntries: List<TaskEntry>? = null

    // Date formatter
    private val dateFormat =
        SimpleDateFormat(TaskAdapter.Companion.DATE_FORMAT, Locale.getDefault())

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Inflate the task_layout to a view
        val view: View = LayoutInflater.from(mContext)
            .inflate(R.layout.task_layout, parent, false)
        return TaskViewHolder(view)
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // Determine the values of the wanted data
        val taskEntry: TaskEntry = mTaskEntries!![position]
        val description: String = taskEntry.getDescription()
        val priority: Int = taskEntry.getPriority()
        val updatedAt = dateFormat.format(taskEntry.getUpdatedAt())

        //Set values
        holder.taskDescriptionView.text = description
        holder.updatedAtView.text = updatedAt

        // Programmatically set the text and color for the priority TextView
        val priorityString = "" + priority // converts int to String
        holder.priorityView.text = priorityString
        val priorityCircle =
            holder.priorityView.background as GradientDrawable
        // Get the appropriate background color based on the priority
        val priorityColor = getPriorityColor(priority)
        priorityCircle.setColor(priorityColor)
    }

    /*
    Helper method for selecting the correct priority circle color.
    P1 = red, P2 = orange, P3 = yellow
    */
    private fun getPriorityColor(priority: Int): Int {
        var priorityColor = 0
        when (priority) {
            1 -> priorityColor = ContextCompat.getColor(mContext, R.color.materialRed)
            2 -> priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange)
            3 -> priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow)
            else -> {
            }
        }
        return priorityColor
    }

    /**
     * Returns the number of items to display.
     */
    val itemCount: Int
        get() = if (mTaskEntries == null) {
            0
        } else mTaskEntries!!.size

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    fun setTasks(taskEntries: List<TaskEntry>?) {
        mTaskEntries = taskEntries
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClickListener(itemId: Int)
    }

    // Inner class for creating ViewHolders
    inner class TaskViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        // Class variables for the task description and priority TextViews
        var taskDescriptionView: TextView
        var updatedAtView: TextView
        var priorityView: TextView
        override fun onClick(view: View) {
            val elementId: Int = mTaskEntries!![getAdapterPosition()].getId()
            mItemClickListener.onItemClickListener(elementId)
        }

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        init {
            taskDescriptionView = itemView.findViewById(R.id.taskDescription)
            updatedAtView = itemView.findViewById(R.id.taskUpdatedAt)
            priorityView = itemView.findViewById(R.id.priorityTextView)
            itemView.setOnClickListener(this)
        }
    }

    companion object {
        // Constant for date format
        private const val DATE_FORMAT = "dd/MM/yyy"
    }

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    init {
        mItemClickListener = listener
    }
}