package com.example.aplikasistoryapp.ui.stories

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasistoryapp.adapter.ListStoryAdapter
import com.example.aplikasistoryapp.adapter.LoadingStateAdapter
import com.example.aplikasistoryapp.databinding.FragmentStoriesBinding
import com.example.aplikasistoryapp.ui.create.CreateActivity
import com.example.aplikasistoryapp.utils.VMFactory

class StoryFragment : Fragment() {

    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var listStoriesAdapter: ListStoryAdapter
    private lateinit var factory: VMFactory
    private val storyViewModel: StoryVM by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)

        setupViewModel()
        setupRecyclerView()
        observeStories()
        setupCreateStoryButton()

        return binding.root
    }

    private fun setupViewModel() {
        factory = VMFactory.getInstance(requireContext())
    }

    private fun setupRecyclerView() {
        val layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext())
        }
        binding.rvStory.layoutManager = layoutManager

        listStoriesAdapter = ListStoryAdapter()
        binding.rvStory.adapter = listStoriesAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { listStoriesAdapter.retry() }
        )
    }

    private fun observeStories() {
        storyViewModel.getListStory.observe(viewLifecycleOwner) { pagingData ->
            listStoriesAdapter.submitData(lifecycle, pagingData)
        }
    }

    private fun setupCreateStoryButton() {
        binding.rvButtonUpload.setOnClickListener {
            val intent = Intent(requireContext(), CreateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
