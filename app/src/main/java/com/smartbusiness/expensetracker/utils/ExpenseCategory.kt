package com.smartbusiness.expensetracker.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    STAFF("Staff", Icons.Default.Person, Color(0xFF4CAF50)),
    TRAVEL("Travel", Icons.Default.DirectionsCar, Color(0xFF2196F3)),
    FOOD("Food", Icons.Default.Restaurant, Color(0xFFFF9800)),
    UTILITY("Utility", Icons.Default.ElectricBolt, Color(0xFF9C27B0));

    companion object {
        fun fromString(value: String): ExpenseCategory {
            return values().find { it.name == value } ?: STAFF
        }

        fun getAllCategories(): List<ExpenseCategory> = values().toList()
    }
}