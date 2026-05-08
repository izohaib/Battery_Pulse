package com.example.battery_pulse.feature.history.di

import android.content.Context
import androidx.room.Room
import com.example.battery_pulse.feature.history.data.local.database.AppDatabase
import com.example.battery_pulse.feature.history.data.local.dao.ChargingSessionDao
import com.example.battery_pulse.feature.history.data.repository.ChargingHistoryRepositoryImpl
import com.example.battery_pulse.feature.history.domain.repository.ChargingHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HistoryModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "battery_pulse_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChargingSessionDao(db: AppDatabase): ChargingSessionDao {
        return db.chargingSessionDao()
    }

    @Provides
    @Singleton
    fun provideChargingHistoryRepository(
        dao: ChargingSessionDao
    ): ChargingHistoryRepository {
        return ChargingHistoryRepositoryImpl(dao)
    }
}