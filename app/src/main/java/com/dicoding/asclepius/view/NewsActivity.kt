package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.ViewModel.HistoryAdapter
import com.dicoding.asclepius.ViewModel.MainViewModel
import com.dicoding.asclepius.ViewModel.NewsAdapter
import com.dicoding.asclepius.ViewModel.ViewModelFactory
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var adapter: NewsAdapter
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this) as ViewModelProvider.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "News"

        setupRecyclerView()
        setupAdapter()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.rvNews.addItemDecoration(itemDecoration)
    }

    private fun setupAdapter() {
        adapter = NewsAdapter()
        binding.rvNews.adapter = adapter
    }


    private fun observeViewModel() {
        mainViewModel.cancerNews.observe(this) { listItem ->
            if (listItem != null) {
                setNews(listItem)
                mainViewModel.clearErrorMessage()
            }
        }

        mainViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage,Toast.LENGTH_SHORT).show()
                mainViewModel.clearErrorMessage()
            }
        }
    }

    private fun setNews(lifeNews: List<ArticlesItem>) {
        val filteredNews = lifeNews.filter {
            it.title != "[Removed]" && it.description != "[Removed]"
        }
        adapter.submitList(filteredNews)
    }

}