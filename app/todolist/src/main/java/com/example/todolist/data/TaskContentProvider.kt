package com.example.todolist.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.annotation.NonNull

// COMPLETED (1) Verify that TaskContentProvider extends from ContentProvider and implements required methods
class TaskContentProvider : ContentProvider() {
    // Member variable for a TaskDbHelper that's initialized in the onCreate() method
    private var mTaskDbHelper: TaskDbHelper? = null

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    override fun onCreate(): Boolean {
        // COMPLETED (2) Complete onCreate() and initialize a TaskDbhelper on startup
        // [Hint] Declare the DbHelper as a global variable
        val context = context
        mTaskDbHelper = TaskDbHelper(context)
        return true
    }

    override fun insert(
        @NonNull uri: Uri,
        values: ContentValues?
    ): Uri? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun query(
        @NonNull uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun delete(
        @NonNull uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun update(
        @NonNull uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun getType(@NonNull uri: Uri): String? {
        throw UnsupportedOperationException("Not yet implemented")
    }
}
