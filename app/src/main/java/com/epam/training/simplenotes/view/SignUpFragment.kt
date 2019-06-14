package com.epam.training.simplenotes.view

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.epam.training.simplenotes.R
import com.epam.training.simplenotes.util.isOnline
import com.epam.training.simplenotes.viewmodel.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment() {

    companion object {
        private const val EMAIL = "EMAIL"
        private const val PASSWORD = "PASSWORD"
    }

    private val loginViewModel: LoginViewModel by viewModel()

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var repeatPasswordText: EditText
    private lateinit var signUpButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        emailText = view.findViewById(R.id.email_input_up)
        passwordText = view.findViewById(R.id.password_input_up)
        repeatPasswordText = view.findViewById(R.id.repeat_password_input_up)
        signUpButton = view.findViewById(R.id.sing_up_button_up)

        loginViewModel.authState.observe(this, Observer {
            when (it) {
                true -> goToMainActivity()
                false -> Toast.makeText(context, R.string.registration_failed, Toast.LENGTH_LONG).show()
            }
        })

        signUpButton.setOnClickListener {
            onSignUpClicked()
        }

        return view
    }

    private fun goToMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(EMAIL, emailText.text.toString())
        intent.putExtra(PASSWORD, passwordText.text.toString())
        startActivity(intent)
        activity?.finish()
    }

    private fun onSignUpClicked() {
        when {
            !isOnline(context) -> Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
            !fieldsNotEmpty() -> Toast.makeText(context, R.string.fill_fields_on_sign_in, Toast.LENGTH_LONG).show()
            passwordText.text.toString() != repeatPasswordText.text.toString() ->
                Toast.makeText(context, R.string.passwords_not_match, Toast.LENGTH_LONG).show()
            else -> loginViewModel.registerAccount(emailText.text.toString(), passwordText.text.toString())
        }
//        if (fieldsNotEmpty()) {
//            if (passwordText.text.toString() == repeatPasswordText.text.toString()) {
//                loginViewModel.registerAccount(emailText.text.toString(), passwordText.text.toString())
//            } else {
//                Toast.makeText(context, R.string.passwords_not_match, Toast.LENGTH_LONG).show()
//            }
//        } else {
//            Toast.makeText(context, R.string.fill_fields_on_sign_in, Toast.LENGTH_LONG).show()
//        }
    }

    private fun fieldsNotEmpty(): Boolean {

        return emailText.text.isNotEmpty() && passwordText.text.isNotEmpty()
    }
}