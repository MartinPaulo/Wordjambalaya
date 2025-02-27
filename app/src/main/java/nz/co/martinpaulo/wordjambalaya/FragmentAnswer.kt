package nz.co.martinpaulo.wordjambalaya

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import nz.co.martinpaulo.wordjambalaya.Dictionary.Companion.getInstance
import nz.co.martinpaulo.wordjambalaya.WordLengthPicker.Companion.newInstance
import java.util.Collections
import java.util.Objects

/**
 * Created by martin paulo on 30/07/2014.
 */
class FragmentAnswer : Fragment() {
    private var dialog: ProgressDialog? = null

    private inner class SolutionFinder :
        AsyncTask<List<Int?>?, Void?, List<List<String>>>() {

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(lists: List<List<String>>) {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }
            val i = Intent(activity, ActivityResult::class.java)
            i.putExtra(RESULTS_AHOY, ArrayList(lists))
            activity
            startActivity(i)
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg lists: List<Int?>?): List<List<String>> {
            activity
            return getInstance().findPossibleAnswers(lists[0])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_answer, container, false)
        val wordCountEditText = view.findViewById<EditText>(R.id.number_of_words_in_answer)
        val answerButton = view.findViewById<Button>(R.id.answer_button)
        answerButton.setOnClickListener { view1: View? ->
            val strEnteredVal = wordCountEditText.text.toString()
            if (strEnteredVal.isEmpty()) {
                Toast.makeText(
                    this@FragmentAnswer.activity,
                    R.string.please_enter_value,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val letterCount =
                getInstance().answerLength
            if (letterCount <= 0) {
                Toast.makeText(
                    this@FragmentAnswer.activity,
                    R.string.are_letters_selected,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val wordCount = strEnteredVal.toInt()
            if (wordCount > 1) {
                val fm = requireActivity().supportFragmentManager
                val wordLengthPicker = newInstance(wordCount, letterCount)
                wordLengthPicker.setTargetFragment(
                    this@FragmentAnswer,
                    WORD_LENGTH
                )
                wordLengthPicker.show(fm, "wordLengthPicker")
            } else {
                val singleWord: MutableList<Int> = ArrayList()
                singleWord.add(letterCount)
                dialog = ProgressDialog.show(
                    this@FragmentAnswer.activity,
                    "",
                    "Thinking...",
                    true
                )
                SolutionFinder().execute(singleWord)
            }
        }
        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == WORD_LENGTH) {
            dialog = ProgressDialog.show(this@FragmentAnswer.activity, "", "Thinking...", true)
            val sizes = Collections.unmodifiableList(
                Objects.requireNonNull(
                    data?.getSerializableExtra(WordLengthPicker.EXTRA_WORD_SIZES)
                ) as List<Int>
            )
            SolutionFinder().execute(sizes)
        }
    }

    companion object {
        const val RESULTS_AHOY: String = "RESULTS_AHOY"
        const val WORD_LENGTH: Int = 101
    }
}
