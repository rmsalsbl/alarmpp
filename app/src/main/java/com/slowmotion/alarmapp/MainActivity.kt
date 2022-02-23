package com.slowmotion.alarmapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slowmotion.alarmapp.adapter.AlarmAdapter
import com.slowmotion.alarmapp.room.alarmDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmReceiver: AlarmReceiver
    val db by lazy { alarmDB(this) }

    override fun onResume() {
        super.onResume()
        db.alarmDao().getAlarm().observe(this@MainActivity) {
            alarmAdapter.setData(it)
            Log.d("MainActivity", "dbresponse: $it")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alarmReceiver = AlarmReceiver()

        initTimeToday()
        initDateToday()
        initAlarmType()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        alarmAdapter = AlarmAdapter()
        rv_reminder_alarm.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alarmAdapter
            swipeToDelete(this)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView?) {
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val typeOfAlarm = alarmAdapter.alarms[viewHolder.adapterPosition].type
                alarmReceiver.cancelAlarm(this@MainActivity, typeOfAlarm)

                val deleteTime = alarmAdapter.alarms[viewHolder.adapterPosition]

                //delete item
                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().deleteAlarm(deleteTime)
                }
                alarmAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(applicationContext, "Success Delete Alarm", Toast.LENGTH_LONG).show()
            }
        }).attachToRecyclerView(recyclerView)

    }




    private fun initAlarmType() {
        cl_one_time.setOnClickListener {
            startActivity(Intent(this, OneTimeAlarmActivity::class.java))
        }

        cl_repeating.setOnClickListener {
            startActivity(Intent(this, RepeatingAlarmActivity::class.java))
        }
    }

    private fun initDateToday() {
        val dateNow: Date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault())
        val formattedDate: String = dateFormat.format(dateNow)

        tv_wed_09_september.text = formattedDate
    }

    private fun initTimeToday() {
        val timeNow = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm")
        val formattedTime = timeFormat.format(timeNow.time)

        tv_09_10.text = formattedTime
    }
}