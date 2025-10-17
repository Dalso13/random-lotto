package com.jdw.random_lotto.data.lotto.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jdw.random_lotto.data.lotto.db.entity.LottoEntity

@Database(
    entities = [LottoEntity::class],
    version = 1,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lottoDao(): LottoDao
}