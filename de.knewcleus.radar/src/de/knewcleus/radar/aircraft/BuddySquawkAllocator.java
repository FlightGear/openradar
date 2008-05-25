package de.knewcleus.radar.aircraft;

import java.util.BitSet;
import java.util.Deque;
import java.util.LinkedList;

public class BuddySquawkAllocator implements ISquawkAllocator {
	protected static class Bucket {
		public final int rangeSize;
		public final BitSet buddyBits;
		public final Deque<Integer> freeRanges=new LinkedList<Integer>();
		
		public Bucket(int rangeSize) {
			this.rangeSize=rangeSize;
			buddyBits=new BitSet(rangeSize/2);
		}
	}
	
	protected static final int availableSquawks=4096;
	protected static final int availableLevels=13;
	protected static final int reservedSquawks[]=new int[] {
		00021,
		00022,
		07000,
		07500,
		07600,
		07700,
		07777
	};
	
	/* Level n has range length 1<<n */
	protected final Bucket[] buckets=new Bucket[availableLevels];
	
	public BuddySquawkAllocator() {
		int rangeSize=1;
		for (int i=0;i<availableLevels;i++) {
			buckets[i]=new Bucket(rangeSize);
			rangeSize<<=1;
		}
		
		int start=0;
		for (int i=0;start<availableSquawks && i<reservedSquawks.length;i++) {
			int end=reservedSquawks[i];
			if (start<end) {
				returnSquawkRange(start,end-start);
			}
			start=end+1;
		}
	}
	
	public void returnSquawkRange(int rangeStart, int rangeLength) {
		int blockLength=availableSquawks;
		int blockLevel=availableLevels-1;
		
		
		while (rangeLength>0) {
			while (blockLevel>0 && blockLength>rangeLength) {
				blockLength>>=1;
				blockLevel--;
			}
			returnSquawkBlock(rangeStart, blockLevel);
			rangeStart+=blockLength;
			rangeLength-=blockLength;
		}
	}
	
	public int allocateSquawkBlock(int level) throws OutOfSquawksException {
		assert(0<=level && level<availableLevels);
		
		int foundLevel=level;
		int rangeLength=1<<level;
		
		/* Find a level which has free blocks */
		while (foundLevel<availableLevels && buckets[foundLevel].freeRanges.isEmpty()) {
			foundLevel++;
			rangeLength<<=1;
		}
		
		if (foundLevel==availableLevels)
			throw new OutOfSquawksException();
		
		final int rangeStart=buckets[foundLevel].freeRanges.removeLast();
		int bitIndex=rangeStart>>(foundLevel+1);
		/* Split off ranges */
		while (foundLevel>level) {
			buckets[foundLevel].buddyBits.flip(bitIndex);
			rangeLength>>=1;
			foundLevel--;
			bitIndex=(bitIndex<<1)+1;
			final int buddyStart=rangeStart+rangeLength;
			buckets[foundLevel].freeRanges.addLast(buddyStart);
		}
		
		buckets[foundLevel].buddyBits.flip(bitIndex);
		
		return rangeStart;
	}
	
	public void returnSquawkBlock(int rangeStart, int level) {
		assert(0<=level && level<availableLevels);
		int bitIndex=rangeStart>>(level+1);
		int rangeLength=1<<level;
		
		while (level<availableLevels-1 && buckets[level].buddyBits.get(bitIndex)) {
			/* our buddy is available */
			final int buddyStart=rangeStart^rangeLength;
			buckets[level].freeRanges.remove(buddyStart);
			buckets[level].buddyBits.flip(bitIndex);
			rangeStart&=~rangeLength;
			level++;
			rangeLength<<=1;
			bitIndex>>=1;
		}
		
		buckets[level].buddyBits.flip(bitIndex);
		buckets[level].freeRanges.addLast(rangeStart);
	}

	@Override
	public String allocateSquawk() throws OutOfSquawksException {
		int squawkNumber=allocateSquawkBlock(0);
		return String.format("%04o", squawkNumber);
	}

	@Override
	public void returnSquawk(String squawk) {
		int squawkNumber=Integer.parseInt(squawk,8);
		returnSquawkBlock(squawkNumber, 0);
	}

}
