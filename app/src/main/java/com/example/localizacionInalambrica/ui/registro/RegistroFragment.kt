package com.example.localizacionInalambrica.ui.registro

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.localizacionInalambrica.R
import com.parse.FunctionCallback
import com.parse.ParseCloud
import com.parse.ParseObject
import com.parse.ParseUser


class RegistroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_registro, container, false)


        val texto_de_registro: TextView = root.findViewById(R.id.Texto_registro_de_usuario)
        val boton_registrar: Button = root.findViewById(R.id.button_de_registro)
        val usuario_de_registro: EditText = root.findViewById(R.id.usuario_de_registro)
        val contrasena_de_registro: EditText = root.findViewById(R.id.contrasena_de_registro)
        val selector_rastreador: SwitchCompat =
            root.findViewById(R.id.selector_rastreador_de_registro)
        val corre_de_registro: EditText = root.findViewById(R.id.correo_de_registro)
        val texto_avisos: TextView = root.findViewById(R.id.texto_confirmacion_de_registro)

        if (ParseUser.getCurrentUser().get("role") != "Rastreador") {
            boton_registrar.visibility = View.GONE
            texto_de_registro.visibility = View.GONE
            usuario_de_registro.visibility = View.GONE
            contrasena_de_registro.visibility = View.GONE
            selector_rastreador.visibility = View.GONE
            corre_de_registro.visibility = View.GONE
            texto_avisos.visibility = View.VISIBLE
            texto_avisos.setTextColor(Color.RED)
            texto_avisos.text = getString(R.string.Error_Usuario_en_registro)
        } else {
            usuario_de_registro.visibility = View.VISIBLE
            contrasena_de_registro.visibility = View.VISIBLE
            selector_rastreador.visibility = View.VISIBLE
            corre_de_registro.visibility = View.VISIBLE
            texto_avisos.visibility = View.GONE
            texto_de_registro.visibility = View.VISIBLE
            boton_registrar.visibility = View.VISIBLE

            boton_registrar.setOnClickListener {
                registrarusuario(
                    usuario_de_registro.text.toString(),
                    contrasena_de_registro.text.toString(),
                    selector_rastreador.isChecked,
                    corre_de_registro.text.toString(),
                    texto_avisos
                )
            }
        }
        return root

    }

    private fun registrarusuario(
        usuario: String,
        contrasena: String,
        selector: Boolean,
        correo: String,
        texto_avisos: TextView
    ) {

        if (usuario.length < 3) {
            texto_avisos.setTextColor(Color.RED)
            texto_avisos.text = getString(R.string.Error_Registro_usuario_muy_pequeño)
        } else {
            if (contrasena.length < 3) {
                texto_avisos.setTextColor(Color.RED)
                texto_avisos.text = getString(R.string.Error_Registro_contrasena_muy_pequeño)
            } else {
                val role: String
                role = if (selector) "Rastreador" else "Usuario"
                val params = HashMap<String, Any?>()
                params["username"] = usuario
                params["password"] = contrasena
                params["role"] = role
                if (correo.length > 0) {
                    params["email"] = correo
                }
                ParseCloud.callFunctionInBackground("Registro", params,
                    FunctionCallback<ParseObject?> { consult, e ->
                        if (e == null) {
                            texto_avisos.setTextColor(Color.GREEN)
                            val msg =
                                getString(R.string.Exito_registro_usuario) + " " + role + " " + usuario
                            texto_avisos.text = msg
                        } else {
                            texto_avisos.setTextColor(Color.RED)
                            texto_avisos.text =
                                getString(R.string.Error_Registrar) + " " + e.message
                        }
                    }
                )
            }
        }
        texto_avisos.visibility = View.VISIBLE
    }


}