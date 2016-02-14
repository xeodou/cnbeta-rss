package com.xeodou.cnbeta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_RSS_TITLE = "rss_title";
    public static final String ARG_RSS_LINK = "rss_link";

    /**
     * The dummy content this fragment is presenting.
     */
    private String link;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RSS_TITLE)) {
            getActivity().setTitle(getArguments().getString(ARG_RSS_TITLE));
        }
        if (getArguments().containsKey(ARG_RSS_LINK)) {
            link = getArguments().getString(ARG_RSS_LINK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (link != "") {
            ((WebView) rootView.findViewById(R.id.item_detail)).loadUrl("http://googleweblight.com/?lite_url=" + link + "&f=1&s=0");
        }

        return rootView;
    }
}
