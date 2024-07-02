package com.example.aplikasistoryapp.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.aplikasistoryapp.constants.Constants
import com.example.aplikasistoryapp.databinding.ActivitySplashScreenBinding
import com.example.aplikasistoryapp.model.LoginModel
import com.example.aplikasistoryapp.preference.LoginPreference
import com.example.aplikasistoryapp.preference.SettingPreferences
import com.example.aplikasistoryapp.ui.login.LoginActivity
import com.example.aplikasistoryapp.ui.main.MainActivity
import com.example.aplikasistoryapp.viewmodel.SettingVM
import com.example.aplikasistoryapp.viewmodel.SettingVMFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@SuppressLint("CustomSplashScreen")

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreference = LoginPreference(this)

        loginModel = mLoginPreference.getUser()

        themeSettingHandler()
        splashScreenHandler()
    }

    private fun splashScreenHandler() {
        if (loginModel.name != null && loginModel.userId != null && loginModel.token != null) {
            val intent = Intent(this, MainActivity::class.java)
            navigate(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            navigate(intent)
        }
    }

    private fun navigate(intent: Intent) {
        val splashTimer: Long = Constants.SPLASHSCREEN
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, splashTimer)
    }

    private fun themeSettingHandler() {
        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingVMFactory(pref)
        )[SettingVM::class.java]
        settingViewModel.getThemeSettings().observe(this
        ) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }
    }
}