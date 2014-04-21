package com.loopj.android.http.sample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WaypointsActivity extends ListActivity {

    private static final String[] samples = new String[]{
            "GET",
            "POST",
            "DELETE",
            "PUT",
            "JSON",
            "FILE",
            "BINARY",
            "GZIP",
            "302 REDIRECT",
            "THREADING TIMEOUTS",
            "CANCEL ALL REQUESTS",
            "CANCEL REQUEST HANDLE",
            "SYNCHRONOUS CLIENT"
    };
    private static final Class[] targets = {
            GetSample.class,
            PostSample.class,
            DeleteSample.class,
            PutSample.class,
            JsonSample.class,
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
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, samples));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position >= 0 && position < targets.length)
            startActivity(new Intent(this, targets[position]));
    }
}
