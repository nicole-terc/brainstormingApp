package com.wizeline.brainstormingapp.champions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wizeline.brainstormingapp.R

class ChampionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_champions)
        title = "Champions"

        var fragment = ChampionsActivityFragment.getInstance(intent.getStringExtra("roomid"))
        supportFragmentManager.beginTransaction().add(R.id.champions_container, fragment).commit()
    }

}
