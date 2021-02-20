package com.example.to_do_list_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), ItemClickListener {
    // Member variables for the adapter and RecyclerView
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: TaskAdapter? = null
    private var mDb: AppDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks)

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(LinearLayoutManager(this))

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = TaskAdapter(this, this)
        mRecyclerView.setAdapter(mAdapter)
        val decoration =
            DividerItemDecoration(applicationContext, VERTICAL)
        mRecyclerView.addItemDecoration(decoration)

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */ItemTouchHelper(object :
            SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            fun onMove(
                recyclerView: RecyclerView?,
                viewHolder: RecyclerView.ViewHolder?,
                target: RecyclerView.ViewHolder?
            ): Boolean {
                return false
            }

            // Called when a user swipes left or right on a ViewHolder
            fun onSwiped(viewHolder: RecyclerView.ViewHolder?, swipeDir: Int) {
                // Here is where you'll implement swipe to delete
            }
        }).attachToRecyclerView(mRecyclerView)

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        val fabButton: FloatingActionButton = findViewById(R.id.fab)
        fabButton.setOnClickListener(View.OnClickListener { // Create a new intent to start an AddTaskActivity
            val addTaskIntent = Intent(this@MainActivity, AddTaskActivity::class.java)
            startActivity(addTaskIntent)
        })
        mDb = AppDatabase.getInstance(applicationContext)
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this re-queries the database data for any changes.
     */
    override fun onResume() {
        super.onResume()
        // COMPLETED (5) Get the diskIO Executor from the instance of AppExecutors and
        // call the diskIO execute method with a new Runnable and implement its run method
        AppExecutors.getInstance().diskIO()
            .execute(Runnable { // COMPLETED (6) Move the logic into the run method and
                // Extract the list of tasks to a final variable
                val tasks: List<TaskEntry> = mDb.taskDao().loadAllTasks()
                // COMPLETED (7) Wrap the setTask call in a call to runOnUiThread
                // We will be able to simplify this once we learn more
                // about Android Architecture Components
                runOnUiThread { mAdapter!!.setTasks(tasks) }
            })
    }

    fun onItemClickListener(itemId: Int) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
    }

    companion object {
        // Constant for logging
        private val TAG = MainActivity::class.java.simpleName
    }
}
