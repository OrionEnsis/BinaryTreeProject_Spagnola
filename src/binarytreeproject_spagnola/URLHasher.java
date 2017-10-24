/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarytreeproject_spagnola;
import binarytreeproject_spagnola.Collections.FrequencyTable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author Jim
 */
public class URLHasher implements Serializable{
    URLTable[] tables;
    URLTable promptedURL;
    
    public URLHasher(String[] urls) throws IOException{
        tables = new URLTable[urls.length];
        for(int i =0; i < urls.length; i++){
            tables[i] = new URLTable(urls[i]);
            System.out.println("Table " + i + " Completed.");
        }
        System.out.println("Hashing Complete!");
    }
    
    public URLHasher() throws IOException, ClassNotFoundException{
        File folder = new File("src\\Hashtables\\");
        File[] files = folder.listFiles();
        String inURL;
        FrequencyTable temp;
        FileInputStream fis;
        ObjectInputStream ois;
        tables = new URLTable[files.length];
        
        
        for(int i = 0; i < files.length; i++) {
            fis = new FileInputStream(files[i]);
            ois = new ObjectInputStream(fis);
            temp = (FrequencyTable)ois.readObject();
            inURL = files[i].getName().replace(' ', ':').replace(',','/');
            tables[i] = new URLTable(inURL,temp);
        }
    }
    
    public void hashURL( String url)throws IOException{
        promptedURL = new URLTable(url);
        sortURL();
    }
    
    //Compare and sort the URLS by most similar
    //sort URLS by most similar
    
    void sortURL(){
        for (int i = 0; i < tables.length; i ++)
        {
            tables[i].comparability = tables[i].table.frequencyRating(promptedURL.table);
        }
        Arrays.sort(tables);
    }

    public URLTable[] getAllTables(){
        return tables;
    }
    
    public URLTable getTable(String inurl){
        for (int i = 0; i < tables.length; i++){
            if(tables[i].equals(inurl))
                return tables[i];
        }
        return null;
    }
    
    public URLTable getMostSimilarURL(){
        System.out.println(tables[tables.length-1].toString());
        return tables[tables.length-1];
    }
    
    public void saveAll() throws IOException{
        FileOutputStream fos;
        ObjectOutputStream oos;
        String fileName;
        for (int i = 0; i < tables.length; i++){
            fileName = tables[i].url.replace('/',',').replace(':',' ');
            fos = new FileOutputStream("src\\Hashtables\\" + fileName);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(tables[i].table);
            //fos.flush();
            //oos.flush();
            
            System.out.println("saving File number: " + i);
        }
    }
}
