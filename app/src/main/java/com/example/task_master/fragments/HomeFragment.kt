package com.example.task_master.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task_master.R
import com.example.task_master.adapter.NoteAdapter
import com.example.task_master.databinding.FragmentHomeBinding
import com.example.task_master.model.Note
import com.example.task_master.viewmodel.NoteViewModel

// Display the all tasks
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    // Create the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        // Implement the note adapter
        noteAdapter = NoteAdapter()
        binding.homeRecyclerView.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Implement the ViewModel
        noteViewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)

        // Observe the sorted notes by priority
        noteViewModel.getAllNotesSortedByPriority().observe(viewLifecycleOwner) { notes ->
            noteAdapter.differ.submitList(notes)
            updateUI(notes)
        }

        // Add a new task
        binding.addNoteFab.setOnClickListener {
            navigateToAddNoteFragment()
        }
    }

    private fun navigateToAddNoteFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToAddNoteFragment()
        requireView().findNavController().navigate(action)
    }

    private fun updateUI(notes: List<Note>) {
        // Check if the current theme is dark or light
        val isDarkTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        if (notes.isEmpty()) {
            binding.emptyNotesImage.visibility = View.VISIBLE

            // Set the appropriate image based on the current theme
            if (isDarkTheme) {
                binding.emptyNotesImage.setImageResource(R.drawable.dark_bg) // Dark theme image
            } else {
                binding.emptyNotesImage.setImageResource(R.drawable.emptybg) // Light theme image
            }

            binding.homeRecyclerView.visibility = View.GONE
        } else {
            binding.emptyNotesImage.visibility = View.GONE
            binding.homeRecyclerView.visibility = View.VISIBLE
        }
    }

    // Create options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)

        // Set search function
        val searchItem = menu.findItem(R.id.searchMenu)
        val searchView = searchItem.actionView as SearchView

        // Get the current theme (dark or light)
        val isDarkTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        // Define colors for dark and light themes
        val textColor = if (isDarkTheme) Color.WHITE else Color.BLACK
        val iconColor = if (isDarkTheme) Color.WHITE else Color.BLACK

        // Change search text color
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(textColor)  // Adjust text color based on the theme
        searchEditText.setHintTextColor(if (isDarkTheme) Color.GRAY else Color.DKGRAY)  // Adjust hint text color

        // Change search icon color
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        searchIcon.setColorFilter(iconColor)

        // Change close icon color
        val closeIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeIcon.setColorFilter(iconColor)

        // Set listener for search query
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchNotes(newText)
                return true
            }
        })
    }


    // Search notes
    private fun searchNotes(query: String?) {
        val searchQuery = "%$query%" // Prepare the query
        noteViewModel.searchNotes(searchQuery).observe(viewLifecycleOwner) { notes ->
            if (notes != null) {
                noteAdapter.differ.submitList(notes)
                updateUI(notes) // Update UI (show/hide empty state)
            }
        }
    }


    // Clean view binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
