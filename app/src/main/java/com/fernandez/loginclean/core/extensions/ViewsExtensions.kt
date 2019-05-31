package com.fernandez.loginclean.core.extensions

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fernandez.loginclean.R

fun ImageView.loadImage(image: String?) {

    val options = RequestOptions()
        // .placeholder(R.mipmap.ic_launcher)
        .error(R.drawable.ic_profile)


    Glide.with(this).load(image).apply(options).into(this)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hidde() {
    this.visibility = View.GONE
}