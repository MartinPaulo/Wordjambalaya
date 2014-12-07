package nz.co.martinpaulo.wordjambalaya;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lots of room for optimisation!
 */
public final class Dictionary {

    private static final String TAG = "Dictionary";

    HashMap<String, Set<String>> sortedWords = new HashMap<String, Set<String>>();

    private static Dictionary instance = new Dictionary();
    private boolean isInitialized;
    private String selectChars = "";

    private Dictionary() {
        // made private to force singleton pattern
    }

    private void addWord(final String word) {
        String key = orderCharacters(word);
        final Set<String> words = sortedWords.get(key);
        if (words == null) {
            sortedWords.put(key, new HashSet<String>(Arrays.asList(word)));
            return;
        }
        words.add(word);
    }

    private String orderCharacters(final String word) {
        char[] content = word.toCharArray();
        java.util.Arrays.sort(content);
        return new String(content);
    }

    /**
     * @param jumbledWord The jumbled word to decode.
     * @return the set of words that match the jumbled word.
     */
    public List<String> findJumbledWord(final String jumbledWord) {
        List<String> answer = new ArrayList<String>();
        final Set<String> candidate = sortedWords.get(orderCharacters(jumbledWord));
        if (candidate != null) {
            answer.addAll(candidate);
        }
        return answer;
    }

    /**
     * @param length the length of the words that we are looking for
     * @param muddledWordsWithOrderedCharacters the set of muddled words, with characters ordered.
     * @return the list of words of the given length that are a subset of the ordered characters
     */
    private List<String> getCandidateWords(int length, final String muddledWordsWithOrderedCharacters) {
        List<String> result = new ArrayList<String>();
        nextWord:
        for (String key : sortedWords.keySet()) {
            if (key.length() == length) {
                for (int i = 0; i < key.length(); i++) {
                    if (muddledWordsWithOrderedCharacters.indexOf(key.charAt(i)) <= -1) {
                        continue nextWord;
                    }
                }
                result.addAll(sortedWords.get(key));
            }
        }
        return result;
    }

    private List<List<String>> getPossibleAnswers(final List<Integer> wordLengths,
                                                  final String muddledWordsWithOrderedCharacters) {
        List<List<String>> columns = new ArrayList<List<String>>();
        for (int wordLength : wordLengths) {
            columns.add(getCandidateWords(wordLength, muddledWordsWithOrderedCharacters));
        }
        return getCandidateWordSets(muddledWordsWithOrderedCharacters, columns);
    }

    /**
     * Todo: recursion would handle more than three words...
     * @param muddledWordsWithOrderedCharacters
     * @param columns
     * @return
     */
    protected List<List<String>> getCandidateWordSets(String muddledWordsWithOrderedCharacters, List<List<String>> columns) {
        List<List<String>> result = new ArrayList<List<String>>();
        if (columns.size() == 1) {
            List<String> firstColumn = columns.get(0);
            for (String firstWord : firstColumn) {
                String orderedWord = orderCharacters(firstWord);
                if (orderedWord.equals(muddledWordsWithOrderedCharacters)) {
                    List<String> line = new ArrayList<String>();
                    line.add(firstWord);
                    result.add(line);
                }
            }
        } else if (columns.size() == 2) {
            List<String> firstColumn = columns.get(0);
            List<String> secondColumn = columns.get(1);
            for (String firstWord : firstColumn) {
                for (String secondWord : secondColumn) {
                    String testWord = firstWord + secondWord;
                    String orderedWord = orderCharacters(testWord);
                    if (orderedWord.equals(muddledWordsWithOrderedCharacters)) {
                        List<String> line = new ArrayList<String>();
                        line.add(firstWord);
                        line.add(secondWord);
                        result.add(line);
                    }
                }
            }
        } else if (columns.size() == 3) {
            List<String> firstColumn = columns.get(0);
            List<String> secondColumn = columns.get(1);
            List<String> thirdColumn = columns.get(2);
            for (String firstWord : firstColumn) {
                for (String secondWord : secondColumn) {
                    for (String thirdWord : thirdColumn) {
                        String testWord = firstWord + secondWord + thirdWord;
                        String orderedWord = orderCharacters(testWord);
                        if (orderedWord.equals(muddledWordsWithOrderedCharacters)) {
                            List<String> line = new ArrayList<String>();
                            line.add(firstWord);
                            line.add(secondWord);
                            line.add(thirdWord);
                            result.add(line);
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<List<String>> findPossibleAnswers(final List<Integer> wordLengths) {
        Log.i(TAG, "Looking for " + wordLengths.size() + " words formed from " + selectChars);
        final String orderedCharactersOfMuddledWords = orderCharacters(selectChars);
        return getPossibleAnswers(wordLengths, orderedCharactersOfMuddledWords);
    }

    /**
     * Need to make sure that this not reloaded if the configuration changes...
     * @param ctx
     * @return
     */
    static Dictionary buildDictionary(Context ctx) {
        Class raw = R.raw.class;
        Field[] fields = raw.getFields();
        for (Field field : fields) {
            try {
                instance.readRawTextFile(ctx, field.getInt(null));
            } catch (IllegalAccessException e) {
                Log.e(TAG, String.format("%s threw IllegalAccessException.",
                        field.getName()));
            }
        }
        instance.isInitialized = true;
        return instance;
    }

    public static Dictionary getInstance() {
        if (!instance.isInitialized) {
            // throw new exception here?
        }
        return instance;
    }

    private void readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufferReader = new BufferedReader(inputReader, 8192);
        String line;
        try {
            while ((line = bufferReader.readLine()) != null) {
                addWord(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not read word resource.", e);
        }
    }

    public void addSelectedChar(char character) {
        selectChars = selectChars + character;
    }

    public void removeSelectedChar(char character) {
        selectChars = selectChars.replaceFirst(String.valueOf(character), "");
    }

    public Integer getAnswerLength() {
        return selectChars.length();
    }
}
