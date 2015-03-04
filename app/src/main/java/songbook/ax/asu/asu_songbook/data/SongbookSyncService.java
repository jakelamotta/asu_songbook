package songbook.ax.asu.asu_songbook.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by EIS i7 Gamer on 2015-03-03.
 */
public class SongbookSyncService extends Service{
    private static final Object sSyncAdapterLock = new Object();
    private static SongSyncAdapter sSongSyncAdapter = null;
    private static final String LOG_TAG = SongbookSyncService.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate - SongbookSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSongSyncAdapter == null) {
                sSongSyncAdapter = new SongSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSongSyncAdapter.getSyncAdapterBinder();
    }
}
