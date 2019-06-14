package com.epam.training.simplenotes.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.epam.training.simplenotes.R
import com.epam.training.simplenotes.viewmodel.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Activity, that contains [SignInFragment] or [SignUpFragment] and allows user to sing in or to sing up to the
 * application respectively.
 */
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (loginViewModel.isUserAuthenticated()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.login_fragment_container,
                SignInFragment()
            ).commit()
        }
    }
}
