package graphs;

import java.lang.reflect.Array;
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
import java.util.Stack;

public class AdjacencyListGraph<T> extends Graph<T> {
	Map<T, Vertex> keyToVertex;
	int edges;

	private class Vertex {
		T key;
		List<Vertex> successors;
		List<Vertex> predecessors;

		Vertex(T key) {
			this.key = key;
			this.successors = new ArrayList<Vertex>();
			this.predecessors = new ArrayList<Vertex>();
		}
	}

	AdjacencyListGraph(Set<T> keys) {
		this.keyToVertex = new HashMap<T, Vertex>();
		for (T key : keys) {
			Vertex v = new Vertex(key);
			this.keyToVertex.put(key, v);
		}
		this.edges = 0;
	}

	@Override
	public int size() {
		return keyToVertex.size();
	}

	@Override
	public int numEdges() {
		return edges;

	}

	@Override
	public boolean addEdge(T from, T to) {
		if (!hasEdge(from, to)) {
			keyToVertex.get(from).successors.add(new Vertex(to));
			keyToVertex.get(to).predecessors.add(new Vertex(from));
			edges++;
			return true;
		}
		return false;
	}

	@Override
	public boolean hasVertex(T key) {
		if (keyToVertex.containsKey(key)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean hasEdge(T from, T to) throws NoSuchElementException {

		if (!hasVertex(from) || !hasVertex(to)) {
			throw new NoSuchElementException();
		}
		Vertex fromVertex = keyToVertex.get(from);
		List<Vertex> fromSuccessors = fromVertex.successors;
		for (Vertex item : fromSuccessors)
			if (item.key.equals(to))
				return true;
		return false;

	}

	@Override
	public boolean removeEdge(T from, T to) throws NoSuchElementException {

		if (!hasVertex(from) || !hasVertex(to))
			throw new NoSuchElementException();
		List<Vertex> fromSuccessors = keyToVertex.get(from).successors;
		List<Vertex> toPrec = keyToVertex.get(to).predecessors;
		boolean out = true;
		for (int i = 0; i < fromSuccessors.size(); i++) {
			if (fromSuccessors.get(i).key.equals(to)) {
				fromSuccessors.remove(i);
				out = true;
				break;
			}
			out = false;
		}
		for (int i = 0; i < toPrec.size(); i++) {
			if (toPrec.get(i).key.equals(from)) {
				toPrec.remove(i);
				out = true;
				break;
			}
			out = false;
		}
		if (out) {
			edges--;
		}
		return out;
	}

	@Override
	public int outDegree(T key) throws NoSuchElementException {
		if (!hasVertex(key)) {
			throw new NoSuchElementException();
		}
		return keyToVertex.get(key).successors.size();
	}

	@Override
	public int inDegree(T key) {
		if (!hasVertex(key)) {
			throw new NoSuchElementException();
		}
		return keyToVertex.get(key).predecessors.size();
	}

	@Override
	public Set<T> keySet() {
		return keyToVertex.keySet();
	}

	@Override
	public Set<T> successorSet(T key) {
		if (!keyToVertex.containsKey(key))
			throw new NoSuchElementException();
		Set<T> output = new HashSet<T>();
		for (Vertex item : keyToVertex.get(key).successors) {
			output.add(item.key);
		}
		return output;
	}

	@Override
	public Set<T> predecessorSet(T key) {
		if (!keyToVertex.containsKey(key))
			throw new NoSuchElementException();
		Set<T> output = new HashSet<T>();
		for (Vertex item : keyToVertex.get(key).predecessors) {
			output.add(item.key);
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

	class SuccessorIterator implements Iterator<T> {
		int index;
		List<Vertex> successors;

		public SuccessorIterator(T key) {
			if (!keyToVertex.containsKey(key))
				throw new NoSuchElementException();
			this.index = 0;
			this.successors = keyToVertex.get(key).successors;
		}

		@Override
		public boolean hasNext() {
			return index < successors.size();
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			T obj = successors.get(index).key;
			index++;
			return obj;
		}
	}

	class PredecessorIterator implements Iterator<T> {
		int index;
		List<Vertex> predecessors;

		public PredecessorIterator(T key) {
			if (!keyToVertex.containsKey(key))
				throw new NoSuchElementException();
			this.index = 0;
			this.predecessors = keyToVertex.get(key).predecessors;
		}

		@Override
		public boolean hasNext() {
			return index < predecessors.size();
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			T obj = predecessors.get(index).key;
			index++;
			return obj;
		}
	}

	@Override
	public Set<T> stronglyConnectedComponent(T key) {
		if (!keyToVertex.containsKey(key))
			throw new NoSuchElementException();
		Set<T> allSucc = new HashSet<T>();
		allSucc.add(key);
		Set<T> allPred = new HashSet<T>();
		allPred.add(key);
		Queue<T> succQueue = new LinkedList<T>();
		for (Vertex succ : keyToVertex.get(key).successors)
			succQueue.add(succ.key);
		Queue<T> predQueue = new LinkedList<T>();
		for (Vertex pred : keyToVertex.get(key).predecessors)
			predQueue.add(pred.key);

		while (!succQueue.isEmpty()) {

			T item = succQueue.remove();
			allSucc.add(item);

			for (Vertex succ : keyToVertex.get(item).successors) {
				if (!allSucc.contains(succ.key)) {
					succQueue.add(succ.key);
					allSucc.add(succ.key);
				}

			}

		}
		while (!predQueue.isEmpty()) {
			T item = predQueue.remove();
			allPred.add(item);
			for (Vertex pred : keyToVertex.get(item).predecessors) {
				if (!allPred.contains(pred.key))
					predQueue.add(pred.key);
					allPred.add(pred.key);
			}
		}
		allSucc.retainAll(allPred);
		return allSucc;
	}

	public class ItemInfo {
		private ItemInfo parent;
		private Vertex item;

		public ItemInfo(ItemInfo parent, Vertex item) {
			this.parent = parent;
			this.item = item;
		}
	}

	@Override
	public List<T> shortestPath(T startLabel, T endLabel) {
		if (!keyToVertex.containsKey(startLabel) || !keyToVertex.containsKey(endLabel))
			throw new NoSuchElementException();
		if (startLabel == endLabel) {
			List<T> output = new ArrayList<T>();
			output.add(endLabel);
			return output;
		}
		Set<T> checked = new HashSet<T>();
		Queue<ItemInfo> queue = new LinkedList<ItemInfo>();
		ItemInfo first = new ItemInfo(null, keyToVertex.get(startLabel));
		queue.add(first);
		while (!queue.isEmpty()) {
			ItemInfo currItem = queue.remove();
			if (!checked.contains(currItem.item.key)) {
				for (Vertex succ : keyToVertex.get(currItem.item.key).successors) {
					ItemInfo newest = new ItemInfo(currItem, succ);
					checked.add(currItem.item.key);
					queue.add(newest);
					if (succ.key.equals(endLabel)) {
						List<T> output = new ArrayList<T>();
						while (newest != null) {
							output.add(newest.item.key);
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
