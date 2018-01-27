package com.ag;

import com.ag.data.ChatStore;

import android.content.Context;

public class Messola extends android.app.Application {
    public static String TAG = "SimpleSMS";
	private static Messola instance;
	
	public Messola() {
		instance = this;
	}
	
	public static Context getContext() {
		return instance.getApplicationContext();
	}
	
	public static ChatStore getConversationStore() {
		// TODO: Implement it this way.
		return null;
	}
}