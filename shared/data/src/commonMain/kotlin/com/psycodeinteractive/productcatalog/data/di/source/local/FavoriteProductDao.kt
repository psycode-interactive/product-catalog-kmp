package com.psycodeinteractive.productcatalog.data.di.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteProductDao {

    @Query("SELECT * FROM favorite_products")
    fun getFavorites(): Flow<List<FavoriteProductDatabaseModel>>

    @Query("SELECT id FROM favorite_products")
    suspend fun getFavoriteIds(): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: FavoriteProductDatabaseModel)

    @Query("DELETE FROM favorite_products WHERE id = :productId")
    suspend fun delete(productId: Long)
}
