package com.example.localizacion_inalambrica.ui.start

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.localizacion_inalambrica.APLICATIONID
import com.example.localizacion_inalambrica.R
import com.parse.Parse
import com.parse.ParseObject
import com.parse.ParseQuery


class ServerFragment : Fragment() {
    private lateinit var serverViewModel: ServerViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        serverViewModel =
            ViewModelProvider(this).get(ServerViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_server, container, false)
        val serverEditText: EditText = root.findViewById(R.id.serveraddres)
        val imageButton: Button = root.findViewById(R.id.imageButton)
        serverEditText.setOnKeyListener { view, keyCode, keyevent ->
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
        sharedPref = context?.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)!!
        val query = sharedPref.getString("serveraddr", null)
        if (query != null) {
            setServer(query)
        }
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
            //TODO TEST CONECTIOn+
            /*val gameScore = ParseObject("Conf")
            gameScore.put("live","1" )
            gameScore.saveInBackground()*/

            val query = ParseQuery.getQuery<ParseObject>("Conf")
            query.getInBackground("e0H1kDwiKC") { `object`, e ->
                if (e == null) {
                    val sharedPref = context?.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    val editor = sharedPref!!.edit()
                    editor.putString("serveraddr", serverip)
                    editor.apply()
                    val a = sharedPref.getString("serveraddr", null)
                    if (a != serverip) {
                        Toast.makeText(context,
                            getString(R.string.errosavepref),
                            Toast.LENGTH_SHORT).show()
                    }
                    // object will be your game score
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