package com.jockeyjs;

import com.google.gson.Gson;

import org.xwalk.core.XWalkView;

public class DefaultJockeyImpl extends JockeyImpl {
	
	private int messageCount = 0;
	private Gson gson = new Gson();

	@Override
	public void send(String type, XWalkView toWebView, Object withPayload,
					 JockeyCallback complete) {
		int messageId = messageCount;

		if (complete != null) {
			add(messageId, complete);
		}

		if (withPayload != null) {
			withPayload = gson.toJson(withPayload);
		}

		String url = String.format("javascript:Jockey.trigger(\"%s\", %d, %s)",
				type, messageId, withPayload);
		toWebView.loadUrl(url,null);

		++messageCount;
	}

	@Override
	public void triggerCallbackOnWebView(WebView webView, int messageId) {
		String url = String.format("javascript:Jockey.triggerCallback(\"%d\")",
				messageId);
		webView.loadUrl(url);
	}

}
