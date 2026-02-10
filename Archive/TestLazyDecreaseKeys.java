import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLazyDecreaseKeys {
    static void testNumMarkedNodes(Heap heap) {
        Heap.HeapItem[] nodes = new Heap.HeapItem[21];
        // insert 8 nodes and extract_min to force a degree-3 tree (B3)
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i, "Value" + i);
        }
        // System.out.println(heap);
        heap.deleteMin();
        // System.out.println(heap);
        // Current State: A single tree with a root and several subtrees.
        // Let's assume 'P' is a parent node with children 'C1' and 'C2'.
        Heap.HeapItem P = nodes[19].node.parent.item; // Example parent node
        Heap.HeapItem C1 = nodes[19]; // Child 1
        Heap.HeapItem C2 = nodes[18]; // Child 2
        assertEquals(0, heap.numMarkedNodes(), "Initially, there should be no marked nodes.");
        // Trigger First Mark (The "Strike 1")
        // Decrease key of C1 to a value smaller than the heap min to force a cut
        int diff = C1.key + 1;
        heap.decreaseKey(C1, diff);
        // System.out.println(heap);
        assertNull("C1 should now be a root", C1.node.parent);
        assertTrue("Parent P should be marked after losing its first child", P.node.mark);
        assertEquals(1, heap.numMarkedNodes(), "After first cut, P should be marked.");
        // Trigger Cascading Cut (The "Strike 2")
        // Decrease key of C2 to a value smaller than the heap min to force a cut
        Heap.HeapItem GP = P.node.parent != null ? P.node.parent.item : null; // Grandparent node
        diff = C2.key + 2;
        heap.decreaseKey(C2, diff);
        assertNull("C2 should now be a root", C2.node.parent);
        assertNull("Parent P should now be a root after losing second child", P.node.parent);
        assertFalse("P should be unmarked after being cut", P.node.mark);
        if (GP != null && GP.node.parent != null) {
            // GP can only be marked if GP is not a root
            assertTrue("Grandparent GP should be marked after losing its first child", GP.node.mark);
            assertEquals(1, heap.numMarkedNodes());
        } else {
            // GP is null or GP is a root - no marking expected
            assertEquals(0, heap.numMarkedNodes());
        }
    }
}
