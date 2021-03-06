package com.xeodou.cnbeta;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import go.cb.Cb;

/**
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ItemDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends SwipeRefreshListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = rssCallback;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(String title, String link);
    }

    private final static int CB_TASK_END = 0;
    private final static int CB_TASK_FAILED=1;
    private final static int CB_TASK_SUCCESS=2;

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks rssCallback = new Callbacks() {
        @Override
        public void onItemSelected(String title, String link) {
        }
    };

    private Handler handler;
    private Cb.CnBeta cbTask;

    private class RssEvent extends Cb.Listener.Stub {

        @Override
        public void OnFailure(final String s) {
            Message completeMessage =
                    handler.obtainMessage(CB_TASK_FAILED, s);
            completeMessage.sendToTarget();
        }

        @Override
        public void OnSuccess(Cb.RssXml rssXml) {
            Message completeMessage =
                    handler.obtainMessage(CB_TASK_SUCCESS, rssXml.getChannel());
            completeMessage.sendToTarget();
        }

        @Override
        public void OnEnd() {
            Message completeMessage =
                    handler.obtainMessage(CB_TASK_END);
            completeMessage.sendToTarget();
        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    private Cb.RssXml rss = Cb.NewRssXml();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: replace with a real list adapter.
        setListAdapter(new RssAdapter(getActivity(), rss.getChannel()));

        handler =  new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CB_TASK_END:
                        setRefreshing(false);
                        break;
                    case CB_TASK_FAILED:
                        Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                    case CB_TASK_SUCCESS:
                        setFeed((Cb.RssFeed)msg.obj);
                        break;
                }
            }
        };

        cbTask = Cb.NewCnBeta(new RssEvent());
    }

    public void setFeed(Cb.RssFeed feed) {
        RssAdapter adapter = (RssAdapter) getListAdapter();
        adapter.setFeed(feed);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        cbTask.Run();
        setRefreshing(true);

        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cbTask.Run();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Activities containing this fragment must implement its callbacks.
        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = rssCallback;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Cb.RssItem item = (Cb.RssItem) getListAdapter().getItem(position);
        mCallbacks.onItemSelected(item.getTitle(), item.getLink());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
