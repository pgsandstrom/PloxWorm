package se.persandstrom.ploxworm.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import se.persandstrom.ploxworm.Constant;

/**
 * The DBConnector class enables program integration with a SQLite database. Only communicate with this class through
 * DbInterface!
 * 
 * @author Per
 */
public class DbConnector {

	protected static final String TAG = "DbConnector";

	public static final String HIGHSCORE_TABLE_NAME = "highscore";
	public static final String HIGHSCORE_ATTRIBUTE_LEVEL = "level";
	public static final String HIGHSCORE_ATTRIBUTE_LOCAL_BEST = "local_best";
	public static final String HIGHSCORE_ATTRIBUTE_GLOBAL_BEST = "global_best";
	public static final String HIGHSCORE_ATTRIBUTE_SYNCED = "synced";

	public static final String HIGHSCORE_TABLE_CREATE = "create table if not exists " + HIGHSCORE_TABLE_NAME + "("
			+ HIGHSCORE_ATTRIBUTE_LEVEL + " integer primary key," + HIGHSCORE_ATTRIBUTE_LOCAL_BEST
			+ " integer not null, " + HIGHSCORE_ATTRIBUTE_GLOBAL_BEST + " integer not null,"
			+ HIGHSCORE_ATTRIBUTE_SYNCED + " integer not null" + ");";

//	public static final String UNLOCK_TABLE_NAME = "unlock";
//	public static final String UNLOCK_ATTRIBUTE_LEVEL = "level";
//
//	public static final String UNLOCK_TABLE_CREATE = "create table if not exists " + UNLOCK_TABLE_NAME + "("
//			+ UNLOCK_ATTRIBUTE_LEVEL + " integer primary key" + ");";

	public static final String DATABASE_NAME = "ploxworm.sqlite";

	private Context context = null;
	private SQLiteDatabase db;

	/**
	 * Constructor. Creates table if they don't exists. Change to your own tables!
	 * 
	 * @param (Context)
	 */
	protected DbConnector(final Context context) {
		this.context = context;
		open();
	}

	protected SQLiteDatabase getDB() {
		return db;
	}

	/**
	 * Get latest input row id.
	 * 
	 * @param databaseTable
	 * @return
	 */
	protected Cursor getLatestInput(final String databaseTable) {
		if (!db.isOpen()) {
			open();
		}
		return db.query(databaseTable, new String[] { "last_insert_rowid();" }, null, null, null, null, null);
	}

	/**
	 * A special addrow that takes ContentValues directly, use this if need be to add bytearrays
	 * 
	 * @param databaseTable
	 * @param key
	 * @param value
	 * @return
	 */
	protected long addRow(final String databaseTable, final ContentValues contentValues) {
		if (!db.isOpen()) {
			open();
		}
		return db.insert(databaseTable, null, contentValues);
	}

	/**
	 * A special updateRow that takes ContentValues directly, use this if need be to add bytearrays
	 * 
	 * @param databaseTable
	 * @param contentValues
	 * @param where
	 * @param whereArgs
	 * @return the number of rows affected
	 */
	protected long updateRow(final String databaseTable, final ContentValues contentValues, final String where,
			final String[] whereArgs) {
		if (!db.isOpen()) {
			open();
		}
		return db.update(databaseTable, contentValues, where, whereArgs);
	}

	/**
	 * Delete row from table
	 * 
	 * @param databaseTable
	 * @param whereClause
	 * @param whereArgs
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a
	 *         count pass "1" as the whereClause.
	 */
	protected long deleteRow(final String databaseTable, final String whereClause, final String[] whereArgs) {
		if (!db.isOpen()) {
			open();
		}
		return db.delete(databaseTable, whereClause, whereArgs);
	}

	/**
	 * Does the SQL UPDATE function on the table with given SQL string
	 * 
	 * @param sqlQuery
	 *            an SQL Query starting at SET
	 */
	protected Cursor update(final String databaseTable, final String sqlQuery) {
		if (!db.isOpen()) {
			open();
		}
		return db.rawQuery("UPDATE " + databaseTable + sqlQuery, null);
	}

	/**
	 * Get all entries in the database sorted by the given value.
	 * 
	 * @param databaseTable
	 *            database table to use
	 * @param columns
	 *            List of columns to include in the result.
	 * @param selection
	 *            Return rows with the following string only. Null returns all rows.
	 * @param selectionArgs
	 *            Arguments of the selection.
	 * @param groupBy
	 *            Group results by.
	 * @param having
	 *            A filter declare which row groups to include in the cursor.
	 * @param sortBy
	 *            Column to sort elements by.
	 * @param limit
	 *            limiting number of records to return
	 * @return Returns a cursor through the results.
	 */
	protected Cursor getEntries(final String databaseTable, final String[] columns, final String selection,
			final String[] selectionArgs, final String groupBy, final String having, final String sortBy,
			final String limit) {
		if (!db.isOpen()) {
			open();
		}
		return db.query(databaseTable, columns, selection, selectionArgs, groupBy, having, sortBy);
	}

	protected Cursor getEntriesCustomJoin(final String customJoin, final String[] columns, final String selection,
			final String[] selectionArgs, final String groupBy, final String having, final String orderBy,
			final String limit) {
		if (!db.isOpen()) {
			open();
		}
		final SQLiteQueryBuilder query = new SQLiteQueryBuilder();

		query.setTables(customJoin);

		final Cursor DBresult = query.query(db, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

		return DBresult;
	}

	/**
	 * This is a function that should only be used if you know what you're doing. It is only here to clear the appended
	 * test data. This clears out all data within the table specified when the database connection was opened.
	 * 
	 * @return Returns TRUE if successful. FALSE if not.
	 */
	protected int clearTable(final String databaseTable) {
		if (!db.isOpen()) {
			open();
		}
		return db.delete(databaseTable, null, null);
	}

	/**
	 * Clear all of user specified tables!
	 */
	protected void clearAllTables() {
		if (!db.isOpen()) {
			open();
		}
		db.execSQL("DROP TABLE " + HIGHSCORE_TABLE_NAME);
	}

	protected Cursor sqlquery(final String sqlQuery, final String[] selectArgs) {
		if (!db.isOpen()) {
			open();
		}

		if (Constant.DEBUG) Log.d(TAG, "sqlquery: " + sqlQuery);
		return db.rawQuery(sqlQuery, selectArgs);
	}

	protected void execSQL(final String sqlQuery) {
		if (!db.isOpen()) {
			open();
		}
		if (Constant.DEBUG) Log.d(TAG, "execSQL: " + sqlQuery);
		db.execSQL(sqlQuery);
	}

	protected void beginTransaction() {
		if (!db.isOpen()) {
			open();
		}
		db.beginTransaction();
	}

	protected void endTransaction() {
		if (!db.isOpen()) {
			open();
		}

		try {
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (final IllegalStateException e) {
			//one desire HD-user got this exception from validateDatabase() repeatedly... zomg
		}
	}

	protected void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	protected boolean isClosed() {
		return !db.isOpen();
	}

	protected void open() {
		open(DATABASE_NAME);
	}

	protected void open(final String database) {
		close();
		db = context.openOrCreateDatabase(database, Context.MODE_PRIVATE, null);

		db.execSQL(HIGHSCORE_TABLE_CREATE);
//		db.execSQL(UNLOCK_TABLE_CREATE);
	}

	protected void switchDatabase(final String database) {
		close();
		open(database);
	}
}