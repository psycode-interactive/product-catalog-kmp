package com.psycodeinteractive.productcatalog.data.di.source.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.mp.KoinPlatform.getKoin

actual fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = getKoin().get<Context>()

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
    )
}
