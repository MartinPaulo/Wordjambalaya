package nz.co.martinpaulo.wordjambalaya;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * The fragment that shows the jumbled words
 */
public class FragmentUnknownWords extends Fragment {

    private static final String TAG = "WordFinder";
    public static final int NUMBER_OF_WORDS = 4;
    private UnknownWords unknownWords;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unknownWords = UnknownWords.getInstance(getActivity(), NUMBER_OF_WORDS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unknown_word, container, false);
        setRetainInstance(true);
        LinearLayout host = (LinearLayout) view.findViewById(R.id.unknown_word_layout);
        for (int i = 0; i < NUMBER_OF_WORDS; i++) {
            host.addView(getView(i));
        }
        return view;
    }

    public View getView(final int position) {
        View jumbledWordView = getActivity().getLayoutInflater().inflate(R.layout.list_item_jumbled_word, null);
        final LinearLayout ll = (LinearLayout) jumbledWordView.findViewById(R.id.word_drawing_holder);
        final EditText et = (EditText) jumbledWordView.findViewById(R.id.jumbled_word_edit_text);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // deliberately left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                final UnknownWord uw = unknownWords.getWords().get(position);
                uw.setJumbledWord(charSequence.toString());
                // need to remove any selected letters from the dictionary at this point....
                int childCount = ll.getChildCount();
                for (int k = 0; k < childCount; k++) {
                    final View childAt = ll.getChildAt(k);
                    if (childAt instanceof WordDrawingView) {
                        ((WordDrawingView) childAt).cleanUp();
                    }
                }
                ll.removeAllViews();

                if (uw.hasCandidateWords()) {
                    for (char character : uw.getCurrentCandidateWord().toCharArray()) {
                        final WordDrawingView wordDrawingView = new WordDrawingView(getActivity());
                        wordDrawingView.setCharacter(character);
                        ll.addView(wordDrawingView);
                    }
                    if (uw.isMoreThanOneCandidateWord()) {
                        // add right button
                        ImageButton right = new ImageButton(getActivity());
                        right.setImageResource(android.R.drawable.ic_media_next);
                        right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i(TAG, "Clicked!");
                                uw.next();
                                int k = 0;
                                for (char character : uw.getCurrentCandidateWord().toCharArray()) {
                                    WordDrawingView childAt = (WordDrawingView) ll.getChildAt(k);
                                    childAt.setCharacter(character);
                                    childAt.invalidate();
                                    k++;
                                }
                            }
                        });
                        ll.addView(right);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return jumbledWordView;
    }


}
