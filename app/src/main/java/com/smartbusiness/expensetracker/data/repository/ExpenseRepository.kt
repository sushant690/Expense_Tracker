package com.smartbusiness.expensetracker.data.repository

import com.smartbusiness.expensetracker.data.database.ExpenseDao
import com.smartbusiness.expensetracker.data.database.ExpenseEntity
import com.smartbusiness.expensetracker.data.database.CategoryExpense
import com.smartbusiness.expensetracker.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {

    fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesByDateRange(startDate, endDate)

    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesByCategory(category)

    fun getTotalAmountByDateRange(startDate: Date, endDate: Date): Flow<Double?> =
        expenseDao.getTotalAmountByDateRange(startDate, endDate)



   // fun getTotalAmountToday(date: Date): Flow<Double?> =
    //    expenseDao.getTotalAmountToday(date)

    fun getCategoryWiseExpenses(startDate: Date, endDate: Date): Flow<List<CategoryExpense>> =
        expenseDao.getCategoryWiseExpenses(startDate, endDate)

    fun getTotalExpenseCount(): Flow<Int> = expenseDao.getTotalExpenseCount()

    suspend fun insertExpense(expense: ExpenseEntity) = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)

    suspend fun deleteAllExpenses() = expenseDao.deleteAllExpenses()

    suspend fun getTotalAmountToday(todayStart: Date): Flow<Double?> {
        val todayEnd = DateUtils.getTodayEnd()
        return expenseDao.getTotalAmountByDateRange(todayStart, todayEnd)
    }
}