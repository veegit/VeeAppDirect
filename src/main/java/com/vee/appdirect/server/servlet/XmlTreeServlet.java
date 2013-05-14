package com.vee.appdirect.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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

public class XmlTreeServlet extends HttpServlet {
	private static final String PATH = "http://www.ecb.int/stats/eurofxref/eurofxref-daily.xml";
	private static final long serialVersionUID = -597731200175989406L;
	private static Logger log = LogManager.getLogger(XmlTreeServlet.class);
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
		out.println(createXML());
		out.flush();
	}
	
	private synchronized String createXML() {
		String xml = "";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document newdoc = dBuilder.newDocument();
			
			//root
			Element rootElement = newdoc.createElement("fxrates");
			newdoc.appendChild(rootElement);
			
			Document doc = dBuilder.parse(PATH);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("Cube");
			
			Node node = list.item(1);
			String timeStr=  "";
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				timeStr = element.getAttribute("time");
			}
				
			for (int i = 2; i < list.getLength(); i++) {
				node = list.item(i);
				Element fxrate = newdoc.createElement("fxrate");
				rootElement.appendChild(fxrate);
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
		 			
					Element time = newdoc.createElement("time");
		 			time.appendChild(newdoc.createTextNode(timeStr));
		 			fxrate.appendChild(time);
		 			
		 			Element source = newdoc.createElement("source");
		 			source.appendChild(newdoc.createTextNode("EUR"));
	 				fxrate.appendChild(source);
	 				
		 			Element destination = newdoc.createElement("destination");
		 			destination.appendChild(newdoc.createTextNode(element.getAttribute("currency")));
	 				fxrate.appendChild(destination);
	 				
	 				Element rate = newdoc.createElement("rate");
	 				rate.appendChild(newdoc.createTextNode(element.getAttribute("rate")));
	 				fxrate.appendChild(rate);
				}
			}
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(newdoc), new StreamResult(writer));
			xml = writer.getBuffer().toString();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return xml;
	}
}
