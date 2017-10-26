/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarytreeproject_spagnola;

import binarytreeproject_spagnola.Collections.FrequencyTable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author j-spa
 */
public class URLTable implements Comparable<URLTable>, Serializable {

    public String url;
    public FrequencyTable table;
    public double comparability;
    
    public URLTable(String inURL)throws IOException{
        this(inURL,Jsoup.connect(inURL).get());
    }
    
    public URLTable(String inURL, Document doc) throws IOException {
        int weight = 1;
        table = new FrequencyTable();
        url = inURL;

        String temp;
        String[] temparray;
        //Document doc = Jsoup.connect(url).get();

        Elements metaTags = doc.getElementsByTag("meta");

        //put them all in the frequency tablea
        temp = doc.text();
        temparray = temp.split(" ");
        //System.out.println(temp);
        for (int i = 0; i < temparray.length; i++) {
            temparray[i] = removeCommonWords(temparray[i]);
            if (temparray[i] != null) {
                if (weightedWordMentioned(temparray[i])) {
                    weight = 3;
                }

                table.put(temparray[i], weight);
            }
            //System.out.print("inserted: "+ temparray[i]);

        }

        for (Element metatag : metaTags) {
            table.put(metatag.data(), 10);
        }
    }
    
    public URLTable(String inURL, FrequencyTable inTable){
        url = inURL;
        table=inTable;
    }
    //remove common words from the comparison


    String removeCommonWords(String word) {
        String[] commonWords = {"the", "a", "an", "attack"};
        for (int i = 0; i < commonWords.length; i++) {
            if (word.equals(commonWords[i].toLowerCase())) {
                return null;
            }
        }

        return word;
    }

    private boolean weightedWordMentioned(String word) {
        String[] weightedWords = {"barbarian", "bard", "cleric", "druid", "fighter", "monk",
            "paladin", "ranger", "rogue", "sorcerer", "warlock", "wizard", "strength", "dexterity",
            "constitution", "intelligence", "wisdom", "charisma", "spell"};

        for (int i = 0; i < weightedWords.length; i++) {
            if (weightedWords[i].equals(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(URLTable other) {
        if (this.comparability == other.comparability) {
            return 0;
        } else if (this.comparability < other.comparability) {
            return -1;
        } else {
            return 1;
        }
    }

    public String toString() {
        return "" + url + "\n" + comparability;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(url);
        s.writeObject(table);

    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        url = (String)s.readObject();
        table = (FrequencyTable)s.readObject();
    }

}
