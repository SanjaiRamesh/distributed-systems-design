package caching;

import java.util.HashMap;
import java.util.Map;

class DLinkedList {
    int key;
    int value;
    DLinkedList post;
    DLinkedList pre;
}
public class LRUCache {

    Map<Integer, DLinkedList> cache =  new HashMap<>();
    int count = 0;
    int capacity  = 0;
    DLinkedList head,tail;
    public LRUCache(int capacity) {
        this.count = 0;
        this.capacity = capacity;
        head =  new DLinkedList();
        tail =  new DLinkedList();
        head.pre = null;
        tail.post = null;
        head.post = tail;
        tail.pre = head;
    }

    private void addNode(DLinkedList node){
        node.pre = head;
        node.post = head.post;
        head.post.pre = node;
        head.post = node;

    }
    private DLinkedList popNode() {

        DLinkedList last = tail.pre;
        removeNode(last);
        return last;
    }

    private void removeNode(DLinkedList node) {

        DLinkedList pre = node.pre;
        DLinkedList post = node.post;
        pre.post = post;
        post.pre = pre;
    }

    public int get(int key) {
        DLinkedList node = cache.get(key);
        if( count ==0 || node == null){
            return -1;
        }
        removeNode(node);
        addNode(node);
        return node.value;
    }

    public void put(int key, int value) {
        DLinkedList node = cache.get(key);
        if( node == null){

            DLinkedList n = new DLinkedList();
            n.key = key;
            n.value = value;

            addNode(n);
            cache.put(key,n);
            ++count;

            if( count>capacity){
                DLinkedList lastNode = popNode();
                removeNode(lastNode);
                cache.remove(lastNode.key);
                --count;
            }
        } else {
            node.value = value;
            removeNode(node);
            addNode(node);


        }
    }


    public static void main(String[] args) {
        LRUCache lRUCache = new LRUCache(2);
        lRUCache.put(1, 1); // cache is {1=1}
        lRUCache.put(2, 2); // cache is {1=1, 2=2}
        System.out.println(lRUCache.get(1));    // return 1
        lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
        System.out.println(lRUCache.get(2));    // returns -1 (not found)
        lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
        System.out.println(lRUCache.get(1));    // return -1 (not found)
        System.out.println(lRUCache.get(3));    // return 3
        System.out.println(lRUCache.get(4));    // return 4

    }
}

/*
Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.

Implement the LRUCache class:

LRUCache(int capacity) Initialize the LRU cache with positive size capacity.
int get(int key) Return the value of the key if the key exists, otherwise return -1.
void put(int key, int value) Update the value of the key if the key exists. Otherwise, add the key-value pair to the cache. If the number of keys exceeds the capacity from this operation, evict the least recently used key.
The functions get and put must each run in O(1) average time complexity.



Example 1:

Input
["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
[[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
Output
[null, null, null, 1, null, -1, null, -1, 3, 4]

Explanation
LRUCache lRUCache = new LRUCache(2);
lRUCache.put(1, 1); // cache is {1=1}
lRUCache.put(2, 2); // cache is {1=1, 2=2}
lRUCache.get(1);    // return 1
lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
lRUCache.get(2);    // returns -1 (not found)
lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
lRUCache.get(1);    // return -1 (not found)
lRUCache.get(3);    // return 3
lRUCache.get(4);    // return 4
 */