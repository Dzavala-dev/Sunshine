package com.example.to_do_list_2

import androidx.lifecycle.ViewModelProvider
import com.example.to_do_list_2.data.AppDatabase
import androidx.lifecycle.ViewModel as ViewModel1


// COMPLETED (1) Make this class extend ViewModel ViewModelProvider.NewInstanceFactory
class AddTaskViewModelFactory(database: AppDatabase, taskId: Int) :
    ViewModelProvider.NewInstanceFactory() {
    // COMPLETED (2) Add two member variables. One for the database and one for the taskId
    private val mDb: AppDatabase
    private val mTaskId: Int

    // COMPLETED (4) Uncomment the following method
    fun <T : ViewModel1?> create(modelClass: Class<T>?): T {
        return AddTaskViewModel(mDb, mTaskId) as T
    }

    // COMPLETED (3) Initialize the member variables in the constructor with the parameters received
    init {
        mDb = database
        mTaskId = taskId
    }
}

