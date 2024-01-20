    /**
 * @author Bryce Roe
 * @version 1.0
 */

/**
 * These are the imports for jsoup, util, and io.
 */
import org.jsoup.nodes.*;
import org.jsoup.*;
import org.jsoup.select.*;
import java.io.*;
import java.util.*;

/**
 * IterativeCrawler extends Crawler and iteratively crawls through the given websites and links.
 */
public class IterativeCrawler extends Crawler {
    /**
     * An arraySet for pendingPages is initialized.
    */
    protected ArraySet<String> pendingPages;

    /**
     * IterativeCrawler instantitates the needed arrayLists for an Iterative Crawler instance.
     */
    public IterativeCrawler() {
        super();
        this.pendingPages = new ArraySet<>();
    }

    /**
     * crawl first adds the given pageFile to the foundPages set. Then it creates a jsoup 
     * document and an array list of links from it. Then, for each link, if it is invalid, 
     * it is added to the skipped set. It is then made relative to the provided file 
     * by name and if it is invalid and not already in skipped, it is added. Else, it is 
     * added to the pendingPages set. Using the pendingPages set, crawlsRemaining is 
     * called to do the rest.
     * 
     * A try catch is used to catch any pages that lead to nowhere. It stops the loop.
     */
    public void crawl(String pageFileName) {
        this.foundPages.add(pageFileName);

        try {
            Document doc = Jsoup.parse(new File(pageFileName), "UTF-8");

            ArrayList<Element> links = doc.select("a[href]");

            for (Element element : links) {

                String linkedPage = element.attr("href");

                if(validPageLink(linkedPage) == false) {
                    this.skippedPages.add(linkedPage);
                    continue;
                }

                String next = Util.relativeFileName(pageFileName, linkedPage);
            
                if (this.foundPages.contains(next) == false && this.skippedPages.contains(next) == false) {
                    if (validPageLink(next) == true) {
                        this.pendingPages.add(next);
                    }
                    else {
                        this.skippedPages.add(next);
                    }
                }
            }
            this.crawlRemaining();
        }
    catch (Exception e) {
        System.out.println("");
        }
    }

    /**
     * crawlRemaining loops until the pendingPage size is 0. The set is made a list 
     * and then a for loop adds each to the found set and a jsoup document and link 
     * array list is made for them. If any found are not valid, they are added to 
     * the skipped set. If it is valid, the name is made relative and if it is still 
     * not in found and not in skipped, it is added to skipped or pending set accordingly. 
     * It removes the page from the list and it keeps looping until it reaches 
     * 0. 
     * 
     * The try catch catches whenever the page leads to nowhere and stops the 
     * loop.
     */
    public void crawlRemaining() {
        while (this.pendingPages.size() != 0) {
            try {
                List<String> current = this.pendingPages.asList();
                for (String page : current) {
                    this.foundPages.add(page);

                    Document doc = Jsoup.parse(new File(page), "UTF-8");

                    ArrayList<Element> links = doc.select("a[href]");

                    for (Element element : links) {

                        String linkedPage = element.attr("href");
        
                        if(validPageLink(linkedPage) == false) {
                            this.skippedPages.add(linkedPage);
                            continue;
                        }
                        else {
                            String next = Util.relativeFileName(page, linkedPage);
        
                            if (this.foundPages.contains(next) == false) {
                                if (this.skippedPages.contains(next) == false) {
                                    if (validPageLink(next) == false) {
                                        this.skippedPages.add(next);
                                    }
                                    else {
                                        this.pendingPages.add(next);
                                    }
                                }
                            }
                        }
                    }
                    current.remove(page);
                }
            }
            catch (Exception e) {
                System.out.print("");
            }
        }    
    }

    /**
     * addPendingPage adds the given pageFile to the pendingPages 
     * set if it is not already in it.
     * @param pageFileName
     */
    public void addPendingPage(String pageFileName) {
        if (this.pendingPages.contains(pageFileName) == false) {
            this.pendingPages.add(pageFileName);
        }
    }

    /**
     * pendingPagesSize returns the size of the pendingPages 
     * set.
     * @return int size of pendingPages
     */
    public int pendingPagesSize() {
        return this.pendingPages.size();
    }

    /**
     * pendingPagesString assembles a string with every pendingPage 
     * on its own line and then returns the string to the user.
     * @return string out
     */
    public String pendingPagesString() {
        List<String> printTime = this.pendingPages.asList();
        String out = "";
        for(String page : printTime) {
            out += page + "\n";
        }
        return out;
    }

    /**
     * crawlNextPage first makes a list from the pendingPages set. Then it uses the same jsoup 
     * setup process from earlier with the same for loop. If the current linkedPage 
     * is not valid, it is added to the skipped set. Then the name is made relative 
     * and it is compared again. If it is not in foundPages and not in skipped but 
     * still invalid, it is put in the skipped set. Otherwise, it is added to the 
     * pending set. 
     * 
     * The try catch sets any pending page that might lead to nowhere as in the 
     * skipped set.
     */
    public void crawlNextPage() {
        List<String> pending = this.pendingPages.asList();
        
        try {
            Document doc = Jsoup.parse(new File(pending.get(0)), "UTF-8");

            this.foundPages.add(pending.get(this.pendingPagesSize()-1));

            ArrayList<Element> links = doc.select("a[href]");

            for (Element element : links) {

                String linkedPage = element.attr("href");

                if(validPageLink(linkedPage) == false) {
                    this.skippedPages.add(linkedPage);
                    continue;
                }

                String next = Util.relativeFileName(pending.get(0), linkedPage);
            
                if (this.foundPages.contains(next) == false && this.skippedPages.contains(next) == false) {
                    if (validPageLink(next) == false) {
                        this.skippedPages.add(next);
                    }
                    else {
                        this.pendingPages.add(next);
                    }
                }

            }
        }
    catch (Exception e) {
        this.skippedPages.add(pending.get(0));
        }
    }
}
