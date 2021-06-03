package com.azizbek.myyandexmapapp.database

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import com.azizbek.myyandexmapapp.model.Model
import java.util.ArrayList

class DBHelper(context: Context?) : SQLiteOpenHelper(context, "myLocation.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("create table locations (id integer primary key autoincrement, addressName text, locationName text)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS locations")
        onCreate(db)
    }

    fun insertData(model: Model):Long{
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("addressName",model.addressName)
        contentValues.put("locationName",model.locationName)
       return database.insert("locations", null, contentValues)
    }



    fun deleteLocation(id: Int):Int {
        val database = this.readableDatabase
        return database.delete("locations", "id=?", arrayOf(id.toString()))
    }

    val allLocations: ArrayList<Model>
        get() {
            val arrayList = ArrayList<Model>()
            val database = this.readableDatabase
            val cursor = database.rawQuery("select*from locations", null)

            if(cursor.moveToFirst()){
                do {
                    val id=cursor.getInt(0)
                    val addressName=cursor.getString(1)
                    val locationName=cursor.getString(2)
                    val models = Model(id, addressName, locationName)
                    arrayList.add(models)
                } while (cursor.moveToNext())
            }

            return arrayList
        }

}