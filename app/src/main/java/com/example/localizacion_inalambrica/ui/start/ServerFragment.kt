package com.example.localizacion_inalambrica.ui.start

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.localizacion_inalambrica.APLICATIONID
import com.example.localizacion_inalambrica.R
import com.parse.Parse
import com.parse.ParseObject
import com.parse.ParseQuery


class ServerFragment : Fragment() {
    private lateinit var serverViewModel: ServerViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        serverViewModel =
            ViewModelProvider(this).get(ServerViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_server,container,false)
        val serverEditText: EditText =root.findViewById(R.id.serveraddres)
        val imageButton : Button = root.findViewById(R.id.imageButton)
        serverEditText.setOnKeyListener(OnKeyListener { view, keyCode, keyevent ->
            //If the keyevent is a key-down event on the "enter" button
            if (keyevent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val serverAddres = serverEditText.text.toString()
                setServer(serverAddres)

                true
            } else false
        })
        imageButton.setOnClickListener{
            val serverAddres = serverEditText.text.toString()
            setServer(serverAddres)
        }
        return root


    }
    fun setServer(serverip:String) {
        Parse.initialize(
           context?.let {
                Parse.Configuration.Builder(it)
                    .applicationId(APLICATIONID) // if desired
                    .server(serverip)
                    .build()
            }
        )
        //TODO TEST CONECTIOn+
        val gameScore = ParseObject("GameScore")
        gameScore.put("score", 1455)
        gameScore.put("playerName", "Sean Plott")
        gameScore.put("cheatMode", false)
        gameScore.saveInBackground()

        val query = ParseQuery.getQuery<ParseObject>("GameScore")
        query.getInBackground("WykaeEpJbP") { `object`, e ->
            if (e == null) {
                // object will be your game score
            } else {
                // something went wrong
            }
        }
    }
}