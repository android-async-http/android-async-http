package com.loopj.android.http.sample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class WaypointsActivity extends ListActivity {

    private static final int[] samples = new int[]{
            R.string.title_get_sample,
            R.string.title_post_sample,
            R.string.title_delete_sample,
            R.string.title_put_sample,
            R.string.title_json_sample,
            R.string.title_sax_example,
            R.string.title_file_sample,
            R.string.title_binary_sample,
            R.string.title_gzip_sample,
            R.string.title_redirect_302,
            R.string.title_threading_timeout,
            R.string.title_cancel_all,
            R.string.title_cancel_handle,
            R.string.title_synchronous
    };
    private static final Class[] targets = {
            GetSample.class,
            PostSample.class,
            DeleteSample.class,
            PutSample.class,
            JsonSample.class,
            SaxSample.class,
            FileSample.class,
            BinarySample.class,
            GzipSample.class,
            Redirect302Sample.class,
            ThreadingTimeoutSample.class,
            CancelAllRequestsSample.class,
            CancelRequestHandleSample.class,
            SynchronousClientSample.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getTitlesList()));
    }

    private List<String> getTitlesList() {
        List<String> titles = new ArrayList<String>();
        for (int title : samples) {
            titles.add(getString(title));
        }
        return titles;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position >= 0 && position < targets.length)
            startActivity(new Intent(this, targets[position]));
    }
}
