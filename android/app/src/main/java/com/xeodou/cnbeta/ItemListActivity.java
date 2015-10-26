package com.xeodou.cnbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import go.Seq;
import go.cb.Cb;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ItemListActivity extends AppCompatActivity
        implements ItemListFragment.Callbacks, SwipeRefreshLayout.OnRefreshListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SwipeRefreshLayout swiplayout;
    private ItemListFragment listFragment;
    private Cb.CnBeta cbTask;


    private final int CB_TASK_END = 0;
    private final int CB_TASK_FAILED=1;
    private final int CB_TASK_SUCCESS=2;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CB_TASK_END:
                    swiplayout.setRefreshing(false);
                    break;
                case CB_TASK_FAILED:
                    Toast.makeText(ItemListActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case CB_TASK_SUCCESS:
                    listFragment.setFeed((Cb.RssFeed)msg.obj);
                    break;
            }
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_list);

        swiplayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swiplayout.setOnRefreshListener(this);

        listFragment = ((ItemListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.item_list));

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.

            listFragment.setActivateOnItemClick(true);
        }

        cbTask = Cb.NewCnBeta(new RssEvent());

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String title, String link) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_RSS_TITLE, title);
            arguments.putString(ItemDetailFragment.ARG_RSS_LINK, link);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_RSS_TITLE, title);
            detailIntent.putExtra(ItemDetailFragment.ARG_RSS_LINK, link);
            startActivity(detailIntent);
        }
    }


    @Override
    public void onRefresh() {
        if (!swiplayout.isRefreshing()) {
            swiplayout.setRefreshing(true);
        }
        cbTask.Run();
    }
}
