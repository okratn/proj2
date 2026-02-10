import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


class TestHeapBinomial {
    protected Heap heap;
    Random random = new Random();

    @BeforeEach
    void setUp() {
        heap = new HeapBinomial();
    }

    @Test
    void testInsert() {
        Heap.HeapItem node;
        int size = 100000;
        for (int i = 0; i < size; i++) {
            node = heap.insert(i, "Value" + i);
            assertEquals(i, node.key);
        }
        assertEquals(size, heap.size());
    }

    @Test
    void testInsertRandom() {
        Heap.HeapItem node;
        int size = 1000;
        for (int i = 0; i < size; i++) {
            int randomInt = random.nextInt(Integer.MAX_VALUE);
            node = heap.insert(randomInt, "Value" + randomInt);
            assertEquals(randomInt, node.key);
        }
        assertEquals(size, heap.size());
    }

    @Test
    void testInsertEquals() {
        Heap.HeapItem node;
        int randomInt = random.nextInt(Integer.MAX_VALUE);
        int size = 1000;
        for (int i = 0; i < size; i++) {
            node = heap.insert(randomInt, "Value" + randomInt);
            assertEquals(randomInt, node.key);
        }
        assertEquals(size, heap.size());
    }

    @Test
    void testFindMin() {
        int minKey = Integer.MAX_VALUE;
        for (int i = 0; i < 1000; i++) {
            int randomInt = random.nextInt(Integer.MAX_VALUE);
            minKey = Math.min(minKey, randomInt);
            heap.insert(randomInt, "Value" + randomInt);
        }
        Heap.HeapItem minNode = heap.findMin();
        assertEquals(minKey, minNode.key);
    }

    @Test
    void testDeleteMin() {
        int randomInt;
        int iters = 1000;
        int keys[] = new int[iters];
        for (int i = 0; i < iters; i++) {
            randomInt = random.nextInt(10000);
            keys[i] = randomInt;
            heap.insert(randomInt, "Value" + randomInt);
        }
        // Sort keys array to get min keys in order
        Arrays.sort(keys);
        for (int i = 1; i < iters; i++) {
            heap.deleteMin();
            Heap.HeapItem minNode = heap.findMin();
            int minKey = keys[i];
            assertEquals(minKey, minNode.key);
        }
        assertEquals(1, heap.size());
    }

