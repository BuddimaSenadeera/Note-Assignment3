package com.example.task_master.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.task_master.MainActivity
import com.example.task_master.R
import com.example.task_master.databinding.FragmentEditNoteBinding
import com.example.task_master.model.Note
import com.example.task_master.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    // Using Safe Args to retrieve arguments
    private val args: EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // menu provider
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Create ViewModel
        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!  // Retrieve the note passed via Safe Args

        // Set values for views
        binding.editNoteTitle.setText(currentNote.noteTitle)
        binding.editNoteDesc.setText(currentNote.noteDesc)
        binding.showDateTime.setText(currentNote.noteDateTime)

        // Set up priority spinner
        setupPrioritySpinner()

        // date and time picker dialog
        val selectDateTimeButton = binding.editDateTimeButton
        val selectedDateTimeTextView = binding.selectedDateTimeTextView

        var formattedDateTime: String? = null

        selectDateTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHourOfDay, selectedMinute ->

                    val selectedDateTime = Calendar.getInstance()
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay, selectedHourOfDay, selectedMinute)

                    formattedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(selectedDateTime.time)
                    Toast.makeText(requireContext(), "Selected Date and Time: $formattedDateTime", Toast.LENGTH_SHORT).show()
                    selectedDateTimeTextView.text = formattedDateTime
                    selectedDateTimeTextView.visibility = View.VISIBLE
                }, hourOfDay, minute, false)
                timePickerDialog.setOnShowListener {
                    customizeDialogButtonColors(timePickerDialog)
                }
                timePickerDialog.show()
            }, year, month, dayOfMonth)
            datePickerDialog.setOnShowListener {
                customizeDialogButtonColors(datePickerDialog)
            }
            datePickerDialog.show()
        }

        // Update note
        binding.editNoteFab.setOnClickListener {
            val noteTitle = binding.editNoteTitle.text.toString().trim()
            val noteDesc = binding.editNoteDesc.text.toString().trim()

            val selectedPriority = binding.editPriority.selectedItem.toString()  // Get selected priority

            if (noteTitle.isNotEmpty()) {
                val note = Note(
                    currentNote.id,
                    noteTitle,
                    noteDesc,
                    selectedPriority,  // Save selected priority
                    formattedDateTime ?: currentNote.noteDateTime
                )
                notesViewModel.updateNote(note)
                view.findNavController().popBackStack(R.id.homeFragment, false)

            } else {
                Toast.makeText(context, "Please enter note title", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun customizeDialogButtonColors(dialog: AlertDialog) {
        // Check the current theme mode
        val isDarkTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        // Get references to the dialog buttons
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        // Dynamically set colors based on the theme
        val positiveColor = if (isDarkTheme) ContextCompat.getColor(requireContext(), R.color.colorOnPrimary)
        else ContextCompat.getColor(requireContext(), R.color.colorOnSecondary)

        val negativeColor = if (isDarkTheme) ContextCompat.getColor(requireContext(), R.color.colorOnPrimary)
        else ContextCompat.getColor(requireContext(), R.color.colorOnSecondary)

        // Apply the colors
        positiveButton.setTextColor(positiveColor)
        negativeButton.setTextColor(negativeColor)
    }

    // Set up priority spinner
    private fun setupPrioritySpinner() {
        val priorityArray = resources.getStringArray(R.array.priority_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editPriority.adapter = adapter

        // Set the spinner to the current note's priority
        val currentPriorityIndex = priorityArray.indexOf(currentNote.notePriority)
        if (currentPriorityIndex >= 0) {
            binding.editPriority.setSelection(currentPriorityIndex)
        }
    }

    // delete note
    private fun deleteNote() {
        val builder = AlertDialog.Builder(requireContext()) // Ensure we use non-null context
        val alertDialog = builder.apply {
            setTitle("Delete Note")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete") { _, _ ->
                notesViewModel.deleteNote(currentNote)
                Toast.makeText(requireContext(), "Note Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create()

        // Show the dialog first, then customize the button colors
        alertDialog.setOnShowListener {
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Check the current theme mode and apply text colors accordingly
            val isDarkTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                else -> false
            }

            // Dynamically set colors based on the theme
            if (isDarkTheme) {
                positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnPrimary)) // Dark theme color
                negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnPrimary)) // Dark theme color
            } else {
                positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSecondary)) // Light theme color
                negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSecondary)) // Light theme color
            }
        }

        alertDialog.show()
    }



    // Create menu items
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_note, menu)

        // Get the delete menu item
        val deleteMenuItem = menu.findItem(R.id.deleteMenu)

        // Get the current theme (dark or light)
        val isDarkTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        // Change delete icon color based on the theme
        val iconColor = if (isDarkTheme) Color.WHITE else Color.BLACK
        deleteMenuItem.icon?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            DrawableCompat.setTint(wrappedDrawable, iconColor)
            deleteMenuItem.icon = wrappedDrawable
        }
    }

    // Handle menu item selection
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.deleteMenu -> {
                deleteNote()
                true
            }
            else -> false
        }
    }

    // Clean view binding
    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding = null
    }
}