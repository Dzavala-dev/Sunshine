package com.example.todolist

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.android.todolist.R


class AddTaskActivity : AppCompatActivity() {
    // Declare a member variable to keep track of a task's selected mPriority
    private var mPriority = 0
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Initialize to highest mPriority by default (mPriority = 1)
        (findViewById(R.id.radButton1) as RadioButton).isChecked = true
        mPriority = 1
    }

    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    fun onClickAddTask(view: View?) {
        // Not yet implemented
    }

    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    fun onPrioritySelected(view: View?) {
        if ((findViewById(R.id.radButton1) as RadioButton).isChecked) {
            mPriority = 1
        } else if ((findViewById(R.id.radButton2) as RadioButton).isChecked) {
            mPriority = 2
        } else if ((findViewById(R.id.radButton3) as RadioButton).isChecked) {
            mPriority = 3
        }
    }
}
