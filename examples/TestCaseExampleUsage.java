import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.test.InstrumentationTestCase;
import android.util.Log;

// Credits to Wuyexiong <forever_crying@qq.com>
// See: https://github.com/loopj/android-async-http/pull/236
public class TestCaseExampleUsage extends InstrumentationTestCase
{
	protected String TAG = TestCaseExampleUsage.class.getSimpleName();

	public void testAsync() throws Throwable
	{
		final CountDownLatch signal = new CountDownLatch(1);
		runTestOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				AsyncHttpClient client = new AsyncHttpClient();

				client.get("http://www.google.com", new AsyncHttpResponseHandler()
				{
					@Override
					public void onStart()
					{
						Log.v(TAG , "onStart");
					}

					@Override
					public void onSuccess(String response)
					{
						Log.v(TAG , "onSuccess");
						System.out.println(response);
					}

					@Override
					public void onFailure(Throwable error, String content)
					{
						Log.e(TAG , "onFailure error : " + error.toString() + "content : " + content);
					}

					@Override
					public void onFinish()
					{
						Log.v(TAG , "onFinish");
						signal.countDown();
					}
				});

				try {
					signal.await(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
				}
				Log.v(TAG , "TestCaseExampleUsage Over");
			}
		});
	}
}