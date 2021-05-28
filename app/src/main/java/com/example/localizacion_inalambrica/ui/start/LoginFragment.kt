package com.example.localizacion_inalambrica.ui.start

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.localizacion_inalambrica.MainActivity
import com.example.localizacion_inalambrica.R
import com.parse.ParseUser


class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        val root =inflater.inflate(R.layout.fragment_login, container, false)
        val button: Button = root.findViewById(R.id.login)
        val usertex : EditText = root.findViewById(R.id.User)
        val passtex : EditText = root.findViewById(R.id.Password)
        button.setOnClickListener{
            val user = usertex.text.toString()
            val pass = passtex.text.toString()
            login(user,pass)

        }
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser != null) {
            saltaractividad()
        }

        return root
    }

    private fun login(user : String, pass :String) {
        ParseUser.logInInBackground(user, pass) { userd, e ->
            if (userd != null) {
                // Hooray! The user is logged in.
                Toast.makeText(context,getString(R.string.loginsus),Toast.LENGTH_LONG).show()
                saltaractividad()
            } else {
               Toast.makeText(context,getString(R.string.loginfail),Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saltaractividad(){
        val actividad =  Intent(context, MainActivity::class.java)
        startActivity(actividad)
    }


}