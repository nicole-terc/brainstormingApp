package com.wizeline.brainstormingapp

import android.app.Application
import com.wizeline.brainstormingapp.repository.RepositoryImpl

class App : Application() {

    val repository by lazy { RepositoryImpl(this) }

}
