package com.example.localizacionInalambrica

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast


fun Context.toast(s: String) {
    Toast.makeText(this,s, Toast.LENGTH_SHORT).show()
}