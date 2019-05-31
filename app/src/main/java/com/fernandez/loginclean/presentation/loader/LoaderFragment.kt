package com.fernandez.loginclean.presentation.loader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fernandez.loginclean.R

class LoaderFragment(): Fragment()
{

    companion object {

        fun newInstance(): Fragment
        {
            return LoaderFragment().apply {
                val args = Bundle()
                arguments = args
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loader,container,false)

    }

}