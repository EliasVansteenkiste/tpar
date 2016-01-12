package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * MultiSet that counts how many of each object are present. Implemented using a
 * HashMap<N,Integer>. Only objects that are present once or more are stored in
 * the Map.
 */
public class CountingSet<N> implements Collection<N> {
	private Map<N,Integer> map;
	private int size;

	public CountingSet() {
		map = new HashMap<N,Integer>();
		size = 0;
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public Iterator<N> iterator() {
		throw new RuntimeException();
	}

	@Override
	public Object[] toArray() {
		throw new RuntimeException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new RuntimeException();
	}

	@Override
	public boolean add(N e) {
		Integer count = map.get(e);
		if (count != null)
			map.put(e, count + 1);
		else
			map.put(e, 1);
		size++;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		N key = (N)o;
		Integer count = map.get(key);
		if (count == null)
			return false;
		size--;
		if (count == 1)
			map.remove(key);
		else
			map.put(key, count - 1);
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new RuntimeException();
	}

	@Override
	public boolean addAll(Collection<? extends N> c) {
		throw new RuntimeException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new RuntimeException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new RuntimeException();
	}

	@Override
	public void clear() {
		map.clear();
		size = 0;
	}
	
	public int count(N n) {
		Integer count = map.get(n);
		if(count == null)
			return 0;
		else
			return count;
	}

	public int uniqueSize() {
		return map.size();
	}
}
