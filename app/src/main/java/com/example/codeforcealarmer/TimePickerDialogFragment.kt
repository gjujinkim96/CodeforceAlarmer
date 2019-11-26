package com.example.codeforcealarmer

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

class TimePickerDialogFragment(val clickedId: Int) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    var callChangedTime: ChangeTimeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callChangedTime = activity as? ChangeTimeListener
        if (callChangedTime == null){
            throw ClassCastException("$context must implement ChangeTimeListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val hour = arguments?.getInt("hour") ?: throw IllegalArgumentException("There is no \"hour\" in bundle")
        val min = arguments?.getInt("min") ?: throw IllegalArgumentException("There is no \"min\" in bundle")
        return TimePickerDialog(context, this, hour, min, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        callChangedTime?.onChangedTime(clickedId, hourOfDay, minute)
    }

    interface ChangeTimeListener {
        fun onChangedTime(clickedId: Int, hourOfDay: Int, minute: Int)
    }
}