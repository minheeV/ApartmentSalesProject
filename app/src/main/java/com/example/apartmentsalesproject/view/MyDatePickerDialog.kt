package com.example.apartmentsalesproject.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.apartmentsalesproject.R
import java.util.Calendar

class MyDatePickerDialog : DialogFragment() {
    private var listener: DatePickerDialog.OnDateSetListener? = null
    private val MAX_YEAR = 2099
    private val MIN_YEAR = 1980

    private var cal = Calendar.getInstance()
    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    private var btnConfirm: Button? = null
    private var btnCancel: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val dialog: View = inflater.inflate(R.layout.dialog_datepicker, null).also {
            btnConfirm = it.findViewById(R.id.btn_confirm)
            btnCancel = it.findViewById(R.id.btn_cancel)
        }

        val monthPicker =
            dialog.findViewById<View>(R.id.monthpicker_datepicker) as NumberPicker
        val yearPicker =
            dialog.findViewById<View>(R.id.yearpicker_datepicker) as NumberPicker
        btnConfirm?.setOnClickListener {
            listener?.onDateSet(null, yearPicker.value, monthPicker.value, 0)
            dismiss()
        }
        btnCancel?.setOnClickListener {
            dismiss()
        }

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal[Calendar.MONTH] + 1
        val year = cal[Calendar.YEAR]
        yearPicker.minValue = MIN_YEAR
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year

        builder.setView(dialog)

        return builder.create()
    }
}