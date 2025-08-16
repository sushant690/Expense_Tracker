package com.smartbusiness.expensetracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalAmountByDateRange(startDate: Date, endDate: Date): Flow<Double?>


    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date")
    fun getTotalAmountToday(date: Date): Flow<Double?>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses WHERE date >= :startDate AND date <= :endDate GROUP BY category")
    fun getCategoryWiseExpenses(startDate: Date, endDate: Date): Flow<List<CategoryExpense>>

    @Query("SELECT COUNT(*) FROM expenses")
    fun getTotalExpenseCount(): Flow<Int>

    @Insert
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}

data class CategoryExpense(
    val category: String,
    val totalAmount: Double
)