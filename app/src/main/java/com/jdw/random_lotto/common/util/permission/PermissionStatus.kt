package com.jdw.random_lotto.common.util.permission

enum class PermissionStatus {
    GRANTED,  // 허용된 상태
    DENIED,   // 요청 가능
    BLOCKED   // "다시 묻지 않음" 등으로 시스템 다이얼로그로는 더 이상 못 받는 상태
}
