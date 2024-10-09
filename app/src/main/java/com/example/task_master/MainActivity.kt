package com.example.task_master

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.task_master.database.NoteDatabase
import com.example.task_master.repository.NoteRepository
import com.example.task_master.viewmodel.NoteViewModel
import com.example.task_master.viewmodel.NoteViewModelFactory
import android.content.res.Configuration
import android.graphics.Color
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.ActionBar

class MainActivity : AppCompatActivity() {

    lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the ViewModel
        setUpViewModel()

        // Change the ActionBar title color and status bar color based on the current theme
        updateActionBarTitleColor()
        updateStatusBarColor()
    }

    // Method to update ActionBar title color and font size based on theme
    private fun updateActionBarTitleColor() {
        // Check if the current theme is dark or light
        val isDarkTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        // Set title color based on the theme
        val titleColor = if (isDarkTheme) Color.WHITE else Color.BLACK

        // Create a TextView programmatically for the ActionBar title
        val actionBarTitle = TextView(this)
        actionBarTitle.text = getString(R.string.app_name) // Set the title
        actionBarTitle.setTextColor(titleColor) // Set the title color
        actionBarTitle.textSize = 20f // Set the desired font size
        actionBarTitle.setPadding(0, 0, 0, 0) // Optional: Adjust padding if needed
        actionBarTitle.textAlignment = TextView.TEXT_ALIGNMENT_CENTER // Optional: Center the text

        // Apply the custom TextView to the ActionBar
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(actionBarTitle)
            setDisplayShowTitleEnabled(false) // Disable default title
        }
    }

    // Method to update StatusBar color based on the theme
    private fun updateStatusBarColor() {
        // Check if the current theme is dark or light
        val isDarkTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        // Set status bar color based on the theme
        val statusBarColor = if (isDarkTheme) {
            ContextCompat.getColor(this, R.color.colorOnSecondary)  // Use black for dark theme
        } else {
            ContextCompat.getColor(this, R.color.white)  // Use primary variant for light theme
        }

        // Set the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = statusBarColor
        }
    }

    // Implement NoteViewModel
    private fun setUpViewModel() {
        // Create NoteRepository
        val noteRepository = NoteRepository(NoteDatabase(this))

        // Create NoteViewModelFactory
        val viewModelProviderFactory = NoteViewModelFactory(application, noteRepository)

        // Get NoteViewModel instance
        noteViewModel = ViewModelProvider(this, viewModelProviderFactory)[NoteViewModel::class.java]
    }
}