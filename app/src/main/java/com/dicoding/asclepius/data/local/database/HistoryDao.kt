package com.dicoding.asclepius.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.model.HistoryDB

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistoryDB(historyDB: HistoryDB): Long

    @Delete
    suspend fun deleteHistoryDB(historyDB: HistoryDB)

    @Query("SELECT * from history_database")
    fun getAllHistory(): LiveData<List<HistoryDB>>

    @Query("SELECT * from history_database WHERE id= :id")
    fun getHistoryById(id: String?): LiveData<HistoryDB>
}