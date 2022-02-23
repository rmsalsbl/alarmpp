package com.slowmotion.alarmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.slowmotion.alarmapp.databinding.ActivityMainBinding
import com.slowmotion.alarmapp.fragment.DatePickerFragment
import com.slowmotion.alarmapp.fragment.TimePickerFragment
import com.slowmotion.alarmapp.room.alarm
import com.slowmotion.alarmapp.room.alarmDB
import kotlinx.android.synthetic.main.one_time_alarm_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {

    private var binding: ActivityMainBinding? = null
    private lateinit var alarmReceiver: AlarmReceiver
    val db by lazy { alarmDB(this) }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.one_time_alarm_activity)

        btn_set_date_one_time.setOnClickListener(this)
        btn_set_time_one_time.setOnClickListener(this)
        btn_add_set_alarm.setOnClickListener(this)
        btn_cancel_set_one_time_alarm.setOnClickListener(this)

        alarmReceiver = AlarmReceiver()
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.btn_set_date_one_time -> {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
            R.id.btn_set_time_one_time -> {
                val timePickerFragmentOneTime = TimePickerFragment()
                timePickerFragmentOneTime.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
            }
            R.id.btn_add_set_alarm -> {
                val onceDate = tv_one_date.text.toString()
                val onceTime = tv_one_time.text.toString()
                val onceMessage = et_note_one_time.text.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().AddAlarm(
                        alarm(0, onceTime, onceDate, onceMessage, AlarmReceiver.TYPE_ONE_TIME)
                    )
                    finish()
                }

                alarmReceiver.setOneTimeAlarm(
                    this, AlarmReceiver.TYPE_ONE_TIME,
                    onceDate,
                    onceTime,
                    onceMessage
                )
            }
            R.id.btn_cancel_set_one_time_alarm -> {
                onBackPressed()
                finish()
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormatOneTime = SimpleDateFormat("dd-MM-yyyyy", Locale.getDefault())

        //set text dari textview once
        tv_one_date.text = dateFormatOneTime.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormatOneTime = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            TIME_PICKER_ONCE_TAG -> tv_one_time.text = timeFormatOneTime.format(calendar.time)
            else -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}