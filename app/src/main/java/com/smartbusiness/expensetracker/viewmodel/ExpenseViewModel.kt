package com.smartbusiness.expensetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbusiness.expensetracker.data.database.ExpenseEntity
import com.smartbusiness.expensetracker.data.database.CategoryExpense
import com.smartbusiness.expensetracker.data.repository.ExpenseRepository
import com.smartbusiness.expensetracker.utils.DateUtils
import com.smartbusiness.expensetracker.utils.ExpenseCategory
import com.smartbusiness.expensetracker.utils.ExportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    // UI State for different screens
    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    // All expenses
    val allExpenses = repository.getAllExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Today's total
    val todayTotalCalculated = allExpenses.map { expenses ->
        val todayStart = DateUtils.getTodayStart()
        val todayEnd = DateUtils.getTodayEnd()

        expenses.filter { expense ->
            expense.date.after(todayStart) && expense.date.before(todayEnd) ||
                    expense.date == todayStart || expense.date == todayEnd
        }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Category-wise expenses for charts (last 7 days)
    val categoryExpenses = repository.getCategoryWiseExpenses(
        DateUtils.getSevenDaysAgo(),
        DateUtils.getCurrentDate()
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Total expense count
    val totalExpenseCount = repository.getTotalExpenseCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        // Load initial data
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Load data is handled by StateFlow automatically
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Unknown error occurred"
                    ) 
                }
            }
        }
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: ExpenseCategory,
        notes: String? = null,
        date: Date = DateUtils.getCurrentDate(),
        receiptImagePath: String? = null
    ) {
        viewModelScope.launch {
            try {
                val expense = ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category.name,
                    notes = notes,
                    date = date,
                    receiptImagePath = receiptImagePath
                )

                repository.insertExpense(expense)
                _uiState.update { it.copy(isExpenseAdded = true, error = null) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to add expense"
                    ) 
                }
            }
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                repository.updateExpense(expense)
                _uiState.update { it.copy(error = null) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to update expense"
                    ) 
                }
            }
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
                _uiState.update { it.copy(error = null) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to delete expense"
                    ) 
                }
            }
        }
    }

    fun filterExpensesByCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetExpenseAddedState() {
        _uiState.update { it.copy(isExpenseAdded = false) }
    }

    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<ExpenseEntity>> {
        return repository.getExpensesByDateRange(startDate, endDate)
    }

    // Validation functions
    fun validateExpenseInput(
        title: String,
        amount: String,
        category: ExpenseCategory?
    ): ExpenseValidationResult {
        val errors = mutableListOf<String>()

        if (title.isBlank()) {
            errors.add("Title cannot be empty")
        }

        if (amount.isBlank()) {
            errors.add("Amount cannot be empty")
        } else {
            try {
                val amountDouble = amount.toDouble()
                if (amountDouble <= 0) {
                    errors.add("Amount must be greater than 0")
                }
            } catch (e: NumberFormatException) {
                errors.add("Invalid amount format")
            }
        }

        if (category == null) {
            errors.add("Please select a category")
        }

        return ExpenseValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    // In ExpenseViewModel - Update these methods
    fun exportToCSV(context: Context, expenses: List<ExpenseEntity> = allExpenses.value) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val file = ExportUtils.exportToCSV(context, expenses)

                if (file != null) {
                    ExportUtils.shareFile(
                        context = context,
                        file = file,
                        mimeType = "text/csv",
                        title = "Expense Report (CSV)"
                    )
                } else {
                    _uiState.update { it.copy(error = "Failed to export CSV", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Export failed", isLoading = false) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun exportToPDF(context: Context, expenses: List<ExpenseEntity> = allExpenses.value) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val file = ExportUtils.exportToPDF(context, expenses)

                if (file != null) {
                    ExportUtils.shareFile(
                        context = context,
                        file = file,
                        mimeType = "application/pdf",
                        title = "Expense Report (PDF)"
                    )
                } else {
                    _uiState.update { it.copy(error = "Failed to export PDF", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Export failed", isLoading = false) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


}

data class ExpenseUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isExpenseAdded: Boolean = false,
    val selectedCategory: String? = null
)

data class ExpenseValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)