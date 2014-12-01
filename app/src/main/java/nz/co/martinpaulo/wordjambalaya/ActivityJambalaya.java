package nz.co.martinpaulo.wordjambalaya;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class ActivityJambalaya extends ActionBarActivity {

    private ProgressDialog dialog;

    private class DictionaryLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Dictionary.buildDictionary(ActivityJambalaya.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jambalaya);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentUnknownWords())
                    .add(R.id.container, new FragmentAnswer())
                    .commit();
        }
        dialog = ProgressDialog.show(this, "", "Loading Dictionary...", true);
        new DictionaryLoader().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.jambalaya, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
