package com.jdw.random_lotto.data.lotto.winning

import com.google.gson.Gson
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.data.lotto.winning.dto.AnnuityWinningDto
import com.jdw.random_lotto.data.lotto.winning.dto.StandardWinningDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton


interface LottoWinningSource{
    suspend fun fetchStandard(round: Int): LottoResult<StandardWinningDto>
    suspend fun fetchAnnuity(): LottoResult<AnnuityWinningDto>
}

@Singleton
class LottoWinningSourceImpl @Inject constructor() : LottoWinningSource {
    private val gson = Gson()

    override suspend fun fetchStandard(round: Int): LottoResult<StandardWinningDto> {
        val url = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=$round"
        return try {
            val response = fetch(url)
            val dto: StandardWinningDto = gson.fromJson(response, StandardWinningDto::class.java)

            if (!dto.returnValue.equals("success", ignoreCase = true)) {
                LottoResult.Fail("round=$round returnValue=${dto.returnValue}")
            } else {
                LottoResult.Success(dto)
            }
        } catch (e: Exception) {
            LottoResult.Fail("round=$round parse/network failed: ${e.message}", e)
        }
    }


    override suspend fun fetchAnnuity(): LottoResult<AnnuityWinningDto> {
        val url = "https://www.dhlottery.co.kr/common.do?method=get720Number"
        val response = fetch(url)
        val dto = gson.fromJson(response, AnnuityWinningDto::class.java)
        return LottoResult.Success(dto)
    }

    private suspend fun fetch(url: String, timeoutMs: Int = 5000, headers: Map<String, String> = emptyMap()): String =
        withContext(Dispatchers.IO) {
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = timeoutMs
                readTimeout = timeoutMs
                doInput = true
                setRequestProperty("User-Agent", "JDW/1.0")
                headers.forEach { (k, v) -> setRequestProperty(k, v) }
            }
            try {
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val text = BufferedInputStream(stream).bufferedReader(Charsets.UTF_8).use { it.readText() }
                if (code !in 200..299) throw IOException("HTTP $code\n$text")
                text
            } finally {
                try { conn.disconnect() } catch (_: Throwable) {}
            }
        }
}