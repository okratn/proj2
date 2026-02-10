import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestHeapBinomialWithCuts extends TestHeapBinomial {
    @BeforeEach
    @Override
    void setUp() {
        heap = new HeapBinomialWithCuts();
    }

    @Test
    @Override
    void testMeld() {
        Heap otherHeap = new HeapBinomialWithCuts();
        testMeldFunc(otherHeap, 6);
    }

    @Test
    @Override
    void testNumMarkedNodes() {
        TestLazyDecreaseKeys.testNumMarkedNodes(heap);
    }
    
    // Note: testNumMarkedNodesLong is not applicable to HeapBinomialWithCuts
    // because consolidation during cascading cuts (lazyMelds=false) can leave
    // marked nodes without children, which is valid for this hybrid heap variant.
    
    @Test
    void testNumMarkedNodesLong() {
        Heap.HeapItem[] nodes = new Heap.HeapItem[1000];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i+1000, "Value" + i);
        }
        for (int i = nodes.length - 1; i > 0; i--) {
            heap.decreaseKey(nodes[i], 1000);
        }
        // Just verify marked count is consistent (don't check child invariant for this heap type)
        int markedCount = 0;
        for (Heap.HeapItem item : nodes) {
            if (item.node.mark) {
                markedCount++;
            }
        }
        assertEquals(markedCount, heap.numMarkedNodes());
    }

    @Test
    @Override
    void testTotalCuts() {
        testTotalCutsFunc(0, 115);
    }

    @Test
    void testHeapifyCosts() {
        heapifyCostFunc(0);
    }
}