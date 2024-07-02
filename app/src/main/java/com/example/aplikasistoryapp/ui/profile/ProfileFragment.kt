package com.example.aplikasistoryapp.ui.profile

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aplikasistoryapp.databinding.FragmentProfileBinding
import com.example.aplikasistoryapp.model.LoginModel
import com.example.aplikasistoryapp.preference.LoginPreference
import com.example.aplikasistoryapp.preference.SettingPreferences
import com.example.aplikasistoryapp.ui.login.LoginActivity
import com.example.aplikasistoryapp.viewmodel.SettingVM
import com.example.aplikasistoryapp.viewmodel.SettingVMFactory

private val Context.dataStore by preferencesDataStore(name = "settings")

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginPreference: LoginPreference
    private lateinit var loginModel: LoginModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        loginPreference = LoginPreference(requireContext())
        loginModel = loginPreference.getUser()

        setupUi()
        configureLanguageOption()
        configureLogoutOption()
        applyCurrentTheme()
        handleThemeSwitch()
        initiateAnimation()

        return binding.root
    }

    private fun setupUi() {
        binding.tvName.text = loginModel.name
        binding.tvUserId.text = loginModel.userId
    }

    private fun configureLanguageOption() {
        binding.cvLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun configureLogoutOption() {
        binding.cvLogout.setOnClickListener {
            loginPreference.removeUser()
            val logoutIntent = Intent(activity, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
            activity?.finishAffinity()
        }
    }

    private fun applyCurrentTheme() {
        val preferences = SettingPreferences.getInstance(requireContext().dataStore)
        val viewModelFactory = SettingVMFactory(preferences)
        val settingViewModel = ViewModelProvider(this, viewModelFactory)[SettingVM::class.java]

        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }
    }

    private fun handleThemeSwitch() {
        val preferences = SettingPreferences.getInstance(requireContext().dataStore)
        val viewModelFactory = SettingVMFactory(preferences)
        val settingViewModel = ViewModelProvider(this, viewModelFactory)[SettingVM::class.java]

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun initiateAnimation() {
        val animationDuration = 600L

        val userDetailAnimation = ObjectAnimator.ofFloat(binding.cvTheme, View.ALPHA, 1f).apply {
            duration = animationDuration
        }

        val settingAnimation = ObjectAnimator.ofFloat(binding.tvSetting, View.ALPHA, 1f).apply {
            duration = animationDuration
        }

        val themeCardAnimation = ObjectAnimator.ofFloat(binding.cvTheme, View.ALPHA, 1f).apply {
            duration = animationDuration
        }

        val languageCardAnimation = ObjectAnimator.ofFloat(binding.cvLanguage, View.ALPHA, 1f).apply {
            duration = animationDuration
        }

        val logoutCardAnimation = ObjectAnimator.ofFloat(binding.cvLogout, View.ALPHA, 1f).apply {
            duration = animationDuration
        }

        AnimatorSet().apply {
            playSequentially(userDetailAnimation, settingAnimation, themeCardAnimation, languageCardAnimation, logoutCardAnimation)
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
