package com.jdw.random_lotto.di

import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoConvertUseCase
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoConvertUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoEditUseCase
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoEditUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoSaveUseCase
import com.jdw.random_lotto.domain.lotto.useCase.edit.LottoSaveUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.history.LottoHistoryLoadUseCase
import com.jdw.random_lotto.domain.lotto.useCase.history.LottoHistoryLoadUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.history.LottoHistorySaveUseCase
import com.jdw.random_lotto.domain.lotto.useCase.history.LottoHistorySaveUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoAnnuityWinningUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoAnnuityWinningUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoCheckUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoCheckUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoLoadUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoLoadUseCaseImpl
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoStandardWinningUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoStandardWinningUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

    // QR 코드 변환 usecase 바인딩
    @Binds
    @Singleton
    abstract fun bindLottoConvertUseCase(
        impl: LottoConvertUseCaseImpl
    ): LottoConvertUseCase

    // 로또 당첨 이력 조회 usecase 바인딩
    @Binds
    @Singleton
    abstract fun bindLottoHistoryLoadUseCase(
        impl: LottoHistoryLoadUseCaseImpl
    ): LottoHistoryLoadUseCase

    // 로또 당첨 이력 저장 usecase 바인딩
    @Binds
    @Singleton
    abstract fun bindLottoHistorySaveUseCase(
        impl: LottoHistorySaveUseCaseImpl
    ): LottoHistorySaveUseCase
}