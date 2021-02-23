package com.example.to_do_list_2

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.to_do_list_2.data.AppDatabase
import com.example.to_do_list_2.data.TaskEntry


class MainViewModel(application: Application?) :
    AndroidViewModel(application!!) {
    private val tasks: LiveData<List<TaskEntry>>
    fun getTasks(): LiveData<List<TaskEntry>> {
        return tasks
    }

    companion object {
        // Constant for logging
        private val TAG = MainViewModel::class.java.simpleName
    }

    init {
        val database: AppDatabase? = AppDatabase.getInstance(this.getApplication())
        Log.d(
            TAG,
            "Actively retrieving the tasks from the DataBase"
        )
        tasks = database.taskDao().loadAllTasks()?()
    }
}


