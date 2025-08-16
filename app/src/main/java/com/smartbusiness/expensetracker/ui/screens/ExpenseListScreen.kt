package com.smartbusiness.expensetracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smartbusiness.expensetracker.ui.components.CategoryChip
import com.smartbusiness.expensetracker.ui.components.ExpenseCard
import com.smartbusiness.expensetracker.utils.ExpenseCategory
import com.smartbusiness.expensetracker.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    navController: NavController,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()

    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilterCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var groupByCategory by remember { mutableStateOf(false) }

    // Filter expenses based on selected category
    val filteredExpenses = remember(allExpenses, selectedFilterCategory) {
        if (selectedFilterCategory == null) {
            allExpenses
        } else {
            allExpenses.filter { it.category == selectedFilterCategory!!.name }
        }
    }

    // Group expenses by category if needed
    val displayExpenses = remember(filteredExpenses, groupByCategory) {
        if (groupByCategory) {
            filteredExpenses.sortedBy { it.category }
        } else {
            filteredExpenses.sortedByDescending { it.date }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "All Expenses",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Expenses",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = displayExpenses.size.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "₹${String.format("%.2f", displayExpenses.sumOf { it.amount })}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    selectedFilterCategory?.let { category ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filtered by: ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            // Use Card instead of CategoryChip for consistency
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = category.displayName,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp) // Smaller for compact display
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = category.displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Clear button as Card for consistency
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier
                                    .height(28.dp)
                                    .clickable { selectedFilterCategory = null }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Clear",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                }
            }

            // Toggle for grouping
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Group by Category",
                    style = MaterialTheme.typography.bodyMedium
                )

                Switch(
                    checked = groupByCategory,
                    onCheckedChange = { groupByCategory = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expense list
            if (displayExpenses.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (selectedFilterCategory == null)
                                "No expenses found"
                            else
                                "No expenses in ${selectedFilterCategory?.displayName} category",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedFilterCategory == null)
                                "Start adding expenses to see them here"
                            else
                                "Try a different category or clear the filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (groupByCategory) {
                        // Group by category
                        val groupedExpenses = displayExpenses.groupBy { it.category }
                        groupedExpenses.forEach { (category, expenses) ->
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                ) {
                                    Text(
                                        text = "${ExpenseCategory.fromString(category).displayName} (${expenses.size} expenses)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }

                            items(
                                items = expenses,
                                key = { "${category}_${it.id}" }
                            ) { expense ->
                                ExpenseCard(
                                    expense = expense,
                                    onEdit = { /* TODO: Navigate to edit */ },
                                    onDelete = { viewModel.deleteExpense(it) }
                                )
                            }
                        }
                    } else {
                        items(
                            items = displayExpenses,
                            key = { it.id }
                        ) { expense ->
                            ExpenseCard(
                                expense = expense,
                                onEdit = { /* TODO: Navigate to edit */ },
                                onDelete = { viewModel.deleteExpense(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Filter dialog with consistent Card theming and same icon sizes
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter by Category") },
            text = {
                LazyColumn {
                    // All Categories option
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedFilterCategory = null
                                    showFilterDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedFilterCategory == null)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Changed: Use Icon with same size as other categories
                                Icon(
                                    imageVector = Icons.Default.List, // Use a proper Material icon
                                    contentDescription = "All Categories",
                                    tint = if (selectedFilterCategory == null)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp) // Same size as category icons
                                )
                                Spacer(modifier = Modifier.width(12.dp)) // Use Spacer instead of padding
                                Text(
                                    text = "All Categories",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedFilterCategory == null)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Individual category options
                    items(ExpenseCategory.getAllCategories()) { category ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedFilterCategory = category
                                    showFilterDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedFilterCategory == category)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.displayName,
                                    tint = if (selectedFilterCategory == category)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        category.color,
                                    modifier = Modifier.size(20.dp) // Same size for all icons
                                )
                                Spacer(modifier = Modifier.width(12.dp)) // Consistent spacing
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedFilterCategory == category)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Done")
                }
            }
        )
    }


    // Handle errors
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }
}
