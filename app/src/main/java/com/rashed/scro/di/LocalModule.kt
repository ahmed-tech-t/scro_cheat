package com.rashed.scro.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.rashed.scro.datastore.model.CardInfo
import com.rashed.scro.datastore.serializer.CardInfoSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val DATA_STORE_CARD_INFO = "cardInfo.pb"

@InstallIn(SingletonComponent::class)
@Module
object LocalModule {

    @Singleton
    @Provides
    fun provideUserInfoProtoDataStore(@ApplicationContext appContext: Context): DataStore<CardInfo> {
        return DataStoreFactory.create(
            serializer = CardInfoSerializer,
            produceFile = { appContext.dataStoreFile(DATA_STORE_CARD_INFO) },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

}

