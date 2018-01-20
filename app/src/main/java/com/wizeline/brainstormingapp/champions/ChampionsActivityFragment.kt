package com.wizeline.brainstormingapp.champions

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wizeline.brainstormingapp.App
import com.wizeline.brainstormingapp.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_champions.*
import kotlinx.android.synthetic.main.fragment_champions.view.*

class ChampionsActivityFragment : Fragment() {

    private val roomId = "-L3Gvjp-JBCpmCZUKqs5" // TODO room id
    private val championsAdapter = ChampionsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_champions, container, false)
        view.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = championsAdapter
        }
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context.applicationContext as App).repository.getTopMessages(roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    championsAdapter.champions.clear()
                    championsAdapter.champions.addAll(it)
                    championsAdapter.notifyDataSetChanged()
                    load.visibility = View.GONE
                }, {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    it.printStackTrace()
                })
    }

}
