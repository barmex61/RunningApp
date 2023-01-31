package com.fatih.runningapp.di

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fatih.runningapp.R
import com.fatih.runningapp.ui.activities.MainActivity
import com.fatih.runningapp.util.Constants
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideFuseLocationProvider(@ApplicationContext context: Context)=LocationServices.getFusedLocationProviderClient(context)

    @ServiceScoped
    @Provides
    fun providePendingIntent(@ApplicationContext context: Context)=PendingIntent.getActivity(context,
        0,
        Intent(context, MainActivity::class.java).apply {
            this.action= Constants.ACTION_SHOW_TRACKING_FRAGMENT
        }, PendingIntent.FLAG_UPDATE_CURRENT
    )

    @Provides
    @ServiceScoped
    fun provideNotificationCompat(@ApplicationContext context: Context,pendingIntent: PendingIntent)=
        NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)

}