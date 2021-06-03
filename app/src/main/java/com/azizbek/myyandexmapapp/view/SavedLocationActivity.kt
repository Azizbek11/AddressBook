package com.azizbek.myyandexmapapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azizbek.myyandexmapapp.database.DBHelper
import com.azizbek.myyandexmapapp.model.Model
import com.azizbek.myyandexmapapp.adapter.ModelAdapter
import com.azizbek.myyandexmapapp.R

class SavedLocationActivity : AppCompatActivity(){
    private var recyclerView: RecyclerView? = null
    private var modelAdapter: ModelAdapter? = null
    private var models: ArrayList<Model>? = null
    private var dbHelper: DBHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_location)
        recyclerView = findViewById(R.id.myRecyclerView)
        dbHelper = DBHelper(this)
        supportActionBar!!.hide()
    }

    override fun onStart() {
        super.onStart()
        models=dbHelper!!.allLocations
        modelAdapter= ModelAdapter(models!!,this)
        recyclerView!!.adapter=modelAdapter
        val manager = LinearLayoutManager(this)
        manager.orientation + RecyclerView.VERTICAL
        recyclerView!!.layoutManager=manager

    }
}