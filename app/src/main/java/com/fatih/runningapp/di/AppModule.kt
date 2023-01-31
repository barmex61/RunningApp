package com.fatih.runningapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.fatih.runningapp.database.RunDatabase
import com.fatih.runningapp.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.fatih.runningapp.util.Constants.KEY_NAME
import com.fatih.runningapp.util.Constants.KEY_WEIGHT
import com.fatih.runningapp.util.Constants.ROOM_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunDao(@ApplicationContext context:Context)= Room.databaseBuilder(context,RunDatabase::class.java,ROOM_DATABASE_NAME).build().runDao()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context)=context.getSharedPreferences(
        KEY_NAME,Context.MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideName( sharedPref:SharedPreferences)=sharedPref.getString(KEY_NAME,"")?:""

    @Provides
    @Singleton
    fun provideWeight( sharedPref:SharedPreferences)=sharedPref.getFloat(KEY_WEIGHT,80f)

    @Provides
    @Singleton
    fun provideFirstTimeToggle( sharedPref:SharedPreferences)=sharedPref.getBoolean(
        KEY_FIRST_TIME_TOGGLE,true)
}