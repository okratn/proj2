import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLazyMelds {
    static void testTotalLinks(Heap heap) {
        Heap.HeapItem[] nodes = new Heap.HeapItem[10];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i, "Value" + i);
        }
        assertEquals(0, heap.totalLinks());
    }
}
