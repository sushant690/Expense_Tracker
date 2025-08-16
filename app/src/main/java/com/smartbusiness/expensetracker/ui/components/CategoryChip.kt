package com.smartbusiness.expensetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.smartbusiness.expensetracker.utils.ExpenseCategory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border


@Composable
fun CategoryChip(
    category: ExpenseCategory,
    isSelected: Boolean,
    onCategorySelected: (ExpenseCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .selectable(
                selected = isSelected,
                onClick = { onCategorySelected(category) }
            ),
        color = if (isSelected) category.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) category.color else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.displayName,
                tint = if (isSelected) category.color else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = category.displayName,
                color = if (isSelected) category.color else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

}

@Composable
fun CategorySelector(
    selectedCategory: ExpenseCategory?,
    onCategorySelected: (ExpenseCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExpenseCategory.getAllCategories().forEach { category ->
                CategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}