package se.persandstrom.ploxworm.core.database;

import se.persandstrom.ploxworm.Constant;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Is accessed through the StorageInterface
 * @author Per Sandström
 *
 */
public class DbOperations {

	protected static final String TAG = "DbOperations";

	private static final String[] HIGHSCORE_PROJECTION = new String[] { DbConnector.HIGHSCORE_ATTRIBUTE_LEVEL, //0 
			DbConnector.HIGHSCORE_ATTRIBUTE_LOCAL_BEST, //1
			DbConnector.HIGHSCORE_ATTRIBUTE_GLOBAL_BEST, //2
			DbConnector.HIGHSCORE_ATTRIBUTE_SYNCED, //3
	};

	public static final int HIGHSCORE_ATTRIBUTE_LEVEL = 0;
	public static final int HIGHSCORE_ATTRIBUTE_LOCAL_BEST = 1;
	public static final int HIGHSCORE_ATTRIBUTE_GLOBAL_BEST = 2;
	public static final int HIGHSCORE_ATTRIBUTE_SYNCED = 3;

	final private DbConnector dbConnector;

	protected DbOperations(Context context) {
		dbConnector = new DbConnector(context);
	}

	public synchronized void addHighscore(long level, long localHighscore, long globalHighscore, boolean synced) {
		if (Constant.DEBUG) Log.d(TAG, "addHighscore");

		ContentValues values = new ContentValues(4);
		values.put(DbConnector.HIGHSCORE_ATTRIBUTE_LEVEL, level);
		values.put(DbConnector.HIGHSCORE_ATTRIBUTE_LOCAL_BEST, localHighscore);
		values.put(DbConnector.HIGHSCORE_ATTRIBUTE_GLOBAL_BEST, globalHighscore);
		values.put(DbConnector.HIGHSCORE_ATTRIBUTE_SYNCED, synced ? 1 : 0);

		dbConnector.addRow(DbConnector.HIGHSCORE_TABLE_NAME, values);
	}

	public synchronized long getLocalHighscore(long level) {
		String where = DbConnector.HIGHSCORE_ATTRIBUTE_LEVEL + "=?";
		String[] whereArgs = new String[] { String.valueOf(level) };
		Cursor entries = dbConnector.getEntries(DbConnector.HIGHSCORE_TABLE_NAME, HIGHSCORE_PROJECTION, where,
				whereArgs, null, null, null, null);

		if (entries.moveToFirst()) {
			long score = entries.getLong(HIGHSCORE_ATTRIBUTE_LOCAL_BEST);
			entries.close();
			return score;
		} else {
			entries.close();
			return -1;
		}
	}

	public synchronized Cursor getLevel(long level) {
		String where = DbConnector.HIGHSCORE_ATTRIBUTE_LEVEL + "=?";
		String[] whereArgs = new String[] { String.valueOf(level) };
		return dbConnector.getEntries(DbConnector.HIGHSCORE_TABLE_NAME, HIGHSCORE_PROJECTION, where, whereArgs, null,
				null, null, null);
	}

	public synchronized Cursor getAllLevels() {
		String sortBy = DbConnector.HIGHSCORE_ATTRIBUTE_LEVEL + " ASC";
		return dbConnector.getEntries(DbConnector.HIGHSCORE_TABLE_NAME, HIGHSCORE_PROJECTION, null, null, null, null,
				sortBy, null);
	}

	public synchronized long updateLocalHighscore(long level, long localHighscore, boolean synced) {
		String where = DbConnector.HIGHSCORE_ATTRIBUTE_LEVEL + "=?";
		String[] whereArgs = new String[] { String.valueOf(level) };

		ContentValues contentValues = new ContentValues(2);
		contentValues.put(DbConnector.HIGHSCORE_ATTRIBUTE_LOCAL_BEST, localHighscore);
		contentValues.put(DbConnector.HIGHSCORE_ATTRIBUTE_SYNCED, synced ? 1 : 0);
		return dbConnector.updateRow(DbConnector.HIGHSCORE_TABLE_NAME, contentValues, where, whereArgs);
	}

	public synchronized long updateGlobalHighscore(long level, long globalHighscore, boolean synced) {
		String where = DbConnector.HIGHSCORE_ATTRIBUTE_LEVEL + "=?";
		String[] whereArgs = new String[] { String.valueOf(level) };

		ContentValues contentValues = new ContentValues(2);
		contentValues.put(DbConnector.HIGHSCORE_ATTRIBUTE_GLOBAL_BEST, globalHighscore);
		contentValues.put(DbConnector.HIGHSCORE_ATTRIBUTE_SYNCED, synced ? 1 : 0);
		return dbConnector.updateRow(DbConnector.HIGHSCORE_TABLE_NAME, contentValues, where, whereArgs);
	}

	public synchronized void beginTransaction() {
		dbConnector.beginTransaction();
	}

	public synchronized void endTransaction() {
		dbConnector.endTransaction();
	}

}
