# Handwritten report


## Purpose
 I compared Binary Search Tree and Hash-table data structures with insertion, search and 
 delete operations.

## How each structure works
- If inserted value in BST is less than parent node it goes to the left node, and if value is greater than 
parent node it goes to the right node.
- Hash function calculates index of a bucket using key
- Collisions handled using separate chaining. If keys produce the same index they are 
stored in a Linked List.

## Complexity
- BST usually have height close to log n because with each next level number of nodes doubles.
- If we insert already sorted list of integers into BST it can turn into linked list chain, because every 
value will go to the same side.
- Hash-table operation usually constant time because hash function calculates index directly.
- Many collisions can create the hash table's worst case. When all keys produce the same bucket index. And
hash table becomes linked list.

## Experiment
- Were tested 100, 1000, 10000 elements
- Warm-up runs were used for Java to have more precise results.
- One value in the results table measure ns per 1 operation. N operations / ns
- For my computer Hash Table was faster
- For BST time increased when number of elements increased, for Hash Table is opposite.

## Conclusion
- For fast exact-key lookup I would choose Hash Table
- BST still might be useful for  minimum/maximum values, or when order is matter.
- Theoretical complexity and actual timing can differ because of tree shape, collisions, resizing,
cache

