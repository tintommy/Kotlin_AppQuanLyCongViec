package com.example.kotlin_appquanlycongviec.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideUserSP(application: Application) =
        application.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
}