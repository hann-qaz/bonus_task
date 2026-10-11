public class StructureTest {
    public static void main(String[] args) {
        testBst();
        testHashTable();
        System.out.println("All structure tests passed.");
    }

    private static void testBst() {
        BinarySearchTree tree = new BinarySearchTree();
        int[] keys = {8, 3, 10, 1, 6, 14, 4, 7, 13};
        for (int key : keys) {
            tree.insert(key);
        }

        check(tree.size() == keys.length, "BST size after insertion");
        tree.insert(6);
        check(tree.size() == keys.length, "BST must ignore a duplicate");
        check(tree.contains(7), "BST must find an existing key");
        check(!tree.contains(2), "BST must not find a missing key");
        check(tree.delete(1), "BST must delete a leaf");
        check(tree.delete(14), "BST must delete a node with one child");
        check(tree.delete(8), "BST must delete a node with two children");
        check(!tree.delete(99), "BST must report a missing key");
        check(tree.size() == keys.length - 3, "BST size after deletion");
    }

    private static void testHashTable() {
        SimpleHashTable table = new SimpleHashTable();
        for (int i = 0; i < 100; i++) {
            table.insert(i * 16); // exercises collisions and resizing
        }

        check(table.size() == 100, "Hash table size after insertion");
        table.insert(32);
        check(table.size() == 100, "Hash table must ignore a duplicate");
        check(table.contains(16 * 55), "Hash table must find an existing key");
        check(!table.contains(-1), "Hash table must not find a missing key");
        check(table.delete(16 * 55), "Hash table must delete an existing key");
        check(!table.delete(-1), "Hash table must report a missing key");
        check(table.size() == 99, "Hash table size after deletion");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
