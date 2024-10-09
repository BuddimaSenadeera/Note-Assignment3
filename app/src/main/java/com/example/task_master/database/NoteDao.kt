package com.example.task_master.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.task_master.model.Note

@Dao
interface NoteDao {

    // Insert a task in database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    // Update a task in the database.
    @Update
    suspend fun updateNote(note: Note)

    // Delete a task from the database.
    @Delete
    suspend fun deleteNote(note: Note)

    // get all task from the database
    @Query("SELECT * FROM NOTES ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    //search task using query
    @Query("SELECT * FROM NOTES WHERE noteTitle LIKE :query OR noteDesc LIKE :query")
    fun searchNote(query: String?): LiveData<List<Note>>


    @Query("SELECT * FROM notes ORDER BY CASE WHEN notePriority = 'High' THEN 1 WHEN notePriority = 'Moderate' THEN 2 WHEN notePriority = 'Low' THEN 3 ELSE 4 END")
    fun getAllNotesSortedByPriority(): LiveData<List<Note>>

}
