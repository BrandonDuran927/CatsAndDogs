package com.example.catsanddogs.homeScreen.data

import android.content.Context
import androidx.room.Room
import com.example.catsanddogs.homeScreen.data.local.ImageDAO
import com.example.catsanddogs.homeScreen.data.local.ImageDatabase
import com.example.catsanddogs.homeScreen.domain.model.Repository
import com.example.catsanddogs.common.utilities.ConnectivityObserver
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DI {
    @Singleton
    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context) : ImageDatabase =
        Room.databaseBuilder(
            context = context,
            klass = ImageDatabase::class.java,
            name = "ImageDatabase"
        ).build()

    @Singleton
    @Provides
    fun provideImageDAO(db: ImageDatabase) : ImageDAO = db.dao

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun provideStorageFirebase(): FirebaseStorage = Firebase.storage

    @Singleton
    @Provides
    fun repositoryProvider(
        firestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
        firebaseAuth: FirebaseAuth
    ): Repository = RepositoryImpl(firebaseAuth = firebaseAuth, firebaseFirestore = firestore, firebaseStorage = firebaseStorage)

    @Singleton
    @Provides
    fun provideConnectivityObserver(
      @ApplicationContext context: Context
    ): ConnectivityObserver = ConnectivityObserver(context)

}