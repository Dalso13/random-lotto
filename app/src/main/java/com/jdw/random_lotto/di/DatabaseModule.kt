package com.jdw.random_lotto.di

import android.content.Context
import androidx.room.Room
import com.jdw.random_lotto.data.lotto.db.AppDatabase
import com.jdw.random_lotto.data.lotto.db.LottoDao
import com.jdw.random_lotto.data.lotto.db.LottoRepo
import com.jdw.random_lotto.data.lotto.db.LottoRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 데이터베이스 관련 바인딩
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "random_lotto.db")
            // 개발 중 임시: 마이그레이션 없이 스키마 변경 시 테이블 재생성
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLottoDao(db: AppDatabase): LottoDao = db.lottoDao()

    @Provides @Singleton
    fun provideLottoRepository(dao: LottoDao): LottoRepo =
        LottoRepoImpl(dao)
}