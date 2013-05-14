package com.vee.appdirect.client.fx;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.HttpProxy;
import com.sencha.gxt.data.client.loader.XmlReader;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.LoaderHandler;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
 
public class FXGrid implements IsWidget, EntryPoint {
 
  interface XmlAutoBeanFactory extends AutoBeanFactory {
    static XmlAutoBeanFactory instance = GWT.create(XmlAutoBeanFactory.class);
 
    AutoBean<FXRateCollection> items();
 
    AutoBean<ListLoadConfig> loadConfig();
 
  }
 
  interface FXRate {
    @PropertyName("source")
    String getSourceCurrency();
    
    @PropertyName("destination")
    String getDestCurrency();
 
    @PropertyName("rate")
    Double getRate();
 
    @PropertyName("time")
    String getUpdated();
  }
 
  interface FXRateCollection {
    @PropertyName("fxrate")
    List<FXRate> getValues();
  }
 
  interface FXRateProperties extends PropertyAccess<FXRate> {
 
    ValueProvider<FXRate, String> sourceCurrency();
 
    ValueProvider<FXRate, String> destCurrency();
 
    ValueProvider<FXRate, Double> rate();
 
    ValueProvider<FXRate, String> updated();
  }
 
  @Override
  public void onModuleLoad() {
    RootPanel.get().add(this);
  }
 
  @Override
  public Widget asWidget() {
    String path = GWT.getModuleBaseURL() + "fx/xmltree";
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, path);
    HttpProxy<ListLoadConfig> proxy = new HttpProxy<ListLoadConfig>(builder);
 
    XmlReader<ListLoadResult<FXRate>, FXRateCollection> reader = new XmlReader<ListLoadResult<FXRate>, FXRateCollection>(
        XmlAutoBeanFactory.instance, FXRateCollection.class) {
      protected com.sencha.gxt.data.shared.loader.ListLoadResult<FXRate> createReturnData(Object loadConfig,
          FXRateCollection records) {
        return new ListLoadResultBean<FXRate>(records.getValues());
      };
    };
 
    ListStore<FXRate> store = new ListStore<FXRate>(new ModelKeyProvider<FXRate>() {
      @Override
      public String getKey(FXRate item) {
        return item.getDestCurrency();
      }
    });
 
    final ListLoader<ListLoadConfig, ListLoadResult<FXRate>> loader = new ListLoader<ListLoadConfig, ListLoadResult<FXRate>>(
        proxy, reader);
    loader.useLoadConfig(XmlAutoBeanFactory.instance.create(ListLoadConfig.class).as());
    loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, FXRate, ListLoadResult<FXRate>>(store));
 
    FXRateProperties props = GWT.create(FXRateProperties.class);
 
    ColumnConfig<FXRate, String> cc1 = new ColumnConfig<FXRate, String>(props.sourceCurrency(), 100, "From");
    ColumnConfig<FXRate, String> cc2 = new ColumnConfig<FXRate, String>(props.destCurrency(), 165, "To");
    ColumnConfig<FXRate, Double> cc3 = new ColumnConfig<FXRate, Double>(props.rate(), 100, "FX Rate");
    ColumnConfig<FXRate, String> cc4 = new ColumnConfig<FXRate, String>(props.updated(), 50, "Last Updated");
 
    List<ColumnConfig<FXRate, ?>> l = new ArrayList<ColumnConfig<FXRate, ?>>();
    l.add(cc1);
    l.add(cc2);
    l.add(cc3);
    l.add(cc4);
    ColumnModel<FXRate> cm = new ColumnModel<FXRate>(l);
 
    cc1.setWidth(50);
    cc2.setWidth(50);
    
    Grid<FXRate> grid = new Grid<FXRate>(store, cm);
    grid.getView().setForceFit(true);
    grid.setBorders(true);
    grid.setLoadMask(true);
    grid.setLoader(loader);
    grid.getView().setEmptyText("Please hit the load button.");
 
    final FramedPanel cp = new FramedPanel();
    cp.setWidget(grid);
    cp.setPixelSize(500, 400);
    cp.setCollapsible(true);
    cp.setAnimCollapse(true);
    cp.addStyleName("margin-10");
    cp.setButtonAlign(BoxLayoutPack.CENTER);
    loader.addLoaderHandler(new LoaderHandler<ListLoadConfig, ListLoadResult<FXRate>>() {
		
		@Override
		public void onLoad(LoadEvent<ListLoadConfig, ListLoadResult<FXRate>> event) {
			cp.setHeadingText("FX Rates as of " + event.getLoadResult().getData().get(0).getUpdated() );
		}
		
		@Override
		public void onLoadException(LoadExceptionEvent<ListLoadConfig> event) {
			
		}
		
		@Override
		public void onBeforeLoad(BeforeLoadEvent<ListLoadConfig> event) {
			
		}
	});
    loader.load();
    return cp;
  }
 
}