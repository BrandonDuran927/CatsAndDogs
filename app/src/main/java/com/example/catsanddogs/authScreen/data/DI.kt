package com.example.catsanddogs.authScreen.data

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import com.example.catsanddogs.authScreen.domain.model.Repository
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DI {
    @Singleton
    @Provides
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideOneTapClient(@ApplicationContext context: Context) = Identity.getSignInClient(context)

    @Singleton
    @Provides
    fun provideRepository(firebaseAuth: FirebaseAuth) : Repository =
        RepositoryImpl(firebaseAuth)
}