package com.example.to_do_list_2.data

import java.util.*

// COMPLETED (2) Annotate the class with Entity. Use "task" for the table name

class TaskEntry {
    // COMPLETED (3) Annotate the id as PrimaryKey. Set autoGenerate to true.

    var id = 0
    var description: String
    var priority: Int
    var updatedAt: Date

    // COMPLETED (4) Use the Ignore annotation so Room knows that it has to use the other constructor instead

    constructor(description: String, priority: Int, updatedAt: Date) {
        this.description = description
        this.priority = priority
        this.updatedAt = updatedAt
    }

    constructor(id: Int, description: String, priority: Int, updatedAt: Date) {
        this.id = id
        this.description = description
        this.priority = priority
        this.updatedAt = updatedAt
    }

}
