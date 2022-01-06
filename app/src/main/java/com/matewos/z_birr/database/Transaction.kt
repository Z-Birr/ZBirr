package com.matewos.z_birr.database

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
    @ColumnInfo(name = "amount") val amount: Double
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
    @Query("SELECT * FROM 'transaction'")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE  userId LIKE :search OR sender LIKE :search")
    fun search(search: String): Flowable<List<Transaction>> // Don't forget to put %uid%

    @Query("SELECT COUNT(transactionId) FROM `transaction`")
    fun count():Int

    @Query("INSERT INTO `transaction` (fullName, userId, sender, date, amount) VALUES (:fullName, :userId, :sender, :date, :amount)")
    fun insert(fullName: String, userId: String, amount: Double, sender: Boolean, date: Calendar)

    @Query("DELETE FROM `transaction`")
    fun deleteAll()
}