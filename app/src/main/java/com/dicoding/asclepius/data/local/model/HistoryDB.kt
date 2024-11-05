package com.dicoding.asclepius.data.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = "history_database")
@Parcelize
data class HistoryDB (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "uri")
    var uri: String,
    @ColumnInfo(name = "category")
    var category: String,
    @ColumnInfo(name = "percentage")
    var percentage: String
) : Parcelable