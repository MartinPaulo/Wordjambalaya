package nz.co.martinpaulo.wordjambalaya;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DictionaryTest extends AndroidTestCase {

    private Dictionary dictionary;
    private List<List<String>> columns;

    public void setUp() throws Exception {
        super.setUp();
        dictionary = Dictionary.buildDictionary(getContext());
        columns = new ArrayList<List<String>>();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testOrderCharacters() {
        // shouldn't have build entire dictionary to test this...
        // but we need to know if it works, as we are using it in our tests.
        assertEquals("abeggr", dictionary.orderCharacters("beggar"));
    }

    public void testOrderCharactersOfWords() {
        // shouldn't have build entire dictionary to test this...
        // but we need to know if it works, as we are using it in our tests.
        assertEquals("abeeggmnory", dictionary.orderCharactersOf(Arrays.asList("beggar", "money")));
    }

    public void testGetTwoCandidateWordSets() throws Exception {
        String[] words = {"beggar", "thief"};
        assertDictionaryFindsCandidateWordSets(words);
    }

    public void testGetThreeCandidateWordSets() throws Exception {
        String[] words = {"bag", "of", "bones"};
        assertDictionaryFindsCandidateWordSets(words);
    }

    public void testGetFourCandidateWordSets() throws Exception {
        String[] words = {"broken", "down", "relics", "run"};
        assertDictionaryFindsCandidateWordSets(words);
    }

    private void assertDictionaryFindsCandidateWordSets(String[] words) {
        addWordsToColumns(words);
        String muddledWordsWithOrderedCharacters = dictionary.orderCharactersOf(Arrays.asList(words));
        List<List<String>> candidateAnswerSets = dictionary.getCandidateWordSets(muddledWordsWithOrderedCharacters, columns);
        assertEquals(getAnswer(words), candidateAnswerSets);
    }

    private void addWordsToColumns(String[] words) {
        for (String word : words) {
            columns.add(getColumn(word));
        }
    }

    private List<List<String>> getAnswer(String... words) {
        List<List<String>> result = new ArrayList<List<String>>();
        result.add(getColumn(words));
        return result;
    }

    private List<String> getColumn(String... words) {
        List<String> result = new ArrayList<String>();
        result.addAll(Arrays.asList(words));
        return result;
    }
}
