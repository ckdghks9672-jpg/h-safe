package com.example.myapplication.dialer

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

data class DialResult(
    val launched: Boolean,
    val message: String
)

object ClaimDialer {
    fun dial(context: Context, phoneNumber: String): DialResult {
        val normalized = phoneNumber.filter { it.isDigit() || it == '+' }
        if (normalized.isBlank()) {
            return DialResult(false, "등록된 보험사 번호가 없습니다. 설정에서 번호를 확인하세요.")
        }
        return try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$normalized")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            DialResult(true, "전화 화면으로 번호를 전달했습니다. 발신을 확인하세요.")
        } catch (_: ActivityNotFoundException) {
            DialResult(
                false,
                "차량 전화 화면을 열 수 없습니다. 휴대폰에서 $phoneNumber 으로 직접 발신하세요."
            )
        } catch (t: Throwable) {
            DialResult(
                false,
                "전화 연결 실패: ${t.message ?: "알 수 없는 오류"}. 휴대폰에서 $phoneNumber 으로 직접 발신하세요."
            )
        }
    }
}
