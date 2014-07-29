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

	// Comparator for sorting File objects
	Comparator<File> fileComparator = new Comparator<File>() {
		public int compare(File f1, File f2) {

			String s1 = f1.getName();
			String s2 = f2.getName();

			// Make sure Up is always on top of the list
			if (s1 == "..") {
				return -1;
			}

			if (s2 == "..") {
				return 1;
			}

			return s1.compareToIgnoreCase(s2);
		}
	};

	// Filtering folders accessible for user
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

		// Initialize activity UI elements
		setContentView(R.layout.activity_folder_chooser);
		listView = (ListView) findViewById(R.id.lstFolders);
		tvSelectedFolder = (TextView) findViewById(R.id.txtSelectedFolder);

		// Read previously selected folder and try to open it
		settings = getPreferences(MODE_PRIVATE);

		// Open root folder if there is not preserved setting yet
		File currentFolder = new File(settings.getString("folder", "/"));

		// Open root folder if previously selected folder doesn't exist anymore
		// or is not accessible anymore
		if (currentFolder.exists() && currentFolder.canRead()
				&& !currentFolder.isHidden()) {
			updateList(currentFolder);
		} else {
			updateList(new File("/"));
		}
	}

	// Method is invoked on each CustomOnItemClickListener event in listView
	private void updateList(File path) {

		try {
			fileList = new ArrayList<File>(Arrays.asList(path
					.listFiles(directoryFilter)));

			// Adding Up element to the list for every folder other than root
			if (path.getParent() != null) {
				fileList.add(parentDir);
			}

			// Sorting elements by names
			Collections.sort(fileList, fileComparator);

			ArrayAdapter<File> adapter = new ArrayAdapter<File>(this,
					android.R.layout.simple_list_item_1, fileList);
			listView.setAdapter(adapter);
			listener = new CustomOnItemClickListener(path);
			listView.setOnItemClickListener(listener);

			// Updating UI with currently opened folder
			tvSelectedFolder.setText(path.getPath());

		} catch (NullPointerException e) {
			// Showing popup message if can't go down to selected folder
			toast = Toast.makeText(getApplicationContext(), getResources()
					.getString(R.string.no_folder_access), Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	// Preserving selected folder in SharedPreferences
	public void confirmFolder(View view) {
		editor = settings.edit();
		editor.putString("folder", tvSelectedFolder.getText().toString());
		editor.commit();
		finish();
	}

	// Custom listener provides new File objects to move down or up from current
	// folder
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
