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

	private ListView mListView = null;
	private List<File> mFileList = null;
	private static final File sParentDir = new File("..");
	private Toast mToast = null;
	private TextView mTvSelectedFolder = null;
	private CustomOnItemClickListener mListener = null;
	private SharedPreferences mSharedPreferences = null;
	private SharedPreferences.Editor mSharedPreferencesEditor = null;

	// Comparator for sorting File objects
	private Comparator<File> mFileComparator = new Comparator<File>() {
		public int compare(File f1, File f2) {

			String s1 = f1.getName();
			String s2 = f2.getName();

			// Make sure Up is always on top of the list
			if ("..".equals(s1)) {
				return -1;
			}
			if ("..".equals(s2)) {
				return 1;
			}
			return s1.compareToIgnoreCase(s2);
		}
	};

	// Filtering folders accessible for user
	private FilenameFilter mDirectoryFilter = new FilenameFilter() {
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
		mListView = (ListView) findViewById(R.id.lstFolders);
		mTvSelectedFolder = (TextView) findViewById(R.id.txtSelectedFolder);

		// Read previously selected folder and try to open it
		mSharedPreferences = getPreferences(MODE_PRIVATE);

		// Open root folder if there is not preserved setting yet
		File mCurrentFolder = new File(mSharedPreferences.getString("folder",
				"/"));

		// Open root folder if previously selected folder doesn't exist anymore
		// or is not accessible anymore
		if (mCurrentFolder.exists() && mCurrentFolder.canRead()
				&& !mCurrentFolder.isHidden()) {
			updateList(mCurrentFolder);
		} else {
			updateList(new File("/"));
		}
	}

	// Method is invoked on each CustomOnItemClickListener event in listView
	private void updateList(File path) {

		try {
			mFileList = new ArrayList<File>(Arrays.asList(path
					.listFiles(mDirectoryFilter)));

			// Adding Up element to the list for every folder other than root
			if (path.getParent() != null) {
				mFileList.add(sParentDir);
			}

			// Sorting elements by names
			Collections.sort(mFileList, mFileComparator);

			ArrayAdapter<File> adapter = new ArrayAdapter<File>(this,
					android.R.layout.simple_list_item_1, mFileList);
			mListView.setAdapter(adapter);
			mListener = new CustomOnItemClickListener(path);
			mListView.setOnItemClickListener(mListener);

			// Updating UI with currently opened folder
			mTvSelectedFolder.setText(path.getPath());

		} catch (NullPointerException e) {
			// Showing pop-up message if can't go down to selected folder
			mToast = Toast.makeText(getApplicationContext(), getResources()
					.getString(R.string.no_folder_access), Toast.LENGTH_SHORT);
			mToast.show();
		}
	}

	// Preserving selected folder in SharedPreferences
	public void confirmFolder(View view) {
		mSharedPreferencesEditor = mSharedPreferences.edit();
		mSharedPreferencesEditor.putString("folder", mTvSelectedFolder
				.getText().toString());
		mSharedPreferencesEditor.commit();
		finish();
	}

	// Custom listener provides new File objects to move down or up from current
	// folder
	private class CustomOnItemClickListener implements OnItemClickListener {

		File mPath = new File("/");

		CustomOnItemClickListener(File path) {
			super();
			this.mPath = path;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if ("..".equals(parent.getAdapter().getItem(position).toString())) {
				updateList(new File(mPath.getParent()));
			} else {
				updateList((File) parent.getAdapter().getItem(position));
			}
		}

	}

}
