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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.ViewModel.HistoryAdapter
import com.dicoding.asclepius.ViewModel.MainViewModel
import com.dicoding.asclepius.ViewModel.ViewModelFactory
import com.dicoding.asclepius.data.local.model.HistoryDB
import com.dicoding.asclepius.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this) as ViewModelProvider.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "History"

        setupRecyclerView()
        setupAdapter()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.rvHistory.addItemDecoration(itemDecoration)
    }

    private fun setupAdapter() {
        adapter = HistoryAdapter { historyId ->
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("historyId", historyId)
            }
            Log.d("HistoryActivity", "historyId: $historyId")
            startActivity(intent)
        }
        binding.rvHistory.adapter = adapter
    }


    private fun observeViewModel() {
        mainViewModel.history.observe(this) { listItem ->
            setHistory(listItem)
            mainViewModel.clearErrorMessage()
        }

        mainViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage,Toast.LENGTH_SHORT).show()
                mainViewModel.clearErrorMessage()
            }
        }
    }

    private fun setHistory(lifeHistory: List<HistoryDB?>) {
        adapter.submitList(lifeHistory)
    }
}