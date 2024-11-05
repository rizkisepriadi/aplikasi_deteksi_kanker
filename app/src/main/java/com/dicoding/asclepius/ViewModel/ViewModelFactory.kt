package com.dicoding.asclepius.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.repository.CancerRepository
import com.dicoding.asclepius.di.Injection

class ViewModelFactory(
    private val cancerRepository: CancerRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(cancerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): Any =
            instance ?: synchronized(this) {
                val repository = Injection.provideRepository(context)
                instance ?: ViewModelFactory(repository)
            }.also { instance = it }
    }
}