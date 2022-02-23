package com.slowmotion.alarmapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.slowmotion.alarmapp.R
import com.slowmotion.alarmapp.room.alarm
import com.slowmotion.alarmapp.AlarmReceiver
import kotlinx.android.synthetic.main.your_alarm.view.*
import kotlinx.android.synthetic.main.your_alarm.view.*

class AlarmAdapter() :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    var alarms = emptyList<alarm>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlarmViewHolder {
        return AlarmViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.your_alarm, parent, false)
        )
    }

    class AlarmViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    fun setData(list: List<alarm>){
        val alarmDiffUtil = alarmDiffUtil(alarms, list)
        val alarmDiffUtilResult = DiffUtil.calculateDiff(alarmDiffUtil)
        this.alarms = list
        alarmDiffUtilResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.view.item_time_alarm.text = alarm.time
        holder.view.item_note_alarm.text = alarm.note

        when (alarm.type) {
            0 -> holder.view.img_time.loadImageDrawable(holder.view.context, R.drawable.ic_bi_clock_history)
            1 -> holder.view.img_time.loadImageDrawable(holder.view.context, R.drawable.ic_ph_clock_clockwise)
        }
    }

    override fun getItemCount() = alarms.size

    private fun ImageView.loadImageDrawable(context: Context, drawable: Int){
        Glide.with(context).load(drawable).into(this)
    }

}