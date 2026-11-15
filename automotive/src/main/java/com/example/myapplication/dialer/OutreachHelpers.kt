package com.example.myapplication.dialer

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

data class OutreachResult(
    val launched: Boolean,
    val message: String
)

object OutreachHelpers {
    fun sendSms(context: Context, number: String, body: String): OutreachResult {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$number")
                putExtra("sms_body", body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            OutreachResult(true, "메시지 작성 화면을 열었습니다. 내용을 확인한 뒤 전송하세요.")
        } catch (_: ActivityNotFoundException) {
            OutreachResult(false, "메시지 앱을 열 수 없습니다. 아래 요약을 복사해 119로 보내세요.\n\n$body")
        } catch (t: Throwable) {
            OutreachResult(false, "메시지 실행 실패: ${t.message}\n\n$body")
        }
    }

    fun shareText(context: Context, title: String, body: String): OutreachResult {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            OutreachResult(true, "공유 화면을 열었습니다.")
        } catch (t: Throwable) {
            OutreachResult(false, "공유를 열 수 없습니다: ${t.message}")
        }
    }
}
