package songbook.ax.asu.asu_songbook.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by EIS i7 Gamer on 2015-03-03.
 */
public class SongbookAuthenticatorService extends Service{
    // Instance field that stores the authenticator object
    private SongbookAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new SongbookAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}