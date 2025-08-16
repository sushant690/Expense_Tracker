package com.smartbusiness.expensetracker.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.smartbusiness.expensetracker.data.database.CategoryExpense
import com.smartbusiness.expensetracker.utils.ExpenseCategory

@Composable
fun ExpensePieChart(
    categoryExpenses: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Category-wise Expenses (Last 7 Days)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (categoryExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No expenses to display",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                AndroidView(
                    factory = { context ->
                        PieChart(context).apply {
                            setUsePercentValues(true)
                            description.isEnabled = false
                            setExtraOffsets(5f, 10f, 5f, 5f)

                            dragDecelerationFrictionCoef = 0.95f

                            setDrawCenterText(true)
                            centerText = "Expenses\nBreakdown"
                            setCenterTextSize(12f)
                            setCenterTextColor(onSurfaceColor)

                            isDrawHoleEnabled = true
                            setHoleColor(surfaceColor)

                            setTransparentCircleColor(Color.WHITE)
                            setTransparentCircleAlpha(110)

                            holeRadius = 45f
                            transparentCircleRadius = 50f

                            setDrawCenterText(true)

                            rotationAngle = 0f
                            isRotationEnabled = true
                            isHighlightPerTapEnabled = true

                            // Legend
                            legend.isEnabled = true
                            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                            legend.orientation = Legend.LegendOrientation.HORIZONTAL
                            legend.setDrawInside(false)
                            legend.textColor = onSurfaceColor
                            legend.textSize = 10f

                            animateY(1400)
                        }
                    },
                    update = { chart ->
                        val entries = categoryExpenses.map { expense ->
                            PieEntry(expense.totalAmount.toFloat(), expense.category)
                        }

                        val colors = categoryExpenses.map { expense ->
                            ExpenseCategory.fromString(expense.category).color.toArgb()
                        }

                        val dataSet = PieDataSet(entries, "").apply {
                            this.colors = colors
                            sliceSpace = 3f
                            selectionShift = 5f
                            valueTextColor = Color.WHITE
                            valueTextSize = 12f
                        }

                        val data = PieData(dataSet).apply {
                            setValueFormatter(PercentFormatter(chart))
                            setValueTextSize(11f)
                            setValueTextColor(Color.WHITE)
                        }

                        chart.data = data
                        chart.invalidate()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun DailyExpenseChart(
    dailyExpenses: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Expenses (Last 7 Days)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (dailyExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No expenses to display",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Simple text-based chart for now
                // In a full implementation, you could use a bar chart here
                LazyColumn {
                    items(dailyExpenses.size) { index ->
                        val (day, amount) = dailyExpenses[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = day,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "₹${String.format("%.2f", amount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}