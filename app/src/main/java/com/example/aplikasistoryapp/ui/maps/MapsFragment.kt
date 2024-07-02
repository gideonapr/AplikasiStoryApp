package com.example.aplikasistoryapp.ui.maps

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.aplikasistoryapp.R
import com.example.aplikasistoryapp.constants.Constants
import com.example.aplikasistoryapp.customview.CustomAlertDialog
import com.example.aplikasistoryapp.data.Result
import com.example.aplikasistoryapp.databinding.FragmentMapsBinding
import com.example.aplikasistoryapp.model.Story
import com.example.aplikasistoryapp.utils.VMFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: VMFactory
    private val mapsViewModel: MapsVM by viewModels { viewModelFactory }
    private var googleMap: GoogleMap? = null
    private val markers = mutableListOf<Marker>()

    private val onMapReadyCallback = OnMapReadyCallback { map ->
        googleMap = map
        configureMapUI(map)
        initializeViewModel()
        displayDummyMarker(map)
        applyMapStyle(map)
        loadStoriesWithLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(onMapReadyCallback)
    }

    private fun initializeViewModel() {
        viewModelFactory = VMFactory.getInstance(requireContext())
    }

    private fun configureMapUI(map: GoogleMap) {
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
    }

    private fun displayDummyMarker(map: GoogleMap) {
        val dummyLocation = LatLng(Constants.LATITUDE, Constants.LONGITUDE)
        map.addMarker(
            MarkerOptions()
                .position(dummyLocation)
                .title("Marker in chosen location")
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(dummyLocation, 15f))
    }

    private fun loadStoriesWithLocation() {
        mapsViewModel.getStories().observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Loading -> toggleLoadingIndicator(true)
                is Result.Error -> {
                    toggleLoadingIndicator(false)
                    showErrorDialog()
                }
                is Result.Success -> {
                    toggleLoadingIndicator(false)
                    updateMarkers(result.data.listStory)
                }
            }
        }
    }

    private fun updateMarkers(storyList: List<Story>) {
        googleMap?.let { map ->
            // Clear existing markers
            markers.forEach { it.remove() }
            markers.clear()

            val newBoundsBuilder = LatLngBounds.Builder()

            storyList.forEach { story ->
                val storyLocation = LatLng(story.lat, story.lon)
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(storyLocation)
                        .title(story.createdAt)
                        .snippet("created: ${story.createdAt.substring(11, 16)}")
                )
                marker?.let { markers.add(it) }
                newBoundsBuilder.include(storyLocation)
            }

            // Move camera to fit all markers
            val bounds = newBoundsBuilder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    private fun applyMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) {
                showErrorDialog()
            }
        } catch (exception: Resources.NotFoundException) {
            showErrorDialog()
        }
    }

    private fun showErrorDialog() {
        CustomAlertDialog(requireContext(), R.string.error_message, R.drawable.error).show()
    }

    private fun toggleLoadingIndicator(isLoading: Boolean) {
        binding.progressBarMap.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        binding.root.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
