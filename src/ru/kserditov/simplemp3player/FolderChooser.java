package ru.kserditov.simplemp3player;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FolderChooser extends Activity {

	ListView listView = null;
	List<File> fileList = null;
	File parentDir = new File("..");
	Toast toast = null;
	TextView tvSelectedFolder = null;
	CustomOnItemClickListener listener = null;
	SharedPreferences settings = null;
	SharedPreferences.Editor editor = null;

	Comparator<File> fileComparator = new Comparator<File>() {
		public int compare(File f1, File f2) {

			String s1 = f1.getName();
			String s2 = f2.getName();

			if (s1 == "..") {
				return -1;
			}

			if (s2 == "..") {
				return 1;
			}

			return s1.compareToIgnoreCase(s2);
		}
	};

	FilenameFilter directoryFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String filename) {
			File f = new File(dir, filename);
			return f.isDirectory() && f.canRead() && !f.isHidden();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_folder_chooser);
		listView = (ListView) findViewById(R.id.lstFolders);
		tvSelectedFolder = (TextView) findViewById(R.id.txtSelectedFolder);
		settings = getPreferences(MODE_PRIVATE);
		
		File currentFolder = new File(settings.getString("folder", "/"));

		if (currentFolder.exists() && currentFolder.canRead()
				&& !currentFolder.isHidden()) {
			updateList(currentFolder);
		} else {
			updateList(new File("/"));
		}
	}

	private void updateList(File path) {

		try {
			fileList = new ArrayList<File>(Arrays.asList(path
					.listFiles(directoryFilter)));

			if (path.getParent() != null) {
				fileList.add(parentDir);
			}

			Collections.sort(fileList, fileComparator);

			ArrayAdapter<File> adapter = new ArrayAdapter<File>(this,
					android.R.layout.simple_list_item_1, fileList);
			listView.setAdapter(adapter);
			listener = new CustomOnItemClickListener(path);
			listView.setOnItemClickListener(listener);
			tvSelectedFolder.setText(path.getPath());

		} catch (NullPointerException e) {
			toast = Toast.makeText(getApplicationContext(), getResources()
					.getString(R.string.no_folder_access), Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void confirmFolder(View view) {
		editor = settings.edit();
		editor.putString("folder", tvSelectedFolder.getText().toString());
		editor.commit();
		finish();
	}

	private class CustomOnItemClickListener implements OnItemClickListener {

		File path = new File("/");

		CustomOnItemClickListener(File _path) {
			super();
			path = _path;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (parent.getAdapter().getItem(position).toString() == "..") {
				updateList(new File(path.getParent()));
			} else {
				updateList((File) parent.getAdapter().getItem(position));
			}
		}

	}

}