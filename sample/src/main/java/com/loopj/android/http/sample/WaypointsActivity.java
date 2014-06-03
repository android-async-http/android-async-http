/*
    Android Asynchronous Http Client Sample
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

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
