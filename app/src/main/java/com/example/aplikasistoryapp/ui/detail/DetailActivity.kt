package com.example.aplikasistoryapp.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.aplikasistoryapp.R
import com.example.aplikasistoryapp.constants.Constants
import com.example.aplikasistoryapp.databinding.ActivityDetailBinding
import com.example.aplikasistoryapp.model.Story
import com.example.aplikasistoryapp.utils.withDateFormat

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val storyDetail = intent.getParcelableExtra(Constants.DETAIL_STORY) as? Story
        storyDetail?.let {
            initializeToolbar()
            displayStoryDetails(it)
        }
    }

    private fun initializeToolbar() {
        title = getString(R.string.detail_story)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
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

    private fun displayStoryDetails(story: Story) {
        with(viewBinding) {
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .fitCenter()
                .into(ivDetailPhoto)

            tvName.text = story.name
            tvDescription.text = story.description
            tvDate.text = story.createdAt.withDateFormat()
        }
    }
}
