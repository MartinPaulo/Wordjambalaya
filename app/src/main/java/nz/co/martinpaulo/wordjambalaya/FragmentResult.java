package nz.co.martinpaulo.wordjambalaya;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class FragmentResult extends ListFragment {

    public FragmentResult() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<List<String>> possibleAnswers = (List<List<String>>) getActivity().
                getIntent().getSerializableExtra(FragmentAnswer.RESULTS_AHOY);
        ResultAdapter ra = new ResultAdapter(possibleAnswers);
        setListAdapter(ra);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //https://code.google.com/p/android/issues/detail?id=21742
        // http://baroqueworksdev.blogspot.com.au/2012/02/how-to-use-listfragment.html
        //((TextView)getListView().getEmptyView()).setText("No results have been found :(");
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    private class ResultAdapter extends ArrayAdapter<List<String>> {
        public ResultAdapter(List<List<String>> possibleAnswers) {
            super(getActivity(), 0, possibleAnswers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_result, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.list_item_result_text);
            String answer = "<b>" + position + ":</b> ";

            List<String> possibleAnswer = getItem(position);
            for (String word : possibleAnswer) {
                answer = answer + word + " ";
            }
            tv.setText(Html.fromHtml(answer));
            return convertView;
        }


    }


}
