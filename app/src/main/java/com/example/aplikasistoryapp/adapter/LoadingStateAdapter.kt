package com.example.aplikasistoryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasistoryapp.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retryCallback: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.CustomLoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): CustomLoadStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomLoadStateViewHolder(binding, retryCallback)
    }

    override fun onBindViewHolder(holder: CustomLoadStateViewHolder, loadState: LoadState) {
        holder.bindState(loadState)
    }

    class CustomLoadStateViewHolder(private val binding: ItemLoadingBinding, retryCallback: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retryCallback() }
        }

        fun bindState(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error

            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
        }
    }
}
