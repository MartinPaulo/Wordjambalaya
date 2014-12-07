package nz.co.martinpaulo.wordjambalaya;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DictionaryTest extends AndroidTestCase {

    private Dictionary dictionary;

    public void setUp() throws Exception {
        super.setUp();
        dictionary = Dictionary.buildDictionary(getContext());
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetCandidateWordSets() throws Exception {
        List<List<String>> columns = new ArrayList<List<String>>();
        columns.add(getColumn("bag"));
        columns.add(getColumn("of"));
        columns.add(getColumn("bones"));
        List<List<String>> result = dictionary.getCandidateWordSets("abbefgnoos", columns);
        assertEquals(getAnswer("bag", "of", "bones"), result);
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
