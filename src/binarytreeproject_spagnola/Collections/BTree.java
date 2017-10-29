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
import java.util.LinkedList;
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
        public static int test = 0;
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
            if (count == K-1) {
                return true;
            } else {
                return false;
            }
        }

        public static Node diskRead(long x) {

            try {
                RandomAccessFile file = new RandomAccessFile(fileName, "rw");
                //setup file to read
                file.seek(x);
                
                //setup node variables
                long inID = file.readLong();
                int inCount = file.readInt();
                boolean inLeaf;
                String[] inKeys = new String[K - 1];
                String[] inValues = new String[K - 1];
                long[] inChildren = new long[K];

                //read non array members
                inLeaf = file.readBoolean();

                //read arrays
                int length;
                byte[] tempString;

                for (int i = 0; i < inCount; i++) {
                    //get the length of the next string
                    length = file.readInt();
                    tempString = new byte[length];

                    //get the next string
                    file.readFully(tempString);
                    inKeys[i] = new String(tempString);
                }
                for (int i = 0; i < inCount; i++) {
                    //get the length of the next string
                    length = file.readInt();
                    tempString = new byte[length];

                    //get the next string
                    file.readFully(tempString);
                    inValues[i] = new String(tempString);
                }
                if(!inLeaf){
                    for (int i = 0; i < inCount+1; i++) {
                        inChildren[i] = file.readLong();
                    }
                }
                file.close();
                return new Node(inID, inCount, inLeaf, inKeys, inValues, inChildren);
            } catch (IOException E) {;
                return null;
            }
        }

        public void diskWrite(long x) {
            try {
                RandomAccessFile file = new RandomAccessFile(fileName, "rw");
                file.seek(x);

                //setup the position and file

                //put non array elements in
                file.writeLong(id);
                file.writeInt(count);
                file.writeBoolean(leaf);

                byte[] stringConversion;
                int length;
                //for(each member of the array)
                for (int i = 0; i < count; i++) {
                    //write the length of the string
                    //write the string
                    stringConversion = keys[i].getBytes();
                    length = stringConversion.length;
                    file.writeInt(length);
                    file.write(stringConversion);

                }
                for (int i = 0; i < count; i++) {
                    //write the length of the string
                    //write the string
                    stringConversion = values[i].getBytes();
                    file.writeInt(stringConversion.length);
                    file.write(stringConversion);

                }
                if(!leaf){
                    for (int i = 0; i < count +1; i++) {
                        file.writeLong(children[i]);
                    }
                }

                file.close();
            } catch (IOException ex) {
                Logger.getLogger(BTree.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void traverse() throws IOException {
            int i = 0;
            Node n;
            for (i = 0; i < count; i++) {
                if (!leaf) {
                    n = diskRead(children[i]);
                    n.traverse();
                }
                System.out.println("Key: " + keys[i] + " Value: " + values[i]);
                System.out.println(test);
                test++;
            }
            if (!leaf) {
                n = diskRead(children[i]);
                if (n != null) {
                    n.traverse();
                }
            }
        }
    }

    public BTree() throws FileNotFoundException, IOException {
        File file = new File(Node.fileName);
        if (file.exists()) {
            root = Node.diskRead(getRoot());
        } else {
            file.createNewFile();
            root = new Node();
            setRoot(8);
            allocateNode(root);
        }
    }
    private void setRoot(long rootID)throws FileNotFoundException, IOException{
        RandomAccessFile file = new RandomAccessFile(Node.fileName, "rw");
        file.writeLong(rootID);
        file.close();
    }
    private long getRoot() throws FileNotFoundException, IOException{
        RandomAccessFile file = new RandomAccessFile(Node.fileName, "rw");
        long size = file.readLong();
        file.close();
        return size;
    }
    private void allocateNode(Node n) {
        try {
            RandomAccessFile file = new RandomAccessFile(Node.fileName, "rw");
            long size = file.length();
            file.seek(size + Node.SIZE);
            file.write(0);
            file.close();
            n.id = size;
            n.diskWrite(size);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void traverse() throws IOException{
        root.traverse();
    }
    public void put(String key, String value) throws IOException {
        //if(!searchTree(key))
            treeInsert(key, value);
    }

    private void treeInsert(String k, String v) throws IOException {
        System.out.println("inserting: "+k);
        //make sure the node is the root
        Node r = root;

        //IF the root is full
        if (r.isFull()) {
            System.out.println("Root is full.");
            //create new node s
            Node s = new Node();
            allocateNode(s);

            //s is not a leaf
            s.leaf = false;
            //s has no keys
            s.count = 0;
            //s first child is the old root
            s.children[0] = r.id;
            
            //Split child(s,1,r)
            treeSplitChild(s, 0, r);
            //insertnonfull(s,k)
            treeInsertNonFull(s, k, v);
            //The root is now s
            root = s;
            setRoot(s.id);
        }

        //ELSE treeinsert nonfull(r,k)
        else
            treeInsertNonFull(r, k, v);
        
    }

    private void treeInsertNonFull(Node x, String k, String v) {
        //index = elements of x
        int i = x.count-1;
        System.out.println("inserting into nonFull node.");
        //IF x is a leaf
        if (x.leaf) {
            System.out.println("we're a leaf!");
            //WHILE i>= 1 AND k< nodex.key[i]
            while (i >= 0 && k.compareTo(x.keys[i]) <= 0) {
                //nodex.key[i+1] = nodex.key[i];
                x.keys[i + 1] = x.keys[i];
                x.values[i+1] = x.values[i];
                i--;
            }
            //nodex.key[i+1] = k
            if(k.equals(x.keys[i+1])){
                System.out.println("Found a duplicate: "+k);
                return;
            }
            x.keys[i + 1] = k;
            x.values[i + 1] = v;

            //nodex count++
            x.count++;

            //DISK WRITE X
            x.diskWrite(x.id);
            
        } else {
            System.out.println("We're not a leaf!");
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
            System.out.println("going one deeper");
            treeInsertNonFull(y,k,v);
        }
    }

    private void treeSplitChild(Node x, int i, Node y) {
        
        System.out.println("Splitting a child.");
        //allocate a node
        Node z = new Node();
        allocateNode(z);

        //assign zleaf= yleaf
        z.leaf = y.leaf;

        //count keys of nodez = half keys -1
        z.count = Node.T - 1;

        //for 0 to half the keys-1
        for (int j = 0; j < Node.T-1; j++) {
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
        for (int j = x.count; j >= i+1; j--) {
            //nodex.children[j+1] is nodex.children[j]
            x.children[j + 1] = x.children[j];
        }
        //nodex[i+1]= z
        x.children[i+1] = z.id;

        //FOR j = num of nodex's children downto i
        for (int j = x.count -1; j >= i; j--) {
            //nodex.keys[j+1] = nodex.keys[j]
            x.keys[j + 1] = x.keys[j];
            x.values[j + 1] = x.values[j];
        }
        //nodex.key[i] = nodex.key[half the keys]
        x.keys[i] = y.keys[Node.T -1];
        x.values[i] = y.values[Node.T -1];

        //number of keysx++
        x.count++;

        //write x,y,z to disk
        x.diskWrite(x.id);
        y.diskWrite(y.id);
        z.diskWrite(z.id);
    }

    public boolean searchTree(String key) throws FileNotFoundException {
        if(root.count != 0)
            return searchTree(root, key);
        else
            return false;
    }

    private boolean searchTree(Node n, String k) throws FileNotFoundException {
        int i = 0;
        //WHILE i <= count && k >= n.key[i]
        while (i < n.count && k.compareTo(n.keys[i]) > 0) {
            i++;
        }
        //IF i <= count && k == n.key[i]
        if (i < n.count&& k.equals(n.keys[i])) {
            return true;
        } //ELSE IF (n.leaf)
        else if (n.leaf) {
            return false;
        } else {
            return searchTree(Node.diskRead(n.children[i]), k);
        }
    }

}
