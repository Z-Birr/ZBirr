package com.matewos.z_birr.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Flowable
import java.sql.Date
import java.sql.Time
import java.util.*

@Entity(
    tableName = Transaction.TABLE_NAME,
    indices = [Index(value = [Transaction.TRANSACTION_ID, Transaction.FULL_NAME, Transaction.USER_ID], unique = true)])
data class Transaction (
    @PrimaryKey(autoGenerate = true) val transactionId: Int,
    @ColumnInfo(name= "sender") val sender : Boolean?,
    @ColumnInfo(name= "userId") val userId: String?,
    @ColumnInfo(name = "fullName") val fullName: String?,
    @ColumnInfo(name = "date") val date: Calendar?,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "balance") val balance: Double,
){
    companion object{
        const val FULL_NAME = "fullName"
        const val TABLE_NAME = "transaction"
        const val USER_ID = "userId"
        const val TRANSACTION_ID = "transactionId"
    }
}

@Dao
interface TransactionDao{
    @Query("SELECT * FROM 'transaction' ORDER BY date DESC")
    fun getAll() : List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE  userId LIKE :search OR fullName LIKE :search ORDER BY date DESC")
    fun search(search: String): List<Transaction> // Don't forget to put %uid%

    @Query("SELECT COUNT(transactionId) FROM `transaction`")
    fun count():Int

    @Query("INSERT INTO `transaction` (fullName, userId, sender, balance, date, amount) VALUES (:fullName, :userId, :sender, :balance, :date, :amount)")
    fun insert(fullName: String, userId: String, balance: Double ,amount: Double, sender: Boolean, date: Calendar)

    @Query("DELETE FROM `transaction`")
    fun deleteAll()

    @Query("SELECT * FROM 'transaction' ORDER BY date DESC LIMIT :count")
    fun getUpdates(count: Int): List<Transaction>
}