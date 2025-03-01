package nz.co.martinpaulo.wordjambalaya

import junit.framework.TestCase
import nz.co.martinpaulo.wordjambalaya.Dictionary.Companion.buildDictionary

class DictionaryTest: TestCase() {
    private var dictionary: Dictionary? = null // The dictionary instance being tested

    @Throws(Exception::class)
    override fun setUp() {
        dictionary =
            buildDictionary(androidx.test.core.app.ApplicationProvider.getApplicationContext())
    }

    fun testOrderCharacters() {
        // shouldn't have build entire dictionary to test this...
        // but we need to know if it works, as we are using it in our tests.
        assertEquals("abeggr", dictionary!!.orderCharacters("beggar"))
    }

    fun testOrderCharactersOfWords() {
        // shouldn't have build entire dictionary to test this...
        // but we need to know if it works, as we are using it in our tests.
        assertEquals(
            "abeeggmnory",
            dictionary!!.orderCharactersOf(listOf("beggar", "money"))
        )
    }

    fun testGetTwoCandidateWordSets() {
        assertDictionaryFindsCandidateWordSets(arrayOf("beggar", "thief"))
    }

    fun testGetThreeCandidateWordSets() {
        assertDictionaryFindsCandidateWordSets(arrayOf("bag", "of", "bones"))
    }

    fun testGetFourCandidateWordSets() {
        assertDictionaryFindsCandidateWordSets(arrayOf("broken", "down", "relics", "run"))
    }

    private fun getAnswer(vararg words: String): List<List<String>> {
        return arrayListOf(arrayListOf(*words))
    }

    private fun assertDictionaryFindsCandidateWordSets(words: Array<String>) {
        val columns: MutableList<List<String>> = ArrayList()
        for (word in words) {
            columns.add(arrayListOf(word))
        }
        val muddledWordsWithOrderedCharacters = dictionary!!.orderCharactersOf(listOf(*words))
        val candidateAnswerSets = dictionary!!.getCandidateWordSets(
            muddledWordsWithOrderedCharacters, columns
        )
        assertEquals(getAnswer(*words), candidateAnswerSets)
    }


    fun testFindsJumbledWords() {
        val orderedCharacters = dictionary!!.orderCharactersOf(mutableListOf("bin"))
        assertEquals(listOf("bin", "nib"), dictionary?.findJumbledWords(orderedCharacters))
    }


    fun testOrdersCharactersOf() {
        assertEquals("beeehllo", dictionary?.orderCharactersOf(listOf("hello", "bee")))
    }


    fun testAddAndRemoveRiddleAnswerChar() {
        dictionary?.addCharToRiddleAnswer('a')
        dictionary?.let { assertEquals(1, it.riddleAnswerLength) }
        dictionary?.removeCharFromRiddleAnswer('a')
        dictionary?.let { assertEquals(0, it.riddleAnswerLength) }
    }

}