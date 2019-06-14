package com.epam.training.simplenotes

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.epam.training.simplenotes.koin.firebaseModule
import com.epam.training.simplenotes.koin.loginModule
import com.epam.training.simplenotes.koin.mainModule
import com.epam.training.simplenotes.koin.mapperModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SimpleNotesApplication : Application() {
    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)

        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SimpleNotesApplication)
            modules(listOf(loginModule, mainModule, firebaseModule, mapperModule))
        }

        FirebaseApp.initializeApp(this)
    }
}