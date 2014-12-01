package nz.co.martinpaulo.wordjambalaya;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martinpaulo on 1/08/2014.
 */
public class WordLengthPicker extends DialogFragment {

    public static final String EXTRA_WORD_SIZES = "WORD_SIZES";
    public static final String LETTER_COUNT = "LETTER_COUNT";
    public static final String WORD_COUNT = "WORD_COUNT";
    private List<Integer> wordLengths;

    public WordLengthPicker() {
        wordLengths = new ArrayList<Integer>();
    }

    public static WordLengthPicker newInstance(int wordCount, int letterCount) {
        Bundle args = new Bundle();
        args.putInt(WORD_COUNT, wordCount);
        args.putInt(LETTER_COUNT, letterCount);
        WordLengthPicker result = new WordLengthPicker();
        result.setArguments(args);
        return result;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int letterCount = getArguments().getInt(LETTER_COUNT);
        final int wordCount = getArguments().getInt(WORD_COUNT);
        View view = getActivity().getLayoutInflater().inflate(R.layout.word_length_picker, null);
        TextView total = (TextView) view.findViewById(R.id.total_number_of_letters);
        total.setText(String.valueOf(letterCount));

        LinearLayout[] inputPanels = new LinearLayout[4];
        inputPanels[0] = (LinearLayout) view.findViewById(R.id.number_of_letters_1st_word_wrapper);
        inputPanels[1] = (LinearLayout) view.findViewById(R.id.number_of_letters_2nd_word_wrapper);
        inputPanels[2] = (LinearLayout) view.findViewById(R.id.number_of_letters_3rd_word_wrapper);
        inputPanels[3] = (LinearLayout) view.findViewById(R.id.number_of_letters_4th_word_wrapper);
        for (int i = 0; i < inputPanels.length; i++) {
            inputPanels[i].setVisibility(i < wordCount ? View.VISIBLE : View.INVISIBLE);
        }
        final TextView[] inputFields = new TextView[4];
        inputFields[0] = (TextView) view.findViewById(R.id.number_of_letters_1st_word);
        inputFields[1] = (TextView) view.findViewById(R.id.number_of_letters_2nd_word);
        inputFields[2] = (TextView) view.findViewById(R.id.number_of_letters_3rd_word);
        inputFields[3] = (TextView) view.findViewById(R.id.number_of_letters_4th_word);
        for (int i = 0; i < inputFields.length; i++) {
            inputFields[i].setText("0");
        }


        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.dialog_word_length_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int total = 0;
                        for (int i = 0; i < wordCount; i++) {
                            int value = Integer.parseInt(inputFields[i].getText().toString());
                            total = total + value;
                        }
                        // don't dismiss the dialog if the totals add up...
                        if (total != letterCount) {
                            Toast.makeText(WordLengthPicker.this.getActivity(), R.string.correct_the_totals, Toast.LENGTH_LONG).show();
                            return;
                        }
                        wordLengths.clear();
                        for (int i = 0; i < wordCount; i++) {
                            wordLengths.add(Integer.parseInt(inputFields[i].getText().toString()));
                        }
                        sendResults(Activity.RESULT_OK);
                        alertDialog.dismiss();
                    }
                });
            }
        });
        return alertDialog;
    }

    private void sendResults(int result) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_WORD_SIZES, new ArrayList(wordLengths));
        getTargetFragment().onActivityResult(getTargetRequestCode(), result, i);
    }
}
