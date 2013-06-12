package com.vee.appdirect.server.servlet;

public class FXRate {
	String sourceCurrency;
	String destCurrency;
	Double rate;
	String updated;
	
	FXRate(String sourceCurrency,String destCurrency, Double rate, String updated) {
		this.sourceCurrency = sourceCurrency;
		this.destCurrency = destCurrency;
		this.rate = rate;
		this.updated = updated;
	}
	
	String getSourceCurrency() {
		return sourceCurrency;
	}
    
    String getDestCurrency() {
    	return destCurrency;
    }
 
    Double getRate() {
    	return rate;
    }
 
    String getUpdated() {
    	return updated;
    }
}
