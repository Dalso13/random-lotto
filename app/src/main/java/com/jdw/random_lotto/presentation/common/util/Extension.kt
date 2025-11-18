package com.jdw.random_lotto.presentation.common.util

fun Long.toMmDd(): String = java.time.Instant.ofEpochMilli(this)
    .atZone(java.time.ZoneId.systemDefault())
    .toLocalDate()
    .let { "%02d/%02d".format(it.monthValue, it.dayOfMonth) }