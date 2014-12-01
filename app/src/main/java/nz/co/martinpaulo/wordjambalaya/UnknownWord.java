package nz.co.martinpaulo.wordjambalaya;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martinpaulo on 29/07/2014.
 */
public class UnknownWord {

    private static final String TAG = "UnknownWord";
    private static int counter;

    private int id;
    private String jumbledWord;
    private ArrayList<String> candidateWords = new ArrayList<String>();
    private int currentWord;

    public UnknownWord() {
        id = counter++;
        currentWord = 0;
    }

    public String getJumbledWord() {
        return jumbledWord;
    }

    private ArrayList<String> getCandidateWords() {
        return candidateWords;
    }

    public String getCurrentCandidateWord() {
        String result = "";
        if (candidateWords.size() > 0) {
            result = candidateWords.get(currentWord);
        }
        return result;
    }

    public void setJumbledWord(String jumbledWord) {
        this.jumbledWord = jumbledWord;
        candidateWords.clear();
        currentWord = 0;
        List<String> words = Dictionary.getInstance().findJumbledWord(jumbledWord);
        if (words.size() > 0) {
            candidateWords.addAll(words);
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return jumbledWord;
    }

    public boolean hasCandidateWords() {
        return candidateWords.size() > 0;
    }

    public boolean isMoreThanOneCandidateWord() {
        return candidateWords.size() > 1;
    }

    public void next() {
        currentWord = currentWord + 1;
        if (currentWord >= candidateWords.size()) {
            currentWord = 0;
        }
        Log.i(TAG, "Current word: " + currentWord);
    }
}
