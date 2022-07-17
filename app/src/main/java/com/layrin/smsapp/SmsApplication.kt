package com.layrin.smsapp

import android.app.Application
import androidx.room.Room
import com.layrin.smsapp.model.SmsDb

class SmsApplication : Application() {

    companion object {
        lateinit var database: SmsDb
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            SmsDb::class.java,
            "sms"
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

}