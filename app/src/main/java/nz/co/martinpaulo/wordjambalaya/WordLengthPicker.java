package nz.co.martinpaulo.wordjambalaya;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin paulo on 1/08/2014.
 */
public class WordLengthPicker extends DialogFragment {

    public static final String EXTRA_WORD_SIZES = "WORD_SIZES";
    private static final String CLAUSES[] = {"st", "nd", "rd", "th" };

    private List<Integer> wordLengths;
    private int letterCount;
    private int wordCount;
    private final TextView[] inputFields = new TextView[4];

    public WordLengthPicker() {
        wordLengths = new ArrayList<Integer>();
    }

    public static WordLengthPicker newInstance(int wordCount, int letterCount) {
        WordLengthPicker result = new WordLengthPicker();
        result.letterCount = letterCount;
        result.wordCount = wordCount;
        return result;
    }

    /**
     * TODO: Make the input panels expand dynamically to match the number of words, rather than
     * this static collection.
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.word_length_picker, null);
        TextView total = (TextView) view.findViewById(R.id.total_number_of_letters);
        total.setText(String.valueOf(letterCount));

        LinearLayout[] inputPanels = new LinearLayout[4];
        inputPanels[0] = (LinearLayout) view.findViewById(R.id.number_of_letters_1st_word_wrapper);
        inputPanels[1] = (LinearLayout) view.findViewById(R.id.number_of_letters_2nd_word_wrapper);
        inputPanels[2] = (LinearLayout) view.findViewById(R.id.number_of_letters_3rd_word_wrapper);
        inputPanels[3] = (LinearLayout) view.findViewById(R.id.number_of_letters_4th_word_wrapper);
        //inputPanels[4] = new LinearLayout(self);
        for (int i = 0; i < inputPanels.length; i++) {
            inputPanels[i].setVisibility(i < wordCount ? View.VISIBLE : View.INVISIBLE);
        }
        inputFields[0] = (TextView) view.findViewById(R.id.number_of_letters_1st_word);
        inputFields[1] = (TextView) view.findViewById(R.id.number_of_letters_2nd_word);
        inputFields[2] = (TextView) view.findViewById(R.id.number_of_letters_3rd_word);
        inputFields[3] = (TextView) view.findViewById(R.id.number_of_letters_4th_word);
        for (int i = 0; i < wordCount; i++) {
            if (i == wordCount - 1) {
                inputFields[i].setText(String.valueOf(letterCount));
            } else {
                inputFields[i].setText("0");
                inputFields[i].addTextChangedListener(getCountUpdater(inputFields[wordCount - 1]));
            }
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.dialog_word_length_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        alertDialog.setOnShowListener(getOnShowListener(alertDialog));
        // request that the keyboard be shown when dialog is shown.
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return alertDialog;
    }

    private DialogInterface.OnShowListener getOnShowListener(final AlertDialog alertDialog) {
        return new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(getOnCloseListener(alertDialog));
            }
        };
    }

    private View.OnClickListener getOnCloseListener(final AlertDialog alertDialog) {
        return new View.OnClickListener() {

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
                } else {
                    wordLengths.clear();
                    for (int i = 0; i < wordCount; i++) {
                        wordLengths.add(Integer.parseInt(inputFields[i].getText().toString()));
                    }
                    sendResults(Activity.RESULT_OK);
                    alertDialog.dismiss();
                }
            }
        };
    }

    private TextWatcher getCountUpdater(final TextView lastInputField) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int lastValue = Integer.parseInt(lastInputField.getText().toString());
                int totalEntered = Integer.parseInt(editable.toString());
                lastInputField.setText(String.valueOf(lastValue - totalEntered));
            }

        };
    }

    private void sendResults(int result) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_WORD_SIZES, new ArrayList<Integer>(wordLengths));
        getTargetFragment().onActivityResult(getTargetRequestCode(), result, i);
    }
}
