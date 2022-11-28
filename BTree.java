/**
 * Do NOT modify.
 * This is the class with the main function
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * B+Tree Structure
 * Key - StudentId
 * Leaf Node should contain [ key,recordId ]
 */
class BTree {

    /**
     * Pointer to the root node.
     */
    private BTreeNode root;
    /**
     * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
     **/
    private int t;

    private ArrayList<BTreeNode> res = new ArrayList<BTreeNode>();

    private BTreeNode internal = null;
    private BTreeNode index = null;

    BTree(int t) {
        this.root = null;
        this.t = t;
    }

    long search(long studentId) {

        long rc = find(root, studentId);
        if(rc >= 0){
            return rc;
        }else{
            return -1;
        }
    }

    private long find(BTreeNode root, long studentId){
        index = root;
        if(root == null){
            return -1;
        }else if(root.leaf){
            for(int i = 0; i < root.keys.size(); i++) {
                if (root.keys.get(i) == studentId) {
                    return root.values.get(i);
                }
            }
        }else{
            if(studentId < index.keys.get(0)){
                return find(index.children.get(0), studentId);
            }else if (studentId >= index.keys.get(index.keys.size() - 1)){
                return find(index.children.get(index.children.size() - 1), studentId);
            }else{
                for (int i = 0; i < index.keys.size(); i++) {
                    if (index.keys.get(i) > studentId) {
                        return find(index.children.get(i), studentId);
                    }
                }
            }
        }
        return -1;
    }

    BTree insert(Student student) {
    	if (root == null){  
            root = new BTreeNode(t, true);
            root.keys.add(student.studentId);
            root.values.add(student.recordId);
        } else {
            BTreeNode leaf = findLeafNode(root, student.studentId);
            if (leaf.keys.size() < 2 * t - 1){
                leaf.keys.add(student.studentId);
                Collections.sort(leaf.keys);
                leaf.values.add(leaf.keys.indexOf(student.studentId), student.recordId);
            } else if (leaf.keys.size() == 2 * t - 1){
                leaf.keys.add(student.studentId);   
                Collections.sort(leaf.keys);
                leaf.values.add(leaf.keys.indexOf(student.studentId), student.recordId);
                BTreeNode leafParent = findParent(root, leaf);
                if (leafParent == null){
                    root = splitLeafNode(leaf); 
                } else {    
                    BTreeNode newInternal = splitLeafNode(leaf);    
                    insertInternal(newInternal, leafParent, leaf);  
                }
            }
        }
        return this;
    }

    private BTreeNode findLeafNode(BTreeNode nodePointer, long studentId){
        if (nodePointer.leaf){  
            return nodePointer;
        } else {
            int i;  
            for (i = 0; i < nodePointer.keys.size(); i++) {
                if (studentId < nodePointer.keys.get(i)){
                    break;
                }
            }
            BTreeNode nextLevelNode = nodePointer.children.get(i);  
            if (nextLevelNode.leaf){
                return nextLevelNode;
            } else {
                return findLeafNode(nodePointer.children.get(i), studentId);
            }
        }
    }

    private BTreeNode splitLeafNode(BTreeNode nodePointer){
        BTreeNode nextNode = nodePointer.next;
        BTreeNode secondHalf = new BTreeNode(t, true);
        for(int i = 0; i < t; i++){
            secondHalf.keys.add(nodePointer.keys.get(t + i));
            secondHalf.values.add(nodePointer.values.get(t + i));
        }
        for(int j = 0; j < t; j++){
        	nodePointer.keys.remove(nodePointer.keys.size() - 1);
        	nodePointer.values.remove(nodePointer.values.size() - 1);
        }
        secondHalf.next = nextNode;
        nodePointer.next = secondHalf;

        BTreeNode copyup = new BTreeNode(t, false);
        copyup.keys.add(secondHalf.keys.get(0));    
        copyup.children.add(nodePointer);                  
        copyup.children.add(secondHalf);
        return copyup;
    }

