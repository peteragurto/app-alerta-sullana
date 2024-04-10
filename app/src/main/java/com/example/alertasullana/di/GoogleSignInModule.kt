package com.example.alertasullana.di

import android.app.Application
import android.content.Context
import com.example.alertasullana.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleSignInModule {

    @Provides
    @Singleton
    fun providesContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((R.string.oauthclient).toString())
            .requestEmail()
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(context: Context, googleSignInOptions: GoogleSignInOptions): GoogleSignInClient {
        return try {
            GoogleSignIn.getClient(context, googleSignInOptions)
        } catch (e: Exception) {
            // Handle any errors that occur during initialization
            throw RuntimeException("Failed to initialize Google Sign-In client", e)
        }
    }
}