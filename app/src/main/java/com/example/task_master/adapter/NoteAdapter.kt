package com.example.task_master.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.task_master.databinding.NoteLayoutBinding
import com.example.task_master.fragments.HomeFragmentDirections
import com.example.task_master.model.Note

class NoteAdapter: RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    // hold references
    class NoteViewHolder(val itemBinding: NoteLayoutBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            // Check items have the same ID and other details
            return oldItem.id == newItem.id &&
                    oldItem.noteDesc == newItem.noteDesc &&
                    oldItem.noteTitle == newItem.noteTitle &&
                    oldItem.notePriority == newItem.notePriority &&
                    oldItem.noteDateTime == newItem.noteDateTime
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    // Create ViewHolders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        // Inflate the item layout
        return NoteViewHolder(
            NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Return the size of dataset
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Replace contents
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        // Get the current details
        val currentNote = differ.currentList[position]

        // Bind the data with ViewHolder
        holder.itemBinding.noteTitle.text = currentNote.noteTitle
        holder.itemBinding.noteDesc.text = currentNote.noteDesc
        holder.itemBinding.notePriority.text = currentNote.notePriority
        holder.itemBinding.noteDateTime.text = currentNote.noteDateTime

        // navigate to EditNoteFragment
        holder.itemView.setOnClickListener{
            val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }
    }
}
