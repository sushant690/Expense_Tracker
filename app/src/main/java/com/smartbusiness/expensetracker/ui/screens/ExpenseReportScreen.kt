package com.smartbusiness.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smartbusiness.expensetracker.ui.components.ExpensePieChart
import com.smartbusiness.expensetracker.ui.components.DailyExpenseChart
import com.smartbusiness.expensetracker.utils.DateUtils
import com.smartbusiness.expensetracker.utils.ExpenseCategory
import com.smartbusiness.expensetracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    navController: NavController,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categoryExpenses by viewModel.categoryExpenses.collectAsStateWithLifecycle()

    // Get ALL expenses for export (not just 7 days)
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()

    // Export menu state
    var showExportMenu by remember { mutableStateOf(false) }

    // Get expenses for the last 7 days (for display only)
    val sevenDaysExpenses by viewModel.getExpensesByDateRange(
        DateUtils.getSevenDaysAgo(),
        DateUtils.getCurrentDate()
    ).collectAsStateWithLifecycle(initialValue = emptyList())

    // Debug: Check if we have data
    LaunchedEffect(allExpenses, sevenDaysExpenses) {
        println("DEBUG: All expenses count: ${allExpenses.size}")
        println("DEBUG: Seven days expenses count: ${sevenDaysExpenses.size}")
        allExpenses.take(3).forEach { expense ->
            println("DEBUG: Expense - ${expense.title}, ₹${expense.amount}, ${expense.date}")
        }
    }

    // Calculate daily totals
    val dailyTotals = remember(sevenDaysExpenses) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        (0..6).map { dayOffset ->
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)
            val day = calendar.time

            val dayStart = Calendar.getInstance().apply {
                time = day
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val dayEnd = Calendar.getInstance().apply {
                time = day
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time

            val dayExpenses = sevenDaysExpenses.filter { expense ->
                expense.date.after(dayStart) && expense.date.before(dayEnd) ||
                        expense.date == dayStart || expense.date == dayEnd
            }

            dateFormat.format(day) to dayExpenses.sumOf { it.amount }
        }.reversed()
    }

    // Calculate totals
    val totalAmount = remember(sevenDaysExpenses) {
        sevenDaysExpenses.sumOf { it.amount }
    }

    val averageDaily = remember(totalAmount) {
        totalAmount / 7.0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Expense Report",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Export dropdown menu
                    IconButton(onClick = { showExportMenu = true }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
                    }

                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export CSV") },
                            leadingIcon = {
                                Icon(Icons.Default.Description, contentDescription = null)
                            },
                            onClick = {
                                showExportMenu = false
                                // Export ALL expenses, not just 7 days
                                viewModel.exportToCSV(context, allExpenses)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export PDF") },
                            leadingIcon = {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                            },
                            onClick = {
                                showExportMenu = false
                                // Export ALL expenses, not just 7 days
                                viewModel.exportToPDF(context, allExpenses)
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Report header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Last 7 Days Report",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Total Spent",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "₹${String.format("%.2f", totalAmount)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Daily Average",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "₹${String.format("%.2f", averageDaily)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Transactions",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = sevenDaysExpenses.size.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Category-wise pie chart
                ExpensePieChart(
                    categoryExpenses = categoryExpenses,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Daily expenses chart
                DailyExpenseChart(
                    dailyExpenses = dailyTotals,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Category breakdown
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Category Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (categoryExpenses.isEmpty()) {
                            Text(
                                text = "No expenses to display",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            categoryExpenses.forEach { categoryExpense ->
                                val category = ExpenseCategory.fromString(categoryExpense.category)
                                val percentage = if (totalAmount > 0) {
                                    (categoryExpense.totalAmount / totalAmount) * 100
                                } else 0.0

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = category.icon,
                                            contentDescription = category.displayName,
                                            tint = category.color,
                                            modifier = Modifier.size(20.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = category.displayName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${String.format("%.1f", percentage)}%",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Text(
                                        text = "₹${String.format("%.2f", categoryExpense.totalAmount)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                if (categoryExpense != categoryExpenses.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Export options
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Export Options",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    // Export ALL expenses, not just 7 days
                                    viewModel.exportToPDF(context, allExpenses)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Export PDF")
                            }

                            OutlinedButton(
                                onClick = {
                                    // Export ALL expenses, not just 7 days
                                    viewModel.exportToCSV(context, allExpenses)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Export CSV")
                            }
                        }

                    }
                }
            }
        }
    }

    // Handle errors
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }
}
