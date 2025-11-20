package com.jdw.random_lotto.presentation.common.util

import java.time.Instant
import java.time.LocalDate

fun Long.toMmDd(): String {
    val date = Instant.ofEpochMilli(this)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()

    val thisYear = LocalDate.now().year
    return if (date.year == thisYear) {
        "%02d/%02d".format(date.monthValue, date.dayOfMonth)
    } else {
        "%04d %02d/%02d".format(date.year, date.monthValue, date.dayOfMonth)
    }
}
