package songbook.ax.asu.asu_songbook.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by EIS i7 Gamer on 2015-02-28.
 */
public class SongContract {

    public static final String CONTENT_AUTHORITY = "songbook.ax.asu.asu_songbook";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SONG = "song";


    public static final class SongTable implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONG).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SONG;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SONG;

        public static final String NAME = "song";
        public static final String COLUMN_SONG_NAME = "song_name";

        //ID is used to query for songs from the server.
        public static final String COLUMN_SONG_ID = "song_id";
        public static final String COLUMN_SONG_MELODY = "song_melody";

        //A version control value
        public static final String COLUMN_LAST_UPADTED = "last_updated";

        public static final String COLUMN_TEXT = "song_text";

        public static Uri buildSongUri(String id) {
            //return ContentUris.withAppendedId(CONTENT_URI, id);
            return CONTENT_URI.buildUpon().build();//.appendPath("song").build();
        }
    }

}


