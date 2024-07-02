package com.example.aplikasistoryapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasistoryapp.R
import com.example.aplikasistoryapp.customview.CustomAlertDialog
import com.example.aplikasistoryapp.data.Result
import com.example.aplikasistoryapp.databinding.ActivityLoginBinding
import com.example.aplikasistoryapp.model.LoginResponse
import com.example.aplikasistoryapp.model.LoginResultResponse
import com.example.aplikasistoryapp.preference.LoginPreference
import com.example.aplikasistoryapp.ui.main.MainActivity
import com.example.aplikasistoryapp.ui.register.RegisterActivity
import com.example.aplikasistoryapp.utils.VMFactory
import com.example.aplikasistoryapp.utils.isValidEmail
import com.example.aplikasistoryapp.utils.validateMinLength

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: VMFactory
    private val userViewModel: LoginVM by viewModels { factory }
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        hideActionBar()
        startAnimations()
        setupRegisterLink()
        setupLoginButton()
        handleEmailInput()
        handlePasswordInput()
        configurePasswordVisibilityToggle()
    }

    private fun setupViewModel() {
        factory = VMFactory.getInstance(binding.root.context)
    }

    private fun setupRegisterLink() {
        val fullText = getString(R.string.go_regist)
        val registerText = getString(R.string.register)
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }

        val startIndex = fullText.indexOf(registerText)
        if (startIndex != -1) {
            val endIndex = startIndex + registerText.length
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.loginLayout.tvGoregist.text = spannableString
            binding.loginLayout.tvGoregist.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setupLoginButton() {
        binding.loginLayout.loginButton.setOnClickListener {
            val email = binding.loginLayout.edLoginEmail.text.toString()
            val password = binding.loginLayout.edLoginPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                performLogin(email, password)
            } else {
                showValidationError()
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        userViewModel.postLogin(email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    showLoading(false)
                    showErrorDialog()
                }
                is Result.Success -> {
                    showLoading(false)
                    handleLoginSuccess(result.data)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingLayout.root.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginLayout.root.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun handleLoginSuccess(userResponse: LoginResponse) {
        saveUserData(userResponse.loginResult)
        navigateToHome()
    }

    private fun showErrorDialog() {
        CustomAlertDialog(this, R.string.error_message, R.drawable.error).show()
    }

    private fun showValidationError() {
        CustomAlertDialog(this, R.string.error_validation, R.drawable.formerror).show()
    }

    private fun saveUserData(loginModel: LoginResultResponse?) {
        loginModel?.let {
            val userPreference = LoginPreference(this)
            userPreference.setLogin(it)
        }
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun startAnimations() {
        val duration = 800L

        val emailTextAnim = ObjectAnimator.ofFloat(binding.loginLayout.tvEmail,
            View.TRANSLATION_X,
            -500f, 0f).setDuration(duration)

        val emailInputAnim = ObjectAnimator.ofFloat(binding.loginLayout.edLoginEmail,
            View.TRANSLATION_X,
            -500f, 0f).setDuration(duration)

        val passwordTextAnim = ObjectAnimator.ofFloat(binding.loginLayout.tvPassword,
            View.TRANSLATION_X,
            500f, 0f).setDuration(duration)

        val passwordInputAnim = ObjectAnimator.ofFloat(binding.loginLayout.edLoginPassword,
            View.TRANSLATION_X,
            500f, 0f).setDuration(duration)

        val loginBtnAnim = ObjectAnimator.ofFloat(binding.loginLayout.loginButton,
            View.SCALE_X,
            0f, 1f).setDuration(duration)

        val registerLinkAnim = ObjectAnimator.ofFloat(binding.loginLayout.tvGoregist,
            View.SCALE_Y,
            0f, 1f).setDuration(duration)

        AnimatorSet().apply {
            playTogether(
                emailTextAnim, emailInputAnim,
                passwordTextAnim, passwordInputAnim,
                loginBtnAnim, registerLinkAnim
            )
            start()
        }
    }

    private fun handleEmailInput() {
        binding.loginLayout.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handlePasswordInput() {
        binding.loginLayout.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateLoginButtonState() {
        binding.loginLayout.loginButton.isEnabled = isInputValid()
    }

    private fun isInputValid(): Boolean {
        val email = binding.loginLayout.edLoginEmail.text.toString()
        val password = binding.loginLayout.edLoginPassword.text.toString()
        return isValidEmail(email) && validateMinLength(password)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configurePasswordVisibilityToggle() {
        binding.loginLayout.edLoginPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (binding.loginLayout.edLoginPassword.right - binding.loginLayout.edLoginPassword.compoundDrawables[drawableEnd].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility() {
        val passwordInput = binding.loginLayout.edLoginPassword
        val selection = passwordInput.selectionEnd
        if (isPasswordVisible) {
            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.baseline_locked_24, 0, R.drawable.ic_visibility_on, 0
            )
            passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.baseline_locked_24, 0, R.drawable.ic_visibility_off, 0
            )
            passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        passwordInput.setSelection(selection)
    }
}