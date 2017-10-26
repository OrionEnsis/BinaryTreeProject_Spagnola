/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarytreeproject_spagnola.Collections;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author Jim
 * @desc This class manages a Frequency of integer values on a hashtable
 * @param <T> Key
 */
public class FrequencyTable implements Serializable{
    final static int START_SIZE = 16;
    int count;
    Node[] table;
    
    static class Node {
        public String key;
        public int value;
        public Node next;
        
        public Node(String k, int v){
            key = k;
            value = v;
        }
    }
    
    //Default constructor
    public FrequencyTable(){
        table = new Node[START_SIZE];
        count = 0;
    }
    
    /*FrequencyTable(int size){
        table = new Node[START_SIZE];
        count = 0;
    }
    */
    
    public void put(String key, int value){
        int h = key.hashCode();
        int i = h &(table.length - 1);
        Node e = table[i];
        
        if(table[i] == null){
            table[i] = new Node(key, value);
            count++;
            if((float)count > (float)table.length*.75){
                    //make the table bigger!
                    resize();
            }
            return;
        }
        
        //FOREVER
        for(;;){
            //if null put it here
            if(e.next == null){
                //add the new Node to the list
                //table[i] = new Node(key,value,table[i]);
                e.next = new Node(key,value);
                count++;
                
                //IF we are reaching birthday paradox
                if((float)count > (float)table.length*.75){
                    //make the table bigger!
                    resize();
                }
                //System.out.println(count + " " + key);
                //leave
                return;
            }
            //if its already here
            else if(e.key.equals(key)){
                //give weight to the value
                e.value += value;
                
                //leave
                return;
            }
            else{
                e= e.next;
            }
        }
    }
    
    public void remove(String key){
        int h = key.hashCode();
        int i = h &(table.length - 1);
        Node prev = null;
        Node e = table[i];
        
        //FOREVER
        for(;;){
            //IF null we're done
            if(e == null){
                return;
            }
            //ELSE IF we find the key...
            else if(e.key.equals(key)){
                //reduce the count
                e.value--;
                
                //IF no more occurances, remove the NODE
                if(e.value <= 0){
                    if(prev ==null){
                        table[i] = e.next;
                    }
                    else{
                        prev.next = e.next;
                    }
                }
                count--;
                return;
            }
            //ELSE got to the next node
            else{
                prev = e;
                e = e.next;
            }
        }
    }
    
    public int getValue(String key){
        int h = key.hashCode();
        int i = h &(table.length - 1);
        Node e = table[i];
        
        //FOREVER
        for(;;){
            //if null put it here
            if(e == null){
                return 0;
            }
            //if its already here
            else if(e.key.equals(key)){
                return e.value;
            }
            else{
                e= e.next;
            }
        }
    }
    
    public String[] getAllKeys(){
        String[] tempArray;
        ArrayList<String> tempList = new ArrayList<String>();
        for(int i = 0; i < table.length; i ++){
            for(Node e = table[i]; e != null; e = e.next){
                tempList.add(e.key);
            }
        }
        tempArray = new String[tempList.size()];
        for(int i  = 0; i < tempArray.length; i++){
            tempArray[i] = tempList.get(i);
        }
        return tempArray;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public double frequencyRating(FrequencyTable b){
        int vectorProduct = 0;
        float sumA = 0;
        float sumB = 0;
        double finalValue = 0;
        String[] masterArray = getMasterList(b);//GetList of all values for both
        int[] arrayA = new int[masterArray.length];
        int[] arrayB = new int[masterArray.length];
        
        //convert to arrays
        for(int i = 0; i < masterArray.length; i++){
            arrayA[i] = getValue(masterArray[i]);
            arrayB[i] = b.getValue(masterArray[i]);
            
            //prep for calculations
            vectorProduct += arrayA[i]*arrayB[i];
            sumA += arrayA[i] * arrayA[i];
            sumB += arrayB[i] * arrayB[i];
        }
        
        finalValue = vectorProduct/(Math.pow((double)sumA, .5) * Math.pow((double)sumB,.5));
        
        //calculate
        return finalValue;
    }
    
    String[] getMasterList(FrequencyTable b){
        HashMap<String,Integer> map = new HashMap<>();;
        String[] arrayA = getAllKeys();
        String[] arrayB = b.getAllKeys();    
        String[] tempArray;
        ArrayList<String> tempList = new ArrayList<>();//get list of all words of both lists so each is the same length
        
        //Add all the words to the list
        for(int i = 0; i < arrayA.length; i++){
            map.put(arrayA[i], i);
            tempList.add(arrayA[i]);
        }
        //add any words unique to b to the list
        for(int i = 0; i < arrayB.length; i++){
            if(!map.containsKey(arrayB[i])){
                map.put(arrayB[i], 1);
                tempList.add(arrayA[i]);
            }
        }
        
        tempArray = new String[map.size()];
        //Remove common words and return
        for(int i  = 0; i < tempArray.length; i++){
            tempArray[i] = tempList.get(i);
        }
        return tempArray;
    }
    
    //resize the hash array
    void resize(){
        Node[] newTable = new Node[count*2];
        ArrayList<Node> tempList = new ArrayList<>();
        
        //System.out.println("resizing...");
        count = 0;
        //store every node in a temporary holding place
        for(int i = 0; i < table.length; i++){
            for(Node e = table[i]; e != null; e = e.next){
                tempList.add(e);
            }
        }
        
        //switch to the new table
        table = newTable;
        
        //Get all the information rehashed.
        for(int i = 0; i < tempList.size(); i ++){
            put(tempList.get(i).key, tempList.get(i).value);
        }
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException{
        //s.defaultWriteObject();
        s.writeInt(count);
        System.out.println(count);
        int j =0;
        //Write all elements to the file
        for(int i = 0; i < table.length; i ++){
            for(Node e = table[i]; e != null; e = e.next){
                //System.out.println(e.key + " " + e.value + " " + j);
                s.writeObject(e.key);
                s.writeInt(e.value);
                j++;
            }
        }
    }
    
    private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
        //s.defaultReadObject();
        count = s.readInt();
        int temp = count;
        //System.out.println(count);
        table = new Node[START_SIZE];
        String key;
        int value;
        for(int i = 0; i < temp; i++){
            key =(String)s.readObject();
            value = s.readInt();
            put(key,value);
            //System.out.println(i + " "+ key);
        }
    }
    
    public void printAll(){
        int j =0;
         for(int i = 0; i < table.length; i ++){
            for(Node e = table[i]; e != null; e = e.next){
                
                System.out.println(e.key + " " + e.value + " " + j);
                j++;
            }
        }
    }
}
