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
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Jim
 */
public class URLHasher implements Serializable {

    URLTable[] tables;
    URLTable promptedURL;

    public URLHasher(String url, int size) throws IOException {
        tables = new URLTable[size];
        addURLs(url);
        /*
        for(int i =0; i < tables.length; i++){
            tables[i] = new URLTable(urls[i]);
            System.out.println("Table " + i + " Completed.");
        }*/
        System.out.println("Hashing Complete!");
    }

    public URLHasher() throws IOException, ClassNotFoundException {
        loadAll();
    }

    public void hashURL(String url) throws IOException {
        promptedURL = new URLTable(url);
        sortURL();
    }

    //Compare and sort the URLS by most similar
    //sort URLS by most similar
    void sortURL() {
        for (int i = 0; i < tables.length; i++) {
            tables[i].comparability = tables[i].table.frequencyRating(promptedURL.table);
        }
        Arrays.sort(tables);
    }

    public URLTable[] getAllTables() {
        return tables;
    }

    public URLTable getTable(String inurl) {
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].equals(inurl)) {
                return tables[i];
            }
        }
        return null;
    }

    public URLTable getMostSimilarURL() {
        System.out.println(tables[tables.length - 1].toString());
        return tables[tables.length - 1];
    }

    public void saveAll() throws IOException {
        FileOutputStream fos;
        ObjectOutputStream oos;
        String fileName;
        for (int i = 0; i < tables.length; i++) {
            //fileName = URLEncoder.encode(tables[i].url,"UTF-8");
            fileName = i +".txt";
            System.out.println(tables[i].url);
            System.out.println(fileName);
            fos = new FileOutputStream("src\\Hashtables\\" + fileName);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(tables[i]);
            //fos.flush();
            //oos.flush();

            System.out.println("saving File number: " + i);
        }
    }
    
    public void loadAll() throws IOException, ClassNotFoundException{
        File folder = new File("src\\Hashtables\\");
        File[] files = folder.listFiles();
        URLTable temp;
        FileInputStream fis;
        ObjectInputStream ois;
        tables = new URLTable[files.length];
        int longestURL =0;
        for (int i = 0; i < files.length; i++) {
            fis = new FileInputStream(files[i]);
            ois = new ObjectInputStream(fis);
            temp = (URLTable)ois.readObject();
            tables[i] = temp;
            if(tables[i].url.length() > longestURL)
                longestURL = tables[i].url.length();
        }
        System.out.println("longest URL: " + longestURL);
    }
    
    void addURLs(String startURL) throws IOException {

        //String[] urls = new String[1000];
        LinkedList<String> urlQueue = new LinkedList<>();
        urlQueue.add(startURL);
        String tempURL;
        //for every needed url
        for (int i = 0; i < tables.length; i++) {

            boolean moreNeeded = true;

            //WHILE the URL isn't good to use, get the next one.
            //IF there are enough elements to complete the tree...turn the queue off
            if (urlQueue.size() >= tables.length) {
                moreNeeded = false;
                //System.out.println("we have enough URLS to finish.  Queueing is stopped.");
            } //ELSE IF the Queue empty, its time to leave
            else if (urlQueue.isEmpty()) {
                System.out.println("Out of URLS");
                break;
            } //ELSE we need more stuff, turn the queue on.
            else {
                moreNeeded = true;
                //System.out.println("Need more URLS.  Starting Queue.");
            }
            System.out.println("Processing: " + i);
            System.out.println("Queue size: " + urlQueue.size());
            //get something from the queue
            tempURL = urlQueue.pollFirst();

            boolean no404Error = true;
            while (no404Error) {
                while (tempURL == null || tempURL.isEmpty() || !checkForUniqueURL(tempURL, i) 
                        || URLEncoder.encode(tempURL,"UTF-8").length() >200) {
                    tempURL = urlQueue.pollFirst();
                    System.out.println("empty or null URL");
                    if (urlQueue.isEmpty()) {
                        break;
                    }
                }

                Document doc;
                try {
                    doc = Jsoup.connect(tempURL).get();
                    tables[i] = new URLTable(tempURL, doc);
                    no404Error = false;

                } catch (IOException e) {
                    System.out.println("io - " + e);
                    no404Error = true;
                    tempURL = urlQueue.pollFirst();
                }
            }

            //Add the URL to the "btree"
            //int tempLength = URLEncoder.encode(tempURL,"UTF-8").length();
            //System.out.println(i+ " " +tempLength+"/200");
            //try to process the URLS
            try {

                //IF we need more elements... get some from the internet!
                if (moreNeeded) {
                    //check it for other URLS
                    System.out.println("Checking for Duplicate webpages.");
                    Document document = Jsoup.connect(tempURL).get();
                    Elements links = document.select("a[href]");
                    System.out.println("Found " + links.size() + " links.");
                    
                    //check each element
                    for (Element link : links) {
                        //get the absolute reference
                        String url = link.absUrl("href");

                        //check if it is already in the "btree"
                        boolean alreadyExists = false;

                        for (int j = 0; j <= i; j++) {
                            if (tables[j].url.equals(url)) {
                                alreadyExists = true;
                                break;
                            }
                        }
                        //if its new
                        if (!alreadyExists) {
                            //System.out.println("Found a new url.  adding...");
                            urlQueue.add(url);
                        } else {

                        }
                    }
                }
            } catch (Exception e) {
                System.out.print("Improper URL, retrying.");
                i--;
            }

        }

    }

    private boolean checkForUniqueURL(String url, int count) {

        for (int i = 0; i < count; i++) {
            if (tables[i].url != null && tables[i].url.equals(url)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkFor404Error(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();

        } catch (IOException e) {
            System.out.println("io - " + e);
            return true;
        }
        if (doc != null) {
            return false;
        } else {
            return true;
        }
    }
    
}
