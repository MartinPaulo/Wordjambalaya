package nz.co.martinpaulo.wordjambalaya

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.io.Serializable
import java.util.Objects

/**
 * A dialog that asks the user to enter the lengths of each of the answer words, if more than one.
 * Created by martin paulo on 1/08/2014.
 */
class WordLengthPicker : DialogFragment() {
    private var letterCount = 0
    private var wordCount = 0
    private val inputFields: MutableList<TextView> = ArrayList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.word_length_picker, null)
        for (i in 0 until wordCount) {
            (view as LinearLayout).addView(getEditBox(i), i)
        }
        val total = view.findViewById<View>(R.id.total_number_of_letters) as TextView
        total.text = "$letterCount"
        for (i in 0 until wordCount) {
            if (i < wordCount - 1) {
                inputFields[i].addTextChangedListener(getCountUpdater(inputFields[wordCount - 1]))
            }
        }

        val alertDialog = AlertDialog.Builder(activity)
            .setView(view)
            .setTitle(R.string.dialog_word_length_title)
            .setPositiveButton(android.R.string.ok, null)
            .create()

        alertDialog.setOnShowListener(getOnShowListener(alertDialog))
        // request that the keyboard be shown when dialog is shown.
        Objects.requireNonNull(alertDialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return alertDialog
    }

    private fun getEditBox(position: Int): View {
        val view = requireActivity().layoutInflater.inflate(R.layout.word_length_edit_text, null)
        val text = view.findViewById<TextView>(R.id.number_of_letters_word_text)
        text.text = String.format(text.text.toString(), ordinal(position + 1))
        val e = view.findViewById<TextView>(R.id.number_of_letters_word_view)
        e.text = if (position == wordCount - 1) letterCount.toString() else "0"
        inputFields.add(e)
        return view
    }

    private fun ordinal(i: Int): String {
        return when (i % 100) {
            11, 12, 13 -> i.toString() + "th"
            else -> i.toString() + SUFFIXES[i % 10]
        }
    }

    private fun getOnShowListener(alertDialog: AlertDialog): OnShowListener {
        return OnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(getOnCloseListener(alertDialog))
        }
    }

    private fun getOnCloseListener(alertDialog: AlertDialog): View.OnClickListener {
        return View.OnClickListener {
            if (wordLengthTotalEntered() != letterCount) {
                // don't dismiss the dialog if the totals don't add up...
                Toast.makeText(
                    this@WordLengthPicker.activity,
                    R.string.correct_the_totals,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                sendResults()
                alertDialog.dismiss()
            }
        }
    }

    private fun wordLengthTotalEntered(): Int {
        var total = 0
        for (field in inputFields) {
            val value = field.text.toString().toInt()
            total = total + value
        }
        return total
    }

    private fun getCountUpdater(lastInputField: TextView): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                val lastValue = getIntegerValue(lastInputField.text.toString())
                val totalEntered = getIntegerValue(editable.toString())
                lastInputField.text = "${lastValue - totalEntered}"
            }
        }
    }

    private fun getIntegerValue(value: String): Int {
        return if (value.isNotEmpty()) value.toInt() else 0
    }


    private val wordLengthsEntered: Serializable
        get() {
            val wordLengths = ArrayList<Int>()
            for (inputField in inputFields)
                wordLengths.add(getIntegerValue(inputField.text.toString()))
            return wordLengths
        }

    private fun sendResults() {
        if (targetFragment == null) {
            return
        }
        val i = Intent()
        i.putExtra(EXTRA_WORD_SIZES, wordLengthsEntered)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
    }

    companion object {
        const val EXTRA_WORD_SIZES: String = "WORD_SIZES"
        private val SUFFIXES = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")

        @JvmStatic
        fun newInstance(wordCount: Int, letterCount: Int): WordLengthPicker {
            val result = WordLengthPicker()
            result.letterCount = letterCount
            result.wordCount = wordCount
            return result
        }
    }
}
