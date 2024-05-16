package com.sendhur.contactsapp.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.sendhur.contactsapp.common.Constants
import com.sendhur.contactsapp.data.local.ContactEntity
import com.sendhur.contactsapp.data.local.ContactsDao
import com.sendhur.contactsapp.data.local.ContactsDatabase
import com.sendhur.contactsapp.data.remote.ApiInterface
import com.sendhur.contactsapp.data.remote.ContactRemoteMediator
import com.sendhur.contactsapp.data.repository.ContactsRepositoryImpl
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesApiInterface(): ApiInterface {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun providesContactsRepository(
        apiInterface: ApiInterface,
        contactsDatabase: ContactsDatabase
    ): ContactsRepository {
        return ContactsRepositoryImpl(apiInterface, contactsDatabase.dao, contactsDatabase)
    }

    @Provides
    @Singleton
    fun providesContactDatabase(@ApplicationContext context: Context): ContactsDatabase {
        return Room.databaseBuilder(
            context,
            ContactsDatabase::class.java,
            "contacts.db"
        ).build()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun providesPager(
        contactsDatabase: ContactsDatabase,
        contactsRepository: ContactsRepository
    ): Pager<Int, ContactEntity> {
        return Pager(
            config = PagingConfig(25),
            remoteMediator = ContactRemoteMediator(
                contactsDatabase = contactsDatabase,
                contactsRepository = contactsRepository
            ),
            pagingSourceFactory = {
                contactsDatabase.dao.pagingSource()
            }
        )
    }
}