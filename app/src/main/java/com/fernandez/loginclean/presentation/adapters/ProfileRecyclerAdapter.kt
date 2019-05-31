package com.fernandez.loginclean.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fernandez.loginclean.R
import com.fernandez.loginclean.core.extensions.loadImage
import com.fernandez.loginclean.domain.models.Profile

class ProfileRecyclerAdapter(var profiles: List<Profile>,
                             val onClickInProfile: (Profile)->Unit): RecyclerView.Adapter<ProfileRecyclerAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile,parent,false))

    }

    override fun getItemCount(): Int = profiles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.onBind(profiles[position])

    }

    fun setData(profiles: List<Profile>)
    {
        this.profiles = profiles
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        private val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        private val txtProfileName: TextView = itemView.findViewById(R.id.txtProfileName)


        fun onBind(user: Profile)
        {
            imgProfile.loadImage(user.image)
            txtProfileName.text = user.name
            itemView.setOnClickListener {
                onClickInProfile(user)
            }
        }
    }
}