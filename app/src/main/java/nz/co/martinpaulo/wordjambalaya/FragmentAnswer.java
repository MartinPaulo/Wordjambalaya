package nz.co.martinpaulo.wordjambalaya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martinpaulo on 30/07/2014.
 */
public class FragmentAnswer extends Fragment {

    private static final String TAG = "AnswerFragment";
    public static final String RESULTS_AHOY = "RESULTS_AHOY";
    public static final int WORD_LENGTH = 101;
    private ProgressDialog dialog;

    private class SolutionFinder extends AsyncTask<List<Integer>, Void, List<List<String>>> {

        @Override
        protected List<List<String>> doInBackground(List<Integer>... lists) {
            ActivityJambalaya activity = (ActivityJambalaya) getActivity();
            return Dictionary.getInstance().findPossibleAnswers(lists[0]);
        }

        @Override
        protected void onPostExecute(List<List<String>> lists) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            Intent i = new Intent(getActivity(), ActivityResult.class);
            i.putExtra(RESULTS_AHOY, new ArrayList(lists));
            ActivityJambalaya activity = (ActivityJambalaya) getActivity();
            startActivity(i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer, container, false);
        final EditText wordCountEditText = (EditText) view.findViewById(R.id.number_of_words_in_answer);
        Button answerButton = (Button) view.findViewById(R.id.answer_button);
        answerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String strEnteredVal = wordCountEditText.getText().toString();
                if ("".equals(strEnteredVal)) {
                    Toast.makeText(FragmentAnswer.this.getActivity(), R.string.please_enter_value, Toast.LENGTH_LONG).show();
                    return;
                }
                int letterCount = Dictionary.getInstance().getAnswerLength();
                if (letterCount <= 0) {
                    Toast.makeText(FragmentAnswer.this.getActivity(), R.string.are_letters_selected, Toast.LENGTH_LONG).show();
                    return;
                }
                int wordCount = Integer.parseInt(strEnteredVal);
                if (wordCount > 1) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    WordLengthPicker wordLengthPicker = WordLengthPicker.newInstance(wordCount, letterCount);
                    wordLengthPicker.setTargetFragment(FragmentAnswer.this, WORD_LENGTH);
                    wordLengthPicker.show(fm, "wordLengthPicker");
                } else {
                    List<Integer> singleWord = new ArrayList<Integer>();
                    singleWord.add(letterCount);
                    dialog = ProgressDialog.show(FragmentAnswer.this.getActivity(), "", "Thinking...", true);
                    new SolutionFinder().execute(singleWord);
                }
            }

        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == WORD_LENGTH) {
            dialog = ProgressDialog.show(FragmentAnswer.this.getActivity(), "", "Thinking...", true);
            List<Integer> sizes = (List<Integer>) data.getSerializableExtra(WordLengthPicker.EXTRA_WORD_SIZES);
            new SolutionFinder().execute(sizes);
        }
    }
}
