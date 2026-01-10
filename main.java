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
    Heap nonLazyHeap = new Heap(false, true);
    nonLazyHeap.insert(10, "A");
    nonLazyHeap.insert(20, "B"); // Should link with 10
    nonLazyHeap.insert(5, "C");  
    nonLazyHeap.insert(15, "D"); // Should link with 5
    nonLazyHeap.print();
    System.out.println("Total Trees (Non-Lazy): " + nonLazyHeap.numTrees); // Should be much lower
}