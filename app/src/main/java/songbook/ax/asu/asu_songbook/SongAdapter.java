package songbook.ax.asu.asu_songbook;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by EIS i7 Gamer on 2015-02-28.
 */
public class SongAdapter extends CursorAdapter {

    public static class ViewHolder{
        public final ImageView iconView;
        public final TextView nameView;
        public final TextView melodyView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.image_view_item);
            nameView = (TextView) view.findViewById(R.id.text_view_songname);
            melodyView =(TextView) view.findViewById(R.id.text_view_melody);
        }
    }

    public SongAdapter(Context context, Cursor cursor,int flags){
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        int viewType = getItemViewType(cursor.getPosition());

        int layoutId = R.layout.list_item_song;

        View view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Get weather icon
        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);

        String name = cursor.getString(MainFragment.COL_SONG_NAME);
        viewHolder.nameView.setText(name);

        String melody = cursor.getString(MainFragment.COL_SONG_MELODY);
        viewHolder.melodyView.setText(melody);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
