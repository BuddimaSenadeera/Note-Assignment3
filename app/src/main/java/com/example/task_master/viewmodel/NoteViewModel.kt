package com.example.task_master.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.task_master.model.Note
import com.example.task_master.repository.NoteRepository
import kotlinx.coroutines.launch

// ViewModel class
class NoteViewModel(app: Application, private val noteRepository: NoteRepository): AndroidViewModel(app) {

    // Add a new note
    fun addNote(note: Note) {
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }
    }

    // Delete a note
    fun deleteNote(note: Note) =
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }

    // Update a note
    fun updateNote(note: Note) =
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }

    // Get all notes
    fun getAllNotes() = noteRepository.getAllNotes()

    // Get all notes sorted by priority (High -> Moderate -> Low)
    fun getAllNotesSortedByPriority(): LiveData<List<Note>> = noteRepository.getAllNotesSortedByPriority()

    // Search notes using query
    fun searchNotes(query: String?) =
        noteRepository.searchNote(query)
}
