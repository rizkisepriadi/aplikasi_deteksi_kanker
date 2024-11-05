package com.dicoding.asclepius.ViewModel

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.local.model.HistoryDB
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemNewsBinding

class NewsAdapter(private val onItemClick: ((Int) -> Unit)? = null) :
    ListAdapter<ArticlesItem, NewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {

    class NewsViewHolder(
        private val binding: ItemNewsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: ArticlesItem) {
            news.let { it ->
                Glide.with(binding.ivThumbnail.context)
                    .load(it.urlToImage)
                    .into(binding.ivThumbnail)
                binding.tvName.text = it.title
                binding.tvItem1.text = it.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
