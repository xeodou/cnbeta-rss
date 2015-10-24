package com.xeodou.cnbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import go.cb.Cb;

/**
 * Created by xeodou on 10/23/15.
 */
public class RssAdapter extends ArrayAdapter {

    private Cb.RssFeed feed;

    private static class ViewHolder {
        TextView title;
        TextView description;
    }

    public RssAdapter(Context context, Cb.RssFeed feed) {
        super(context, 0);
        this.feed = feed;
    }

    public void setFeed(Cb.RssFeed feed) {
        this.feed = feed;
    }

    @Override
    public Cb.RssItem getItem(int position) {
        return feed.Item((long)position);
    }

    @Override
    public int getCount() {
        return (int)feed.Length();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Cb.RssItem item = this.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rss_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.rss_title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.rss_description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
        // Return the completed view to render on screen
        return convertView;
    }
}
