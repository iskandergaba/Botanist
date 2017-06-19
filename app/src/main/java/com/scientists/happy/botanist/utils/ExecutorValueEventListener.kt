package com.scientists.happy.botanist.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import java.util.concurrent.Executor

/**
 * Utility class to help Firebase Database events get processed on a thread
 * determined by an Executor, rather than the main thread.  Use this when
 * your processing may block or take more time on the main thread than is
 * healthy for your app.

 * The [.onDataChange] callback will trigger a call
 * to [.onDataChangeExecutor], executing on the Executor
 * with the same DataSnapshot argument.  Similarly, the
 * [.onCancelled] callback will trigger a call to
 * [.onDataChangeExecutor].
 */

abstract class ExecutorValueEventListener protected constructor(private val executor: Executor) : ValueEventListener {

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        executor.execute { onDataChangeExecutor(dataSnapshot) }
    }

    override fun onCancelled(databaseError: DatabaseError) {
        executor.execute { onCancelledExecutor(databaseError) }
    }

    protected abstract fun onDataChangeExecutor(dataSnapshot: DataSnapshot)
    protected abstract fun onCancelledExecutor(databaseError: DatabaseError)

}
