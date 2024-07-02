package com.example.aplikasistoryapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasistoryapp.R
import com.example.aplikasistoryapp.customview.CustomAlertDialog
import com.example.aplikasistoryapp.data.Result
import com.example.aplikasistoryapp.databinding.ActivityRegisterBinding
import com.example.aplikasistoryapp.ui.login.LoginActivity
import com.example.aplikasistoryapp.utils.VMFactory
import com.example.aplikasistoryapp.utils.isValidEmail

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: VMFactory
    private val viewModel: RegisterVM by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViewModel()
        playAnimations()
        setupToolbar()
        setupNavigationToLogin()
        setupRegisterAction()
        checkInputValidity()
        setupEmailInputListener()
        setupNameInputListener()
        setupPasswordInputListener()
        setupConfirmPasswordInputListener()
    }

    private fun initializeViewModel() {
        factory = VMFactory.getInstance(applicationContext)
    }

    private fun setupToolbar() {
        title = getString(R.string.register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavigationToLogin() {
        val backText = getString(R.string.back)
        val loginText = getString(R.string.login)
        val spannableString = SpannableString(backText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                navigateToLogin()
            }
        }
        val loginStartIndex = backText.indexOf(loginText)
        val loginEndIndex = loginStartIndex + loginText.length
        spannableString.setSpan(clickableSpan, loginStartIndex, loginEndIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.registerLayout.backButton.text = spannableString
        binding.registerLayout.backButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showSuccess() {
        CustomAlertDialog(
            this,
            R.string.success_create_user,
            R.drawable.user_created
        ).show()
        clearInputFields()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingLayout.root.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerLayout.root.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError() {
        CustomAlertDialog(this, R.string.error_message, R.drawable.error).show()
    }

    private fun clearInputFields() {
        binding.registerLayout.edRegisterEmail.text?.clear()
        binding.registerLayout.edRegisterPassword.text?.clear()
        binding.registerLayout.edRegisterName.text?.clear()
        binding.registerLayout.edRegisterConfirm.text?.clear()
    }

    private fun setupRegisterAction() {
        binding.registerLayout.registerButton.setOnClickListener {
            val email = binding.registerLayout.edRegisterEmail.text.toString()
            val password = binding.registerLayout.edRegisterPassword.text.toString()
            val name = binding.registerLayout.edRegisterName.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                registerUser(name, email, password)
            } else {
                CustomAlertDialog(this, R.string.error_validation, R.drawable.formerror).show()
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        viewModel.postRegister(name, email, password).observe(this@RegisterActivity) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    showLoading(false)
                    showError()
                }
                is Result.Success -> {
                    showLoading(false)
                    showSuccess()
                }
            }
        }
    }

    private fun playAnimations() {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.registerLayout.tvEmail, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 100
                },
                ObjectAnimator.ofFloat(binding.registerLayout.tvEmail, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 100
                },
                ObjectAnimator.ofFloat(binding.registerLayout.edRegisterEmail, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 300
                },
                ObjectAnimator.ofFloat(binding.registerLayout.edRegisterEmail, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 300
                },
                ObjectAnimator.ofFloat(binding.registerLayout.tvName, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 400
                },
                ObjectAnimator.ofFloat(binding.registerLayout.tvName, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 400
                },
                ObjectAnimator.ofFloat(binding.registerLayout.edRegisterName, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 600
                },
                ObjectAnimator.ofFloat(binding.registerLayout.edRegisterName, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 600
                },
                ObjectAnimator.ofFloat(binding.registerLayout.tvPassword, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 700
                },
                ObjectAnimator.ofFloat(binding.registerLayout.tvPassword, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 700
                },
                ObjectAnimator.ofFloat(binding.registerLayout.edRegisterPassword, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 900
                },
                ObjectAnimator.ofFloat(binding.registerLayout.edRegisterPassword, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 900
                },
                ObjectAnimator.ofFloat(binding.registerLayout.tvConfirm, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 1000
                },
                ObjectAnimator.ofFloat(binding.registerLayout.tvConfirm, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 1000
                },
                ObjectAnimator.ofFloat(binding.registerLayout.edRegisterConfirm, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 1200
                },
                ObjectAnimator.ofFloat(binding.registerLayout.edRegisterConfirm, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 1200
                },
                ObjectAnimator.ofFloat(binding.registerLayout.registerButton, View.TRANSLATION_X, -200f, 0f).apply {
                    duration = 500
                    startDelay = 1300
                },
                ObjectAnimator.ofFloat(binding.registerLayout.registerButton, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                    startDelay = 1300
                }
            )
            start()
        }
    }

    private fun checkInputValidity() {
        val email = binding.registerLayout.edRegisterEmail.text.toString()
        val password = binding.registerLayout.edRegisterPassword.text.toString()
        val name = binding.registerLayout.edRegisterName.text.toString()
        val confirmPassword = binding.registerLayout.edRegisterConfirm.text.toString()

        binding.registerLayout.registerButton.isEnabled =
            isValidEmail(email) && name.isNotEmpty() && password.isNotEmpty() && confirmPassword == password
    }

    private fun setupEmailInputListener() {
        binding.registerLayout.edRegisterEmail.addTextChangedListener(createTextWatcher { checkInputValidity() })
    }

    private fun setupNameInputListener() {
        binding.registerLayout.edRegisterName.addTextChangedListener(createTextWatcher {
            checkInputValidity()
        })
    }

    private fun setupPasswordInputListener() {
        binding.registerLayout.edRegisterPassword.addTextChangedListener(createTextWatcher { checkInputValidity() })
    }

    private fun setupConfirmPasswordInputListener() {
        binding.registerLayout.edRegisterConfirm.addTextChangedListener(createTextWatcher { checkInputValidity() })
    }

    private fun createTextWatcher(onTextChangedAction: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChangedAction()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
    }
}