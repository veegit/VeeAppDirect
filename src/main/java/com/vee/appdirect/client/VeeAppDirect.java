package com.vee.appdirect.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.vee.appdirect.client.fx.FXGrid;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class VeeAppDirect implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get().add(new FXGrid());
	}
}
