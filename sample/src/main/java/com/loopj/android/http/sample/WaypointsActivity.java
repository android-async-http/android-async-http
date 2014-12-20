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

    private static final SampleConfig[] samplesConfig = new SampleConfig[]{
            new SampleConfig(R.string.title_get_sample, GetSample.class),
            new SampleConfig(R.string.title_post_sample, PostSample.class),
            new SampleConfig(R.string.title_delete_sample, DeleteSample.class),
            new SampleConfig(R.string.title_put_sample, PutSample.class),
            new SampleConfig(R.string.title_patch_sample, PatchSample.class),
            new SampleConfig(R.string.title_json_sample, JsonSample.class),
            new SampleConfig(R.string.title_json_streamer_sample, JsonStreamerSample.class),
            new SampleConfig(R.string.title_sax_example, SaxSample.class),
            new SampleConfig(R.string.title_file_sample, FileSample.class),
            new SampleConfig(R.string.title_binary_sample, BinarySample.class),
            new SampleConfig(R.string.title_gzip_sample, GzipSample.class),
            new SampleConfig(R.string.title_redirect_302, Redirect302Sample.class),
            new SampleConfig(R.string.title_threading_timeout, ThreadingTimeoutSample.class),
            new SampleConfig(R.string.title_cancel_all, CancelAllRequestsSample.class),
            new SampleConfig(R.string.title_cancel_handle, CancelRequestHandleSample.class),
            new SampleConfig(R.string.title_synchronous, SynchronousClientSample.class),
            new SampleConfig(R.string.title_intent_service_sample, IntentServiceSample.class),
            new SampleConfig(R.string.title_post_files, FilesSample.class),
            new SampleConfig(R.string.title_persistent_cookies, PersistentCookiesSample.class),
            new SampleConfig(R.string.title_custom_ca, CustomCASample.class),
            new SampleConfig(R.string.title_retry_handler, RetryRequestSample.class),
            new SampleConfig(R.string.title_range_sample, RangeResponseSample.class),
            new SampleConfig(R.string.title_401_unauth, Http401AuthSample.class),
            new SampleConfig(R.string.title_pre_post_processing, PrePostProcessingSample.class),
            new SampleConfig(R.string.title_content_type_http_entity, ContentTypeForHttpEntitySample.class),
            new SampleConfig(R.string.title_resume_download, ResumeDownloadSample.class),
            new SampleConfig(R.string.title_digest_auth, DigestAuthSample.class),
            new SampleConfig(R.string.title_use_pool_thread, UsePoolThreadSample.class)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getTitlesList()));
    }

    private List<String> getTitlesList() {
        List<String> titles = new ArrayList<String>();
        for (SampleConfig config : samplesConfig) {
            titles.add(getString(config.titleId));
        }
        return titles;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position >= 0 && position < samplesConfig.length)
            startActivity(new Intent(this, samplesConfig[position].targetClass));
    }

    private static class SampleConfig {

        final int titleId;
        final Class targetClass;

        SampleConfig(int titleId, Class targetClass) {
            this.titleId = titleId;
            this.targetClass = targetClass;
        }

    }

}
