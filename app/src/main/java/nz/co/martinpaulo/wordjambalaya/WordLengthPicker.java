package nz.co.martinpaulo.wordjambalaya;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A dialog that asks the user to enter the lengths of each of the answer words, if more than one.
 * Created by martin paulo on 1/08/2014.
 */
public class WordLengthPicker extends DialogFragment {

    public static final String EXTRA_WORD_SIZES = "WORD_SIZES";
    private static final String SUFFIXES[] = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};

    private int letterCount;
    private int wordCount;
    private final List<TextView> inputFields = new ArrayList<TextView>();

    public static WordLengthPicker newInstance(int wordCount, int letterCount) {
        WordLengthPicker result = new WordLengthPicker();
        result.letterCount = letterCount;
        result.wordCount = wordCount;
        return result;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.word_length_picker, null);
        for (int i = 0; i < wordCount; i++) {
            ((LinearLayout) view).addView(getEditBox(i), i);
        }
        TextView total = (TextView) view.findViewById(R.id.total_number_of_letters);
        total.setText(String.valueOf(letterCount));
        for (int i = 0; i < wordCount; i++) {
            if (i < wordCount - 1) {
                inputFields.get(i).addTextChangedListener(getCountUpdater(inputFields.get(wordCount - 1)));
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

    private View getEditBox(int position) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.word_length_edit_text, null);
        TextView text = (TextView) view.findViewById(R.id.number_of_letters_word_text);
        text.setText(String.format(text.getText().toString(), ordinal(position + 1)));
        TextView e = (TextView) view.findViewById(R.id.number_of_letters_word_view);
        e.setText(position == wordCount - 1 ? String.valueOf(letterCount) : "0");
        inputFields.add(e);
        return view;
    }

    private String ordinal(int i) {
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + SUFFIXES[i % 10];
        }
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
                if (wordLengthTotalEntered() != letterCount) {
                    // don't dismiss the dialog if the totals don't add up...
                    Toast.makeText(WordLengthPicker.this.getActivity(), R.string.correct_the_totals, Toast.LENGTH_LONG).show();
                } else {
                    sendResults(Activity.RESULT_OK);
                    alertDialog.dismiss();
                }
            }
        };
    }

    private int wordLengthTotalEntered() {
        int total = 0;
        for (TextView field : inputFields) {
            int value = Integer.parseInt(field.getText().toString());
            total = total + value;
        }
        return total;
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
                int lastValue = getIntegerValue(lastInputField.getText().toString());
                int totalEntered = getIntegerValue(editable.toString());
                lastInputField.setText(String.valueOf(lastValue - totalEntered));
            }

        };
    }

    private int getIntegerValue(String value) {
        return value.length() > 0 ? Integer.parseInt(value) : 0;
    }


    private Serializable getWordLengthsEntered() {
        ArrayList<Integer> wordLengths = new ArrayList<Integer>();
        for (TextView field : inputFields) {
            wordLengths.add(getIntegerValue(field.getText().toString()));
        }
        return wordLengths;
    }

    private void sendResults(int result) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_WORD_SIZES, getWordLengthsEntered());
        getTargetFragment().onActivityResult(getTargetRequestCode(), result, i);
    }
}
