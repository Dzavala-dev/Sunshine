package com.example.to_do_list_2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
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
        mDb = AppDatabase.getInstance(applicationContext)
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(
                INSTANCE_TASK_ID,
                DEFAULT_TASK_ID
            )
        }
        val intent = intent
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton!!.setText(R.string.update_button)
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
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
        mButton.setOnClickListener(View.OnClickListener { onSaveButtonClicked() })
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private fun populateUI(task: TaskEntry) {}

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    fun onSaveButtonClicked() {
        val description = mEditText!!.text.toString()
        val priority = priorityFromViews
        val date = Date()

        // COMPLETED (4) Make taskEntry final so it is visible inside the run method
        val taskEntry = TaskEntry(description, priority, date)
        // COMPLETED (2) Get the diskIO Executor from the instance of AppExecutors and
        // call the diskIO execute method with a new Runnable and implement its run method
        AppExecutors.getInstance().diskIO()
            .execute(Runnable { // COMPLETED (3) Move the remaining logic inside the run method
                mDb!!.taskDao().insertTask(taskEntry)
                finish()
            })
    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    val priorityFromViews: Int
        get() {
            var priority = 1
            val checkedId = (findViewById(R.id.radioGroup) as RadioGroup).checkedRadioButtonId
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
            PRIORITY_HIGH -> (findViewById(R.id.radioGroup) as RadioGroup).check(
                R.id.radButton1
            )
            PRIORITY_MEDIUM -> (findViewById(R.id.radioGroup) as RadioGroup).check(
                R.id.radButton2
            )
            PRIORITY_LOW -> (findViewById(R.id.radioGroup) as RadioGroup).check(
                R.id.radButton3
            )
        }
    }

    companion object {
        // Extra for the task ID to be received in the intent
        const val EXTRA_TASK_ID = "extraTaskId"

        // Extra for the task ID to be received after rotation
        const val INSTANCE_TASK_ID = "instanceTaskId"

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
