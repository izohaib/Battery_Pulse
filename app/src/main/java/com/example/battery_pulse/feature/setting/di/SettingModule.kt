package com.example.battery_pulse.feature.setting.di

import android.content.Context
import com.example.battery_pulse.feature.setting.data.datasource.SettingsDataSource
import com.example.battery_pulse.feature.setting.data.repository.SettingsRepositoryImpl
import com.example.battery_pulse.feature.setting.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsDataSource(
        @ApplicationContext context: Context
    ): SettingsDataSource {
        return SettingsDataSource(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataSource: SettingsDataSource
    ): SettingsRepository {
        return SettingsRepositoryImpl(dataSource)
    }
}