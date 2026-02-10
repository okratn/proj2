import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestHeapFibonacci extends TestHeapBinomialLazy {
    @BeforeEach
    @Override
    void setUp() {
        heap = new HeapFibonacci();
    }

    @Test
    @Override
    void testMeld() {
        Heap otherHeap = new HeapFibonacci();
        testMeldFunc(otherHeap, 2000);
    }

    @Test
    @Override
    void testNumMarkedNodes() {
        TestLazyDecreaseKeys.testNumMarkedNodes(heap);
    }

    @Test
    @Override
    void testTotalLinks() {
        TestLazyMelds.testTotalLinks(heap);
    }

    @Test
    @Override
    void testTotalCuts() {
        testTotalCutsFunc(0, 95);
    }

    @Test
    void testHeapifyCosts() {
        heapifyCostFunc(0);
    }
}