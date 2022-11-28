import java.util.ArrayList;

/**
 * Notification: I have changed BTreeNode constructor; keys, values and children have been changed from array to
 * 					arraylist for time complexity and other purpose. 
 */

class BTreeNode {

	//arrayList
    ArrayList<Long> keys;

	//arrayList
    ArrayList<Long> values;

    /**
     * Minimum degree (defines the range for number of keys)
     **/
    int t;

	//arrayList
    ArrayList<BTreeNode> children;

    /**
     * number of key-value pairs in the B-tree
     */
    int n;

    /**
     * true when node is leaf. Otherwise false
     */
    boolean leaf;

    /**
     * point to other next node when it is a leaf node. Otherwise null
     */
    BTreeNode next;

    // Constructor
    BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.n = 0;
        this.next = null;
        
        //changed to arrayList
        this.values = new ArrayList<Long>(2 * t - 1);        
        this.keys = new ArrayList<Long>(2 * t - 1);
        this.children = new ArrayList<BTreeNode>(2 * t);
    }
}
