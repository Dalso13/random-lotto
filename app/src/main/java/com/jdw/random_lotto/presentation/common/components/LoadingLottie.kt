package com.jdw.random_lotto.presentation.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jdw.random_lotto.R

/**
 * 로딩용 로티 애니메이션
 * @param modifier Modifier
 */
@Composable
fun LoadingLottie(
    modifier: Modifier = Modifier
) {
    // raw 리소스 기준
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_lottie)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // 무한 반복
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}