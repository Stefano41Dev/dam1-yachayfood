package com.example.yachayfood.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class NutrimentsEntity(
    val energy_kcal_100g: Double?,
    val energy_100g: Double?,
    val fat_100g: Double?,
    val saturated_fat_100g: Double?,
    val sugars_100g: Double?,
    val proteins_100g: Double?,
    val carbohydrates_100g: Double?,
    val fiber_100g: Double?
) : Parcelable
