/*
 * Author: Nikita Pandya
 * Prof. Snyder
 * CS112 - HW10 maxHeap
 * April 29, 2015
 * Purpose: Class maxHeap that will be used to store and retrieve articles for the phraseSearch method 
 * in MiniGoogle. The maxHeap class contains the HeapNode which is used node class used in creating the maxHeap.
 * It stores a double, an article and a pointer to the next node. 
 *
 */

//Class maxHeap that will be used to store and retrieve articles for the phraseSearch method 
public class maxHeap {
    
    
    //Private class HeapNode which is used node class used in creating the maxHeap
    private class HeapNode {
        double cSim;
        public Article data;
        public HeapNode next;
        //Constructor 
        public HeapNode(double cSim, Article data, HeapNode n) {
            this.data = data;
            this.cSim = cSim;
            this.next = n;
        }
    }
    
    private final int SIZE = 20;       // initial length of array
    private int next = 0;              // limit of elements in array
    private HeapNode[] A = new HeapNode[SIZE];   // implements tree by storing elements in level order
    
    // methods to move up and down tree as array
    
    private int parent(int i) { return (i-1) / 2; }
    private int lchild(int i) { return 2 * i + 1; }
    private int rchild(int i) { return 2 * i + 2; }
    
    private boolean isLeaf(int i) { return (lchild(i) >= next); }
    private boolean isRoot(int i) { return i == 0; }
    
    // standard swap, using indices in array
    private void swap(int i, int j) {
        HeapNode temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }
    
    //returns true if heap is empty 
    public boolean isEmpty() {
        return (next == 0);
    }
    //returns size of the heap
    public double size() {
        return (next);
    }
    
    //Insert an integer into array at next available location
    //and fix any violations of heap property on path up to root
    public void insert(double cSim, Article a) {
        HeapNode HN = new HeapNode(cSim, a, null);
        A[next] = HN; 
        int i = next;
        int p = parent(i); 
        while(!isRoot(i) && A[i].cSim > A[p].cSim) {
            swap(i,p);
            i = p;
            p = parent(i); 
        }
        ++next;
    }
    
    //Remove top (maximum) element, and replace with last element in level
    //order; fix any violations of heap property on a path downwards
    public Article getMax() {
        --next;
        swap(0,next);                // swap root with last element
        int i = 0;                   // i is location of new key as it moves down tree
        // while there is a maximum child and element out of order, swap with max child
        int mc = maxChild(i); 
        while(!isLeaf(i) && A[i].cSim < A[mc].cSim) { 
            swap(i,mc);
            i = mc; 
            mc = maxChild(i);
        }
        return A[next].data;
    }
    
    //This method returns the index of maximum child of i or -1 if i is a leaf node (no children)
    int maxChild(int i) {
        if(lchild(i) >= next)
            return -1;
        if(rchild(i) >= next)
            return lchild(i);
        else if(A[lchild(i)].cSim > A[rchild(i)].cSim)
            return lchild(i);
        else
            return rchild(i); 
    } 
}