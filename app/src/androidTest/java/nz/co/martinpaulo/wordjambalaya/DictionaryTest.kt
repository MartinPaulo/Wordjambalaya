package nz.co.martinpaulo.wordjambalaya

import junit.framework.TestCase
import nz.co.martinpaulo.wordjambalaya.Dictionary.Companion.buildDictionary

class DictionaryTest: TestCase() {
    private var dictionary: Dictionary? = null
    private var columns: MutableList<List<String>>? = null

    @Throws(Exception::class)
    override fun setUp() {
        dictionary = buildDictionary(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        columns = ArrayList()
    }

    fun testOrderCharacters() {
        // shouldn't have build entire dictionary to test this...
        // but we need to know if it works, as we are using it in our tests.
        assertEquals("abeggr", dictionary!!.orderCharacters("beggar"))
    }

    fun testOrderCharactersOfWords() {
        // shouldn't have build entire dictionary to test this...
        // but we need to know if it works, as we are using it in our tests.
        assertEquals("abeeggmnory",
            dictionary!!.orderCharactersOf(mutableListOf("beggar", "money")))
    }

    @Throws(Exception::class)
    fun testGetTwoCandidateWordSets() {
        val words = arrayOf("beggar", "thief")
        assertDictionaryFindsCandidateWordSets(words)
    }

    @Throws(Exception::class)
    fun testGetThreeCandidateWordSets() {
        val words = arrayOf("bag", "of", "bones")
        assertDictionaryFindsCandidateWordSets(words)
    }

    @Throws(Exception::class)
    fun testGetFourCandidateWordSets() {
        val words = arrayOf("broken", "down", "relics", "run")
        assertDictionaryFindsCandidateWordSets(words)
    }

    private fun addWordsToColumns(words: Array<String>) {
        for (word in words) {
            columns!!.add(getColumn(word))
        }
    }

    private fun getAnswer(vararg words: String): List<List<String>> {
        val result: MutableList<List<String>> = ArrayList()
        result.add(getColumn(*words))
        return result
    }

    private fun getColumn(vararg words: String): List<String> {
        val result: MutableList<String> = ArrayList()
        result.addAll(listOf(*words))
        return result
    }

    private fun assertDictionaryFindsCandidateWordSets(words: Array<String>) {
        addWordsToColumns(words)
        val muddledWordsWithOrderedCharacters =
            dictionary!!.orderCharactersOf(listOf(*words))
        val candidateAnswerSets = dictionary!!.getCandidateWordSets(
            muddledWordsWithOrderedCharacters,
            columns!!
        )
        assertEquals(getAnswer(*words), candidateAnswerSets)
    }

    
    fun findJumbledWords() {
    }

    
    fun orderCharactersOf() {
    }

    
    fun getCandidateWordSets() {
    }

    
    fun findPossibleAnswers() {
    }

    
    fun addAnswerChar() {
    }

    
    fun removeAnswerChar() {
    }

    
    fun getAnswerLength() {
    }

    
    fun buildDictionary() {
    }

    
    fun getInstance() {
    }
}