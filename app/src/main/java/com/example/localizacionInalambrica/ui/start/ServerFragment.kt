package com.example.localizacionInalambrica.ui.start

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.other.Constants.APLICATIONID
import com.example.localizacionInalambrica.other.Constants.PREFERENSES_SERVERADDR
import com.parse.Parse
import com.parse.ParseObject
import com.parse.ParseQuery
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServerFragment : Fragment() {
    @Inject
    lateinit var sharedPref: SharedPreferences

    val TAG = "ServerFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_server, container, false)
        val serverEditText: EditText = root.findViewById(R.id.serveraddres)
        val imageButton: Button = root.findViewById(R.id.imageButton)
        serverEditText.setOnKeyListener { _, keyCode, keyevent ->
            //If the keyevent is a key-down event on the "enter" button
            if (keyevent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val serverAddres = serverEditText.text.toString()
                setServer(serverAddres)

                true
            } else false
        }
        imageButton.setOnClickListener {
            val serverAddres = serverEditText.text.toString()
            setServer(serverAddres)
        }
        val query = sharedPref.getString(PREFERENSES_SERVERADDR, null)
        query?.also { serverEditText.setText(it, TextView.BufferType.EDITABLE) }
        return root


    }

    private fun setServer(serverip: String) {

        if (URLUtil.isValidUrl(serverip)) {
            Parse.initialize(
                context?.let {
                    Parse.Configuration.Builder(it)
                        .applicationId(APLICATIONID) // if desired
                        .server(serverip)
                        .build()
                }
            )

            val query = ParseQuery.getQuery<ParseObject>("Conf")
            query.whereEqualTo("live", "1")
            query.findInBackground { _, e ->
                if (e == null) {
                    val editor = sharedPref.edit()
                    editor.putString(PREFERENSES_SERVERADDR, serverip)
                    editor.apply()
                    val urlPref = sharedPref.getString(PREFERENSES_SERVERADDR, null)
                    if (urlPref != serverip) {
                        Log.d(TAG, "Error al guardar serveraddr en preferencias")
                    }
                    requireView().findNavController()
                        .navigate(R.id.action_serverFragment_to_loginFragment)
                } else {
                    Toast.makeText(context, getString(R.string.noconection), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.urlfail), Toast.LENGTH_SHORT).show()
            return
        }
    }
}