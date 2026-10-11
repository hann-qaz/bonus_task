public class SimpleHashTable {
    private static class Node {
        int key;
        Node next;

        Node(int key, Node next) {
            this.key = key;
            this.next = next;
        }
    }

    private static final double LOAD_FACTOR = 0.75;
    private Node[] buckets = new Node[16];
    private int size;

    public void insert(int key) {
        if (contains(key)) {
            return; // duplicates are not stored
        }
        if (size + 1 > buckets.length * LOAD_FACTOR) {
            resize();
        }

        int index = indexFor(key, buckets.length);
        buckets[index] = new Node(key, buckets[index]);
        size++;
    }

    public boolean contains(int key) {
        int index = indexFor(key, buckets.length);
        Node current = buckets[index];
        while (current != null) {
            if (current.key == key) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean delete(int key) {
        int index = indexFor(key, buckets.length);
        Node current = buckets[index];
        Node previous = null;

        while (current != null) {
            if (current.key == key) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    private int indexFor(int key, int length) {
        int hash = Integer.hashCode(key);
        hash = hash ^ (hash >>> 16);
        return hash & (length - 1);
    }

    private void resize() {
        Node[] oldBuckets = buckets;
        buckets = new Node[oldBuckets.length * 2];

        for (Node oldHead : oldBuckets) {
            Node current = oldHead;
            while (current != null) {
                Node next = current.next;
                int newIndex = indexFor(current.key, buckets.length);
                current.next = buckets[newIndex];
                buckets[newIndex] = current;
                current = next;
            }
        }
    }

    public int size() {
        return size;
    }
}
