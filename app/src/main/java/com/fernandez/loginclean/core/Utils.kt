package com.fernandez.loginclean.core

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.fernandez.loginclean.R


fun Fragment.openEmailChooser()
{
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_EMAIL)
        }
        startActivity(Intent.createChooser(intent,getString(R.string.email_client)))
}


fun Activity.openEmailChooser()
{
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_APP_EMAIL)
    }
    startActivity(Intent.createChooser(intent,getString(R.string.email_client)))
}
