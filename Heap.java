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
        //iterate over all roots to find the min
        HeapNode some_root= this.min.node; // temporary min
        do {
            if (some_root.item.key < this.min.key){
                this.min = some_root.item;
            }
            some_root = some_root.next;
        } while (some_root != this.min.node);
    



        return this.min; 
    }

    /**
     * 
     * Delete the minimal item.
     *
     */
    public void deleteMin()
    {
        HeapNode min_node = this.min.node;
        
        // Case 1: empty heap
        if (min_node == null) {
            return;
        }
        
        // Case 2: heap with only one node
        if (this.size == 1) {
            this.min = null;
            this.numTrees = 0;
            this.size = 0;
            return;
        }
        
        // Case 3: min is the only root
        if (min_node.next == min_node) {
            // Only min in root list, promote all its children to roots
            if (min_node.child != null) {
                HeapNode child = min_node.child;
                HeapNode firstChild = child;
                
                // Disconnect all children from parent and count them
                int childCount = 0;
                do {
                    child.parent = null;
                    childCount++;
                    child = child.next;
                } while (child != firstChild);
                
                this.min = firstChild.item; // temporary min
                this.numTrees = childCount;
            } else {
                // Min was the only node (shouldn't happen due to size check, but just in case)
                this.min = null;
                this.numTrees = 0;
            }
        } else {
            // Case 4: min is one of multiple roots
            // Remove min from root list
            min_node.prev.next = min_node.next;
            min_node.next.prev = min_node.prev;
            
            // Get a reference to a remaining root and detach min from the root list
            HeapNode someRoot = min_node.next;
            min_node.next = min_node;
            min_node.prev = min_node;
            
            // Add min's children to root list
            if (min_node.child != null) {
                HeapNode child = min_node.child;
                HeapNode firstChild = child;

                do {
                    child.parent = null;
                    child = child.next;
                } while (child != firstChild);

                // Splice the entire child list into the root list once.
                this.appendNodes(someRoot, firstChild);
                this.numTrees = this.numTrees - 1 + min_node.rank;
            } else {
                // Min had no children
                this.numTrees--;
            }
            
            // Ensure min points to a valid root before scanning
            this.min = someRoot.item;
            this.min = findMin();
        }
        
        this.size--;
        
        // Find new actual minimum 
        this.min = findMin();
        
        // Consolidate if not lazy
        if (!this.lazyMelds) {
            this.consolidate();
        }
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
        HeapNode x_node = x.node;
        if (diff < 0 ) {
            throw new IllegalArgumentException("diff must be in the positive range");
        }

            x.key -= diff;
        HeapNode parent = x_node.parent;
        if (parent != null && x.key < parent.item.key) {
   
        if (lazyDecreaseKeys) {
            cascadingCut(x_node);
        } else {
            heapify_up(x_node);
        }
    }
        if (this.min == null || x.key < this.min.key) {
            this.min = x;
        }
        return;
    }

    /**
     * 
     * Delete the x from the heap.
     *
     */


    public void heapify_up(HeapNode node) {
        HeapNode parent = node.parent;
        while(parent != null && node.item.key < parent.item.key) {
            HeapItem temp = node.item;
            node.item = parent.item;
            parent.item = temp;
            node.item.node = node;
            parent.item.node = parent;

            node = parent;
            parent = node.parent;
            this.totalHeapifyCosts++;

        }
    return;
    }




    public void cascadingCut(HeapNode node) {
        if (node.parent == null) {
            return; // Node is a root, no need to cut
        }

        HeapNode parent = node.parent;

        // Remove node from its sibling list
        node.prev.next = node.next;
        node.next.prev = node.prev;

        // Update parent's child pointer if necessary
        if (parent.child == node) {
            if (node.next != node) {
                parent.child = node.next;
            } else {
                parent.child = null;
            }
        }

        parent.rank--;

        // meld node and its siblings into the root list
        node.parent = null;
        node.next = node;
        node.prev = node;
        Heap tmp_heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys);
        tmp_heap2.min = node.item;
        tmp_heap2.numTrees = 1;
        tmp_heap2.size = 0; // size is not relevant here , cascading cut doesn't affect size
        this.meld(tmp_heap2);

        
 

        this.totalCuts++;
        

        // Handle marking
        if (node.mark == true) {
             node.mark = false;// Node is now a root, unmark it
             this._numMarkedNodes--; }
        
        if (parent.parent == null) {
            return; // Parent is a root, no further action needed
        }
        
        if (parent.mark == false) {
            parent.mark = true;
            this._numMarkedNodes++;
        } else {
            cascadingCut(parent);
        }

   
        return;
    }

    public void delete(HeapItem x) 
    {    
        decreaseKey(x,Integer.MAX_VALUE); // decrease to negative infinity
        deleteMin();
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
        if(heap2.min.key < this.min.key) {
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
        return this._numMarkedNodes; // should be replaced by student code
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
        return this.totalCuts; // should be replaced by student code
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return this.totalHeapifyCosts; // should be replaced by student code
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
    int guard = 0;
    // We use a do-while because it's a circular list
    do {
        printTree(curr, 0);
        curr = curr.next;
        guard++;
        if (guard > this.size + 1) {
            System.out.println("WARNING: root list traversal exceeded size; possible cycle corruption.");
            break;
        }
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
    if (level > this.size + 1) {
        System.out.println("WARNING: printTree depth exceeded size; possible parent/child cycle at key " + node.item.key + ".");
        return;
    }
    if (node.child == node) {
        System.out.println("WARNING: node is its own child at key " + node.item.key + ".");
        return;
    }

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
        int guard = 0;
        do {
            printTree(child, level + 1);
            child = child.next;
            guard++;
            if (guard > this.size + 1) {
                System.out.println("WARNING: child list traversal exceeded size at node " + node.item.key + ".");
                break;
            }
        } while (child != firstChild);
    }
    }
}
