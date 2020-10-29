

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to many different
 * indexes of a large data set. BPTree objects are created for each type of
 * index needed by the program. BPTrees provide an efficient range search as
 * compared to other types of data structures due to the ability to perform
 * log_m N lookups and linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food
 *        item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

	// Root of the tree
	private Node root;

	// Branching factor is the number of children nodes
	// for internal nodes of the tree
	private int branchingFactor;

	// Size of the tree (The number of LeafNodes in the tree)
	private int size;

	/**
	 * Public constructor
	 * 
	 * @param branchingFactor
	 */
	public BPTree(int branchingFactor) {
		if (branchingFactor <= 2) {
			throw new IllegalArgumentException("Illegal branching factor: " + branchingFactor);
		}
		root = new LeafNode();
		size = 0;
		this.branchingFactor = branchingFactor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void insert(K key, V value) {
		// checks if the input key and value are null
		if (key == null || value == null) {
			throw (new IllegalArgumentException());
		}

		// Inserts the values and returns a node if a split occured
		Node newNode = root.insert(key, value);
		// checks if a split occurred within the internal nodes)
		if (newNode != null) {
			// creates a node that will become the new root of the tree
			InternalNode newRoot = new InternalNode();
			newRoot.children.add(root);
			newRoot.children.add(newNode);

			newRoot.keys.add(newNode.keys.get(0));
			root = newRoot;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
	 */
	@Override
	public List<V> rangeSearch(K key, String comparator) {
		if (!comparator.contentEquals(">=") && !comparator.contentEquals("==") && !comparator.contentEquals("<="))
			return new ArrayList<V>();
		// the list of values to be returned at the end of the method
		List<V> valuesList = new ArrayList<V>();
		LeafNode currentChild = (BPTree<K, V>.LeafNode) root.getFirstChild();

		// checks which comparator is being used in the range search
		if (comparator.contentEquals(">=")) {

			// this loop will iterate as long as there is a next for the leaf Child
			while (currentChild != null) {

				// a for loop to go through each key in the currentChild's list
				for (int i = 0; i < currentChild.keys.size(); i++) {

					// checks what if the current key being iterated is ">=" to the input key
					if (currentChild.keys.get(i).compareTo(key) >= 0) {
						valuesList.add(currentChild.values.get(i));
					}

				}
				// iterates to the next LeafNode
				currentChild = currentChild.next;
			}
		}
		if (comparator.contentEquals("==")) {

			// this loop will iterate as long as there is a next for the leaf Child
			while (currentChild != null) {

				// a for loop to go through each key in the currentChild's list
				for (int i = 0; i < currentChild.keys.size(); i++) {

					// checks what if the current key being iterated is ">=" to the input key
					if (currentChild.keys.get(i).compareTo(key) == 0) {
						valuesList.add(currentChild.values.get(i));
					}

				}
				// iterates to the next LeafNode
				currentChild = currentChild.next;
			}
		}
		if (comparator.contentEquals("<=")) {

			// this loop will iterate as long as there is a next for the leaf Child
			while (currentChild != null) {

				// a for loop to go through each key in the currentChild's list
				for (int i = 0; i < currentChild.keys.size(); i++) {

					// checks what if the current key being iterated is ">=" to the input key
					if (currentChild.keys.get(i).compareTo(key) <= 0) {
						valuesList.add(currentChild.values.get(i));
					}

				}
				// iterates to the next LeafNode
				currentChild = currentChild.next;
			}
		}
		valuesList.sort(null);
		return valuesList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#get(java.lang.Object)
	 */
	@Override
	public V get(K key) {
		// checks if the input key is null or not
		if (key == null) {
			return null;
		}
		// gets the first child of the whole list
		LeafNode node = (BPTree<K, V>.LeafNode) root.getFirstChild();
		// while loop to check whether the current node is null or not
		while (node != null) {
			// loops through each node and checks if the key exists in the list of keys
			for (int i = 0; i < node.keys.size(); i++) {
				if (key.compareTo(node.keys.get(i)) == 0) {
					return node.values.get(i);
				}
			}
			// iterates to the next node
			node = node.next;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#size()
	 */
	@Override
	public int size() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Queue<List<Node>> queue = new LinkedList<List<Node>>();
		queue.add(Arrays.asList(root));
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
			while (!queue.isEmpty()) {
				List<Node> nodes = queue.remove();
				sb.append('{');
				Iterator<Node> it = nodes.iterator();
				while (it.hasNext()) {
					Node node = it.next();
					sb.append(node.toString());
					if (it.hasNext())
						sb.append(", ");
					if (node instanceof BPTree.InternalNode)
						nextQueue.add(((InternalNode) node).children);
				}
				sb.append('}');
				if (!queue.isEmpty())
					sb.append(", ");
				else {
					sb.append('\n');
				}
			}
			queue = nextQueue;
		}
		return sb.toString();
	}

	/**
	 * This abstract class represents any type of node in the tree This class is a
	 * super class of the LeafNode and InternalNode types.
	 * 
	 * @author sapan
	 */
	private abstract class Node {

		// List of keys
		List<K> keys;

		/**
		 * Package constructor
		 */
		Node() {
			keys = new ArrayList<K>();
		}

		/**
		 * Inserts key and value in the appropriate leaf node and balances the tree if
		 * required by splitting
		 * 
		 * @param key
		 * @param value
		 * @return
		 */
		abstract BPTree<K, V>.Node insert(K key, V value);

		/**
		 * Gets the first leaf key of the tree
		 * 
		 * @return key
		 */
		abstract K getFirstLeafKey();

		/**
		 * Gets the new sibling created after splitting the node
		 * 
		 * @return Node
		 */
		abstract Node split();

		/*
		 * (non-Javadoc)
		 * 
		 * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
		 */
		abstract List<V> rangeSearch(K key, String comparator);

		/**
		 * 
		 * @return boolean
		 */
		abstract boolean isOverflow();

		public String toString() {
			return keys.toString();
		}

		abstract BPTree<K, V>.Node getFirstChild();

	} // End of abstract class Node

	/**
	 * This class represents an internal node of the tree. This class is a concrete
	 * sub class of the abstract Node class and provides implementation of the
	 * operations required for internal (non-leaf) nodes.
	 * 
	 * @author sapan
	 */
	private class InternalNode extends Node {

		// List of children nodes
		List<Node> children;

		/**
		 * Package constructor
		 */
		InternalNode() {
			super();

			// instantiates the list of the Nodes children
			children = new ArrayList<Node>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			// recursively calls the method on the child until it reaches the LeafNode
			return children.get(0).getFirstLeafKey();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			return children.size() > branchingFactor;
		}

		/**
		 * Returns the nodes firstChild
		 * 
		 * @return
		 */
		Node getFirstChild() {
			return children.get(0).getFirstChild();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
		 */
		Node insert(K key, V value) {
			// accesses the index for which child the key should be added to
			int ind = getChildInd(key);
			Node child = children.get(ind);
			Node possibleSplit = child.insert(key, value);

			// checks for overflow within the child node
			if (possibleSplit != null) {
				// if overflow occurs, the node is split into two nodes, one containing the
				// median (sib) and one containing everything less than the median (child).
				ind = getChildInd(possibleSplit.getFirstLeafKey());
				if(ind >= 0) {
					children.set(ind, possibleSplit);
				}else {
					children.add(ind + 1, possibleSplit);
					keys.add(ind, possibleSplit.keys.get(0));
				}

			}
			// checks if the parent node now has overflow due to the previous insert(s)
			if (this.isOverflow()) {
				return (InternalNode) this.split();
			}
			return null;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#split()
		 */
		Node split() {
			// the index of the middle key
			int median = (keys.size() + 1) / 2;
			// The Node to be returned (A sibling node)
			InternalNode sib = new InternalNode();

			// copy all the keys from index 0 to before the median into the sibling nodes
			// list of keys
			sib.keys.addAll(keys.subList(median, keys.size()));
			// Adds all the children from index 0 to median -1 into sib's list of
			// children.
			sib.children.addAll(children.subList(median + 1, children.size()));

			// clears the keys and children added from the current node that were added into
			// the sib.
			keys.subList(median, keys.size()).clear();
			children.subList(median + 1, children.size()).clear();

			// returns sib
			return sib;

		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			// TODO : Complete
			return null;
		}

		/**
		 * Returns the index of the child within the node to add the current key to
		 * 
		 * @param K key - the key to be inserted
		 * @return int index - the index of the child
		 */
		private int getChildInd(K key) {
			
			int ind = Collections.binarySearch(keys,key);
			return ind >= 0 ? ind + 1 : -ind -1;
		}

	} // End of class InternalNode

	/**
	 * This class represents a leaf node of the tree. This class is a concrete sub
	 * class of the abstract Node class and provides implementation of the
	 * operations that required for leaf nodes.
	 * 
	 * @author sapan
	 */
	private class LeafNode extends Node {

		// List of values
		List<V> values;

		// Reference to the next leaf node
		LeafNode next;

		// Reference to the previous leaf node
		LeafNode previous;

		/**
		 * Package constructor
		 */
		LeafNode() {
			super();
			values = new ArrayList<V>();
			next = null;
			previous = null;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			// returns the first key in its list of keys
			return keys.get(0);
		}

		/**
		 * if the node is a leaf node, this is the node that will be returned from the
		 * getFirstChild method
		 * 
		 * @return Node - the first child of the whole tree
		 */
		Node getFirstChild() {
			return this;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			// returns if there are the same amount of keys as the branchingFactor
			return (this.keys.size() >= branchingFactor);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(Comparable, Object)
		 */
		Node insert(K key, V value) {
			size++;
			// checks if the list of keys is empty or not, and if so, just adds the key and
			// value to their respective lists
			if (this.keys.isEmpty()) {
				this.keys.add(key);
				this.values.add(value);
				return null;
			} else {
				// boolean to check if the key was added or not
				boolean wasAdded = false;
				// adds the key to the keys list and the value to the values list
				for (int i = 0; i < keys.size()-1; i++) {
					// finds where to put the key in the list of keys
					if (key.compareTo(keys.get(i)) < 0) {
						keys.add(i, key);
						values.add(i, value);
						wasAdded = true;
						break;
					}
					if (key.compareTo(keys.get(i)) == 0) {
						keys.add(i + 1, key);
						values.add(i + 1, value);
						wasAdded = true;
						break;
					}
				}
				// checks if the key was added or not
				if (!wasAdded) {
					keys.add(key);
					values.add(value);
				}
			}
			// checks if there is overflow (really only applies if this is the root)
			if (this.isOverflow()) {
				return (LeafNode) this.split();
			}
			return null;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#split()
		 */
		Node split() {
			// the index of the middle key
			int median = (keys.size() + 1) / 2;
			// The Node to be returned (A sibling node)
			LeafNode sib = new LeafNode();

			// copy all the keys from the median to the size of the list of keys into the
			// sibling nodes
			// list of keys
			sib.keys.addAll(keys.subList(median, keys.size()));
			// Adds all the children from index median to the size of the list into sib's
			// list of
			// children.
			sib.values.addAll(values.subList(median, values.size()));

			// clears the keys and children added from the current node that were added into
			// the sib.
			keys.subList(median, keys.size()).clear();
			values.subList(median, values.size()).clear();

			// sets the node's next and previous to eachother
			sib.next = this.next;
			this.next = sib;
			sib.previous = this;

			// returns sib
			return sib;
		}

		

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#rangeSearch(Comparable, String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			return null;
		}

	} // End of class LeafNode

	/*
	 * Class for a LinkedList
	 * 
	 * /** Contains a basic test scenario for a BPTree instance. It shows a simple
	 * example of the use of this class and its related types.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create empty BPTree with branching factor of 3
		BPTree<Double, Double> bpTree = new BPTree<>(4);

		// create a pseudo random number generator
		Random rnd1 = new Random();

		// some value to add to the BPTree
		Double[] dd = { 0.0d, 0.5d, 0.2d, 0.8d };

		// build an ArrayList of those value and add to BPTree also
		// allows for comparing the contents of the ArrayList
		// against the contents and functionality of the BPTree
		// does not ensure BPTree is implemented correctly
		// just that it functions as a data structure with
		// insert, rangeSearch, and toString() working.
		List<Double> list = new ArrayList<>();
		for (int i = 0; i < 500; i++) {
			Double j = dd[rnd1.nextInt(4)];
			list.add(j);
			System.out.print(j);
			bpTree.insert(j, j);
			System.out.println("\n\nTree structure:\n" + bpTree.toString());
		}
		List<Double> filteredValues = bpTree.rangeSearch(0.2d, ">=");
		System.out.println("Filtered values: " + filteredValues.toString());
	}

} // End of class BPTree
