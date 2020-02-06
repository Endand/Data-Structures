package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.MinHeap;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		if(graph == null) {
			return null;
		}
		PartialTreeList ptlist = new PartialTreeList();
		for(int i = 0; i < graph.vertices.length; i++) {
			Vertex vroot= graph.vertices[i]; //vroot= vertex you are at
			PartialTree ptree= new PartialTree(vroot); //ptree= partial tree of the vertex 
			MinHeap<Arc> pq= new MinHeap<Arc>(); //pq= priority queue=arcs in priority order
			pq= ptree.getArcs();
			Vertex.Neighbor neighbor = vroot.neighbors;
			while(neighbor !=null) {
				Arc temp= new Arc(vroot, neighbor.vertex, neighbor.weight);
				pq.insert(temp);
				neighbor= neighbor.next;
			}
			
			ptlist.append(ptree);
			
		}
		
		return ptlist;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
public static ArrayList<Arc> execute(PartialTreeList ptlist) {
	ArrayList<Arc> mst = new ArrayList<Arc>();
	while(ptlist.size()>1) {
		
	PartialTree ptx= ptlist.rear.next.tree;
	MinHeap<Arc> pqx=ptx.getArcs();
	Arc alpha= pqx.deleteMin();
	while(alpha!=null) {
		
		if(!(alpha.getv2().getRoot().equals(ptx.getRoot()))) {
			mst.add(alpha);
			PartialTree pty= ptlist.removeTreeContaining(alpha.getv2());
			ptx= ptlist.remove();
			ptx.merge(pty);
			ptlist.append(ptx);
			break;
		}
		alpha=pqx.deleteMin();
	}
	}
	return mst;	

	}
	

	private static PartialTree findPT(PartialTreeList ptlist, Vertex v2) {
		PartialTree pty = null;
		Iterator<PartialTree> iter = ptlist.iterator();
		   while (iter.hasNext()) {
			   PartialTree pt = iter.next();
			   Vertex root= pt.getRoot();
			   if(root.name.equals(v2.name)) {
				   pty=pt;
				   break;
			   }
			   else {
				   Vertex.Neighbor neighbor = root.neighbors;
					while(neighbor !=null) {
						if(neighbor.vertex.name.equals(v2.name)) {
							pty=pt;
							break;
						}
						neighbor=neighbor.next;
					}
			   }
				
		   }
		    return pty;   
	}
	/*
	 * 	if(ptlist==null) {
		return null;}
		ArrayList<Arc> mst = new ArrayList<Arc>();
		while(ptlist.size() > 1){
			 PartialTree ptx = ptlist.remove(); 
			 MinHeap<Arc> pqx= ptx.getArcs();  
			 Arc minArc= pqx.deleteMin();
			 Vertex v1= minArc.getv1();
			 Vertex v2= minArc.getv2();
		
			 if(!(v1.getRoot().name.equals(v2.getRoot().name))) {
				 mst.add(minArc);
				 PartialTree pty= removeTreeContaining(v2); 
				 MinHeap<Arc> pqy= pty.getArcs(); 
				 pqx.merge(pqy);
				 ptx.merge(pty);
				 ptlist.append(ptx);
			 }
			 
			
		 }
		return mst;
	 */
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param Vertex vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    	if (vertex == null) {
			throw new NoSuchElementException();}
    	
    	PartialTreeList.Node prev = rear;
    	PartialTreeList.Node ptr = rear.next;
		PartialTree rTree = null;	//rTree==removedTree
		do {
			if (ptr.tree.getRoot().name.equals(vertex.getRoot().name)) {
				rTree= ptr.tree; //save tree
				prev.next=ptr.next;
				size--;
				break;
			}
			prev=ptr;
			ptr=ptr.next;
		
		}while(ptr!=rear.next);
		
		if(rTree==null) { throw new NoSuchElementException();}
		if (size == 0) {
			rear = null;}
		return rTree;
     }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}

