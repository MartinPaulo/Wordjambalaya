package nz.co.martinpaulo.wordjambalaya;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by martin paulo on 29/07/2014.
 */
public class UnknownWords {

    private static UnknownWords instance;
    private Context context;


    private ArrayList<UnknownWord> unknownWords;

    private UnknownWords(Context context) {
        this.context = context;
        unknownWords = new ArrayList<UnknownWord>();
    }

    public static UnknownWords getInstance(Context context, int numberOfWords) {
        if (instance == null) {
            instance = new UnknownWords(context);
        }
        for (int i = instance.getWords().size(); i < numberOfWords; i++) {
            final UnknownWord word = new UnknownWord();
            instance.unknownWords.add(word);
        }
        return instance;
    }

    public ArrayList<UnknownWord> getWords() {
        return unknownWords;
    }
}
