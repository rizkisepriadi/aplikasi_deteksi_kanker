package com.dicoding.asclepius.ViewModel

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.model.HistoryDB
import com.dicoding.asclepius.databinding.ItemNewsBinding

class HistoryAdapter(private val onItemClick: ((String) -> Unit)? = null) :
    ListAdapter<HistoryDB, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    class HistoryViewHolder(
        private val binding: ItemNewsBinding,
        private val onItemClick: ((String) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryDB) {
            Log.d("HistoryAdapter", "Binding history: ${history.id}, uri: ${history.uri}, category: ${history.category}, percentage: ${history.percentage}")
            binding.ivThumbnail.setImageURI(Uri.parse(history.uri))
            binding.tvItem1.text = history.category
            binding.tvItem2.text = history.percentage

            binding.root.setOnClickListener {
                onItemClick?.invoke(history.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryDB>() {
            override fun areItemsTheSame(
                oldItem: HistoryDB,
                newItem: HistoryDB
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: HistoryDB,
                newItem: HistoryDB
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
