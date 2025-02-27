package nz.co.martinpaulo.wordjambalaya

import android.util.Log

/**
 * Represents a jumbled word in the list of words.
 * Created by martin paulo on 29/07/2014.
 */
class UnknownWord {
//    val id: Int = counter++
    private var jumbledWord: String? = null
    private val candidateWords = ArrayList<String>()
    private var currentWord = 0

    val currentCandidateWord: String
        get() = if (candidateWords.isNotEmpty()) candidateWords[currentWord] else ""

    fun setJumbledWord(jumbledWord: String?) {
        this.jumbledWord = jumbledWord
        candidateWords.clear()  // no word has yet been found
        currentWord = 0
        val words = jumbledWord?.let { Dictionary.getInstance().findJumbledWords(it) }
        if (words != null) {
            if (words.isNotEmpty()) {
                candidateWords.addAll(words)
            }
        }
    }

    override fun toString(): String {
        return jumbledWord!!
    }

    /**
     * Will be true if there are candidate found words, false otherwise
     */
    val hasCandidateWords: Boolean
        get() = candidateWords.isNotEmpty()


    /**
     * Will be true if more than one candidate word was found, false otherwise
     */
    val isMoreThanOneCandidateWord: Boolean
        get() = candidateWords.size > 1

    fun next() {
        currentWord += 1
        if (currentWord >= candidateWords.size) {
            currentWord = 0
        }
        Log.i(TAG, "Current word: $currentWord")
    }

    companion object {
        private const val TAG = "UnknownWord"
//        private var counter = 0
    }
}
