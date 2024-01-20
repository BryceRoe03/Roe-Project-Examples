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
 * RecursiveCrawler extends Crawler and recursively crawls through the given websites and links.
 */
public class RecursiveCrawler extends Crawler {

    /**
     * RecursiveCrawler initializes a new Recursive Crawler by setting up the arraySets.
     */
    public RecursiveCrawler() {
        super();
    }

    /**
     * crawl creates a jsoup document, adds the given pageFile to the foundPages set, and 
     * makes an array of links from the links found in the pageFile. Then it uses a for loop 
     * to check every element and make their location a string. If it is not a valid link, 
     * it is added to the skipped set. Then, its name is fixed to be in relation to the 
     * provided page. If it is both not in the found set and not in the skipped set already, 
     * it is added to the skipped set if it is not valid. If it is valid, it is used to crawl 
     * through in a new recursion. This goes until it reaches the end of its branch, then it 
     * finishes off each branch one by one until all the pages are added to either list.
     * 
     * A try catch is used to catch any pages that lead to nowhere but have a valid looking name. 
     * They are added to skipped set and the process continues.
     */
    public void crawl(String pageFileName) {
        try {
            Document doc = Jsoup.parse(new File(pageFileName), "UTF-8");

            this.foundPages.add(pageFileName);

            ArrayList<Element> links = doc.select("a[href]");

            for (Element element : links) {

                String linkedPage = element.attr("href");

                if(validPageLink(linkedPage) == false) {
                    this.skippedPages.add(linkedPage);
                    continue;
                }

                String next = Util.relativeFileName(pageFileName, linkedPage);
            
                if (this.foundPages.contains(next) == false && this.skippedPages.contains(next) == false) {
                    if (validPageLink(next) == false) {
                        this.skippedPages.add(next);
                    }
                    else {
                        this.crawl(next);
                    }
                }
            }
        }
    catch (Exception e) {
        this.skippedPages.add(pageFileName);
        }
    }
}
