package com.example.localizacionInalambrica.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.localizacionInalambrica.MainActivity
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.other.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region


@Module
@InstallIn(ServiceComponent::class)
object ModuloServicios {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)


    @ServiceScoped
    @Provides
    fun provideMainActivityPendinIntent(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_HOME_FRAGMEWNT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    fun providerBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_my_location_24)
        .setContentTitle(app.getString(R.string.tituloServicioRastreoNotificacion))
        .setContentText("0,0")
        .setContentIntent(pendingIntent)

    @ServiceScoped
    @Provides
    fun providerBeaconManager(
        @ApplicationContext app: Context,
    ) = BeaconManager.getInstanceForApplication(app)

    @ServiceScoped
    @Provides
    fun providerRegion() =
        Region("region", null, Identifier.parse("65535"), null)


}