package com.jdw.random_lotto.common.util

// 상단 탭 모드
enum class LottoType(val label: String, val title: String) {
    STANDARD("Standard", "6/45 로또"),
    ANNUITY("Annuity", "연금복권"),;

    override fun toString(): String = label
}

// 하단 탭 모드
enum class BottomMode(val label: String) {
    VIEW("View"),
    ADD("Add");

    override fun toString(): String = label
}

// 회차 선택
enum class Segment {
    LAST,      // 지난주
    CURRENT   // 이번주
}