class Main {
    public static void main(String[] args) {
        // 1. Test Lazy Melds (Standard)
        System.out.println("TESTING LAZY MELD HEAP:");
        Heap lazyHeap = new Heap(true, true);
        lazyHeap.insert(10, "A");
        lazyHeap.insert(20, "B");
        lazyHeap.insert(5, "C"); // This will be the new min
        lazyHeap.insert(15, "D");
        lazyHeap.print();
        System.out.println("Total Trees (Lazy): " + lazyHeap.numTrees); // Should be 4

        System.out.println("\n\n");

        // 2. Test Non-Lazy Melds (Immediate Consolidation)
        System.out.println("TESTING NON-LAZY MELD HEAP:");
        Heap nonLazyHeap = new Heap(true, true);
        nonLazyHeap.insert(10, "A");
        Heap.HeapItem itemB = nonLazyHeap.insert(20, "B"); // Should link with 10
        nonLazyHeap.insert(5, "C");
        Heap.HeapItem itemD = nonLazyHeap.insert(15, "D"); // Should link with 5
        nonLazyHeap.print();
        System.out.println("Total Trees (Non-Lazy): " + nonLazyHeap.numTrees); // Should be much lower
        //test deleteMin on non-lazy heap
        System.out.println("\nDeleting min from Non-Lazy Heap:");
        nonLazyHeap.deleteMin();
        nonLazyHeap.print();
        // test decrease key using the HeapItem handle returned from insert
        System.out.println("\nDecreasing key of node 'B' from 20 to 7 in Non-Lazy Heap:");
        nonLazyHeap.decreaseKey(itemB, 13); // diff=13 so new key is 7
        System.out.println("After decreaseKey:");
        System.out.println("New min should be 7: " + nonLazyHeap.findMin().key);
        System.out.println("Heap after decreaseKey:");
        nonLazyHeap.print();   


    }
}