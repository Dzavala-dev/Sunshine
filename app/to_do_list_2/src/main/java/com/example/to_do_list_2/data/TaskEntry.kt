package com.example.to_do_list_2.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "task")
class TaskEntry {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var description: String
    var priority: Int

    @ColumnInfo(name = "updated_at")
    var updatedAt: Date

    @Ignore
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
