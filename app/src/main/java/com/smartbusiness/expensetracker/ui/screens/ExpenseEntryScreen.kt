package com.smartbusiness.expensetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smartbusiness.expensetracker.ui.components.CategoryChip
import com.smartbusiness.expensetracker.utils.ExpenseCategory
import com.smartbusiness.expensetracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    navController: NavController,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var notes by remember { mutableStateOf("") }

    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showValidationErrors by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Navigate back when expense is added successfully
    LaunchedEffect(uiState.isExpenseAdded) {
        if (uiState.isExpenseAdded) {
            viewModel.resetExpenseAddedState()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showValidationErrors && title.isBlank()
            )

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
                        amount = newValue
                    }
                },
                label = { Text("Amount (₹)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("₹", style = MaterialTheme.typography.titleMedium) },
                isError = showValidationErrors && (amount.isBlank() || amount.toDoubleOrNull()?.let { it <= 0 } == true)
            )

            // Improved Category selection with better outlines
            Column {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (showValidationErrors && selectedCategory == null)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Responsive grid layout for categories with clean borders
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = ExpenseCategory.getAllCategories()
                    val chunkedCategories = categories.chunked(2) // 2 items per row

                    chunkedCategories.forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCategories.forEach { category ->
                                // Clean category card with proper borders
                                Card(
                                    onClick = { selectedCategory = category },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedCategory == category)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        width = 1.5.dp,
                                        color = if (selectedCategory == category)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.outline
                                    ),
                                    shape = RoundedCornerShape(12.dp) // Consistent rounded corners
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = category.icon,
                                            contentDescription = category.displayName,
                                            tint = if (selectedCategory == category)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                category.color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = category.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (selectedCategory == category)
                                                FontWeight.SemiBold
                                            else
                                                FontWeight.Normal,
                                            color = if (selectedCategory == category)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            // Fill remaining space if odd number of categories
                            if (rowCategories.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Error message for category validation
                if (showValidationErrors && selectedCategory == null) {
                    Text(
                        text = "Please select a category",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    )
                }
            }


            // Date selection
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Date", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            }

            // Date Picker Dialog
            if (showDatePicker) {
                AlertDialog(
                    onDismissRequest = { showDatePicker = false },
                    title = { Text("Select Date") },
                    text = {
                        ExpenseDatePicker(
                            selectedDate = selectedDate.time,
                            onDateSelected = { millis ->
                                selectedDate = Date(millis)
                                // Don't auto-close here, let user choose
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { if (it.length <= 100) notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                supportingText = { Text("${notes.length}/100") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add expense button
            Button(
                onClick = {
                    val validation = viewModel.validateExpenseInput(title, amount, selectedCategory)
                    showValidationErrors = !validation.isValid
                    if (validation.isValid) {
                        viewModel.addExpense(
                            title = title.trim(),
                            amount = amount.toDouble(),
                            category = selectedCategory!!,
                            notes = if (notes.isBlank()) null else notes.trim(),
                            date = selectedDate
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Add Expense", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePicker(
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    DatePicker(
        state = datePickerState,
        modifier = Modifier.padding(16.dp)
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            onDateSelected(millis)
        }
    }
}
