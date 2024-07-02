package com.example.aplikasistoryapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplikasistoryapp.utils.withDateFormat
import com.example.aplikasistoryapp.constants.Constants
import com.example.aplikasistoryapp.databinding.StoryLayoutBinding
import com.example.aplikasistoryapp.model.Story
import com.example.aplikasistoryapp.ui.detail.DetailActivity

class ListStoryAdapter:
    PagingDataAdapter<Story, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ListViewHolder {
            return ListViewHolder(
                StoryLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            val data = getItem(position)
            if (data != null) {
                holder.bind(story = data)
            }
        }

        class ListViewHolder(private val binding: StoryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(story: Story) {
                with(binding) {
                    tvItemName.text = story.name
                    tvItemDate.text = story.createdAt.withDateFormat()
                    tvItemDescription.text = story.description
                    Glide.with(itemView.context)
                        .load(story.photoUrl)
                        .fitCenter()
                        .into(ivItemPhoto)

                    storyCardView.setOnClickListener {
                        val intent = Intent(itemView.context, DetailActivity::class.java)
                        intent.putExtra(Constants.DETAIL_STORY, story)
                        itemView.context.startActivity(
                            intent,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(binding.root.context as Activity).toBundle()
                        )
                    }
                }
            }
        }

        companion object {
            val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                    return oldItem.id == newItem.id
                }
            }
        }
    }