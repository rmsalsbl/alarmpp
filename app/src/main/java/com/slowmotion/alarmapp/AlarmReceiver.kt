package com.slowmotion.alarmapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.number.IntegerWidth
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Message
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        // TODO change value of const val TYPE_ONE_TIME & TYPE_REPEATING to Int
        //variabel konstanta dibawah digunakan untuk menentukan tipe alarm
        const val TYPE_ONE_TIME = 0
        const val TYPE_REPEATING = 1

        //variabel konstanta dibawah ini berfungsi untuk intent key
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"

        //Buat 2 ID untuk 2 Macam Alarm
        //ini digunakan sebagai ID untuk menampilkan notifikasi kepada pengguna
        private const val ID_ONETIME = 100
        private const val ID_REPEATING = 101

        //Berfungsi untuk mengatur format date dan time
        private const val DATE_FORMAT = "dd-MM-yyyy"
        private const val TIME_FORMAT = "HH:mm"
    }

    //Method yang akan memproses data dari Input yang masuk
    override fun onReceive(context: Context, intent: Intent) {
        // TODO Change getStringExtra() to be getIntExtra()
        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val title = if (type == TYPE_ONE_TIME) "One Time Alarm" else "Repeating Alarm"
        val notifId = if (type == TYPE_ONE_TIME) ID_ONETIME else ID_REPEATING

        if (message != null) {
            showAlarmNotification(context, title, message, notifId)
        }
    }

    // method untuk menampilkan notification umum dan getaran
    // method ini memanfaatkan fasilitas dari NotificationCompat
    private fun showAlarmNotification(
        context: Context,
        title: String,
        message: String,
        notifId: Int
    ) {
        val channelId = "Channel_1"
        val channelName = "AlarmManager channel"
        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_one_time)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(
                longArrayOf(
                    1000,
                    1000,
                    1000,
                    1000,
                    1000
                )
            )//mengatur tiap 1000Ms/ 1 detik getar-diam-getar-diam-diam
            .setSound(alarmSound)

        /*
            argumen dibawah merupakan komponen yang harus dibuat
            agar notifikasi dapat berjalan yang disebut dengan Channel,
            channel ini berfungsi mengatur prioritas, suara, maupun getaran
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }

    //Method(Obyek) untuk mengatur AlarmManager
    //Terdapat sebuah intent yang akan menjalankan AlarmReceiver dan membawa data berupa alarm dan pesan
    fun setOneTimeAlarm(context: Context, type: Int, date: String, time: String, message: String) {
        if (isDateInvalid(date, DATE_FORMAT) || isDateInvalid(time, TIME_FORMAT)) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //Intent ini akan menjalankan AlarmReceiver membawa data berupa alarm dan pesan
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)
        Log.e("ONE TIME", "$date $time")
        val dateArray = date.split("-").toTypedArray()
        val timeArray = time.split(":").toTypedArray()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]))
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
        /*
            mengapa pada code diatas data dikurangi 1 ?
            misal tanggal yang dimasukkan adalah 2022-06-27
            jika dipecah kita memproleh nilai 2022(tahun), 6(bulan), dan 27(hari)
            Masalahnya nilai bulan ke 6 pada kelas Calendar bukan Juni. ini karena
            Indexnya ini dimulai dari 0, untuk memperoleh
            bulan juni, maka nilai 6 tadi harus kita kurangi -1
        */
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]))
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        //PendingIntent akan dieksekusi ketika waktu alarm sama
        // dengan waktu pada sistem android
        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONETIME, intent, 0)

        /*
        Maksud dari baris ini adalah kita memasang alarm dengan tipe RTC_WAKEUP
        dimana tipe alarm ini dapat membangunkan perangkat
        jika dalam posisi sleep untuk menjalankan PendingIntent
        Jika kondisi sesuai makan BroadcastReceiver akan running dengan
        semua proses yang terdapat didala method onReceive()
        */
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Succes Set Up One Time Alarm", Toast.LENGTH_SHORT).show()
    }

    fun setRepeatingAlarm(
        context: Context,
        type: Int,
        time: String,
        message: String
    ) {
        if (isDateInvalid(time, TIME_FORMAT)) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //Intent ini akan menjalankan AlarmReceiver membawa data berupa alarm dan pesan
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        val putExtra = intent.putExtra(EXTRA_TYPE, type)
        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        //PendingIntent akan dieksekusi ketika waktu alarm sama
        // dengan waktu pada sistem android
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, "Succes Set Up Repeating Alarm", Toast.LENGTH_SHORT).show()
    }

    // TODO Cancel Alarm by type
    fun cancelAlarm(context: Context, type: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = when (type) {
            TYPE_ONE_TIME -> ID_ONETIME
            TYPE_REPEATING -> ID_REPEATING
            else -> Log.i("CancelAlarm", "cancelAlarm: Unknown type of alarm")
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        if (type == TYPE_ONE_TIME) {
            Toast.makeText(context, "Cancel One Time Alarm", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Cancel Repeating Alarm", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    Exception adalah suatu mekanisme yang digunakan untuk mendeskripsikan
    sesuatu yang harus dilakukan jika ada suatu kondisi yang tidak diinginkan terjadi
    keywoard try digunakan menjalankan block program kemudian mengidentifikasi
    dimana muncuknya kesalahan yang ingin diproses
    keyword catch berfungsi untuk menangkap bug yang terjadi dalam block try
    parse exception muncul saat kita mencoba mengubah string menjadi
    format data tanggal namun tidak terformat dengan benar
    */
    private fun isDateInvalid(date: String, format: String): Boolean {
        return try {
            val df = SimpleDateFormat(format, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e: ParseException) {
            true
        }
    }
}