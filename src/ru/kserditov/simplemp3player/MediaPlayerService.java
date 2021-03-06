package ru.kserditov.simplemp3player;

import java.io.File;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MediaPlayerService extends Service {

	final MediaPlayer mediaPlayer = new MediaPlayer();
	final File path = new File("/storage/extSdCard/Music");
	final Random r = new Random();
	public static final String PLAYING = "ru.kserditov.simplemp3player.service.PLAYING";

	public MediaPlayerService() {
		super();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("intent", "onStartCommand fired");
		int action = intent.getIntExtra(MainActivity.ACTION, 1);
		Log.d("intent", "action = " + String.valueOf(action));
		if (action == 1) {
			playNext();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void playNext() {
		try {
			mediaPlayer.setDataSource(chooseSong().toString());
			mediaPlayer.prepare();
			Log.d("intent", "media player starting");
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							mp.stop();
							mp.reset();
							playNext();
						}
					});

			mediaPlayer.start();

			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
			Notification notification = new Notification.Builder(
					getApplicationContext()).setContentTitle("Simple mp3 Player")
					.setContentText("Now Playing")
					.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pi)
					.build();

			startForeground(6558, notification);

		} catch (Exception e) {
			Log.e("ERROR", e.getStackTrace().toString());
		}

	}

	private File chooseSong() {
		File[] songsList = path.listFiles();
		int index = (r.nextInt(songsList.length));
		return songsList[index];
	}

	@Override
	public void onDestroy() {
		Log.d("intent", "OnDestroy occured");
		super.onDestroy();
		mediaPlayer.release();
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
