/**
 * Heap
 *
 * An implementation of Fibonacci heap over positive integers 
 * with the possibility of not performing lazy melds and 
 * the possibility of not performing lazy decrease keys.
 *
 */
public class Heap
{
    public final boolean lazyMelds;
    public final boolean lazyDecreaseKeys;
    public HeapItem min;
    public int numTrees;
    public int size;
    public int totalLinks;
    public int totalCuts;
    public int totalHeapifyCosts;
    public int _numMarkedNodes;
    
    /**
     *
     * Constructor to initialize an empty heap.
     *
     */
    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys)
    {
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
        this.numTrees = 0;
        this.size = 0;
        this.totalLinks = 0;
        this.totalCuts = 0;
        this.totalHeapifyCosts = 0;
        this._numMarkedNodes = 0;
        // student code can be added here
    }

    /**
     * 
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
     */
    public HeapItem insert(int key, String info) 
    {    
        if (key <= 0)
        {
            throw new IllegalArgumentException("key must be > 0");
        }

        HeapItem item = new HeapItem(key, info);
        HeapNode node = new HeapNode(item);
        
        //HeapItem fields
        item.node = node;
    
        //Usecase 1: Current heap is empty
        if (this.min == null){ 
        this.min = item;
        node.next = node;
        node.prev = node;
        this.size = 1;
        this.numTrees = 1;

        return item;
        }

        //create a tmp heap and insert according to lazeMelds flag
        Heap heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys);
        heap2.min = item;
        heap2.size = 1;
        heap2.numTrees = 1;
        this.meld(heap2);
        return item;

    }

    /**
     * 
     * Return the minimal HeapNode, null if empty.
     *
     */
    public HeapItem findMin()
    {
        if (this.min == null){
            return null;
        }
        return this.min; // should be replaced by student code
    }

    /**
     * 
     * Delete the minimal item.
     *
     */
    public void deleteMin()
    {
        return; // should be replaced by student code
    }

    /**
     * 
     * pre: 0<=diff<=x.key
     * 
     * Decrease the key of x by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapItem x, int diff) 
    {    
        return; // should be replaced by student code
    }

    /**
     * 
     * Delete the x from the heap.
     *
     */
    public void delete(HeapItem x) 
    {    
        return; // should be replaced by student code
    }


    /**
     * 
     * Meld the heap with heap2
     * Adding heap2 to roots list of this heap
     * pre: heap2.lazyMelds = this.lazyMelds AND heap2.lazyDecreaseKeys = this.lazyDecreaseKeys
     *
     */
    public void meld(Heap heap2)
    {
        //Check the heap2 isn't empty
        if (heap2 == null || heap2.min == null){
            return;
        }        
        // Current heap is empty
        if (this.min == null){
            this.min = heap2.min;
            this.size = heap2.size;
            this.numTrees = heap2.numTrees;
            this.totalLinks = heap2.totalLinks;
            this.totalCuts = heap2.totalCuts;
            this.totalHeapifyCosts = heap2.totalHeapifyCosts;
            this._numMarkedNodes = heap2._numMarkedNodes;
            
            return;

        }

        // Always need to join the node to the nodes list
        HeapNode root2 = heap2.min.node;
        HeapNode root1 = this.min.node;
        appendNodes(root1, root2);

        //Update min pointer
        if(heap2.min.key < this.min.key){
            this.min = heap2.min;
        }

        //Updating global attributes- according yo Lazy meld (non-lazy meld will adjust accordingly)
        this.size += heap2.size;
        this.numTrees += heap2.numTrees;
        this.totalLinks += heap2.totalLinks;
        this.totalCuts += heap2.totalCuts;
        this.totalHeapifyCosts += heap2.totalHeapifyCosts;
        this._numMarkedNodes += heap2._numMarkedNodes;

        //If this is not a lazy meld, we need to consolidate
        if (!lazyMelds){
            this.consolidate();
        }

        return; // should be replaced by student code           
    }


    /**
     * Consolidate function  joins trees of the same rank, leaving only one tree for each rank
     */
    private void consolidate(){
        //1: ranks list - needs to be of size(logn) of the current list
        int n = this.size;
        // golden ratio for a tighter bound on max degree
        double phi = (1.0 + Math.sqrt(5.0)) / 2.0;
        int maxDegree = (int) Math.floor(Math.log(Math.max(1, n)) / Math.log(phi)) + 2;
        HeapNode[] ranksArray = new HeapNode[maxDegree + 5];

        //Keep a pointer to all of the roots
        HeapNode[] rootsArray = new HeapNode[this.numTrees];
        HeapNode curr = this.min.node;
        for (int i = 0 ; i < rootsArray.length ; i++){
            rootsArray[i] = curr;
            curr = curr.next;
        }

        for (HeapNode root : rootsArray) {
            root.next = root;
            root.prev = root;
        }

        //Consolidating, connecting each node in the rootsArray
        for(HeapNode curr_root : rootsArray){
            int curr_rank = curr_root.rank;
            while(ranksArray[curr_rank] != null){
                HeapNode existing_root = ranksArray[curr_rank];
                curr_root = this.link(existing_root, curr_root);
                ranksArray[curr_rank] = null;
                curr_rank = curr_root.rank; 
            }
            ranksArray[curr_rank] = curr_root;
            curr_root = curr_root.next;
        }

        //Re-build the roots list
        this.min = null;
        this.numTrees = 0;
        for (HeapNode node : ranksArray){
            if (node != null){
            this.numTrees ++;
            if (this.min == null){
                this.min = node.item;
                node.next = node;
                node.prev = node;
            }
            else {
                this.appendNodes(this.min.node, node);
                if (node.item.key < this.min.key){
                    this.min = node.item;
                }
            }
        }
        }
    }


    /**
     * link two trees of the same rank, moving on tree to be the child of the other
     * 
     * @return
     */
    private HeapNode link(HeapNode node1, HeapNode node2) {
        // Link node2 as son of node1
        HeapNode parent, newChild, child;
        if (node1.item.key < node2.item.key) {
            parent = node1;
            newChild = node2;
        } else {
            parent = node2;
            newChild = node1;
        }
        // Disconnecting the newChild from the rest of the brothers.
        newChild.prev.next = newChild.next;
        newChild.next.prev = newChild.prev;

        newChild.next = newChild;
        newChild.prev = newChild;

        //current child list is empty, we want to append to this list
        if (newChild.mark){//A node that was a root, must always be marked false
            newChild.mark = false; 
            this._numMarkedNodes --; 
        }

        child = parent.child;
        newChild.parent = parent; //Do all children need to point to parent**?

        if (child == null){ //if we don't have any children, create a new list
            parent.child = newChild;
            newChild.parent = parent;
            newChild.next = newChild;
            newChild.prev = newChild;
        }
        else { //Add to and existing list of child nodes
            
            this.appendNodes(child, newChild);            
            }
        
        parent.rank++;
        this.numTrees--;
        this.totalLinks++;
        return parent;

        }

    /**
     * 
     * @param node1
     * @param node2
     * Adds a new node to the heaps nodes list (adds a brother)
     */
    private void appendNodes(HeapNode node1, HeapNode node2){
        HeapNode next1, next2;
        next1 = node1.next;
        next2 = node2.next;

        node1.next = next2;
        next2.prev = node1;

        node2.next = next1;
        next1.prev = node2;

        return;

    }

    
    /**
     * 
     * Return the number of elements in the heap
     *   
     */
    public int size() {
        if (this.min == null) {
                return 0;
            }
            return this.size;
        }

    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return this.numTrees; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return this.totalLinks;
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        return 46; // should be replaced by student code
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * Class implementing a node in a Heap.
     *  
     */
    public static class HeapNode{
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int rank;
        public boolean mark;

        public HeapNode(HeapItem item){
            this.item = item;
            this.child = null;
            this.next = this;
            this.prev = this;
            this.parent = null;
            this.rank = 0;
            this.mark = false;
        }
    }
    
    /**
     * Class implementing an item in a Heap.
     *  
     */
    public static class HeapItem {
        public HeapNode node;
        public int key;
        public String info;
        
        public HeapItem(int key, String info) {
            this.key = key;
            this.info = info;
        }
    }
    
    public void print() {
    System.out.println("--- Fibonacci Heap Structure ---");
    if (this.min == null) {
        System.out.println("Empty Heap");
        return;
    }
    
    HeapNode curr = this.min.node;
    // We use a do-while because it's a circular list
    do {
        printTree(curr, 0);
        curr = curr.next;
    } while (curr != this.min.node);
    System.out.println("--------------------------------");
    }

    // private void printTree(HeapNode node, int level) {
    //     if (node == null) return;

    //     // Indentation based on depth (level)
    //     for (int i = 0; i < level; i++) {
    //         System.out.print("  | ");
    //     }

    //     // Print the key and rank
    //     System.out.println("Key: " + node.item.key + " (Rank: " + node.rank + ")");

    //     // Recursively print children
    //     if (node.child != null) {
    //         HeapNode child = node.child;
    //         HeapNode firstChild = child;
    //         do {
    //             printTree(child, level + 1);
    //             child = child.next;
    //         } while (child != firstChild);
    //     }
    // }
    private void printTree(HeapNode node, int level) {
    if (node == null) return;

    String indent = "";
    for (int i = 0; i < level; i++) {
        indent += "    ";
    }

    // Labeling the relationship
    String relation = (level == 0) ? "[Root] " : "[Child of " + node.parent.item.key + "] ";
    
    System.out.println(indent + relation + "Key: " + node.item.key + " (Rank: " + node.rank + ")");

    if (node.child != null) {
        HeapNode child = node.child;
        HeapNode firstChild = child;
        do {
            printTree(child, level + 1);
            child = child.next;
        } while (child != firstChild);
    }
    }
}