    @Test
    void testDecreaseKey() {
        Heap.HeapItem[] nodes = new Heap.HeapItem[1000];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i+1000, "Value" + i);
        }
        for (int i = nodes.length - 1; i > 0; i--) {
            heap.decreaseKey(nodes[i], 1000);
            Heap.HeapItem minNode = heap.findMin();
            assertEquals(i, minNode.key);
        }
        assertEquals(nodes.length, heap.size());
    }

    @Test
    void testDelete() {
        Heap.HeapItem[] nodes = new Heap.HeapItem[1000];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i, "Value" + i);
        }
        for (int i = 0; i < nodes.length - 1; i++) {
            heap.delete(nodes[i]);
            var node = heap.findMin();
            assertEquals(i + 1, node.key);
        }
        assertEquals(1, heap.size());
    }

    @Test
    void testMeld() {
        Heap otherHeap = new HeapBinomial();
        testMeldFunc(otherHeap, 6);
    }

    void testMeldFunc(Heap otherHeap, int numTreesExpected) {
        int MAX = 1000;
        for (int i = MAX; i < 2 * MAX; i++) {
            heap.insert(i, "Value" + i);
        }
        for (int i = 0; i < MAX; i++) {
            otherHeap.insert(i, "Value" + i);        
        }
        System.out.println("Number of trees before meld: " + heap.numTrees());
        System.out.println("Number of trees in other heap: " + otherHeap.numTrees());
        int cuts = heap.totalCuts();
        int links = heap.totalLinks();
        int cost = heap.totalHeapifyCosts();
        int cuts2 = otherHeap.totalCuts();
        int links2 = otherHeap.totalLinks();
        int cost2 = otherHeap.totalHeapifyCosts();
        heap.meld(otherHeap);
        System.out.println("Number of trees after meld: " + heap.numTrees());
        Heap.HeapItem minNode = heap.findMin();
        assertEquals(0, minNode.key);
        assertEquals(2*MAX, heap.size());
        assertEquals(numTreesExpected, heap.numTrees());
        assertTrue(cuts + cuts2 <= heap.totalCuts());
        assertTrue(links + links2 <= heap.totalLinks());
        assertTrue(cost + cost2 <= heap.totalHeapifyCosts());
    }

    @Test
    void testSize() {
        int MAX = 1000;
        assertEquals(0, heap.size());
        for (int i = 0; i < MAX; i++) {
            heap.insert(i, "Value" + i);
        }
        assertEquals(MAX, heap.size());
    }

    @Test
    void testNumTrees() {
        List<Heap.HeapItem> nodes = new ArrayList<>();
        int num_nodes = 10000;
        for (int i = 0; i < num_nodes; i++) {
            nodes.add(heap.insert(i+1000, "Value" + i));
        }
        int num_trees_expected = countTreesAfterInsert(nodes.size());
        int num_trees = heap.numTrees();
        System.out.println("Number of trees after " + num_nodes + " insert: " + num_trees);
        assertEquals(num_trees_expected, num_trees);
        for (int i = 0; i < nodes.size(); i++) {
            heap.decreaseKey(nodes.get(i), 1000);
        }
        num_trees = heap.numTrees();
        System.out.println("Number of trees after " + num_nodes + " decreaseKey: " + num_trees);
        assertEquals(num_trees_expected, num_trees);
        int num_delete = 1000;
        for (int i = 0; i < num_delete; i++) {
            heap.deleteMin();
            nodes.remove(0);
        }
        num_trees_expected = countTreesAfterMeld(nodes.size());
        num_trees = heap.numTrees();
        System.out.println("Number of trees after " + num_delete + " deleteMin: " + num_trees);
        assertEquals(num_trees_expected, num_trees, "Wrong number of trees after deleteMin");
        // Delete
        for (int i = 0; i < num_delete; i++) {
            heap.delete(nodes.get(i));
            nodes.remove(0);
        }
        num_trees_expected = countTreesAfterMeld(nodes.size());
        num_trees = heap.numTrees();
        System.out.println("Number of trees after " + num_delete + " delete: " + num_trees);
        // System.out.println(heap.shortDescription());
        // System.out.println(heap);
        assertTrue(num_trees_expected <= num_trees, "Wrong number of trees after delete");
    }
    
    protected int countTrees(int num_nodes) {
        int count = 0;
        while (num_nodes > 0) {
            if ((num_nodes & 1) == 1) {
                count++;
            }
            num_nodes >>= 1;
        }
        return count;
    }
    protected int countTreesAfterInsert(int num_nodes) {
        return countTrees(num_nodes);
    }

    protected int countTreesAfterMeld(int num_nodes) {
        return countTrees(num_nodes);
    }

    @Test
    void testNumMarkedNodes() {
        Heap.HeapItem[] nodes = new Heap.HeapItem[10];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i+1000, "Value" + i);
        }
        for (int i = nodes.length - 1; i > 0; i--) {
            heap.decreaseKey(nodes[i], 1000);
        }
        assertEquals(0, heap.numMarkedNodes());
    }

    @Test
    void testTotalLinks() {
        int[] expected_links = {0, 1, 1, 3, 3, 4, 4, 7, 7, 8};
        Heap.HeapItem[] nodes = new Heap.HeapItem[10];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i, "Value" + i);
            assertEquals(expected_links[i], heap.totalLinks());
        }
    }

    @Test
    void testTotalCuts() {
        testTotalCutsFunc(0, 0);
    }

    void testTotalCutsFunc(int expectedCutsNumAfterInsert, int expectedCutsNumAfterDecreaseKey) {
        // Insert nodes
        Heap.HeapItem[] nodes = new Heap.HeapItem[100];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i+1000, "Value" + i);
        }
        // Delete min for triggering links
        heap.deleteMin();
        assertEquals(expectedCutsNumAfterInsert, heap.totalCuts());
        // Decrease keys
        for (int i = nodes.length - 1; i > 0; i--) {
            heap.decreaseKey(nodes[i], 1000);
        }
        assertEquals(expectedCutsNumAfterDecreaseKey, heap.totalCuts());
        // Delete nodes
        for (int i = 0; i < 30; i++) {
            heap.delete(nodes[i+1]);
        }
        assertEquals(expectedCutsNumAfterDecreaseKey, heap.totalCuts());
        // Final delete min
        for (int i = 0; i < 30; i++) {
            heap.deleteMin();
        }
        assertEquals(expectedCutsNumAfterDecreaseKey, heap.totalCuts());
    }

    @Test
    void testHeapifyCosts() {
        heapifyCostFunc(363);
    }

    void heapifyCostFunc(int expectedCost) {
        // Insert nodes
        Heap.HeapItem[] nodes = new Heap.HeapItem[100];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = heap.insert(i+1000, "Value" + i);
        }
        // Delete min for triggering links
        heap.deleteMin();
        assertEquals(0, heap.totalHeapifyCosts());
        // Decrease keys
        for (int i = nodes.length - 1; i > 0; i--) {
            heap.decreaseKey(nodes[i], 1000);
        }
        assertEquals(expectedCost, heap.totalHeapifyCosts());
        // Delete nodes
        for (int i = 0; i < 30; i++) {
            heap.delete(nodes[i+1]);
        }
        assertEquals(expectedCost, heap.totalHeapifyCosts());
        // Final delete min
        for (int i = 0; i < 30; i++) {
            heap.deleteMin();
        }
        assertEquals(expectedCost, heap.totalHeapifyCosts());
    }
}
