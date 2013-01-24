package com.loopj.android.http;

import java.io.UnsupportedEncodingException;

/**
 * Used to intercept and handle the responses from requests made using 
 * {@link AsyncHttpClient}. The {@link #onSuccess(String)} method is 
 * designed to be anonymously overridden with your own response handling code.
 * <p>
 * Additionally, you can override the {@link #onFailure(String, Throwable)},
 * {@link #onStart()}, and {@link #onFinish()} methods as required.
 * <p>
 * For example:
 * <p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.get("http://www.google.com", new AsyncHttpResponseHandler() {
 *     &#064;Override
 *     public void onStart() {
 *         // Initiated the request
 *     }
 *
 *     &#064;Override
 *     public void onSuccess(String responseBody ) {
 *         // Successfully got a response
 *     }
 * 
 *     &#064;Override
 *     public void onFailure(String responseBody, Throwable e) {
 *         // Response failed :(
 *     }
 *
 *     &#064;Override
 *     public void onFinish() {
 *         // Completed the request (either success or failure)
 *     }
 * });
 * </pre>
 */
public class TextHttpResponseHandler extends AsyncHttpResponseHandler {

    private String _encoding;
    /**
     * Creates a new TextHttpResponseHandler
     */
    
    public TextHttpResponseHandler()
    {
        this("UTF-8");
    }
    
    public TextHttpResponseHandler(String encoding) {
        super();
        _encoding = encoding;
    }
    //
    // Callbacks to be overridden, typically anonymously
    //


    /**
     * Fired when a request returns successfully, override to handle in your own
     * code
     * 
     * @param responseBody the body of the HTTP response from the server
     */
    public void onSuccess(String responseBody) {
    }
    
    /**
     * Fired when a request returns successfully, override to handle in your own
     * code
     * 
     * @param statusCode the status code of the response
     * @param responseBody the body of the HTTP response from the server
     */
    public void onSuccess(int statusCode, String responseBody) {
      onSuccess( responseBody );
    }

    /**
     * Fired when a request fails to complete, override to handle in your own
     * code
     * 
     * @param responseBody the response body, if any
     * @param error the underlying cause of the failure
     */    
    public void onFailure(String responseBody, Throwable e) {
    }
    
    /**
     * Fired when a request fails to complete, override to handle in your own
     * code
     * 
     * @param statusCode the status code of the response
     * @param responseBody the response body, if any
     * @param error the underlying cause of the failure
     */    
    public void onFailure(int statusCode, String responseBody, Throwable e) {
      onFailure( responseBody, e );
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    @Override
    protected void handleSuccessMessage(int statusCode,byte[] responseBody) {
        try {
            onSuccess(statusCode, new String(responseBody, _encoding));
        } catch (UnsupportedEncodingException e) {
            onFailure(0, (String) null, e);
        }
    }

    @Override
    protected void handleFailureMessage(int statusCode, byte[] responseBody, Throwable error) {
        try {
            onFailure(statusCode, new String(responseBody, _encoding), error);
        } catch (UnsupportedEncodingException e) {
            onFailure(0, (String) null, e);
        }
    }
    
}
