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
package de.knewcleus.fgfs.util;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RingBuffer<E> extends AbstractCollection<E> {
	protected final Object[] ring;
	protected int readMarker=0,fillCount=0;
	
	public RingBuffer(int size) {
		ring=new Object[size];
	}
	
	@Override
	public boolean add(E e) {
		int writeMarker=(readMarker+fillCount)%ring.length;
		ring[writeMarker]=e;
		if (fillCount==ring.length) {
			readMarker=(readMarker+1)%ring.length;
		} else {
			fillCount++;
		}
		return true;
	}
	
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			protected int index=0;
			
			public boolean hasNext() {
				return index<fillCount;
			}
			
			@SuppressWarnings("unchecked")
			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();
				E element=(E)ring[(readMarker+index)%ring.length];
				index++;
				return element;
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Override
	public int size() {
		return fillCount;
	}
}
