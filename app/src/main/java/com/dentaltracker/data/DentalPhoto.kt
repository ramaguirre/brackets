package com.dentaltracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "dental_photos")
data class DentalPhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val timestamp: Date,
    val alignmentScore: Float = 0f, // How well aligned the photo is (0-1)
    val notes: String = ""
)