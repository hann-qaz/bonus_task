# BST vs Hash Table (Java)

Project comparing a Binary Search Tree and a Hash Table. Both data
structures are implemented from scratch with integer keys. Java's `TreeMap` and
`HashMap` are not used.

## Files

- `src/BinarySearchTree.java` - unbalanced binary search tree.
- `src/SimpleHashTable.java` - hash table with separate chaining and resizing.
- `src/PerformanceTest.java` - correctness checks and performance experiment.
- `src/StructureTest.java` - small tests for normal and edge cases.
- `results/benchmark_results.csv` - machine-readable real measurements.
- `results/benchmark_output.txt` - full output, environment, and method.
- `HANDWRITTEN_REPORT.md` - explanation of my analysis and findings

## How to compile and run

Requirements: JDK 17 or newer.

```bash
mkdir -p out
javac -d out src/*.java
java -cp out StructureTest
java -cp out PerformanceTest
```

Running the program checks the returned results, prints the benchmark table,
and replaces both files in `results/` with measurements from the current
computer.

## Implementation

### Binary Search Tree

Each node stores a key and links to its left and right children. Smaller keys go
left and larger keys go right. Search follows the same rule. Deletion handles
three cases: a leaf, a node with one child, and a node with two children. In the
last case, the node is replaced by the smallest key from its right subtree.

This is a deliberately simple **unbalanced** BST. Its speed depends on the tree
height.

### Hash Table

The hash table uses an array of buckets. A bucket stores a linked list, so keys
with the same bucket index can coexist (separate chaining). When the load factor
would exceed 0.75, the bucket array is doubled and keys are redistributed.

## Time complexity analysis

Here `n` is the number of stored keys and `h` is the BST height.

| Structure | Operation | Best case | Expected / average case | Worst case |
|---|---|---:|---:|---:|
| BST | Insert | O(1) | O(log n) | O(n) |
| BST | Search | O(1) | O(log n) | O(n) |
| BST | Delete | O(1) | O(log n) | O(n) |
| Hash Table | Insert | O(1) | O(1) amortized | O(n) |
| Hash Table | Search | O(1) | O(1) | O(n) |
| Hash Table | Delete | O(1) | O(1) | O(n) |

For the BST, an operation can finish at the root in the best case. With a fairly
balanced tree, its height is O(log n), but sorted insertion can create a chain
of `n` nodes. The implementation does not rebalance itself.

For the hash table, a good distribution keeps bucket chains short. Insertion is
amortized O(1): an occasional resize costs O(n), but it does not happen on every
insertion. In the worst case, many keys collide in one bucket, creating a chain
of length `n`. A single insertion that triggers resizing is also O(n).

## Experiment method

The test uses 100, 1,000, and 10,000 unique, widely distributed integer keys.
For every measured
run, keys are shuffled before insertion, search, and deletion. Searches contain
50% existing and 50% missing keys. The same deterministic seed and equivalent key orders are used for
both structures. Five JVM warm-up runs are excluded, then the mean of 20 runs
is reported. Each table value is the average total time for a complete batch of
`n` operations, measured with `System.nanoTime()`.

The test represents typical randomly ordered data. It does not experimentally
claim that the BST can never degrade: its O(n) worst case is explained above.


## Results

These are the actual results from the included run on 10 October 2026 using
Java 25.0.2 on macOS 15.6 (Apple Silicon). Times are milliseconds for a complete
batch of `n` operations and are the mean of 20 measured runs.

| Elements | Structure | Insert (ms) | Search (ms) | Delete (ms) |
|---:|---|---:|---:|---:|
| 100 | BST | 0.004831 | 0.005338 | 0.006942 |
| 100 | Hash Table | 0.012965 | 0.002694 | 0.002187 |
| 1,000 | BST | 0.051533 | 0.039648 | 0.053448 |
| 1,000 | Hash Table | 0.041121 | 0.018675 | 0.015594 |
| 10,000 | BST | 0.568206 | 0.421079 | 0.601462 |
| 10,000 | Hash Table | 0.170227 | 0.081898 | 0.037888 |

In this run, the BST inserted 100 elements faster, probably because the hash
table had resizing overhead. At 1,000 and 10,000 elements, the hash table was
faster for insertion. It was faster for search and deletion at every tested
size. The gap generally became clearer for larger inputs, which agrees with the
expected O(1) hash-table operations versus O(log n) operations in the randomly
shaped BST.

Raw results and exact environment information are stored in
`results/benchmark_results.csv` and `results/benchmark_output.txt`.

## Comparison and conclusion

The measured results should be interpreted together with the complexity table.
A hash table normally searches by computing an array index and then examining a
short bucket chain. A BST search follows object references through several tree
levels, which may cause more comparisons and less cache-friendly memory access.

Real performance also differs because of tree shape, collisions, resizing,
memory allocation, JVM JIT compilation, garbage collection, CPU caching, and
measurement noise. A hash table is usually preferable for exact-key lookup. A
BST can still be preferable when sorted traversal, minimum/maximum values, or
range queries are required, because this simple hash table does not keep keys
in order.

## GitHub

https://github.com/hann-qaz/bonus_task
git init
git add README.md HANDWRITTEN_REPORT.md .gitignore src results
git commit -m "Add BST and hash table comparison"
git branch -M main
git remote add origin https://github.com/hann-qaz/bonus_task
git push -u origin main



