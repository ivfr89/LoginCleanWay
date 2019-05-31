package com.fernandez.loginclean.data.server

import com.fernandez.loginclean.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


val gso : GoogleSignInOptions
    get() {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.GOOGLE.GOOGLE_OAUTH_CLIENT)
            .requestEmail()
            .build()
    }


