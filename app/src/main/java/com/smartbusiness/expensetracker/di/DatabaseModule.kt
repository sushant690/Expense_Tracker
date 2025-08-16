package com.smartbusiness.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.smartbusiness.expensetracker.data.database.ExpenseDatabase
import com.smartbusiness.expensetracker.data.database.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideExpenseDatabase(@ApplicationContext context: Context): ExpenseDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ExpenseDatabase::class.java,
            "expense_database"
        ).build()
    }

    @Provides
    fun provideExpenseDao(database: ExpenseDatabase): ExpenseDao {
        return database.expenseDao()
    }
}