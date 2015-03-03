package songbook.ax.asu.asu_songbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import songbook.ax.asu.asu_songbook.data.SongContract.*;
/**
 * Created by EIS i7 Gamer on 2015-02-28.
 */
public class SongDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "songs.db";

    public SongDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SONG_TABLE = "CREATE TABLE " + SongTable.NAME + " (" +
                SongTable._ID + " INTEGER PRIMARY KEY," +
                SongTable.COLUMN_SONG_NAME + " TEXT UNIQUE NOT NULL, " +
                SongTable.COLUMN_SONG_ID + " INTEGER UNIQUE NOT NULL, " +
                SongTable.COLUMN_SONG_MELODY + " TEXT UNIQUE, " +
                SongTable.COLUMN_LAST_UPADTED + " INTEGER NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_SONG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SongTable.NAME);
        onCreate(db);
    }
}


