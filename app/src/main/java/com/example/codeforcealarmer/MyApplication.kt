package com.example.codeforcealarmer

import android.app.Application

class MyApplication : Application() {
    val appContainer = AppContainer(this)
}