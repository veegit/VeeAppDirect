package com.vee.appdirect.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vee.appdirect.server.beans.FXRate;

public class FetchAllPricesServlet extends HttpServlet {
	private static final String PATH = "http://www.ecb.int/stats/eurofxref/eurofxref-daily.xml";
	private static final String SOURCE_DATE_FORMAT_STR = "yyyy-MM-dd";
	private static final SimpleDateFormat SOURCE_DATE_FORMAT = new SimpleDateFormat(SOURCE_DATE_FORMAT_STR);
	private static final long serialVersionUID = -597731200175989406L;
	private static Logger log = LogManager.getLogger(FetchAllPricesServlet.class);
	
	private ConcurrentHashMap<Date, List<FXRate>> cache = 
		new ConcurrentHashMap<Date, List<FXRate>>();
	
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
		String dateStr = req.getParameter("date");
		String xml = "";
		if(dateStr == null) {
			Calendar calendar = Calendar.getInstance();
			if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || 
					calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				calendar.add(Calendar.DAY_OF_WEEK,
						(calendar.get(Calendar.DAY_OF_WEEK)%7+1)*-1);
				log.info("Is Weekend " + calendar.getTime());
			}
			dateStr = SOURCE_DATE_FORMAT.format(calendar.getTime());
			log.info(dateStr);
		}
		try {
			List<FXRate> fxRates = cache.get(SOURCE_DATE_FORMAT.parse(dateStr));
			if(fxRates == null) {
				log.info("Cache Miss");
				fxRates = fetchRates();
				cache.put(fxRates.get(0).getUpdated(), fxRates);
			}
			else
				log.info("Cache Hit");
			xml = createXML(fxRates);
		} catch (ParseException e) {
			log.error(e.getMessage());
		}
		out.println(xml);
		out.flush();
	}
	
	private String createXML(List<FXRate> fxRates) {
		String xml = "";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document newdoc = dBuilder.newDocument();
			
			//root
			Element rootElement = newdoc.createElement("fxrates");
			newdoc.appendChild(rootElement);
			
			for (FXRate fxRate : fxRates ) {
				Element fxrateelem = newdoc.createElement("fxrate");
				rootElement.appendChild(fxrateelem);

				Element updated = newdoc.createElement("updated");
				updated.appendChild(newdoc.createTextNode(
						SOURCE_DATE_FORMAT.format(fxRate.getUpdated())));
				fxrateelem.appendChild(updated);

				Element source = newdoc.createElement("source");
				source.appendChild(newdoc.createTextNode(fxRate.getSourceCurrency()));
				fxrateelem.appendChild(source);

				Element destination = newdoc.createElement("destination");
				destination.appendChild(newdoc.createTextNode(fxRate.getDestCurrency()));
				fxrateelem.appendChild(destination);

				Element rate = newdoc.createElement("rate");
				rate.appendChild(newdoc.createTextNode(fxRate.getRate()+""));
				fxrateelem.appendChild(rate);
			}
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(newdoc), new StreamResult(writer));
			xml = writer.getBuffer().toString();
		} catch (ParserConfigurationException e) {
			log.error(e.getMessage());
		} catch (TransformerException e) {
			log.error(e.getMessage());
		}
		return xml;
	}
	
	private List<FXRate> fetchRates() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		List<FXRate> fxRates = new ArrayList<FXRate>();
			
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(PATH);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("Cube");

			Node node = list.item(1);
			Date updated = Calendar.getInstance().getTime();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				String timeStr = element.getAttribute("time");
				updated = SOURCE_DATE_FORMAT.parse(timeStr);
			}

			for (int i = 2; i < list.getLength(); i++) {
				node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					FXRate fxRate = new FXRate("EUR",
							element.getAttribute("currency"),
							Double.parseDouble(element.getAttribute("rate")),
							updated);
					fxRates.add(fxRate);
				}
			}
		} catch (ParserConfigurationException e) {
			log.error(e.getMessage());
		} catch (SAXException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage());
		}
		return fxRates;
	}
}

