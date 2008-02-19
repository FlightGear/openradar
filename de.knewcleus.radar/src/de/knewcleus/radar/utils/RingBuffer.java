package de.knewcleus.radar.utils;

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
