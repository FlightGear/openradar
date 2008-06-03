package de.knewcleus.openradar.aircraft.test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import de.knewcleus.openradar.aircraft.BuddySquawkAllocator;
import de.knewcleus.openradar.aircraft.OutOfSquawksException;

public class BuddyTest {
	public static void main(String[] args) {
		BuddySquawkAllocator squawkAllocator=new BuddySquawkAllocator();
		final Set<String> set=new HashSet<String>();
		final Deque<String> queue=new ArrayDeque<String>();
		final Random random=new Random();
		
		for (int i=0;i<100000;i++) {
			if (!queue.isEmpty() && random.nextDouble()<0.5) {
				final String last=queue.pop();
				set.remove(last);
				squawkAllocator.returnSquawk(last);
			} else {
				String code;
				try {
					code = squawkAllocator.allocateSquawk();
				} catch (OutOfSquawksException e) {
					continue;
				}
				assert(!set.contains(code));
				queue.push(code);
				set.add(code);
			}
		}
	}
}
