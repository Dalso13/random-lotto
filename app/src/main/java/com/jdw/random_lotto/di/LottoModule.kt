package com.jdw.random_lotto.di

import android.content.Context
import androidx.room.Room
import com.jdw.random_lotto.data.lotto.db.AppDatabase
import com.jdw.random_lotto.data.lotto.db.LottoDao
import com.jdw.random_lotto.data.lotto.db.LottoRepo
import com.jdw.random_lotto.data.lotto.db.LottoRepoImpl
import com.jdw.random_lotto.data.lotto.winning.LottoWinningRepo
import com.jdw.random_lotto.data.lotto.winning.LottoWinningRepoImpl
import com.jdw.random_lotto.data.lotto.winning.LottoWinningSource
import com.jdw.random_lotto.data.lotto.winning.LottoWinningSourceImpl
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoAnnuityWinningUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoAnnuityWinningUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoCheckUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoCheckUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoEditUseCase
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoEditUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoLoadUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoLoadUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoSaveUseCase
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoSaveUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoStandardWinningUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoStandardWinningUseCaseImpl
import dagger.Binds
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

// Lotto 당첨 정보 관련 바인딩
@Module
@InstallIn(SingletonComponent::class)
abstract class WinningModule {

    // repo
    @Binds
    @Singleton
    abstract fun bindLottoWinningRepo(
        impl: LottoWinningRepoImpl
    ): LottoWinningRepo

    // source
    @Binds
    @Singleton
    abstract fun bindLottoWinningSource(
        impl: LottoWinningSourceImpl
    ): LottoWinningSource
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    // UseCase 바인딩은 여기에 추가

    // 6/45 당첨 정보 조회 UseCase 바인딩
    @Binds
    @Singleton
    abstract fun bindGetStandardWinning(
        impl: LottoStandardWinningUseCaseImpl
    ): LottoStandardWinningUseCase

    // 연금 복권 당첨 정보 조회 UseCase 바인딩
    @Binds
    @Singleton
    abstract fun bindGetAnnuityWinning(
        impl: LottoAnnuityWinningUseCaseImpl
    ): LottoAnnuityWinningUseCase

    // 복권 데이터 저장 usecase 바인딩
    @Binds
    @Singleton
    abstract fun bindLottoSaveUseCase(
        impl: LottoSaveUseCaseImpl
    ): LottoSaveUseCase

    // 복권 데이터 설정 usecase 바인딩
    @Binds
    @Singleton
    abstract fun bindLottoEditUseCase(
        impl: LottoEditUseCaseImpl
    ): LottoEditUseCase

    // 복권 데이터 조회 usecase 바인딩
    @Binds
    @Singleton
    abstract fun bindLottoLoadUseCase(
        impl: LottoLoadUseCaseImpl
    ): LottoLoadUseCase

    // 복권 데이터 당첨 여부 조회 usecase 바인딩
    @Binds
    @Singleton
    abstract fun bindLottoCheckUseCase(
        impl: LottoCheckUseCaseImpl
    ): LottoCheckUseCase

}