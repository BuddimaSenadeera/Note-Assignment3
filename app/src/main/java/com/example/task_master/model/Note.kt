package com.example.task_master.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// Entity class stored in the database
@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String,
    val noteDesc: String,
    val notePriority: String,

    val noteDateTime: String

): Parcelable
