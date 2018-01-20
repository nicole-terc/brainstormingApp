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
            var roomId = ""
            repository.createRoom("A name")
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { Log.d("Wizeline", "createRoom $it") }
                    .flatMap {
                        roomId = it.id
                        repository.getRoom(roomId).toObservable()
                    }
                    .flatMap { repository.createMessage(it.id, listOf("Hello", "World")).toObservable() }
                    .doOnNext { Log.d("Wizeline", "createMessage $it") }
                    .flatMap { repository.getOtherMessages(it[0].idRoom).toObservable() }
                    .doOnNext { Log.d("Wizeline", "getOtherMessages $it") }
                    .flatMap { repository.vote(listOf(UserVote(it[0].id, if (Math.random() > 0.5) 1 else -1))).toObservable() }
                    .doOnNext { Log.d("Wizeline", "vote $it") }
                    .flatMap { repository.getTopMessages(roomId).toObservable() }
                    .doOnNext { Log.d("Wizeline", "getTopMessages $it") }
                    .flatMap { repository.getRooms() }
                    .subscribe({
                        Log.d("Wizeline", "Success %s".format(it))
                    }, {
                        Log.d("Wizeline", "Error %s".format(it))
                    })
        }
    }

}
