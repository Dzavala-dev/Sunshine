package com.example.to_do_list_2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.to_do_list_2.data.AppDatabase
import com.example.to_do_list_2.data.TaskEntry


// COMPLETED (5) Make this class extend ViewModel
class AddTaskViewModel(database: AppDatabase, taskId: Int) : ViewModel() {
    // COMPLETED (6) Add a task member variable for the TaskEntry object wrapped in a LiveData
    private val task: LiveData<TaskEntry?> = database.taskDao().loadTaskById(taskId)!!

    // COMPLETED (7) Create a getter for the task variable
    fun getTask(): LiveData<TaskEntry?> {
        return task
    }

}

