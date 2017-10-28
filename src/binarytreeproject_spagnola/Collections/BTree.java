/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarytreeproject_spagnola.Collections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jim
 */
public class BTree {

    Node root;      //the root node for performing ALL operations
    int t;          //the minimum degree

    static class Node {

        public static final String fileName = "Btree.txt";
        static final int URL_MAX_LENGTH = 500;
        public static final int T = 4;
        public static final int K = 8;
        //8 id + 4 count + (4 percharacter *(K-1) * 8(for file name length) + 4*(k-1)*500 characters + 8*K + 2(4 *(K-1)
        static final int SIZE = 8 + 4 + (4 * (K - 1) * 8) + (4 * (K - 1) * URL_MAX_LENGTH) + 8 * K + 2 * (4 * K);
        public long id;        //location in file
        public int count;
        public boolean leaf;
        public String[] keys;
        public String[] values;
        public long[] children;

        public Node(long inID, int inCount, boolean inLeaf, String[] inKeys, String inValues[], long[] inChildren) {
            id = inID;
            count = inCount;
            leaf = inLeaf;
            keys = inKeys;
            values = inValues;
            children = inChildren;
        }

        public Node() {
            id = 0;        //location in file
            count = 0;
            leaf = true;
            keys = new String[K - 1];
            values = new String[K - 1];
            children = new long[K];
        }

        public boolean isFull() {
            if (count == keys.length) {
                return true;
            } else {
                return false;
            }
        }

        public static Node diskRead(long x) {

            try {
                RandomAccessFile file = new RandomAccessFile(fileName, "rw");
                FileChannel f = file.getChannel();
                //setup file to read
                ByteBuffer b = ByteBuffer.allocate(SIZE);
                f.read(b, x);

                //setup node variables
                long inID;
                int inCount;
                boolean inLeaf;
                String[] inKeys = new String[K - 1];
                String[] inValues = new String[K - 1];
                long[] inChildren = new long[K];

                //read non array members
                byte temp;
                inID = b.getLong();
                inCount = b.getInt();
                temp = b.get();
                if (temp == 1) {
                    inLeaf = true;
                } else {
                    inLeaf = false;
                }

                //read arrays
                int length;
                byte[] tempString;

                for (int i = 0; i < inKeys.length; i++) {
                    //get the length of the next string
                    length = b.getInt() * 4;
                    tempString = new byte[length];

                    //get the next string
                    b.get(tempString);
                    inKeys[i] = new String(tempString);
                }
                for (int i = 0; i < inValues.length; i++) {
                    //get the length of the next string
                    length = b.getInt() * 4;
                    tempString = new byte[length];

                    //get the next string
                    b.get(tempString);
                    inValues[i] = new String(tempString);
                }

                for (int i = 0; i < inChildren.length; i++) {
                    inChildren[i] = b.getLong();
                }
                b.clear();
                f.close();
                return new Node(inID, inCount, inLeaf, inKeys, inValues, inChildren);
            } catch (IOException E) {;
                return null;
            }
        }

