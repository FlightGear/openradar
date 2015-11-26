/**
 * Copyright (C) 2014-2015 Wolfram Wagner 
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.util;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DocumentFilter;

public class RegexTextField extends JFormattedTextField {

    private static final long serialVersionUID = 0L;
        
    public RegexTextField(int columns, String mask) throws ParseException {
        super();
        setFormatter(new RegexFormatter(mask));
        setColumns(columns);
    }
    
    public class RegexFormatter extends DefaultFormatter {

        private static final long serialVersionUID = 1L;
        private final String pattern;
        private DocumentFilter df = new RegexDocumentFilter();
        
        public RegexFormatter(String pattern) {
            this.pattern=pattern;
            setCommitsOnValidEdit(true);
        }
        
        @Override
        public Object stringToValue(String string) throws ParseException {
            if(string.matches(pattern)) {
                return string;
            } else {
                return null;
            }
        }
        
        @Override
        public String valueToString(Object value) throws ParseException {
            return (String)value;
        }
        
        @Override
        protected DocumentFilter getDocumentFilter() {
            return df;
        }
        
        class RegexDocumentFilter extends DocumentFilter {
            
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String result = fb.getDocument().getText(0, offset)+string+ fb.getDocument().getText(offset,fb.getDocument().getLength()-1);
                if(result.matches(pattern)) {
                    super.insertString(fb, offset, string, attr);
                }fb.getDocument().getText(0, fb.getDocument().getLength() - 1);
            }
            
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String result = fb.getDocument().getText(0, offset)+text;
                if(offset<fb.getDocument().getLength()) {
                    result = result + fb.getDocument().getText(offset,fb.getDocument().getLength()-1);
                }
                if(result.matches(pattern)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        }    
    }
 }
