package ru.kserditov.simplemp3player;

import java.io.File;
import java.util.Random;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	final String TAG = "SimpleMp3Player";
	final File path = new File("/storage/extSdCard/Music");
	final Random r = new Random();
	final MediaPlayer mediaPlayer = new MediaPlayer();
	private Button btnPlay = null;
	private Button btnStop = null;
	private Intent intent = null;
	public static final String ACTION = "ru.kserditov.simplemp3player.ACTION";
	private boolean isPlaying = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnStop = (Button) findViewById(R.id.btnStop);

	}

	public void playNext(View view) {

		Log.d("playnext", "isPlaying = " + String.valueOf(isPlaying));

		if (!isPlaying) {
			Log.d("playnext", "calling intent start");
			intent = new Intent(MainActivity.this, MediaPlayerService.class);
			intent.putExtra(ACTION, 1);
			startService(intent);

		} else {
			stopService(intent);
			startService(intent);
		}

		isPlaying = true;

		btnPlay.setBackgroundResource(R.drawable.forward_black);

		btnStop.setEnabled(true);
		btnStop.setBackgroundResource(R.drawable.stop_black);
	}

	public void stopCurrent(View view) {

		stopService(intent);

		isPlaying = false;

		btnPlay.setBackgroundResource(R.drawable.play_black);

		btnStop.setEnabled(false);
		btnStop.setBackgroundResource(R.drawable.stop_disabled);
	}

	public void selectFolder(View view) {
		Intent myIntent = new Intent(MainActivity.this, FolderChooser.class);
		MainActivity.this.startActivity(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
