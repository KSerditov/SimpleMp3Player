package ru.kserditov.simplemp3player;

import java.io.File;
import java.util.Random;

import android.support.v7.app.ActionBarActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	final String TAG = "SimpleMp3Player";
	final File path = new File("/storage/extSdCard/Music");
	final Random r = new Random();
	final MediaPlayer mediaPlayer = new MediaPlayer();
	private boolean isPlaying = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button btnPlay = (Button) findViewById(R.id.btnPlay);
		final Button btnStop = (Button) findViewById(R.id.btnStop);

		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isPlaying) {
					playNext(btnPlay, btnStop);
				} else {
					stopCurrent(btnPlay, btnStop);
					playNext(btnPlay, btnStop);
				}

			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stopCurrent(btnPlay, btnStop);
			}

		});

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

	public File chooseSong(Random r, File path) {
		File[] songsList = path.listFiles();
		int index = (r.nextInt(songsList.length));
		return songsList[index];
	}

	public void playNext(Button btnPlay, Button btnStop) {

		final Button btnPlayRef = btnPlay;
		final Button btnStopRef = btnStop;

		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer mp) {

						stopCurrent(btnPlayRef, btnStopRef);
						playNext(btnPlayRef, btnStopRef);

					}
				});

		try {
			mediaPlayer.setDataSource(chooseSong(r, path).toString());
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		isPlaying = true;
		//btnPlay.setText("Next");
		btnPlay.setBackgroundResource(R.drawable.forward_black);
		btnStop.setEnabled(true);
		btnStop.setBackgroundResource(R.drawable.stop_black);

	}

	public void stopCurrent(Button btnPlay, Button btnStop) {

		mediaPlayer.stop();
		mediaPlayer.reset();

		isPlaying = false;
		//btnPlay.setText("Play");
		btnPlay.setBackgroundResource(R.drawable.play_black);
		btnStop.setEnabled(false);
		btnStop.setBackgroundResource(R.drawable.stop_disabled);

	}

}
