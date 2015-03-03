package songbook.ax.asu.asu_songbook;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import songbook.ax.asu.asu_songbook.data.SongContract;

/**
 * Created by EIS i7 Gamer on 2015-02-26.
 */
public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

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
            SongContract.SongTable.COLUMN_LAST_UPADTED};

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_SONG_ID = 0;
    static final int COL_SONG_IDENTIFIER = 1;
    static final int COL_SONG_MELODY = 2;
    static final int COL_SONG_NAME = 3;
    static final int COL_SONG_LASTUPDATED = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG,"in onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ListView listView = (ListView) getActivity().findViewById(R.id.list_view_songs);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.v(LOG_TAG,"clicked");
            }
        });

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
}
