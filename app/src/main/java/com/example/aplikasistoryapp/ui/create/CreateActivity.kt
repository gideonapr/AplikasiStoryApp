package com.example.aplikasistoryapp.ui.create

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.aplikasistoryapp.R
import com.example.aplikasistoryapp.customview.CustomAlertDialog
import com.example.aplikasistoryapp.data.Result
import com.example.aplikasistoryapp.databinding.ActivityCreatestoryBinding
import com.example.aplikasistoryapp.ui.main.MainActivity
import com.example.aplikasistoryapp.utils.VMFactory
import com.example.aplikasistoryapp.utils.createCustomTempFile
import com.example.aplikasistoryapp.utils.reduceFileImage
import com.example.aplikasistoryapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatestoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var factory: VMFactory
    private val createStoryViewModel: CreateVM by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatestoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutCreate.edAddDescription.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.layoutCreate.edAddDescription.post {
                    binding.layoutCreate.root.scrollTo(0, binding.layoutCreate.edAddDescription.bottom)
                }
            }
        }

        setupViewModel()
        setupToolbar()
        checkAndRequestPermissions()
        buttonGalleryHandler()
        buttonCameraHandler()
        buttonSubmitStoryHandler()
    }

    private fun setupViewModel() {
        factory = VMFactory.getInstance(binding.root.context)
    }

    private fun setupToolbar() {
        title = resources.getString(R.string.create_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.entries.all { it.value }) {
                // All permissions are granted
            } else {
                // Some permissions are denied, show a message but allow access to CreateActivity
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!checkPermission(Manifest.permission.CAMERA)) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun buttonGalleryHandler() {
        binding.layoutCreate.buttonGalery.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)

            binding.layoutCreate.ivCreateStory.setImageBitmap(result)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun buttonCameraHandler() {
        binding.layoutCreate.buttonCamera.setOnClickListener {
            if (checkPermission(Manifest.permission.CAMERA)) {
                openCamera()
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(applicationContext).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@CreateActivity,
                "com.example.aplikasistoryapp.mycamera",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@CreateActivity)
            getFile = myFile
            binding.layoutCreate.ivCreateStory.setImageURI(selectedImg)
        }
    }

    private fun buttonSubmitStoryHandler() {
        binding.layoutCreate.butonAdd.setOnClickListener {
            val description = binding.layoutCreate.edAddDescription.text.toString()
            if (!isEmpty(description) && getFile != null) {
                createStory(description)
            } else {
                CustomAlertDialog(
                    this,
                    R.string.error_validation,
                    R.drawable.formerror
                ).show()
            }
        }
    }

    private fun convertImage(): MultipartBody.Part {
        val file = reduceFileImage(getFile as File)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
    }

    private fun convertDescription(description: String): RequestBody {
        return description.toRequestBody("text/plain".toMediaType())
    }

    private fun createStory(description: String) {
        val image = convertImage()
        val desc = convertDescription(description)
        createStoryViewModel.postCreateStory(
            image,
            desc,
            0.1,
            0.1
        ).observe(this@CreateActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        loadingHandler(true)
                    }
                    is Result.Error -> {
                        loadingHandler(false)
                        errorHandler()
                    }
                    is Result.Success -> {
                        successHandler()
                    }
                }
            }
        }
    }

    private fun loadingHandler(isLoading: Boolean) {
        if (isLoading) {
            binding.layoutLoading.root.visibility = View.VISIBLE
            binding.layoutCreate.root.visibility = View.GONE
        } else {
            binding.layoutLoading.root.visibility = View.GONE
            binding.layoutCreate.root.visibility = View.VISIBLE
        }
    }

    private fun errorHandler() {
        CustomAlertDialog(this, R.string.error_message, R.drawable.error).show()
    }

    private fun successHandler() {
        CustomAlertDialog(
            this,
            R.string.success_create_story,
            R.drawable.story_created,
            fun() {
                val moveActivity = Intent(this@CreateActivity, MainActivity::class.java)
                moveActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(moveActivity)
                finish()
            }
        ).show()
        binding.layoutCreate.ivCreateStory.setImageResource(R.drawable.imageadd)
        binding.layoutCreate.edAddDescription.text?.clear()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}