package com.example.codeforcealarmer

import android.app.Application
import android.content.Context

class AppContainer(context: Context) {
    val networkChecker = NetworkChecker(context)
}