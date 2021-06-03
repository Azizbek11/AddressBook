package com.azizbek.myyandexmapapp.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.azizbek.myyandexmapapp.R
import com.azizbek.myyandexmapapp.database.DBHelper
import com.azizbek.myyandexmapapp.model.Model

class ModelAdapter(private var models: ArrayList<Model>, private var context: Activity):RecyclerView.Adapter<ModelAdapter.ModelsVH>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelsVH {
        val v = LayoutInflater.from(context).inflate(R.layout.row_location, parent, false)
        return ModelsVH(v)
    }


    override fun onBindViewHolder(holder: ModelsVH, position: Int) {
        val m = models[position]
        holder.addressName.text=m.addressName
        holder.locationName.text=m.locationName

        holder.cardContainer.setOnClickListener {
            val sharedPrefFile = "locationName"
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("locationName",m.addressName +" "+ m.locationName)
            editor.putInt("Count", 1)
            editor.apply()
            editor.commit()
            context.finish()
        }

        holder.deleteLocation.setOnClickListener { view ->
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmation!")
                    .setMessage("Are you sure delete?")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setCancelable(false)
            builder.setPositiveButton("Yes"){ _, _ ->
                val helper = DBHelper(context)
                val result = helper.deleteLocation(m.id)
                if (result > 0) {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()

                    val anim = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)
                    anim.duration = 500
                    view.startAnimation(anim)
                    Handler(Looper.myLooper()!!).postDelayed({
                        models.remove(m)
                        notifyDataSetChanged()
                    }, anim.duration)
                } else {

                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("No", null)
            builder.show()
        }
    }

    override fun getItemCount(): Int {
       return models.size
    }

    inner class ModelsVH(v: View):RecyclerView.ViewHolder(v){

        val addressName: TextView = v.findViewById(R.id.addressName)
        val locationName: TextView = v.findViewById(R.id.locationName)
        val deleteLocation: ImageView = v.findViewById(R.id.deleteLocation)
        val cardContainer: CardView = v.findViewById(R.id.cardContainer)


    }

}