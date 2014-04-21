package com.loopj.android.http.sample;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class Redirect302Sample extends GetSample {

    private boolean enableRedirects = true;
    private boolean enableRelativeRedirects = true;
    private boolean enableCircularRedirects = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, "Enable redirects").setCheckable(true);
        menu.add(Menu.NONE, 1, Menu.NONE, "Enable relative redirects").setCheckable(true);
        menu.add(Menu.NONE, 2, Menu.NONE, "Enable circular redirects").setCheckable(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemEnableRedirects = menu.findItem(0);
        if (menuItemEnableRedirects != null)
            menuItemEnableRedirects.setChecked(enableRedirects);
        MenuItem menuItemEnableRelativeRedirects = menu.findItem(1);
        if (menuItemEnableRelativeRedirects != null)
            menuItemEnableRelativeRedirects.setChecked(enableRelativeRedirects);
        MenuItem menuItemEnableCircularRedirects = menu.findItem(2);
        if (menuItemEnableCircularRedirects != null)
            menuItemEnableCircularRedirects.setChecked(enableCircularRedirects);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isCheckable()) {
            item.setChecked(!item.isChecked());
            if (item.getItemId() == 0) {
                enableRedirects = item.isChecked();
            } else if (item.getItemId() == 1) {
                enableRelativeRedirects = item.isChecked();
            } else if (item.getItemId() == 2) {
                enableCircularRedirects = item.isChecked();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getDefaultURL() {
        return "http://httpbin.org/redirect/6";
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_redirect_302;
    }

    @Override
    public AsyncHttpClient getAsyncHttpClient() {
        AsyncHttpClient ahc = super.getAsyncHttpClient();
        HttpClient client = ahc.getHttpClient();
        if (client instanceof DefaultHttpClient) {
            Toast.makeText(this,
                    String.format("redirects: %b\nrelative redirects: %b\ncircular redirects: %b",
                            enableRedirects, enableRelativeRedirects, enableCircularRedirects),
                    Toast.LENGTH_SHORT
            ).show();
            ahc.setEnableRedirects(enableRedirects, enableRelativeRedirects, enableCircularRedirects);
        }
        return ahc;
    }
}
