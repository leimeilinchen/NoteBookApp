package com.lml.notebook.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lml.notebook.R;
import com.lml.notebook.db.Note;

import java.util.List;


/**
 * Created by huangziwei on 16-4-12.
 */
public class NoteDataAdapter extends BaseAdapter {

    public static final int VIEW_HEADER = 0;
    public static final int VIEW_MOMENT = 1;


    private List<Note> mList;
    private Context mContext;

    ViewHolder holder;
    public NoteDataAdapter(Context context, List<Note> list) {
        mList = list;
        mContext = context;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_HEADER : VIEW_MOMENT;
    }

    @Override
    public int getCount() {
        // heanderView
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = View.inflate(mContext, R.layout.note_item, null);
             holder = new ViewHolder();
            holder.textViewTitle = convertView.findViewById(R.id.text_view_title);
            holder.textViewDescription = convertView.findViewById(R.id.text_view_description);
            holder.textViewPriority = convertView.findViewById(R.id.text_view_priority);
            holder.image = convertView.findViewById(R.id.image);
            holder.delect= convertView.findViewById(R.id.delect);
            convertView.setTag(holder);

        }

        Note currentNote = mList.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
        Glide.with(mContext).load(currentNote.getPath()).into(holder.image);

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPriority;
        private ImageView image;
        private CardView delect;
    }
}
