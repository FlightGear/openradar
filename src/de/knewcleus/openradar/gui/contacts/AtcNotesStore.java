/**
 * Copyright (C) 2015 Wolfram Wagner
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
package de.knewcleus.openradar.gui.contacts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * This class manages all data about a contact that need to be stored locally.
 * 
 * @author Wolfram Wagner
 *
 */

public class AtcNotesStore {

	private Map<String, AtcSettings> store = new TreeMap<>();

	private static final Logger log = Logger.getLogger(AtcNotesStore.class);
	
	/**
	 * This method processes the content of the former ATC notes file
	 * This method will be removed end of 2016
	 * 
	 * @param props
	 */
	private synchronized void loadOldFile() {
		File atcCommentFile = new File("settings" + File.separator + "atcComments.xml");
		if (atcCommentFile.exists()) {
			FileInputStream fis = null;

			try {
				fis = new FileInputStream(atcCommentFile);
				Properties props = new Properties();
				props.loadFromXML(fis);
				for (Object op : props.keySet()) {
					String key = (String) op;
					if (key.indexOf(".") > -1) {
						String callSign = key.substring(0, key.indexOf("."));
						if(!store.containsKey(callSign)) {
							String atcComment = props.getProperty(callSign + ".atcNote", "");
							if(atcComment==null) {
								atcComment = props.getProperty(callSign, "");
							}
							boolean hasRadio = "true".equals(props.getProperty(callSign + ".fgComSupport", "false"));
							if(!atcComment.isEmpty() || hasRadio) {
								// only records that are not the defaults
								store.put(callSign, new AtcSettings(callSign, new Date(), atcComment, hasRadio));
							}
						}
					}
				}
				
				saveStore();
				fis.close();
				fis = null;
				// rename the old file
	           if (atcCommentFile.exists()) {
	                File backup = new File("settings" + File.separator + "atcComments.old");
	                if (!backup.exists()) {
	                	atcCommentFile.renameTo(backup);
	                }
	            }
			} catch (IOException e) {
				log.error("Error while loading atc notes!", e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		} 
	}

	/**
	 * Loads the existing store from disk.
	 */
	public synchronized void loadStore() {
		File atcCommentFile = new File("settings" + File.separator + "contactData.xml");
		if(!atcCommentFile.exists()) {
			// intial conversion
			loadOldFile();
		} else {
			// use existing store
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			GregorianCalendar gcEarliestDate = new GregorianCalendar();
			gcEarliestDate.add(Calendar.MONTH, -6);
			
		    try {
	            Document doc = new SAXBuilder().build(new InputStreamReader(new FileInputStream(atcCommentFile),"UTF-8"));

	            List<Element> settingsList = doc.getRootElement().getChildren("actSetting");
	            for(Element eSetting : settingsList) {
	            	String callSign = eSetting.getAttributeValue("callsign");
	            	Date lastChange = sdf.parse(eSetting.getAttributeValue("lastChange"));
	            	if(gcEarliestDate.getTime().before(lastChange)) {
	            		// import only not expired records
		            	String atcComment = eSetting.getChildText("atcComment");
		            	if(atcComment==null) {
		            		atcComment="";
		            	}
		            	boolean radioSupport = "true".equals(eSetting.getChildText("radioSupport"));
		            	store.put(callSign, new AtcSettings(callSign, lastChange, atcComment, radioSupport));
	            	}
		    	}
	        } catch (JDOMException e) {
	            log.error("Error while parsing "+atcCommentFile.getAbsolutePath(),e);
	        } catch (ParseException e) {
	            log.error("Error while parsing "+atcCommentFile.getAbsolutePath()+" The last change date could not be parsed.",e);
	        } catch (IOException e) {
	            log.error("Error while reading "+atcCommentFile.getAbsolutePath(),e);
	        }		
		}
	}

	/**
	 * Stores the content to disk.
	 */
	public synchronized void saveStore() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				
        Document doc = new Document();
        Element root = new Element("atcSettings");
        doc.addContent(root);

        for(AtcSettings s : store.values()) {
        	if(!s.radioSupport && s.getAtcComment().trim().isEmpty()) {
        		// omit default settings
        		continue;
        	}
        	
        	Element eSetting = new Element("actSetting");
        	eSetting.setAttribute("callsign", s.getCallsign());
        	eSetting.setAttribute("lastChange", sdf.format(s.getLastChange()));
        	Element eComment = new Element("atcComment");
        	if(!s.getAtcComment().isEmpty()) {
        		eComment.setText(s.getAtcComment());
        	}
    		eSetting.addContent(eComment);
    		Element eRadio = new Element("radioSupport");
        	eRadio.setText(s.isRadioSupport() ? "true" : "false");
        	eSetting.addContent(eRadio);
        	
      		root.addContent(eSetting);
        }
        
        
        OutputStreamWriter sw = null;
		try {
			sw = new OutputStreamWriter(new FileOutputStream("settings" + File.separator + "contactData.xml"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Encoding not supported",e);
		} catch (FileNotFoundException e) {
			log.error("File not found",e);
		}
        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(doc, sw);
        } catch(Exception e) {
            log.error("Error while writing "+"settings" + File.separator + "contactData.xml", e);
        } finally {
        	if(sw!=null) {
        		try {
					sw.close();
				} catch (IOException e) {}
        	}
        }

	}

	public void restoreStaticData(GuiRadarContact c) {
		AtcSettings s = store.get(c.getCallSign());
		if(s!=null) {
			c.setAtcComment(s.getAtcComment());
			c.setFgComSupport(s.isRadioSupport());
		}
	}


	public synchronized void updateData(GuiRadarContact c) {
		AtcSettings s = store.get(c.getCallSign());
		if (s == null) {
			s = new AtcSettings(c);
			store.put(c.getCallSign(), s);
		} else {
			s.setAtcComment(c.getAtcComment());
			s.setRadioSupport(c.hasFgComSupport());
		}
	}

	/*
	 * #########################################################################
	 */

	private class AtcSettings {
		public final String callSign;
		private volatile String atcComment = "";
		private volatile boolean radioSupport = false;
		private volatile Date lastChange;

		public AtcSettings(GuiRadarContact c) {
			this.callSign = c.getCallSign();
			this.atcComment = c.getAtcComment();
			this.radioSupport = c.hasFgComSupport();
			this.lastChange = new Date();
		}

		public AtcSettings(String callSign, Date lastChange, String atcComment, boolean radioSupport) {
			this.callSign = callSign;
			this.atcComment = atcComment;
			this.radioSupport = radioSupport;
			this.lastChange = lastChange;
		}

		public String getCallsign() {
			return callSign;
		}

		public String getAtcComment() {
			return atcComment;
		}

		public void setAtcComment(String atcComment) {
			if (!this.atcComment.equals(atcComment)) {
				this.lastChange = new Date();
				this.atcComment = atcComment;
			}

		}

		public boolean isRadioSupport() {
			return radioSupport;
		}

		public void setRadioSupport(boolean radioSupport) {
			if (this.radioSupport != radioSupport) {
				this.lastChange = new Date();
				this.radioSupport = radioSupport;
			}
		}

		public Date getLastChange() {
			return lastChange;
		}
	}

}
