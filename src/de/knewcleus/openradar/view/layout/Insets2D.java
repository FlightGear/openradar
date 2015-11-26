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
package de.knewcleus.openradar.view.layout;

public class Insets2D {
	protected double topInset, bottomInset, leftInset, rightInset;

	public Insets2D() {
		this(0.0, 0.0, 0.0, 0.0);
	}
	
	public Insets2D(double topInset, double bottomInset, double leftInset, double rightInset) {
		this.topInset = topInset;
		this.bottomInset = bottomInset;
		this.leftInset = leftInset;
		this.rightInset = rightInset;
	}
	
	public Insets2D(Insets2D copy) {
		this(copy.topInset, copy.bottomInset, copy.leftInset, copy.rightInset);
	}

	public double getTopInset() {
		return topInset;
	}

	public void setTopInset(double topInset) {
		this.topInset = topInset;
	}

	public double getBottomInset() {
		return bottomInset;
	}

	public void setBottomInset(double bottomInset) {
		this.bottomInset = bottomInset;
	}

	public double getLeftInset() {
		return leftInset;
	}

	public void setLeftInset(double leftInset) {
		this.leftInset = leftInset;
	}

	public double getRightInset() {
		return rightInset;
	}

	public void setRightInset(double rightInset) {
		this.rightInset = rightInset;
	}
	
	public double getHorizontalInsets() {
		return leftInset + rightInset;
	}
	
	public double getVerticalInsets() {
		return topInset + bottomInset;
	}
	
	public static Insets2D add(Insets2D src1, Insets2D src2, Insets2D dest) {
		if (dest==null) {
			dest = new Insets2D();
		}
		dest.setLeftInset(src1.getLeftInset()+src2.getLeftInset());
		dest.setRightInset(src1.getRightInset()+src2.getRightInset());
		dest.setTopInset(src1.getTopInset()+src2.getTopInset());
		dest.setBottomInset(src1.getBottomInset()+src2.getBottomInset());
		
		return dest;
	}
}
