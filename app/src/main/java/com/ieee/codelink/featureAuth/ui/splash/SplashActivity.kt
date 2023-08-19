package com.ieee.codelink.featureAuth.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.ieee.codelink.core.BaseActivity
import com.ieee.codelink.featureAuth.data.local.preference.SharedPreferenceManger
import com.ieee.codelink.featureAuth.ui.AuthActivity
import com.ieee.codelink.featureHome.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "SplashActivity"

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    @Inject
    lateinit var sharedPreferenceManger: SharedPreferenceManger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sharedPreferenceManger.hasLoggedIn){
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
        else{
            startActivity(Intent(this, AuthActivity::class.java))
            this.finish()
        }
    }
}