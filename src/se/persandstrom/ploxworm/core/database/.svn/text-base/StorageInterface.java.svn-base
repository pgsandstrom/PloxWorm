package se.persandstrom.ploxworm.core.database;

import java.util.ArrayList;

import se.persandstrom.ploxworm.core.worm.board.BoardManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.preference.PreferenceManager;

public class StorageInterface {

	protected static final String TAG = "StorageInterface";

	//SharedPreferences key:
	private static final String KEY_UNLOCKED_LEVELS = "unlocked_levels";

	private Context context;

	private static StorageInterface instance;

	private DbOperations dbOperations;

	public synchronized static StorageInterface getInstance(Context context) {
		if (instance == null) {
			instance = new StorageInterface(context);
		}
		return instance;
	}

	private StorageInterface(Context context) {
		context = context.getApplicationContext();
		this.context = context;
		dbOperations = new DbOperations(context);
	}

	/**
	 * 
	 * @param level
	 * @param localScore 
	 * @return if it was a new highscore
	 */
	public synchronized boolean submitNewScore(long level, long localScore, boolean synced) {
		long highscore = dbOperations.getLocalHighscore(level);

		if (highscore == -1) {
			dbOperations.addHighscore(level, localScore, -1, false);
			return true;
		} else {
			if (localScore > highscore) {
				dbOperations.updateLocalHighscore(level, localScore, synced);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Updates the global highscore to the given values
	 * @param scoreList
	 */
	public synchronized void updateGlobalHighScore(ArrayList<ScoreObject> scoreList) {
		dbOperations.beginTransaction();

		for (ScoreObject score : scoreList) {
			dbOperations.updateGlobalHighscore(score.level, score.score, true);
		}

		dbOperations.endTransaction();
	}

	/**
	 * Gets the local and global highscore for a specific level
	 * @param level
	 * @return
	 */
	public synchronized LevelHighscore getScore(long level) {

		Cursor levelCursor = dbOperations.getLevel(level);

		if (levelCursor.moveToFirst()) {
			long localHighscore = levelCursor.getLong(DbOperations.HIGHSCORE_ATTRIBUTE_LOCAL_BEST);
			long globalHighscore = levelCursor.getLong(DbOperations.HIGHSCORE_ATTRIBUTE_GLOBAL_BEST);
			levelCursor.close();
			return new LevelHighscore(level, localHighscore, globalHighscore);
		} else {
			levelCursor.close();
			dbOperations.addHighscore(level, 0, 0, false);
			return new LevelHighscore(level, 0, -1);
		}
	}

	/**
	 * Gets all local and global highscores sorted by level (lowest first) containing all levels, even those without a score.
	 * @return
	 */
	public synchronized ArrayList<LevelHighscore> getAllScores() {

		Cursor levelCursor = dbOperations.getAllLevels();

		ArrayList<LevelHighscore> scoreList = new ArrayList<LevelHighscore>();

		int position = 0;

		while (levelCursor.moveToNext()) {

			position++;

			long level = levelCursor.getLong(DbOperations.HIGHSCORE_ATTRIBUTE_LEVEL);
			long localHighscore = levelCursor.getLong(DbOperations.HIGHSCORE_ATTRIBUTE_LOCAL_BEST);
			long globalHighscore = levelCursor.getLong(DbOperations.HIGHSCORE_ATTRIBUTE_GLOBAL_BEST);

			//if we did not have an entry for the level, add a level with 0 highscore
			while (position < level) {
				LevelHighscore levelHighscore = new LevelHighscore(position, 0, 0);
				scoreList.add(levelHighscore);
				position++;
			}

			LevelHighscore levelHighscore = new LevelHighscore(level, localHighscore, globalHighscore);
			scoreList.add(levelHighscore);
		}

		while (position < BoardManager.TOTAL_LEVELS) {
			LevelHighscore levelHighscore = new LevelHighscore(position, 0, 0);
			scoreList.add(levelHighscore);
			position++;
		}

		return scoreList;
	}

	/**
	 * Set unlocked levels
	 * @param unlockedLevels
	 */
	public void setUnlockedLevels(int unlockedLevels) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putInt(KEY_UNLOCKED_LEVELS, unlockedLevels);
		edit.commit();
	}

	/**
	 * Returns how many levels are 
	 * @return
	 */
	public int getUnlockedLevels() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getInt(KEY_UNLOCKED_LEVELS, 0);
	}
}
