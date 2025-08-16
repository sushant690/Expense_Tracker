package com.smartbusiness.expensetracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String? = null,
    val date: Date,
    val receiptImagePath: String? = null
)