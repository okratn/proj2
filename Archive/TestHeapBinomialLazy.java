import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestHeapBinomialLazy extends TestHeapBinomial {
    @BeforeEach
    @Override
    void setUp() {
        heap = new HeapBinomialLazy();
    }

    protected int countTreesAfterInsert(int num_nodes) {
        return num_nodes;
    }

    protected int countTreesAfterMeld(int num_nodes) {
        return countTrees(num_nodes);
    }

    @Test
    @Override
    void testMeld() {
        Heap otherHeap = new HeapBinomialLazy();
        testMeldFunc(otherHeap, 2000);
    }

    @Test
    @Override
    void testTotalLinks() {
        TestLazyMelds.testTotalLinks(heap);
    }
    
    @Test
    void testHeapifyCosts() {
        heapifyCostFunc(360);
    }
}