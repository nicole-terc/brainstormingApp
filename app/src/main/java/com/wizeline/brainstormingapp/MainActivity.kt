package com.wizeline.brainstormingapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val repository = RepositoryImpl(applicationContext as App)
//        text.setOnClickListener {
//            Log.d("Wizeline", "clicked")
//            repository.createRoom("A name")
//            repository.getRoom("-L3Gvjp-JBCpmCZUKqs5")
//            repository.getRooms()
//            repository.createMessage("-L3Gvjp-JBCpmCZUKqs5", listOf("Hello", "World"))
//            repository.getOtherMessages("-L3Gvjp-JBCpmCZUKqs5")
//            repository.vote(listOf(UserVote("-L3HNPz71CnXX8hwDCSX", if (Math.random() > 0.5) 1 else -1), UserVote("-L3HNPz71CnXX8hwDCSX", if (Math.random() > 0.5) 1 else -1)))
//            repository.getTopMessages("-L3Gvjp-JBCpmCZUKqs5")
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        Log.d("Wizeline", "Success %s".format(it))
//                    }, {
//                        Log.d("Wizeline", "Error %s".format(it))
//                    })
//        }
    }

}
