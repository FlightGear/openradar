package de.knewcleus.openradar.gui.flightstrips.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public abstract class RegExpEdit extends JTextField {

	private static final long serialVersionUID = 1L;

	public void init() {
		String s = fetchStringValue(); 
		setText(s);
		int maxLength = fetchMaxLength();
		if ((maxLength <= 0) && s.isEmpty()) maxLength = 3;
		if (maxLength > 0) setColumns(maxLength);
		setToolTipText(fetchToolTipText());
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				putStringValue(getText());
			}
		});
		((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				String text = getText();
				text = text.substring(0, offset) + string + text.substring(offset);
				if (text.matches(fetchRegExp())) {
					fb.insertString(offset, string, attr);
				}
			}

			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
				String text = getText();
				text = text.substring(0, offset) + string + text.substring(offset + length);
				if (text.matches(fetchRegExp())) {
					fb.replace(offset, length, string, attrs);
				}
			}

			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				String text = getText();
				text = text.substring(0, offset) + text.substring(offset + length);
				if (text.matches(fetchRegExp())) {
					fb.remove(offset, length);
				}
			}
			
		});
	}
	
	protected abstract String fetchStringValue(); 
	protected abstract void putStringValue (String value);
	
	protected int fetchMaxLength() {
		return -1;
	}
	
	protected String fetchRegExp() {
		return ".*";
	}
	
	protected String fetchToolTipText() { 
		return "";
	} 
}
