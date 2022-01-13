package com.matewos.z_birr.ui.notifications

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.room.Query
import com.matewos.z_birr.database.AppDatabase
import com.matewos.z_birr.database.Transaction
import com.matewos.z_birr.database.TransactionDao
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {


    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    private val _adapter = MutableLiveData<ArrayList<Transaction>>().apply{
    }

}