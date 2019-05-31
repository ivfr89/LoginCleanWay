package com.fernandez.loginclean.presentation.access

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fernandez.loginclean.R
import com.fernandez.loginclean.domain.models.Profile
import com.fernandez.loginclean.presentation.adapters.ProfileRecyclerAdapter
import com.fernandez.loginclean.presentation.main.MainActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity: AppCompatActivity()
{

    private lateinit var mAdapterProfile: ProfileRecyclerAdapter
    private lateinit var mProfiles: List<Profile>


    companion object Factory{

        const val ARG_PROFILES = "argProfiles"

        fun newIntent(context: Context, profiles: List<Profile>): Intent
        {
            return Intent(context, ProfileActivity::class.java).apply {
                val bundle = Bundle()
                bundle.putParcelableArrayList(ARG_PROFILES, ArrayList(profiles))
                putExtras(bundle)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mProfiles = intent.getParcelableArrayListExtra(ARG_PROFILES)

        configureUI()
        setData(mProfiles)
    }

    private fun configureUI()
    {
        configureRecyclerView()

    }

    private fun configureRecyclerView()
    {
        mAdapterProfile = ProfileRecyclerAdapter(listOf(),::clickInProfile)
        rcvProfile.adapter = mAdapterProfile
        rcvProfile.layoutManager = GridLayoutManager(this,
                                                     if(mProfiles.size>1) 2 else 1,
                                                     RecyclerView.VERTICAL,false)
    }

    private fun setData(profiles: List<Profile>)
    {
        mAdapterProfile.setData(profiles)
    }



    private fun clickInProfile(profile: Profile) {

        startActivity(MainActivity.newInstance(this,profile))

    }
}