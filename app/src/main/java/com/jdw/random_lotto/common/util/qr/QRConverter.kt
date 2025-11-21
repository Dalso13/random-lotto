package com.jdw.random_lotto.common.util.qr

import androidx.core.net.toUri
import com.jdw.random_lotto.common.util.LottoType

object QRConverter {
    /**
     * QR 코드 결과값 컨버팅
     * @param rawResult - 디코딩된 문자열
     * @return 컨버팅된 문자열 (없으면 null)
     */
    fun convertQrResult(rawResult: String): Pair<LottoType, List<List<String>>?>? {
        val text = rawResult.trim()
        if (text.isEmpty()) return null

        return try {
            val uri = text.toUri()
            val host = uri.host ?: ""

            // 공통: v 파라미터 (동행복권 QR은 보통 v에 핵심 값이 들어감)
            val v = uri.getQueryParameter("v")

            when {
                // 6/45: http://m.dhlottery.co.kr/?v=... or http://qr.645lotto.net/?v=...
                (host == "m.dhlottery.co.kr" || host == "qr.645lotto.net") && !v.isNullOrBlank() -> {
                    LottoType.STANDARD to standardizeQrString(v)
                }

                // 연금복권: http://qr.dhlottery.co.kr/?v=pd1201761s858952
                host == "qr.dhlottery.co.kr" && !v.isNullOrBlank() -> {
                    LottoType.ANNUITY to aunnuityStandardizeQrString(v)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun standardizeQrString(qrString: String): List<List<String>>? {
        return try {
            // 회차(숫자들) + q/m... 구조이므로, 첫 q/m 전까지는 버리고 그 뒤만 사용
            val firstSepIdx = qrString.indexOfFirst { it == 'q' || it == 'm' }
            val body = if (firstSepIdx >= 0) qrString.substring(firstSepIdx) else qrString

            // q, m 기준으로 split → 각 게임 단위로 분리
            val tokens = body.split('q', 'm')
                .map { it.filter { ch -> ch.isDigit() } }
                .filter { it.length >= 12 }

            // 각 토큰에서 앞 12자리만 → 2자리씩 끊어서 번호 6개
            tokens.mapNotNull { digits ->
                val first12 = digits.take(12)
                if (first12.length < 12) return@mapNotNull null

                val nums = first12.chunked(2)
                    .mapNotNull { two ->
                        two.toIntOrNull()?.toString()
                    }

                if (nums.size == 6) nums else return@mapNotNull null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun aunnuityStandardizeQrString(qrParam: String): List<List<String>>? {
        return try {
            val raw = qrParam.trim()
            if (raw.isEmpty()) return emptyList()

            // (조)[s/S](6자리 번호) 패턴 모두 찾기
            // 예: "pd1201422s8529601s8529603s8529604s852960"
            val regex = Regex("""(\d+)[sS](\d{6})""")
            val matches = regex.findAll(raw)

            val result = matches.map { m ->
                val prefix = m.groupValues[1]
                val jo = prefix.last().toString()
                val num = m.groupValues[2]
                val numDigits = num.map { it.toString() }
                listOf(jo) + numDigits
            }.toList()

            result
        } catch (e: Exception) {
            null
        }
    }

}