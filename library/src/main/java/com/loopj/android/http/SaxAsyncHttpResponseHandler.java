package com.loopj.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SaxAsyncHttpResponseHandler<T extends DefaultHandler> extends AsyncHttpResponseHandler {
	
	private T handler = null;
	
	public SaxAsyncHttpResponseHandler(T t) {
		super();
		this.handler = t;
	}
	
	@Override
    protected byte[] getResponseData(HttpEntity entity) throws IOException {
        if (entity != null) {
            InputStream instream = entity.getContent();
            InputStreamReader inputStreamReader = null;
            if (instream != null) {
                try {
                	SAXParserFactory sfactory = SAXParserFactory.newInstance();
    				SAXParser sparser = sfactory.newSAXParser();
    				XMLReader rssReader = sparser.getXMLReader();
    				rssReader.setContentHandler(handler);
    				inputStreamReader = new InputStreamReader(instream, DEFAULT_CHARSET);
    				rssReader.parse(new InputSource(inputStreamReader));
                } catch (SAXException e) { /*ignore*/
                } catch (ParserConfigurationException e) { /*ignore*/ 
                } finally {
                	AsyncHttpClient.silentCloseInputStream(instream);
                	if (inputStreamReader != null) {
                		try {
                			inputStreamReader.close();
                		} catch (IOException e) { /*ignore*/ }
                	}
                    
                }
            }
        }
        return null;
    }
	
	public abstract void onSuccess(int statusCode, Header[] headers, T t);

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		onSuccess(statusCode, headers, handler);
	}

	public abstract void onFailure(int statusCode, Header[] headers, T t);

	@Override
	public void onFailure(int statusCode, Header[] headers,
			byte[] responseBody, Throwable error) {
		onSuccess(statusCode, headers, handler);
	}
}
