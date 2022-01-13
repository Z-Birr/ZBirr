package com.matewos.z_birr.database

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
    companion object {
        var db : AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            if (db==null) {
                Log.i("database", "returned db")
                synchronized(AppDatabase::class.java) {
                    db = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "AppDatabase"
                    )
                        .allowMainThreadQueries().fallbackToDestructiveMigration().build()

                }
            }
            return db!!
        }
    }
}