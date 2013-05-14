package com.vee.appdirect.server.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BuyOrderServlet extends HttpServlet {
	
	private static final long serialVersionUID = -597731200112389406L;
	private static final String KEY =  "veefx-5654";
	private static final String SECRET =  "3vu4goziyPP8huM9";
	
	private static Logger log = LogManager.getLogger(XmlTreeServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String urlStr = URLDecoder.decode(req.getParameter("url"),"UTF-8");
		log.info(urlStr);
		String responseStr = signAndSendRequest(urlStr);
		logAndSendResponse(resp,responseStr);
	}
	
	private String signAndSendRequest(String urlStr) {
		String response = "";
		OAuthConsumer consumer = new DefaultOAuthConsumer(KEY,SECRET);
		try {
			URL url = new URL(urlStr);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			consumer.sign(request);
			request.connect();
			InputStream in = request.getInputStream();
			BufferedReader buff = new BufferedReader(new InputStreamReader(in));
			String inputLine;
			while ((inputLine = buff.readLine()) != null)
				response += inputLine;
			log.info(response);
			buff.close();
			in.close();
			request.disconnect();
		}
		 catch (OAuthMessageSignerException e) {
			log.error(e.getMessage());
		} catch (OAuthExpectationFailedException e) {
			log.error(e.getMessage());
		} catch (OAuthCommunicationException e) {
			log.error(e.getMessage());
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return response;
	}
	
	private void logAndSendResponse(HttpServletResponse resp, String responseString ) {
		resp.setContentType("text/xml");
		PrintWriter out;
		try {
			out = resp.getWriter();
			out.println(createXML(responseString));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String createXML(String responseString ) {
		String xml = "";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document newdoc = dBuilder.newDocument();
			
			//root
			Element rootElement = newdoc.createElement("result");
			newdoc.appendChild(rootElement);
			
			InputStream in = new ByteArrayInputStream(responseString.getBytes());
			Document doc = dBuilder.parse(in);
			doc.getDocumentElement().normalize();
			NodeList namelist = doc.getElementsByTagName("firstName");
			String name = "",company = "";
			if(namelist.getLength() > 0)
				name = namelist.item(0).getTextContent();
			
			NodeList companylist = doc.getElementsByTagName("name");
			if(companylist.getLength() > 0)
				company = companylist.item(0).getTextContent();
			
			Element success = newdoc.createElement("success");
			Boolean successVal = Boolean.TRUE;
			success.appendChild(newdoc.createTextNode(successVal.toString().
					toLowerCase()));
 			rootElement.appendChild(success);
 			
 			Element message = newdoc.createElement("message");
 			String messageVal = String.format("Account creation successful for %s by %s", 
 					company, name); 
 			message.appendChild(newdoc.createTextNode(messageVal));
 			rootElement.appendChild(message);
 			
 			Element acctid = newdoc.createElement("accountidentifier");
 			String acctidVal = company.
				replaceAll("\\s", "").toLowerCase() + (int)(Math.random() * 1000);
 			acctid.appendChild(newdoc.createTextNode(acctidVal));
 			rootElement.appendChild(acctid);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(newdoc), new StreamResult(writer));
			xml = writer.getBuffer().toString();
			log.info(xml);
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

