public class BinarySearchTree {
    private static class Node {
        int key;
        Node left;
        Node right;

        Node(int key) {
            this.key = key;
        }
    }

    private Node root;
    private int size;
    private boolean removed;

    public void insert(int key) {
        if (root == null) {
            root = new Node(key);
            size++;
            return;
        }

        Node current = root;
        while (true) {
            if (key == current.key) {
                return; // duplicates are not stored
            } else if (key < current.key) {
                if (current.left == null) {
                    current.left = new Node(key);
                    size++;
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(key);
                    size++;
                    return;
                }
                current = current.right;
            }
        }
    }

    public boolean contains(int key) {
        Node current = root;
        while (current != null) {
            if (key == current.key) {
                return true;
            }
            current = key < current.key ? current.left : current.right;
        }
        return false;
    }

    public boolean delete(int key) {
        removed = false;
        root = deleteNode(root, key);
        if (removed) {
            size--;
        }
        return removed;
    }

    private Node deleteNode(Node node, int key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            node.left = deleteNode(node.left, key);
        } else if (key > node.key) {
            node.right = deleteNode(node.right, key);
        } else {
            removed = true;
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            Node successor = findSmallest(node.right);
            node.key = successor.key;
            node.right = deleteNode(node.right, successor.key);
        }
        return node;
    }

    private Node findSmallest(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public int size() {
        return size;
    }
}
