package com.matewos.z_birr.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
}