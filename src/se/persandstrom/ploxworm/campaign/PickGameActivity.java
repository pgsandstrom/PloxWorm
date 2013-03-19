package se.persandstrom.ploxworm.campaign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import se.persandstrom.ploxworm.Constant;
import se.persandstrom.ploxworm.R;
import se.persandstrom.ploxworm.database.LevelHighscore;
import se.persandstrom.ploxworm.database.StorageInterface;

import java.util.ArrayList;

public class PickGameActivity extends Activity implements View.OnClickListener {

	protected static final String TAG = "PickGameActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.game_picker_list);

		findViewById(R.id.unlock_levels).setOnClickListener(this);

		PickGameAdapter adapter = new PickGameAdapter(this);
		ListView listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new LevelClickListener());
	}

	private class LevelClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (Constant.DEBUG) Log.d(TAG, "arg2 " + arg2);
			startSingleLevel(arg2 + 1);
		}
	}

	private void startSingleLevel(int level) {
		//TODO
	}

	private void startUnlockCampaign() {
		StorageInterface storageInterface = StorageInterface.getInstance(this);
		int unlockedLevels = storageInterface.getUnlockedLevels();

		Intent unlockLevelsCampaign = new Intent(this, UnlockLevelsCampaign.class);
		unlockLevelsCampaign.putExtra(UnlockLevelsCampaign.INTENT_EXTRA_START_LEVEL, unlockedLevels);
		startActivity(unlockLevelsCampaign);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.unlock_levels:
			startUnlockCampaign();
			break;
		}
	}

	private class PickGameAdapter implements ListAdapter {

		final LayoutInflater inflater;
		final private int unlockedLevels;
		final private ArrayList<LevelHighscore> allScores;

		public PickGameAdapter(Context context) {
			inflater = PickGameActivity.this.getLayoutInflater();
			StorageInterface storageInterface = StorageInterface.getInstance(context);
			unlockedLevels = storageInterface.getUnlockedLevels();
			//			unlockedLevels = BoardManager.TOTAL_LEVELS;
			allScores = storageInterface.getAllScores();
		}

		@Override
		public int getCount() {
			return unlockedLevels;
		}

		@Override
		public Object getItem(int i) {
			return allScores.get(i);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public int getItemViewType(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.game_picker_list_item, null);
			}

			LevelHighscore levelHighscore = allScores.get(position);

			TextView levelNumber = (TextView) convertView.findViewById(R.id.level_number);
			TextView globalHighscore = (TextView) convertView.findViewById(R.id.global_highscore);
			TextView localHighscore = (TextView) convertView.findViewById(R.id.local_highscore);

			levelNumber.setText(String.valueOf(position + 1));
			globalHighscore.setText(String.valueOf(levelHighscore.globalHighscore));
			localHighscore.setText(String.valueOf(levelHighscore.localHighscore));

			return convertView;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return allScores.isEmpty();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

	}
}
