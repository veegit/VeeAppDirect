package com.vee.appdirect.client.fx;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;

public class MyPortal implements IsWidget, EntryPoint {

	PortalLayoutContainer portal = new PortalLayoutContainer(3);

	@Override
	public void onModuleLoad() {
		RootPanel.get().add(this);
		
	}

	@Override
	public Widget asWidget() {
		portal.getElement().getStyle().setBackgroundColor("white");
	    portal.setColumnWidth(0, .60);
	    portal.setColumnWidth(1, .20);
	    portal.setColumnWidth(2, .20);
	 
	    Portlet portlet = new Portlet();
	    configPortlet(portlet);
	    FXGrid fxGrid = new FXGrid();
	    portlet.add(fxGrid.createGrid(portlet));
	    portal.add(portlet,0);
	    return portal;
	}
	
	private void configPortlet(Portlet portlet) {
		portlet.setCollapsible(true);
		portlet.setAnimCollapse(false);
	}
	
}