        public void diskWrite(long x) {
            try {
                RandomAccessFile file = new RandomAccessFile(fileName, "rw");
                FileChannel f = file.getChannel();

                //setup the position and file
                f.position(x);
                ByteBuffer b = ByteBuffer.allocate(SIZE);

                //put non array elements in
                b.putLong(id);
                b.putInt(count);
                if (leaf) {
                    b.put((byte) 1);
                } else {
                    b.put((byte) 0);
                }

                byte[] stringConversion;
                //for(each member of the array)
                for (int i = 0; i < keys.length; i++) {
                    //write the length of the string
                    b.putInt(keys[i].length());
                    //write the string
                    stringConversion = keys[i].getBytes();
                    b.put(stringConversion);

                }
                for (int i = 0; i < values.length; i++) {
                    //write the length of the string
                    b.putInt(keys[i].length());
                    //write the string
                    stringConversion = keys[i].getBytes();
                    b.put(stringConversion);

                }
                for (int i = 0; i < children.length; i++) {
                    b.putLong(children[i]);
                }

                while (b.hasRemaining()) {
                    f.write(b);
                }
                b.clear();
                f.close();
            } catch (IOException ex) {
                Logger.getLogger(BTree.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void traverse() throws IOException {
            int i = 0;
            Node n;
            for (i = 0; i < K - 1; i++) {
                if (!leaf) {
                    n = diskRead(children[i]);
                    n.traverse();
                }

                if (i < count) {
                    System.out.println("Key: " + keys[i] + " Value: " + values[i]);
                }
            }
            if (!leaf) {
                n = diskRead(children[i]);
                if (n != null) {
                    n.traverse();
                }
            }
        }
    }

    public BTree() throws FileNotFoundException {
        File file = new File(Node.fileName);
        if (!file.exists()) {
            root = Node.diskRead(0);
        } else {
            root = new Node();
        }
    }

    private void allocateNode(Node n) {
        try {
            RandomAccessFile file = new RandomAccessFile(Node.fileName, "rw");
            FileChannel f = file.getChannel();
            long size = f.size();
            n.id = size;
            n.diskWrite(size);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void put(String key, String value) {
        treeInsert(key, value);
    }

    private void treeInsert(String k, String v) {
        //make sure the node is the root
        Node r = root;

        //IF the root is full
        if (r.isFull()) {
            //create new node s
            Node s = new Node();
            allocateNode(s);

            //The root is now s
            root = s;
            //s is not a leaf
            s.leaf = false;
            //s has no keys
            s.count = 0;
            //s first child is the old root
            s.children[0] = r.id;
            //Split child(s,1,r)
            treeSplitChild(s, 1, r);
            //insertnonfull(s,k)
            treeInsertNonFull(s, k, v);
        }

        //ELSE treeinsert nonfull(r,k)
        treeInsertNonFull(r, k, v);
    }

    private void treeInsertNonFull(Node x, String k, String v) {
        //index = elements of x
        int i = x.count;

        //IF x is a leaf
        if (x.leaf) {
            //WHILE i>= 1 AND k< nodex.key[i]
            while (i >= 1 && k.compareTo(x.keys[i]) < 0) {
                //nodex.key[i+1] = nodex.key[i];
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            //nodex.key[i+1] = k
            x.keys[i + 1] = k;
            x.values[i + 1] = v;

            //nodex count++
            x.count++;

            //DISK WRITE X
            x.diskWrite(x.id);
        } else {
            //WHILE i>=1 AND k <nodex.key[i]
            while (i >= 1 && k.compareTo(x.keys[i]) < 0) {
                i--;
            }
            //increase i for some reason??
            i++;

            //DISKREAD(node.children[i])
            Node y = Node.diskRead(x.children[i]);
            
            //IF nodex.children[i] == K (if nodeX is full?)
            if(y.count == Node.K -1){
                //TreeSplitChild(x,i,nodex.child[i])
                treeSplitChild(x,i,y);
                //IF(k> nodex.key[i]
                if(k.compareTo(x.keys[i])>0)
                    i++;
            }
            //TreeInsertNonFull(nodex.child[i],k)s 
            treeInsertNonFull(y,k,v);
        }
    }

    private void treeSplitChild(Node x, int i, Node y) {
        //allocate a node
        Node z = new Node();
        allocateNode(z);

        //assign z to y
        z = y;

        //count keys of nodez = half keys -1
        z.count = Node.T - 1;

        //for 0 to half the keys-1
        for (int j = 0; j < z.count; j++) {
            //assign the key over
            z.keys[j] = y.keys[Node.T + j];
            z.values[j] = y.values[Node.T + j];
        }

        //IF not y.leaf
        if (!y.leaf) {
            //For half the keys
            for (int j = 0; j < Node.T; j++) {
                //give z the upper half of y's children
                z.children[j] = y.children[j + Node.T];
            }
        }
        //count keys of nodey = half keys -1
        y.count = Node.T - 1;

        //for j = keys of x +1 down to i+1
        for (int j = x.count + 1; j > i + 1; j--) {
            //nodex.children[j+1] is nodex.children[j]
            x.children[j + 1] = x.children[j];
        }
        //nodex[i+1]= z
        x.children[i + 1] = z.id;

        //FOR j = num of nodex's children downto i
        for (int j = x.count; j > i; i--) {
            //nodex.keys[j+1] = nodex.keys[j]
            x.keys[j + 1] = x.keys[j];
            x.values[j + 1] = x.values[j];
        }
        //nodex.key[i] = nodex.key[half the keys]
        x.keys[i] = y.keys[Node.T];
        x.values[i] = y.values[Node.T];

        //number of keysx++
        x.count++;

        //write x,y,z to disk
        x.diskWrite(x.id);
        y.diskWrite(y.id);
        z.diskWrite(z.id);
    }

    public boolean searchTree(String key) throws FileNotFoundException {
        return searchTree(root, key);
    }

    private boolean searchTree(Node n, String k) throws FileNotFoundException {
        int i = 0;
        //WHILE i <= count && k >= n.key[i]
        while (i <= n.count && k.compareTo(n.keys[i]) >= 0) {
            i++;
        }
        //IF i <= count && k == n.key[i]
        if (i <= n.count && k.equals(n.keys[i])) {
            return true;
        } //ELSE IF (n.leaf)
        else if (n.leaf) {
            return false;
        } else {
            return searchTree(Node.diskRead(n.children[i]), k);
        }
    }

    public String getValue(String key) throws FileNotFoundException {
        return getValue(root, key);
    }

    private String getValue(Node n, String k) throws FileNotFoundException {
        int i = 0;
        //WHILE i <= count && k >= n.key[i]
        while (i <= n.count && k.compareTo(n.keys[i]) >= 0) {
            i++;
        }
        //IF i <= count && k == n.key[i]
        if (i <= n.count && k.equals(n.keys[i])) {
            return n.values[i];
        } //ELSE IF (n.leaf)
        else if (n.leaf) {
            return null;
        } else {
            return getValue(Node.diskRead(n.children[i]), k);
        }
    }
}
