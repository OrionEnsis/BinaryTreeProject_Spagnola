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
        static final int NODE_SIZE = 8;
        static final int MINIMUM_DEGREE = 4;
        String[] keys;
        //String[] values;
        Node[] nodes;
        int count;
        public Node(){
            count = 0;
            keys = new String[NODE_SIZE];
            nodes = new Node[NODE_SIZE + 1];
        }
        
        public boolean isFull(){
            if(count == keys.length)
                return true;
            else
                return false;
        }
        
        public void put(String k){
            for(int i = 0; i < keys.length; i++){
                if(keys[i].equals(k))
                    break;
                else if(keys[i].compareTo(k) < 0){
                    moveElementsUp(i);
                    count++;
                    break;
                }
                else if(keys[i] == null){
                    keys[i] = k;
                }
            }
        }
        
        private void moveElementsUp(int index){
            
        }
    }
    
    public BTree(){
        root = new Node();
    }
    public void put(String key, String value){
        
    }
    
    public
    
    void rebalance(Node n){
        
    }
}
