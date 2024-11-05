package com.dicoding.asclepius.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.model.HistoryDB

@Database(entities = [HistoryDB::class], version = 1, exportSchema = false)
abstract class HistoryRoomDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HistoryRoomDatabase {
            if (INSTANCE == null) {
                synchronized(HistoryRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryRoomDatabase::class.java, "history_database"
                    ).build()
                }
            }
            return INSTANCE as HistoryRoomDatabase
        }
    }
}