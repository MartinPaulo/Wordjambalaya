package nz.co.martinpaulo.wordjambalaya

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import nz.co.martinpaulo.wordjambalaya.Dictionary.Companion.buildDictionary
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ActivityJambalaya : AppCompatActivity() {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jambalaya)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, FragmentUnknownWords())
                .add(R.id.container, FragmentAnswer())
                .commit()
        }
        val dialog = ProgressDialog.show(this, "", "Loading Dictionary...", true)
        executor.execute {
            buildDictionary(this@ActivityJambalaya)
            handler.post { dialog.dismiss() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.jambalaya, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) true else super.onOptionsItemSelected(item)
    }
}
