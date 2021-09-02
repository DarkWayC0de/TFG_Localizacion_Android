package com.example.localizacionInalambrica.ui.start

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.localizacionInalambrica.MainActivity
import com.example.localizacionInalambrica.R
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val root =inflater.inflate(R.layout.fragment_login, container, false)
        val button: Button = root.findViewById(R.id.login)
        val usertex: EditText = root.findViewById(R.id.User)
        val passtex: EditText = root.findViewById(R.id.Password)
        button.setOnClickListener {
            val user = usertex.text.toString()
            val pass = passtex.text.toString()
            login(user, pass)

        }
        return root
    }

    @Inject
    lateinit var sharedPref: SharedPreferences

    private fun login(user: String, pass: String) {
        /*  TODO Default user
        val user1 = ParseUser()
        user1.username = "user1"
        user1.setPassword("my pass")
        user1.email = "email@example.com"
        user1.signUpInBackground { e ->
            if (e == null) {
                // Hooray! Let them use the app now.
                Toast.makeText(context,getString(R.string.loginsus),Toast.LENGTH_LONG).show()
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
            }
        }*/
        ParseUser.logInInBackground(user, pass) { userd, e ->
            if (userd != null) {
                Toast.makeText(context, getString(R.string.loginsus), Toast.LENGTH_LONG).show()
                // TODO GUARDAR PREDERENCIAS USUARIO , ROLL
                val editor = sharedPref.edit()
                editor.putString("userName", user)
                editor.apply()
                val userPref = sharedPref.getString("userName", null)
                if (userPref != user) {
                    Toast.makeText(
                        context,
                        getString(R.string.error_save_pref_user),
                        Toast.LENGTH_SHORT
                    ).show()
                }


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