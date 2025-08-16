package com.smartbusiness.expensetracker.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.smartbusiness.expensetracker.data.database.ExpenseEntity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fileNameDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    /**
     * Export expenses to CSV format
     */
    fun exportToCSV(context: Context, expenses: List<ExpenseEntity>): File? {
        return try {
            val fileName = "expenses_${fileNameDateFormat.format(Date())}.csv"
            val file = File(context.cacheDir, fileName)

            val csvContent = buildString {
                // Header
                appendLine("Date,Title,Category,Amount,Notes")

                // Data rows
                expenses.forEach { expense ->
                    val notes = expense.notes?.replace(",", ";") ?: ""
                    appendLine("${dateFormat.format(expense.date)},${expense.title},${expense.category},${expense.amount},$notes")
                }
            }

            file.writeText(csvContent)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Export expenses to PDF format
     */
    fun exportToPDF(context: Context, expenses: List<ExpenseEntity>): File? {
        return try {
            val fileName = "expenses_${fileNameDateFormat.format(Date())}.pdf"
            val file = File(context.cacheDir, fileName)

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = document.startPage(pageInfo)
            var canvas: Canvas = page.canvas

            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
                isAntiAlias = true
            }

            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 18f
                isFakeBoldText = true
                isAntiAlias = true
            }

            var yPosition = 50f

            // Title
            canvas.drawText("Expense Report", 50f, yPosition, titlePaint)
            yPosition += 30f

            canvas.drawText("Generated on: ${dateFormat.format(Date())}", 50f, yPosition, paint)
            yPosition += 40f

            // Headers
            val headerPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                isFakeBoldText = true
                isAntiAlias = true
            }

            canvas.drawText("Date", 50f, yPosition, headerPaint)
            canvas.drawText("Title", 130f, yPosition, headerPaint)
            canvas.drawText("Category", 250f, yPosition, headerPaint)
            canvas.drawText("Amount", 350f, yPosition, headerPaint)
            canvas.drawText("Notes", 430f, yPosition, headerPaint)
            yPosition += 20f

            // Data rows
            val dataPaint = Paint().apply {
                color = Color.BLACK
                textSize = 9f
                isAntiAlias = true
            }

            var totalAmount = 0.0
            expenses.forEach { expense ->
                if (yPosition > 800f) {
                    document.finishPage(page)
                    val newPage = document.startPage(pageInfo)
                    canvas = newPage.canvas
                    yPosition = 50f
                }

                canvas.drawText(dateFormat.format(expense.date), 50f, yPosition, dataPaint)
                canvas.drawText(expense.title.take(15), 130f, yPosition, dataPaint)
                canvas.drawText(ExpenseCategory.fromString(expense.category).displayName, 250f, yPosition, dataPaint)
                canvas.drawText("₹${String.format("%.2f", expense.amount)}", 350f, yPosition, dataPaint)
                canvas.drawText(expense.notes?.take(20) ?: "", 430f, yPosition, dataPaint)

                totalAmount += expense.amount
                yPosition += 15f
            }

            // Total
            yPosition += 20f
            canvas.drawText("Total: ₹${String.format("%.2f", totalAmount)}", 350f, yPosition, titlePaint)

            document.finishPage(page)

            val fileOutputStream = FileOutputStream(file)
            document.writeTo(fileOutputStream)
            document.close()
            fileOutputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Share file using Android Share Intent
     */
    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Expense report generated from Smart Expense Tracker")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share $title"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
