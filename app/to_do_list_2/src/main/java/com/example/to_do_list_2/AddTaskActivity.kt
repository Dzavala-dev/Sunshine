package com.example.to_do_list_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.android.todolist.R
import com.example.to_do_list_2.data.AppDatabase
import com.example.to_do_list_2.data.TaskEntry
import java.util.*


class AddTaskActivity : AppCompatActivity() {
    // Fields for views
    var mEditText: EditText? = null
    var mRadioGroup: RadioGroup? = null
    var mButton: Button? = null
    private var mTaskId = DEFAULT_TASK_ID

    // Member variable for the Database
    private var mDb: AppDatabase? = null
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        initViews()
        mDb = AppDatabase.getInstance(getApplicationContext())
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(
                INSTANCE_TASK_ID,
                DEFAULT_TASK_ID
            )
        }
        val intent: Intent = getIntent()
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton?.setText(R.string.update_button)
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(
                    EXTRA_TASK_ID,
                    DEFAULT_TASK_ID
                )

                // COMPLETED (9) Remove the logging and the call to loadTaskById, this is done in the ViewModel now
                // COMPLETED (10) Declare a AddTaskViewModelFactory using mDb and mTaskId
                val factory = mDb?.let { AddTaskViewModelFactory(it, mTaskId) }
                // COMPLETED (11) Declare a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
                // for that use the factory created above AddTaskViewModel
                val viewModel: AddTaskViewModel = ViewModelProviders.of(this, factory).get(
                    AddTaskViewModel::class.java
                )

                // COMPLETED (12) Observe the LiveData object in the ViewModel. Use it also when removing the observer
                viewModel.getTask().observe(this, object : Observer<TaskEntry?>() {
                    fun onChanged(@Nullable taskEntry: TaskEntry?) {
                        viewModel.getTask().removeObserver(this)
                        populateUI(taskEntry)
                    }
                })
            }
        }
    }

     override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId)
        super.onSaveInstanceState(outState)
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private fun initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription)
        mRadioGroup = findViewById(R.id.radioGroup)
        mButton = findViewById(R.id.saveButton)
        mButton!!.setOnClickListener { onSaveButtonClicked() }
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private fun populateUI(task: TaskEntry?) {
        if (task == null) {
            return
        }
        mEditText.text(task.description)
        setPriorityInViews(task.priority)
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    fun onSaveButtonClicked() {
        val description = mEditText!!.text.toString()
        val priority = priorityFromViews
        val date = Date()
        val task = TaskEntry(description, priority, date)
        AppExecutors.getInstance().diskIO().execute(Runnable {
            if (mTaskId == DEFAULT_TASK_ID) {
                // insert new task
                mDb.taskDao().insertTask(task)
            } else {
                //update task
                task.setId(mTaskId)
                mDb.taskDao().updateTask(task)
            }
            finish()
        })
    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    val priorityFromViews: Int
        get() {
            var priority = 1
            val checkedId =
                (findViewById(R.id.radioGroup) as RadioGroup?)!!.checkedRadioButtonId
            when (checkedId) {
                R.id.radButton1 -> priority = PRIORITY_HIGH
                R.id.radButton2 -> priority = PRIORITY_MEDIUM
                R.id.radButton3 -> priority = PRIORITY_LOW
            }
            return priority
        }

    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    fun setPriorityInViews(priority: Int) {
        when (priority) {
            PRIORITY_HIGH -> (findViewById(R.id.radioGroup) as RadioGroup?)!!.check(
                R.id.radButton1
            )
            PRIORITY_MEDIUM -> (findViewById(R.id.radioGroup) as RadioGroup?)!!.check(
                R.id.radButton2
            )
            PRIORITY_LOW -> (findViewById(R.id.radioGroup) as RadioGroup?)!!.check(
                R.id.radButton3
            )
        }
    }

    private fun findViewById(radioGroup: Int): RadioGroup? {

    }

    companion object {
        // Extra for the task ID to be received in the intent
        val EXTRA_TASK_ID: String? = "extraTaskId"

        // Extra for the task ID to be received after rotation
        val INSTANCE_TASK_ID: String? = "instanceTaskId"

        // Constants for priority
        const val PRIORITY_HIGH = 1
        const val PRIORITY_MEDIUM = 2
        const val PRIORITY_LOW = 3

        // Constant for default task id to be used when not in update mode
        private const val DEFAULT_TASK_ID = -1

        // Constant for logging
        private val TAG = AddTaskActivity::class.java.simpleName
    }
}
