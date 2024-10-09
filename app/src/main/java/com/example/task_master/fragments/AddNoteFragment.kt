package com.example.task_master.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.task_master.MainActivity
import com.example.task_master.R
import com.example.task_master.databinding.FragmentAddNoteBinding
import com.example.task_master.model.Note
import com.example.task_master.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// add a new note
class AddNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {

    private var addNoteBinding: FragmentAddNoteBinding? = null
    private val binding get() = addNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var addNoteView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // menu provider
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Create ViewModel
        notesViewModel = (requireActivity() as MainActivity).noteViewModel
        addNoteView = view

        // date and time picker dialog
        val selectDateTimeButton = binding.selectDateTimeButton
        val selectedDateTimeTextView = binding.selectedDateTimeTextView

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

                    val formattedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(selectedDateTime.time)
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

    // Save the note
    private fun saveNote(view: View) {
        val noteTitle = binding.addNoteTitle.text.toString().trim()
        val noteDesc = binding.addNoteDesc.text.toString().trim()

        // Get the selected priority from the spinner
        val notePriority = binding.addPriority.selectedItem.toString()

        val noteDateTime = binding.selectedDateTimeTextView.text.toString().trim()

        if (noteTitle.isNotEmpty()) {
            // Create a new note object with the selected priority
            val note = Note(0, noteTitle, noteDesc, notePriority, noteDateTime)
            notesViewModel.addNote(note)

            Toast.makeText(addNoteView.context, "Note Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(addNoteView.context, "Please enter note title", Toast.LENGTH_SHORT).show()
        }
    }


    // Create menu items
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_note, menu)

        // Get the save menu item
        val saveMenuItem = menu.findItem(R.id.saveMenu)

        // Check the current theme (dark or light)
        val isDarkTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        // Change save icon color based on the theme
        val iconColor = if (isDarkTheme) {
            ContextCompat.getColor(requireContext(), R.color.white) // Dark theme color
        } else {
            ContextCompat.getColor(requireContext(), R.color.colorOnSecondary) // Light theme color
        }

        // Apply the color to the icon (if the icon is present)
        saveMenuItem.icon?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            DrawableCompat.setTint(wrappedDrawable, iconColor)
            saveMenuItem.icon = wrappedDrawable
        }

        // If you also want to change the text color (optional)
        val saveMenuTitle = SpannableString(saveMenuItem.title)
        saveMenuTitle.setSpan(ForegroundColorSpan(iconColor), 0, saveMenuTitle.length, 0)
        saveMenuItem.title = saveMenuTitle
    }


    // Handle menu item selection
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.saveMenu -> {
                saveNote(addNoteView)
                true
            }
            else -> false
        }
    }

    // Clean up the view binding
    override fun onDestroy() {
        super.onDestroy()
        addNoteBinding = null
    }
}
