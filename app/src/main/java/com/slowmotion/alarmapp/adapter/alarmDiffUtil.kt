package com.slowmotion.alarmapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.slowmotion.alarmapp.room.alarm

class alarmDiffUtil(private val oldList: List<alarm>, private val newListL: List<alarm>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newListL.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newListL[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldList[oldItemPosition]
        val newData = newListL[newItemPosition]
        return oldData.id == newData.id
                && oldData.date == newData.date
                && oldData.time == newData.time
                && oldData.note == newData.note
                && oldData.type == newData.type

    }
}