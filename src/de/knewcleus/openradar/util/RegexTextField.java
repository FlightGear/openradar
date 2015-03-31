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
