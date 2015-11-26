/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.navaids.ead;

import java.awt.Shape;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.knewcleus.fgfs.navaids.AbstractDBParser;
import de.knewcleus.fgfs.navaids.DBParserException;

public abstract class AbstractSDOParser extends AbstractDBParser {

	public AbstractSDOParser(Shape geographicBounds) {
		super(geographicBounds);
	}

	@Override
	public void read(InputStream inputStream) throws DBParserException {
		DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setCoalescing(true);
		documentBuilderFactory.setIgnoringComments(true);
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);
		
		try {
			DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();
			Document document=documentBuilder.parse(inputStream);
			processDocument(document);
		} catch (ParserConfigurationException e) {
			throw new DBParserException(e);
		} catch (SAXException e) {
			throw new DBParserException(e);
		} catch (IOException e) {
			throw new DBParserException(e);
		}
	}
	
	protected void processDocument(Document document) throws DBParserException {
		NodeList reportResultList=document.getElementsByTagName("SdoReportResult");
		for (int i=0;i<reportResultList.getLength();i++) {
			Node reportResultNode=reportResultList.item(i);
			if (reportResultNode.getNodeType()!=Node.ELEMENT_NODE)
				continue;
			for (Node child=reportResultNode.getFirstChild();child!=null;child=child.getNextSibling()) {
				if (child.getNodeType()!=Node.ELEMENT_NODE)
					continue;
				if (!child.getNodeName().equals("Record"))
					continue;
				Element recordElement=(Element)child;
				processRecord(recordElement);
			}
		}
	}
	
	protected List<Element> getChildrenByTagName(Element parent, String name) {
		List<Element> children=new ArrayList<Element>();
		
		for (Node child=parent.getFirstChild();child!=null;child=child.getNextSibling()) {
			if (child.getNodeType()!=Node.ELEMENT_NODE)
				continue;
			Element element=(Element)child;
			if (element.getNodeName().equals(name))
				children.add(element);
		}
		return children;
	}
	
	protected void requireField(Element record, String field) throws DBParserException {
		List<Element> fieldList=getChildrenByTagName(record, field);
		if (fieldList.size()==0) {
			throw new DBParserException("Required field '"+field+"' is not present, mid="+getFieldValue(record, "mid"));
		}
		if (fieldList.size()>1) {
			throw new DBParserException("Field '"+field+"' has more than one value, mid="+getFieldValue(record, "mid"));
		}
	}
	
	protected void requireListField(Element record, String field) throws DBParserException {
		List<Element> fieldList=getChildrenByTagName(record, field);
		if (fieldList.size()<1) {
			throw new DBParserException("Required field '"+field+"' is not present, mid="+getFieldValue(record, "mid"));
		}
	}
	
	protected Element getSubrecord(Element record, String name, boolean required) throws DBParserException {
		List<Element> recordList=getChildrenByTagName(record, name);
		if (recordList.size()<1) {
			if (required) {
				throw new DBParserException("Required subrecord '"+name+"' is not present, mid="+getFieldValue(record, "mid"));
			} else {
				return null;
			}
		}
		if (recordList.size()!=1) {
			throw new DBParserException("Subrecord '"+name+"' has more than one instance, mid="+getFieldValue(record, "mid"));
		}
		return recordList.get(0);
	}
	
	protected List<Element> getSubrecords(Element record, String name) throws DBParserException {
		List<Element> recordList=getChildrenByTagName(record, name);
		return recordList;
	}
	
	protected String getFieldValue(Element record, String field, String defaultValue) throws DBParserException {
		List<Element> fieldList=getChildrenByTagName(record, field);
		if (fieldList.size()==0) {
			return defaultValue;
		}
		if (fieldList.size()!=1) {
			throw new DBParserException("Field '"+field+"' has more than one value, mid="+getFieldValue(record, "mid"));
		}
		Element fieldElement=fieldList.get(0);
		return fieldElement.getTextContent();
	}
	
	protected String getFieldValue(Element record, String field) throws DBParserException {
		List<Element> fieldList=getChildrenByTagName(record, field);
		if (fieldList.size()!=1) {
			throw new DBParserException("Required field '"+field+"' is not present, mid="+getFieldValue(record, "mid"));
		}
		Element fieldElement=fieldList.get(0);
		return fieldElement.getTextContent();
	}
	
	protected List<String> getFieldValues(Element record, String field) throws DBParserException {
		List<String> values=new ArrayList<String>();
		getFieldValues(record, field, values);
		return values;
	}
	
	protected void getFieldValues(Element record, String field, List<String> values) throws DBParserException {
		List<Element> fieldList=getChildrenByTagName(record, field);
		for (Element fieldElement: fieldList) {
			values.add(fieldElement.getTextContent());
		}
	}
	
	protected double parseLatitude(String latitude) {
		char hemisphere=latitude.charAt(latitude.length()-1);
		double sign=(hemisphere=='S'?-1.0:1.0);

		double val=0.0;
		String deg,min,sec;
		deg=latitude.substring(0,latitude.length()-1);
		if (deg.length()<=2 || deg.charAt(2)=='.')
			return sign*Double.parseDouble(deg);
		val=Double.parseDouble(deg.substring(0,2));
		min=deg.substring(2);
		if (min.isEmpty())
			return sign*val;
		if (min.length()<=2 || min.charAt(2)=='.')
			return sign*(val+Double.parseDouble(min)/60.0);
		val+=Double.parseDouble(min.substring(0,2))/60.0;
		sec=min.substring(2);
		if (sec.isEmpty())
			return sign*val;
		val+=Double.parseDouble(sec)/3600.0;
		return sign*val;
	}
	
	protected double parseLongitude(String longitude) {
		char hemisphere=longitude.charAt(longitude.length()-1);
		double sign=(hemisphere=='W'?-1.0:1.0);

		double val=0.0;
		String deg,min,sec;
		deg=longitude.substring(0,longitude.length()-1);
		if (deg.length()<=3 || deg.charAt(3)=='.')
			return sign*Double.parseDouble(deg);
		val=Double.parseDouble(deg.substring(0,3));
		min=deg.substring(3);
		if (min.isEmpty())
			return sign*val;
		if (min.length()<=2 || min.charAt(2)=='.')
			return sign*(val+Double.parseDouble(min)/60.0);
		val+=Double.parseDouble(min.substring(0,2))/60.0;
		sec=min.substring(2);
		if (sec.isEmpty())
			return sign*val;
		val+=Double.parseDouble(sec)/3600.0;
		return sign*val;
	}
	
	protected double getLatitude(Element record, String field) throws DBParserException {
		return parseLatitude(getFieldValue(record, field));
	}
	
	protected double getLongitude(Element record, String field) throws DBParserException {
		return parseLongitude(getFieldValue(record, field));
	}

	public abstract void processRecord(Element record) throws DBParserException;
}
