package com.wizeline.brainstormingapp.champions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wizeline.brainstormingapp.R
import kotlinx.android.synthetic.main.activity_champions.*

class ChampionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_champions)
        setSupportActionBar(toolbar)
    }

}
