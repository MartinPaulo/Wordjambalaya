package nz.co.martinpaulo.wordjambalaya

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

/**
 * The fragment that shows the jumbled words
 */
class FragmentUnknownWords : Fragment() {
    private var unknownWords: UnknownWords? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unknownWords = UnknownWords.getInstance(NUMBER_OF_WORDS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_unknown_word, container, false)
        retainInstance = true
        val host = view.findViewById<LinearLayout>(R.id.unknown_word_layout)
        for (i in 0 until NUMBER_OF_WORDS) {
            host.addView(getView(i))
        }
        return view
    }

    private fun getView(position: Int): View {
        val jumbledWordView =
            requireActivity().layoutInflater.inflate(R.layout.list_item_jumbled_word, null)
        val ll = jumbledWordView.findViewById<LinearLayout>(R.id.word_drawing_holder)
        val et = jumbledWordView.findViewById<EditText>(R.id.jumbled_word_edit_text)
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                // deliberately left blank
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                val uw = unknownWords!!.words[position]
                uw.setJumbledWord(charSequence.toString())
                // need to remove any selected letters from the dictionary at this point....
                val childCount = ll.childCount
                for (k in 0 until childCount) {
                    val childAt = ll.getChildAt(k)
                    if (childAt is WordDrawingView) {
                        childAt.cleanUp()
                    }
                }
                ll.removeAllViews()

                if (uw.hasCandidateWords) {
                    for (character in uw.currentCandidateWord.toCharArray()) {
                        val wordDrawingView = WordDrawingView(activity)
                        wordDrawingView.setCharacter(character)
                        ll.addView(wordDrawingView)
                    }
                    if (uw.isMoreThanOneCandidateWord) {
                        // add right button
                        val right = getImageButton(uw)
                        ll.addView(right)
                    }
                }
            }

            fun getImageButton(uw: UnknownWord): ImageButton {
                val right = ImageButton(activity)
                right.setImageResource(android.R.drawable.ic_media_next)
                right.setOnClickListener {
                    Log.i(TAG, "Clicked!")
                    uw.next()
                    for ((k, character) in uw.currentCandidateWord.toCharArray().withIndex()) {
                        val childAt = ll.getChildAt(k) as WordDrawingView
                        childAt.setCharacter(character)
                        childAt.invalidate()
                    }
                }
                return right
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
        return jumbledWordView
    }


    companion object {
        private const val TAG = "WordFinder"
        const val NUMBER_OF_WORDS: Int = 4
    }
}
