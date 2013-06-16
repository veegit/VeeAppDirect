package com.vee.appdirect.server.beans;

import java.util.Date;

public class FXRate {
	String sourceCurrency;
	String destCurrency;
	Double rate;
	Date updated;
	
	public FXRate(String sourceCurrency,String destCurrency, Double rate, Date updated) {
		this.sourceCurrency = sourceCurrency;
		this.destCurrency = destCurrency;
		this.rate = rate;
		this.updated = updated;
	}
	
	public String getSourceCurrency() {
		return sourceCurrency;
	}
    
	public String getDestCurrency() {
    	return destCurrency;
    }
 
	public Double getRate() {
    	return rate;
    }
 
	public Date getUpdated() {
    	return updated;
    }
}
