package com.example.todolist.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDbHelper  // Constructor
internal constructor(context: Context?) :
    SQLiteOpenHelper(
        context,
        TaskDbHelper.Companion.DATABASE_NAME,
        null,
        TaskDbHelper.Companion.VERSION
    ) {
    /**
     * Called when the tasks database is created for the first time.
     */
    override fun onCreate(db: SQLiteDatabase) {

        // Create tasks table (careful to follow SQL formatting rules)
        val CREATE_TABLE =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME.toString() + " (" +
                    TaskContract.TaskEntry._ID.toString() + " INTEGER PRIMARY KEY, " +
                    TaskContract.TaskEntry.COLUMN_DESCRIPTION.toString() + " TEXT NOT NULL, " +
                    TaskContract.TaskEntry.COLUMN_PRIORITY.toString() + " INTEGER NOT NULL);"
        db.execSQL(CREATE_TABLE)
    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME)
        onCreate(db)
    }

    companion object {
        // The name of the database
        private const val DATABASE_NAME = "tasksDb.db"

        // If you change the database schema, you must increment the database version
        private const val VERSION = 1
    }
}
