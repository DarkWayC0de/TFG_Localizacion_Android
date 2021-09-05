package com.example.localizacionInalambrica.ui.start

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.localizacionInalambrica.MainActivity
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.other.Constants.PREFERENSES_CIFRADOKEY88
import com.example.localizacionInalambrica.other.Constants.PREFERENSES_MACKEY32
import com.example.localizacionInalambrica.other.Constants.PREFERENSES_USERID
import com.example.localizacionInalambrica.other.Constants.PREFERENSES_USERNAME
import com.example.localizacionInalambrica.other.Constants.PREFERENSES_USERROLE
import com.parse.ParseObject
import com.parse.ParseQuery
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
    val TAG = "LoginFragment"

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
        ParseUser.logInInBackground(user, pass) { userd, _ ->
            if (userd != null) {
                Toast.makeText(context, getString(R.string.loginsus), Toast.LENGTH_LONG).show()

                val editor = sharedPref.edit()
                editor.putString(PREFERENSES_USERNAME, user)
                editor.apply()
                val userPref = sharedPref.getString(PREFERENSES_USERNAME, null)
                if (userPref != user) {
                    Log.d(TAG, "Error al guardar userName en preferencias")
                }
                val userrole = userd.getString("role")
                editor.putString(PREFERENSES_USERROLE, userrole)
                editor.apply()
                val userRolePref = sharedPref.getString(PREFERENSES_USERROLE, null)
                if (userRolePref != userrole) {
                    Log.d(TAG, "Error al guardar userRole en preferencias")
                }
                val query = ParseQuery<ParseObject>("DatosUsuarios")
                query.whereEqualTo("user", userd)
                val obj = query.find()
                if (obj.size == 1) {
                    val respuesta = obj.first()
                    val userID = respuesta.getString("UserID")
                    val mackey = respuesta.getString("Mackey32")
                    val cifradokey = respuesta.getString("CifradoKey88")
                    editor.putString(PREFERENSES_USERID, userID)
                    editor.apply()
                    val userIDPref = sharedPref.getString(PREFERENSES_USERID, null)
                    if (userID != userIDPref) {
                        Log.d(TAG, "Error al guardar la preferencia userID")
                    }
                    editor.putString(PREFERENSES_MACKEY32, mackey)
                    editor.apply()
                    val mackeyPref = sharedPref.getString(PREFERENSES_MACKEY32, null)
                    if (mackey != mackeyPref) {
                        Log.d(TAG, "Error al guardar la preferencia mackey32")
                    }
                    editor.putString(PREFERENSES_CIFRADOKEY88, cifradokey)
                    editor.apply()
                    val cif88Pref = sharedPref.getString(PREFERENSES_CIFRADOKEY88, null)
                    if (cifradokey != cif88Pref) {
                        Log.d(TAG, "Error al guardar la preferencia CIFRADOKEY88")
                    }
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
        requireActivity().finish()
    }


}