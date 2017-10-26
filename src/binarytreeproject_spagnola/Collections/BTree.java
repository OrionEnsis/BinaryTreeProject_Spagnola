/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarytreeproject_spagnola.Collections;

/**
 *
 * @author Jim
 */
public class BTree {
    Node root;
    static class Node{
        
        public static final int K = 8;
        //8 id + 4 count + (4 percharacter *(K-1) * 8(for file name length) + 4*(k-1)*500 characters + 8*K
        static final int SIZE = 8+4+ (4*(K-1)*8) + (4*(K-1)*500)+8*K;
        public String[] keys = new String[K-1];
        public String[] values = new String[K-1];
        public long[] children = new long[K];
        public long id;        //location in file
        public int count;
        public boolean leaf;
        
        public Node(){
            
        }
        public boolean isFull(){
            if(count == keys.length)
                return true;
            else
                return false;
        }
       
    }
    
    public BTree(){
        //allocate a node
        //its leaf is true
        //its children and values are blank
        //write it to the disk
        //it IS a leaf
        root = new Node();
        root.leaf = true;
    }
    public void put(String key, String value){
        
    }
    
    private void treeInsert(String k){
        //make sure the node is the root
        //IF the root is full
            //create new node s
            //The root is now s
            //s is not a leaf
            //s has no keys
            //s first child is the old root
            //Split child(s,1,r)
            //insertnonfull(s,k)
        //ELSE treeinsert nonfull(r,k)
    }
    
    private void treeInsertNonFull(Node x,String k){
        //index = elements of x
        //IF x is a leaf
            //WHILE i>= 1 AND k< nodex.key[i]
                //nodex.key[i+1] = nodex.key[i];
                //i--;
            //nodex.key[i+1] = k
            //nodex count++
            //DISK WRITE X
        //else
            //WHILE i>=1 AND k <nodex.key[i]
                //i--;
            //i++
            //DISKREAD(node.children[i])
            //IF nodex.children[i] == K (if nodeX is full?)
                //TreeSplitChild(x,i,nodex.child[i])
                //IF(k> nodex.key[i]
                    //i++;
            //TreeInsertNonFull(nodex.child[i],k)s
            
    }
    private void treeSplitChild(Node x,int i,Node y){
        //allocate a node
        Node z;
        //assign z to y
        //count keys of nodez = half keys -1
        //for 0 to half the keys-1
            //assign the key over
        //IF not y.leaf
            //For half the keys
                //give z the upper half of y's children
        //count keys of nodey = half keys -1
        //for j = keys of x +1 down to i+1
            //nodex[j+1] is nodex[j]
        //nodex[i+1]= z
        //FOR j = num of nodex's children downto i
            //nodex.keys[j+1] = nodex.keys[j]
        //nodex.key[i] = nodex.key[half the keys]
        //number of keysx++
        //write x,y,z to disk
    }
    
    public void searchTree(String key){
        
    }
}
