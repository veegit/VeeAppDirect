package com.vee.appdirect.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class FetchAllPricesServlet extends HttpServlet {
	private static final String PATH = "http://www.ecb.int/stats/eurofxref/eurofxref-daily.xml";
	private static final String SOURCE_DATE_FORMAT_STR = "yyyy-mm-dd";
	private static final SimpleDateFormat SOURCE_DATE_FORMAT = new SimpleDateFormat(SOURCE_DATE_FORMAT_STR);
	private static final long serialVersionUID = -597731200175989406L;
	private static Logger log = LogManager.getLogger(FetchAllPricesServlet.class);
	
	static {
		System.setProperty("http.proxyHost", "gdcnetcache.us.db.com");
		System.setProperty("http.proxyPort", "3128");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/xml");
		PrintWriter out = resp.getWriter();
		out.println();
		out.flush();
	}
}

