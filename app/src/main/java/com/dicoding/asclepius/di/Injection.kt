package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.local.database.HistoryRoomDatabase
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.data.repository.CancerRepository

object Injection {
    fun provideRepository(context: Context): CancerRepository {
        val apiService = ApiConfig.getApiService()
        val database =  HistoryRoomDatabase.getDatabase(context)
        val dao = database.historyDao()
        return CancerRepository.getInstance(apiService, dao)
    }
}