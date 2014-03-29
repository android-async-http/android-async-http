package com.loopj.android.http.sample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WaypointsActivity extends ListActivity {

    private static final String[] samples = new String[]{"GET", "POST", "DELETE", "PUT", "JSON", "FILE", "BINARY", "THREADING TIMEOUTS", "CANCEL ALL REQUESTS", "CANCEL REQUEST HANDLE", "SYNCHRONOUS CLIENT"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, samples));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Class<?> targetClass;
        switch (position) {
            case 0:
            default:
                targetClass = GetSample.class;
                break;
            case 1:
                targetClass = PostSample.class;
                break;
            case 2:
                targetClass = DeleteSample.class;
                break;
            case 3:
                targetClass = PutSample.class;
                break;
            case 4:
                targetClass = JsonSample.class;
                break;
            case 5:
                targetClass = FileSample.class;
                break;
            case 6:
                targetClass = BinarySample.class;
                break;
            case 7:
                targetClass = ThreadingTimeoutSample.class;
                break;
            case 8:
                targetClass = CancelAllRequestsSample.class;
                break;
            case 9:
                targetClass = CancelRequestHandleSample.class;
                break;
            case 10:
                targetClass = SynchronousClientSample.class;
                break;
        }
        if (targetClass != null)
            startActivity(new Intent(this, targetClass));
    }
}
