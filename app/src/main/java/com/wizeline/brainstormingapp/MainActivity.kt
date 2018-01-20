package com.wizeline.brainstormingapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.wizeline.brainstormingapp.repository.RepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = RepositoryImpl(applicationContext as App)
        text.setOnClickListener {
            Log.d("Wizeline", "clicked")
//            repository.createRoom("A name")
//            repository.getRoom("-L3Gvjp-JBCpmCZUKqs5")
//            repository.getRooms()
//            repository.createMessage("-L3Gvjp-JBCpmCZUKqs5", listOf("hello", "world"))
            repository.getOtherMessages("-L3Gvjp-JBCpmCZUKqs5")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.d("Wizeline", "Success $it")
                    }, {
                        Log.d("Wizeline", "Error $it")
                    })
        }
    }

}
