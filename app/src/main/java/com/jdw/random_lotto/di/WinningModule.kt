package com.jdw.random_lotto.di

import com.jdw.random_lotto.data.lotto.winning.LottoWinningRepo
import com.jdw.random_lotto.data.lotto.winning.LottoWinningRepoImpl
import com.jdw.random_lotto.data.lotto.winning.LottoWinningSource
import com.jdw.random_lotto.data.lotto.winning.LottoWinningSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
