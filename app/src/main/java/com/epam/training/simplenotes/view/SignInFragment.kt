package com.epam.training.simplenotes.view

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.epam.training.simplenotes.R
import com.epam.training.simplenotes.viewmodel.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SignInFragment : Fragment() {

    companion object {
        private const val EMAIL = "EMAIL"
        private const val PASSWORD = "PASSWORD"
    }

    private val loginViewModel: LoginViewModel by viewModel()

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: AppCompatTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        emailText = view.findViewById(R.id.email_input)
        passwordText = view.findViewById(R.id.password_input)
        signInButton = view.findViewById(R.id.sing_in_button)
        signUpButton = view.findViewById(R.id.sign_up_button)

        loginViewModel.authState.observe(this, Observer {
            when (it) {
                true -> goToMainActivity()
                false -> Toast.makeText(context, R.string.wrong_account_text, Toast.LENGTH_LONG).show()
            }
        })

        signInButton.setOnClickListener {
            if (fieldsNotEmpty()) {
                loginViewModel.signIn(emailText.text.toString(), passwordText.text.toString())
            } else {
                Toast.makeText(context, R.string.fill_fields_on_sign_in, Toast.LENGTH_LONG).show()
            }
        }

        signUpButton.setOnClickListener {
            onSignUpClicked()
        }

        return view
    }

    private fun fieldsNotEmpty(): Boolean {

        return emailText.text.isNotEmpty() && passwordText.text.isNotEmpty()
    }

    private fun goToMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(EMAIL, emailText.text.toString())
        intent.putExtra(PASSWORD, passwordText.text.toString())
        startActivity(intent)
        activity?.finish()
    }

    private fun onSignUpClicked() {
        activity?.supportFragmentManager?.beginTransaction()?.replace(
            R.id.login_fragment_container,
            SignUpFragment()
        )?.commit()
    }

}