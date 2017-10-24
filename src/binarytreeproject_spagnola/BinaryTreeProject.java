/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarytreeproject_spagnola;

import java.io.IOException;
import org.jsoup.Connection;
import java.util.LinkedList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author Jim
 */
public class BinaryTreeProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException, ClassNotFoundException{
        String[] urls = addURLs();
        
        URLHasher hasher = new URLHasher(urls);
        //URLHasher hasher = new URLHasher();
        hasher.saveAll();
        
        //hasher.getAllTables()[0].table.printAll();
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProjectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProjectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProjectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProjectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ProjectWindow(hasher).setVisible(true);
        });
        /*
        //FOR Testing purposes
        int count = 0;
        int count2 = 0;
        //URLHasher hasher = new URLHasher(urls);
        for (int i = 0; i < urls.length; i++) {
            if (urls[i] != null) {
                System.out.println(urls[i] + "  " +i);
                count2++;
            } else {
                count++;
            }
        }
        System.out.println(count2);
        System.out.println(count);
        */
        

        
    }

    static String[] addURLs() throws IOException {
        /*String[] temp = {"https://roll20.net/compendium/dnd5e/Classes:Barbarian#content",
        "https://roll20.net/compendium/dnd5e/Classes:Bard#content",
        "https://roll20.net/compendium/dnd5e/Classes:Cleric#content",
        "https://roll20.net/compendium/dnd5e/Classes:Druid#content",
        "https://roll20.net/compendium/dnd5e/Classes:Fighter#content",
        "https://roll20.net/compendium/dnd5e/Classes:Monk#content",
        "https://roll20.net/compendium/dnd5e/Classes:Paladin#content",
        "https://roll20.net/compendium/dnd5e/Classes:Ranger#content",
        "https://roll20.net/compendium/dnd5e/Classes:Rogue#content",
        "https://roll20.net/compendium/dnd5e/Classes:Sorcerer#content",
        "https://roll20.net/compendium/dnd5e/Classes:Warlock#content",
        "https://roll20.net/compendium/dnd5e/Classes:Wizard#content"};*/

        String startURL = "https://roll20.net/";
        String[] urls = new String[1];
        LinkedList<String> urlQueue = new LinkedList<>();
        urlQueue.add(startURL);
        String tempURL;
        //for every needed url
        for (int i = 0; i < urls.length; i++) {
            
            boolean moreNeeded = true;

            //WHILE the URL isn't good to use, get the next one.

            //IF there are enough elements to complete the tree...turn the queue off
            if (urlQueue.size() >= urls.length) {
                moreNeeded = false;
                System.out.println("we have enough URLS to finish.  Queueing is stopped.");
            } 
            //ELSE IF the Queue empty, its time to leave
            else if (urlQueue.isEmpty()) {
                System.out.println("Out of URLS");
                break;
            } 
            //ELSE we need more stuff, turn the queue on.
            else {
                moreNeeded = true;
                System.out.println("Need more URLS.  Starting Queue.");
            }
            System.out.println("Processing: " + i);
            System.out.println("Queue size: " + urlQueue.size());
            //get something from the queue
            tempURL = urlQueue.pollFirst();
            while(tempURL == null ||tempURL.isEmpty() ||!checkForUniqueURL(tempURL, urls) || checkFor404Error(tempURL)){
                tempURL = urlQueue.pollFirst();
                System.out.println("empty or null URL");
                if(urlQueue.isEmpty())
                    break;
            }

            //Add the URL to the "btree"
            urls[i] = tempURL;
            //try to process the URLS
            try {
                
                //IF we need more elements... get some from the internet!
                if (moreNeeded) {
                    //check it for other URLS
                    Document document = Jsoup.connect(tempURL).get();
                    Elements links = document.select("a[href]");
                    System.out.println("Found " + links.size() + " links.");
                    System.out.println("Checking for Duplicate webpages.");
                    //check each element
                    for (Element link : links) {
                        //get the absolute reference
                        String url = link.absUrl("href");

                        //check if it is already in the "btree"
                        boolean alreadyExists = false;

                        for (int j = 0; j <= i; j++) {
                            if (urls[j].equals(url)) {
                                alreadyExists = true;
                                break;
                            }
                        }
                        //if its new
                        if (!alreadyExists) {
                            System.out.println("Found a new url.  adding...");
                            urlQueue.add(url);
                        }
                        else{
                            
                        }
                    }
                }
            } catch (Exception e) {
                System.out.print("Improper URL, retrying.");
                i--;
            }

        }

        return urls;
    }
    
    private static boolean checkForUniqueURL(String url, String[] urls){

        for(int i= 0; i < urls.length; i++){
            if(urls[i] != null && urls[i].equals(url))
                return false;
        }
        return true;
    }
    
    private static boolean checkFor404Error(String url){
        Document doc;
       try {
         doc = Jsoup.connect(url).get();

      } catch (IOException e) {
         System.out.println("io - "+e);
         return true;
      }
      if(doc != null)
          return false;
      else
        return true;
    }
}
