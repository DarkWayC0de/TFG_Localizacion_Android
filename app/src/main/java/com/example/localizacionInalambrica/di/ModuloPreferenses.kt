package com.example.localizacionInalambrica.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.localizacionInalambrica.other.Constants.PREFERENSES_FILE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ModuloPreferenses {

    @Singleton
    @Provides
    fun providerSharedPreferenses(
        @ApplicationContext app: Context
    ) =
        app.getSharedPreferences(PREFERENSES_FILE, MODE_PRIVATE)
}