    private BTreeNode splitInternalNode(BTreeNode nodePointer){
        BTreeNode left = new BTreeNode(t, false);
        BTreeNode right = new BTreeNode(t, false);
        BTreeNode upperNode = new BTreeNode(t, false);
        upperNode.keys.add(nodePointer.keys.get(t));   
        for (int i = 0; i < t; i++) {
            left.keys.add(nodePointer.keys.get(i));
        }
        for (int i = 0; i < t + 1; i++) {
            left.children.add(nodePointer.children.get(i));
        }
        for (int i = t + 1; i < 2 * t; i++) {
            right.keys.add(nodePointer.keys.get(i));
        }
        for (int i = t + 1; i < 2 * t + 1; i++) {
            right.children.add(nodePointer.children.get(i));
        }
        upperNode.children.add(left);
        upperNode.children.add(right);
        return upperNode;
    }

    private BTreeNode findParent(BTreeNode node, BTreeNode curNode){
        BTreeNode parent = new BTreeNode(t, false);
        if (node.leaf){     
            return null;
        }
        for (int i = 0; i < node.children.size(); i++) {
            if (node.children.get(i) == curNode) {
                parent = node;
                return parent;
            } else {    
                parent = findParent(node.children.get(i), curNode);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return parent;
    }

    private void insertInternal(BTreeNode target, BTreeNode curNode, BTreeNode child){
        if (curNode.keys.size() < 2 * t - 1){   
            curNode.keys.add(target.keys.get(0));   
            Collections.sort(curNode.keys);
            int leftChildIndex = curNode.keys.indexOf(target.keys.get(0));
            int rightChildIndex = leftChildIndex + 1;
            curNode.children.remove(child);
            curNode.children.add(leftChildIndex, target.children.get(0));
            curNode.children.add(rightChildIndex, target.children.get(1));
        } else {
            curNode.keys.add(target.keys.get(0));   
            Collections.sort(curNode.keys);
            int leftChildIndex = curNode.keys.indexOf(target.keys.get(0));
            int rightChildIndex = leftChildIndex + 1;
            curNode.children.remove(child);
            curNode.children.add(leftChildIndex, target.children.get(0));
            curNode.children.add(rightChildIndex, target.children.get(1));

            BTreeNode upperLevelNode = findParent(root, curNode);

            if (upperLevelNode == null){    
                root = splitInternalNode(curNode);
            } else {    
                BTreeNode newTarget = splitInternalNode(curNode);
                insertInternal(newTarget, upperLevelNode, curNode);
            }
        }
    }

    private ArrayList<BTreeNode> deleteSearch(BTreeNode root, long key){
        index = root;
        if(index == null){
            return null;
        }else if(index.leaf){
            for(int i = 0; i < index.keys.size(); i++) {
                if (index.keys.get(i) == key) {
                    res.add(index);
                    res.add(internal);
                    return res;
                }
            }
        }else{
            if(key < index.keys.get(0)){
                return deleteSearch(index.children.get(0), key);
            }else if (key >= index.keys.get(index.keys.size() - 1)){
                return deleteSearch(index.children.get(index.children.size() - 1), key);
            }else{
                internal = index; 
                for (int i = 0; i < index.keys.size(); i++) {
                    if (index.keys.get(i) > key) {
                        return deleteSearch(index.children.get(i), key);
                    }
                }
            }
        }
        return null;
    }

    boolean delete(long studentId) {

        if(root == null) return false;
        if(root.keys.isEmpty()){
            root = null;
            return false;
        }
        ArrayList<BTreeNode> info;
        info = deleteSearch(root, studentId);
        if(info == null){
            return false;
        }
        BTreeNode currNode = info.get(0);
        BTreeNode internalNode = info.get(1);

        if(currNode == null && internalNode == null){
            return false;
        }
        if(currNode != null){
            return leaf_delete(studentId, currNode);
        }else if(internalNode != null){
            return internal_delete(studentId, currNode, internalNode);
        }else{
            return false;
        }
    }

    private boolean leaf_delete(long key, BTreeNode currNode){
        BTreeNode parent = findParent(root, currNode);
        int keyIndex = -1;
        for(int i = 0; i < currNode.keys.size(); i++){
            if(currNode.keys.get(i) == key){
                keyIndex = i;
                break;
            }
        }

        if(currNode.keys.size() - 1 >= t){
            currNode.keys.remove(keyIndex);
            currNode.values.remove(keyIndex);
        }else{
            int siblingIndex = -1;
            int childIndex = -1;
            for(int j = 0; j < parent.children.size(); j++){
                if(parent.children.get(j).equals(currNode)) {
                    childIndex = j;
                    break;
                }
            }

            boolean siblingFound = false;
            if (childIndex + 1 < parent.children.size()) { 
                if (parent.children.get(childIndex + 1).keys.size() > t) {
                    siblingIndex = childIndex + 1;
                    siblingFound = true;
                }
            }

            if(!siblingFound) {
                if (childIndex - 1 > 0) { 
                    if (parent.children.get(childIndex - 1).keys.size() > t) {
                        siblingIndex = childIndex - 1;
                    }
                }
            }

            if(siblingIndex != -1){
                if(siblingIndex > childIndex){
                    parent.keys.set(keyIndex, parent.children.get(keyIndex + 1).keys.get(1));
                    currNode.keys.add(parent.children.get(keyIndex + 1).keys.get(0));
                    currNode.values.add(parent.children.get(keyIndex + 1).values.get(0));
                    parent.children.get(keyIndex + 1).keys.remove(0);
                    parent.children.get(keyIndex + 1).values.remove(0);
                }else{
                    parent.keys.set(keyIndex - 1, parent.children.get(keyIndex - 1).keys.get(parent.children.get(keyIndex - 1).keys.size() - 1));
                    currNode.keys.add(0, parent.children.get(keyIndex - 1).keys.get(parent.children.get(keyIndex - 1).keys.size() - 1));
                    currNode.values.add(0, parent.children.get(keyIndex - 1).values.get(parent.children.get(keyIndex - 1).values.size() - 1));
                    parent.children.get(keyIndex - 1).keys.remove(parent.children.get(keyIndex - 1).keys.size() - 1);
                    parent.children.get(keyIndex - 1).values.remove(parent.children.get(keyIndex - 1).values.size() - 1);
                }
            }else{
                return merge(key, currNode, parent, keyIndex, childIndex);
            }
        }
        return true;
    }

    private boolean internal_delete(long key, BTreeNode currNode, BTreeNode internal) {
        int internalIndex = -1;
        for (int i = 0; i < internal.keys.size(); i++) {
            if (key == internal.keys.get(i)) {
                internalIndex = i;
                break;
            }
        }
        internal.keys.set(internalIndex, internal.children.get(internalIndex + 1).keys.get(0));
        return true;
    }

    private boolean merge(long key, BTreeNode currNode, BTreeNode parent, int keyIndex, int childIndex){
        currNode.keys.remove(keyIndex);
        currNode.values.remove(keyIndex);
        if(childIndex == parent.children.size() - 1){
            for(int i = 0; i < currNode.keys.size(); i++){
                parent.children.get(childIndex - 1).keys.add(currNode.keys.get(i));
            }
            parent.children.remove(childIndex);
            parent.keys.remove(keyIndex);
            return true;
        }else{
            for (int i = currNode.keys.size() - 1; i >= 0; i--) {
                parent.children.get(childIndex + 1).keys.add(0, currNode.keys.get(i));
                parent.children.get(childIndex + 1).values.add(0, currNode.values.get(i));
            }
            parent.children.get(childIndex).next = null;
            parent.children.remove(childIndex);
            if(childIndex > 0) {
                parent.children.get(childIndex - 1).next = parent.children.get(childIndex);
            }
            parent.keys.remove(keyIndex);
            return true;
        }
    }


    List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();
        List<Long> listOfStudentID = new ArrayList<>();

 
        BTreeNode curHead = findLeafNode(root, Long.MIN_VALUE);

        while (curHead.next != null){
            listOfRecordID.addAll(curHead.values);
            listOfStudentID.addAll(curHead.keys);
            curHead = curHead.next;
        }
        listOfRecordID.addAll(curHead.values);
        listOfStudentID.addAll(curHead.keys);

        for (int i = 0; i < listOfRecordID.size(); i++){
            System.out.print(" (" + listOfStudentID.get(i) + "," + listOfRecordID.get(i) + ") ");
        }
        return listOfRecordID;
    }
}
