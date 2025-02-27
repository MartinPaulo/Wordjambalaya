package nz.co.martinpaulo.wordjambalaya

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.ListFragment

class FragmentResult : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val possibleAnswers: List<List<String?>?>? =  @Suppress("DEPRECATION")  requireActivity().intent.getSerializableExtra(FragmentAnswer.RESULTS_AHOY) as List<List<String?>?>?
        val ra = ResultAdapter(possibleAnswers!!)
        listAdapter = ra
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //https://code.google.com/p/android/issues/detail?id=21742
        // http://baroqueworksdev.blogspot.com.au/2012/02/how-to-use-listfragment.html
        //((TextView)getListView().getEmptyView()).setText("No results have been found :(");
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    private inner class ResultAdapter(possibleAnswers: List<List<String?>?>) :
        ArrayAdapter<List<String?>?>(requireActivity(), 0, possibleAnswers) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                view =
                    requireActivity().layoutInflater.inflate(R.layout.list_item_result, null)
            }
            val tv = view!!.findViewById<TextView>(R.id.list_item_result_text)
            val answer = StringBuilder("<b>$position:</b> ")

            val possibleAnswer: List<String?> = checkNotNull(getItem(position))
            for (word in possibleAnswer) {
                answer.append(word).append(" ")
            }
            tv.text = HtmlCompat.fromHtml(answer.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            return view
        }
    }
}
