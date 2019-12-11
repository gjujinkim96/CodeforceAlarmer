package com.example.codeforcealarmer

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import kotlin.IllegalArgumentException

class TimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private var clickedId: Int = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hour = arguments?.getInt("hour") ?: throw IllegalArgumentException("There is no \"hour\" in bundle")
        val min = arguments?.getInt("min") ?: throw IllegalArgumentException("There is no \"min\" in bundle")
        clickedId = arguments?.getInt("clicked_id") ?: throw IllegalArgumentException("There is no \"clicked_id\" in bundle")
        return TimePickerDialog(context, this, hour, min, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val beforeContestFragment = targetFragment as? BeforeContestFragment ?:
            throw IllegalArgumentException("This must be BeforeContestFragment")
        beforeContestFragment.changeTime(clickedId, hourOfDay, minute)
    }
}