package graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class AdjacencyMatrixGraph<T> extends Graph<T> {
	Map<T,Integer> keyToIndex;
	List<T> indexToKey;
	int[][] matrix;
	
	AdjacencyMatrixGraph(Set<T> keys) {
		int size = keys.size();
		this.keyToIndex = new HashMap<T,Integer>();
		this.indexToKey = new ArrayList<T>();
		
		this.matrix = new int[size][size];
		int index = 0;
		for (T key : keys) {
			indexToKey.add(key);
			keyToIndex.put(key, index);
			index++;
		}
		// need to populate keyToIndex and indexToKey with info from keys
	}
	
	public boolean hasElement(T from, T to) {
		if (!keyToIndex.containsKey(from) || !keyToIndex.containsKey(to)) return false;
		return true;

	}
	
	public boolean hasElement(T element) {
		if (!keyToIndex.containsKey(element)) return false;
		return true;
	}
	
	@Override
	public int size() {
		return keyToIndex.size();
	}

	@Override
	public int numEdges() {
		int count = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] != 0) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public boolean addEdge(T from, T to) {
		
		if (!hasVertex(from) || !hasVertex(to)) throw new NoSuchElementException();
		
		int fromIndex = keyToIndex.get(from);
		int toIndex = keyToIndex.get(to);
		if (matrix[fromIndex][toIndex] != 0) {
			return false;
		}
		matrix[fromIndex][toIndex] = 1;
		return true;
	}

	@Override
	public boolean hasVertex(T key) {
		return keyToIndex.containsKey(key);
	}

	@Override
	public boolean hasEdge(T from, T to) throws NoSuchElementException {
		if (!hasElement(from, to)) throw new NoSuchElementException();
		
		if (matrix[keyToIndex.get(from)][keyToIndex.get(to)] != 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean removeEdge(T from, T to) throws NoSuchElementException {
		if (!hasElement(from, to)) throw new NoSuchElementException();
		int fromIndex = keyToIndex.get(from);
		int toIndex = keyToIndex.get(to);
		if (matrix[fromIndex][toIndex] != 0) {
			matrix[fromIndex][toIndex] = 0;
			return true;
		}
		return false;
	}

	@Override
	public int outDegree(T key) {
		if (!hasElement(key)) throw new NoSuchElementException();
		int index = keyToIndex.get(key);
		int degree = 0;
		for (int i = 0; i < matrix[index].length; i++) {
			if (matrix[index][i] != 0) {
				degree++;
			}
		}
		return degree;
	}

	@Override
	public int inDegree(T key) {
		if (!hasElement(key)) throw new NoSuchElementException();
		int degree = 0;
		int index = keyToIndex.get(key);
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i][index] != 0) {
				degree++;
			}
		}
		return degree;
	}

	@Override
	public Set<T> keySet() {
		return keyToIndex.keySet();
	}

	@Override
	public Set<T> successorSet(T key) {
		if (!keyToIndex.containsKey(key)) throw new NoSuchElementException();
		Set<T> output = new HashSet<T>();
		int index = keyToIndex.get(key);
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[index][i] != 0) {
				output.add(indexToKey.get(i));
			}
		}
		return output;
	}

	@Override
	public Set<T> predecessorSet(T key) {
		if (!keyToIndex.containsKey(key)) throw new NoSuchElementException();
		int index = keyToIndex.get(key);
		Set<T> output = new HashSet<T>();
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i][index] != 0) {
				output.add(indexToKey.get(i));
			}
		}
		return output;
	}

	@Override
	public Iterator<T> successorIterator(T key) {
		return new SuccessorIterator(key);
	}

	@Override
	public Iterator<T> predecessorIterator(T key) {
		return new PredecessorIterator(key);
	}

	public class SuccessorIterator implements Iterator<T> {
		int index;
		List<T> list;
		int item;
		int lastChecked;
		public SuccessorIterator(T key) {
			if (!keyToIndex.containsKey(key)) throw new NoSuchElementException();
			this.list = new ArrayList<T>();
			this.item = keyToIndex.get(key);
			this.index = 0;
			this.lastChecked = 0;
			for (int i = lastChecked; i < matrix.length; i++) {
				if (matrix[item][i] != 0) {
					list.add(indexToKey.get(i));
					this.lastChecked = i;
					break;
				}
			}
		}
		
		
		@Override
		public boolean hasNext() {
			return index < list.size();
		}

		@Override
		public T next() {
			if (!hasNext()) throw new NoSuchElementException();
			T obj = list.get(index);
			for (int i = lastChecked+1; i < matrix.length; i++) {
				if (matrix[item][i] != 0) {
					list.add(indexToKey.get(i));
					lastChecked = i;
					break;
				}
			}
			index++;
			return obj;	
		}
	}
	
	public class PredecessorIterator implements Iterator<T> {
		int index;
		List<T> list;
		int item;
		int lastChecked;
		public PredecessorIterator(T key) {
			if (!keyToIndex.containsKey(key)) throw new NoSuchElementException();
			this.list = new ArrayList<T>();
			this.item = keyToIndex.get(key);
			this.index = 0;
			this.lastChecked = 0;
			for (int i = lastChecked; i < matrix.length; i++) {
				if (matrix[i][item] != 0) {
					list.add(indexToKey.get(i));
					this.lastChecked = i;
					break;
				}
			}
		}
		
		
		@Override
		public boolean hasNext() {
			return index < list.size();
		}

		@Override
		public T next() {
			if (!hasNext()) throw new NoSuchElementException();
			T obj = list.get(index);
			for (int i = lastChecked+1; i < matrix.length; i++) {
				if (matrix[i][item] != 0) {
					list.add(indexToKey.get(i));
					lastChecked = i;
					break;
				}
			}
			index++;
			return obj;	
		}
	}
	
	@Override
	public Set<T> stronglyConnectedComponent(T key) {
		if (!keyToIndex.containsKey(key)) throw new NoSuchElementException();
		Set<T> strong = new HashSet<T>();
		strong.add(key);
		Set<T> allSucc = new HashSet<T>();
		Set<T> allPred = new HashSet<T>();
		Queue<T> succQueue = new PriorityQueue<T>();
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[keyToIndex.get(key)][i] != 0) succQueue.add(indexToKey.get(i));
		}
		Queue<T> predQueue = new PriorityQueue<T>();
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i][keyToIndex.get(key)] != 0) predQueue.add(indexToKey.get(i));
		}
		while (succQueue.peek() != null)  {
			T item = succQueue.remove();
			allSucc.add(item);
			for (T succ : successorSet(item)) {
				if (!allSucc.contains(succ)) succQueue.add(succ);
			}
		}
		while (predQueue.peek() != null) {
			T item = predQueue.remove();
			allPred.add(item);
			for (T pred : predecessorSet(item)) {
				if (!allPred.contains(pred)) predQueue.add(pred);
			}
			
		}
		for (T curr : allSucc) {
			if (allPred.contains(curr)) strong.add(curr);
		}
	return strong;
	}
	
	public class ItemInfo {
		private ItemInfo parent;
		private T item;
		public ItemInfo(ItemInfo parent, T item) {
			this.parent = parent;
			this.item = item;
		}
	}
	

	@Override
	public List<T> shortestPath(T startLabel, T endLabel) {
		if (!keyToIndex.containsKey(startLabel) || !keyToIndex.containsKey(endLabel)) throw new NoSuchElementException();
		if (startLabel == endLabel) {
			List<T> output = new ArrayList<T>();
			output.add(endLabel);
			return output;
		}
		Set<T> checked = new HashSet<T>();
		Queue<ItemInfo> queue = new LinkedList<ItemInfo>();
		ItemInfo first = new ItemInfo(null, startLabel);
		queue.add(first);
		while (!queue.isEmpty()) {
			ItemInfo currItem = queue.remove();
			if (!checked.contains(currItem.item)) {
				for (T succ : successorSet(currItem.item)) {
					ItemInfo newest = new ItemInfo(currItem, succ);
					checked.add(currItem.item);
					queue.add(newest);
					if (succ.equals(endLabel)) {
						List<T> output = new ArrayList<T>();
						while (newest != null) {
							output.add(newest.item);
							newest = newest.parent;
						}
						List<T> finalOutput = new ArrayList<T>();
						for (int i = output.size() - 1; i >= 0; i--) {
							finalOutput.add(output.get(i));
						}
						return finalOutput;
					}
				}
			}
		}
		return null;
		
	}

}
