package nz.co.martinpaulo.wordjambalaya

import android.content.Context
import android.os.Process
import android.util.Log
import nz.co.martinpaulo.wordjambalaya.R.raw
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Objects

/**
 * A singleton wrapper around the loaded word list.
 * The words are kept in a hashmap, keyed by the sorted list of characters in the words.
 * So, for example:
 * "abc" -> "cab"
 * "abd" -> "bad", "dab"
 * "abn" -> "ban", "nab"
 *
 */
class Dictionary private constructor() {
    private var sortedWords: HashMap<String, MutableSet<String>> = HashMap()

    private var isInitialized = false // has the dictionary been loaded?
    /** the list of characters selected to make up the riddle answer */
    private var riddleAnswerChars = ""

    private fun addWord(word: String) {
        val key = orderCharacters(word)
        val words = sortedWords[key]
        if (words == null) { // no entry for this set of characters
            // we use a set as the words being added on later runs might already be in the set
            sortedWords[key] = HashSet(listOf(word))
        } else {
            // add to the already existing set of words with matching ordered characters
            words.add(word)
        }
    }

    /**
     * Returns the word parameter with its characters sorted in alphabetic order.
     * For example: "hearty" -> "aehrty"
     * @param word the word whose characters are to be ordered
     * @return the input word, now with its characters sorted in alphabetic order
     */
    fun orderCharacters(word: String): String {
        return word.toCharArray().sorted().joinToString("")
    }

    /**
     * @param jumbledWord the jumbled word to decode.
     * @return the list of words that match the jumbled word (if any).
     */
    fun findJumbledWords(jumbledWord: String): List<String> {
        val answer: MutableList<String> = ArrayList()
        val candidates: Set<String>? = sortedWords[orderCharacters(jumbledWord)]
        if (candidates != null) {
            answer.addAll(candidates)
        }
        return answer
    }

    /**
     * @param length                            the length of the words that we are looking for
     * @param muddledWordsWithOrderedCharacters the set of muddled words, with the characters ordered.
     * @return the list of words of the given length that are a subset of the ordered characters
     */
    private fun getCandidateWords(
        length: Int,
        muddledWordsWithOrderedCharacters: String
    ): List<String> {
        val result: MutableList<String> = ArrayList()
        nextWord@ for (key in sortedWords.keys) {
            if (length == key.length) {
                for (element in key) {
                    if (muddledWordsWithOrderedCharacters.indexOf(element) == -1) {
                        continue@nextWord
                    }
                }
                result.addAll(Objects.requireNonNull<Set<String>>(sortedWords[key])
                )
            }
        }
        return result
    }

    /**
     *
     * @param wordLengths
     * @param muddledWordsWithOrderedCharacters
     * @return
     */
    private fun getPossibleAnswers(
        wordLengths: List<Int?>?,
        muddledWordsWithOrderedCharacters: String
    ): List<List<String>> {
        val columns: MutableList<List<String>> = ArrayList()
        if (wordLengths != null) {
            for (wordLength in wordLengths) {
                if (wordLength != null)
                    columns.add(getCandidateWords(wordLength, muddledWordsWithOrderedCharacters))
            }
        }
        return getCandidateWordSets(muddledWordsWithOrderedCharacters, columns)
    }

    /**
     *
     * @param words a list of words
     * @return the list of input words concatenated and then with their characters sorted
     */
    fun orderCharactersOf(words: List<String>): String {
        return orderCharacters(words.joinToString("") { it }.takeWhile { it.isLetter() })
    }

    /**
     *
     * @param muddledWordsWithOrderedCharacters
     * @param columns
     * @return
     */
    fun getCandidateWordSets(
        muddledWordsWithOrderedCharacters: String,
        columns: List<List<String>>
    ): List<List<String>> {
        val result = ArrayList<List<String>>()
        recursivelyFindCandidateWordSets(
            muddledWordsWithOrderedCharacters,
            columns,
            result,
            0,
            ArrayList()
        )
        return result
    }

    private fun recursivelyFindCandidateWordSets(
        muddledWordsWithOrderedCharacters: String,
        columns: List<List<String>>,
        result: MutableList<List<String>>,
        currentColumn: Int,
        candidateWordSet: List<String>
    ) {
        for (word in columns[currentColumn]) {
            val newCandidateWordSet: MutableList<String> = ArrayList(candidateWordSet)
            newCandidateWordSet.add(word)
            if (currentColumn >= columns.size - 1) {
                testAndAddToResultIfMatch(
                    result,
                    muddledWordsWithOrderedCharacters,
                    newCandidateWordSet
                )
            } else {
                recursivelyFindCandidateWordSets(
                    muddledWordsWithOrderedCharacters,
                    columns,
                    result,
                    currentColumn + 1,
                    newCandidateWordSet
                )
            }
        }
    }

    /**
     *
     * @param result
     * @param muddledWordsWithOrderedCharacters
     * @param candidateWords
     */
    private fun testAndAddToResultIfMatch(
        result: MutableList<List<String>>,
        muddledWordsWithOrderedCharacters: String,
        candidateWords: List<String>
    ) {
        val orderedWord = orderCharactersOf(candidateWords)
        if (orderedWord == muddledWordsWithOrderedCharacters) {
            result.add(candidateWords)
        }
    }


    fun findPossibleRiddleAnswers(wordLengths: List<Int?>?): List<List<String>> {
        Log.i(TAG, "Looking for " + (wordLengths?.size ?: 1) + " words formed from " + riddleAnswerChars)
        val orderedCharactersOfMuddledWords = orderCharacters(riddleAnswerChars)
        return getPossibleAnswers(wordLengths, orderedCharactersOfMuddledWords)
    }

    private fun readWordFile(ctx: Context, resId: Int) {
        val inputStream = ctx.resources.openRawResource(resId)
        val inputReader = InputStreamReader(inputStream)
        val bufferReader = BufferedReader(inputReader, 8192)
        var line: String
        try {
            while ((bufferReader.readLine().also { line = it }) != null) {
                addWord(line)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Could not read word resource.", e)
        }
    }

    /**
     * Add a character to the list of characters to be used to make the riddle answer.
     */
    fun addCharToRiddleAnswer(character: Char) {
        riddleAnswerChars += character
    }

    /**
     * Remove the first instance of the character from the list of characters to be used to make the
     * riddle  answer.
     */
    fun removeCharFromRiddleAnswer(character: Char) {
        riddleAnswerChars = riddleAnswerChars.replaceFirst(character.toString(), "")
    }

    /**
     * The length of the answer string so far
     */
    val riddleAnswerLength: Int
        get() = riddleAnswerChars.length

    companion object {
        private const val TAG = "Dictionary"
        private val instance = Dictionary()

        /**
         * Need to make sure that this not reloaded if the configuration changes...
         */
        @JvmStatic
        fun buildDictionary(ctx: Context): Dictionary {
            val raw = raw::class.java   // represents the raw folder
            var exceptionThrown = false // record if there were any errors reading the files
            for (file in raw.fields) {  // for each of the files in the raw folder...
                try {
                    instance.readWordFile(ctx, file.getInt(null))
                } catch (e: IllegalAccessException) {
                    exceptionThrown = true
                    Log.e(TAG, String.format("%s threw IllegalAccessException.", file.name)
                    )
                }
            }
            instance.isInitialized = !exceptionThrown
            return instance
        }

        @JvmStatic
        fun getInstance(): Dictionary {
            if (!instance.isInitialized) {
                // throw new exception here rather than exit?
                val pid = Process.myPid()
                Process.killProcess(pid)
            }
            return instance
        }
    }
}
