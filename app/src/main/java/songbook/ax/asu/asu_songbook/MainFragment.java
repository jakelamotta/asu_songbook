package songbook.ax.asu.asu_songbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Button;

import java.util.List;

import songbook.ax.asu.asu_songbook.data.SongContract;
import songbook.ax.asu.asu_songbook.data.SongSyncAdapter;

/**
 * Created by EIS i7 Gamer on 2015-02-26.
 */
public class MainFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    public static final String SONG_NAME = "song_name";
    public static final String MELODY = "melody";
    public static final String TEXT = "song_text";
    public static final String BUNDLE_KEY = "fragment_bundle";
    private SongAdapter mSongAdapter;
    private static final int SONG_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            SongContract.SongTable.NAME + "." + SongContract.SongTable._ID,
            SongContract.SongTable.COLUMN_SONG_ID,
            SongContract.SongTable.COLUMN_SONG_MELODY,
            SongContract.SongTable.COLUMN_SONG_NAME,
            SongContract.SongTable.COLUMN_LAST_UPADTED,
            SongContract.SongTable.COLUMN_TEXT};

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_SONG_ID = 0;
    static final int COL_SONG_IDENTIFIER = 1;
    static final int COL_SONG_MELODY = 2;
    static final int COL_SONG_NAME = 3;
    static final int COL_SONG_LASTUPDATED = 4;
    static final int COL_SONG_TEXT = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mSongAdapter = new SongAdapter(getActivity(),null,0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button syncButton = (Button) rootView.findViewById(R.id.sync_button);

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongSyncAdapter.syncImmediately(getActivity());
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_songs);
        listView.setAdapter(mSongAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    String name = cursor.getString(COL_SONG_NAME);
                    String melody = cursor.getString(COL_SONG_MELODY);
                    String text = cursor.getString(COL_SONG_TEXT);

                    Intent intent = new Intent(getActivity(),DetailActivity.class);
                    //intent.putExtra(SONG_NAME,name);
                    //intent.putExtra(MELODY,melody);
                    //intent.putExtra(TEXT,text);

                    Bundle bundle = new Bundle();
                    bundle.putString(SONG_NAME,name);
                    bundle.putString(TEXT,text);
                    bundle.putString(MELODY,melody);
                    intent.putExtra(BUNDLE_KEY,bundle);

                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SONG_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = SongContract.SongTable.COLUMN_SONG_NAME + " ASC";

        Uri songUri = SongContract.SongTable.buildSongUri("song");

        return new CursorLoader(getActivity(),
                songUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mSongAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mSongAdapter.swapCursor(null);
    }
}
