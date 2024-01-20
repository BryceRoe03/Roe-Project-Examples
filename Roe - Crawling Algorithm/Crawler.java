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
 * Crawler is an abstract class that is the basis for the 2 crawlers. It holds the code 
 * for working the found and skipped page arraySets and if pages are valid.
 */
public abstract class Crawler {
    /**
     * These two arraySets are initialized so they can be used later.
     */
    protected ArraySet<String> foundPages;
    protected ArraySet<String> skippedPages;

    /**
     * Crawler instantiates a new empty found and empty pages.
     */
    public Crawler() {
        this.foundPages = new ArraySet<>();
        this.skippedPages = new ArraySet<>();
    }

    /**
     * crawl is left to be finished by the invididual extensions.
     * @param pageFileName
     */
    public abstract void crawl(String pageFileName);
    
    /**
     * foundPagesList returns the foundPages as a List instead of an ArraySet.
     * @return List of foundPages
     */
    public List<String> foundPagesList() {
        List<String> found = this.foundPages.asList();
        return found;
    }

    /**
     * skippedPagesList returns the skippedPages as a List instead of an ArraySet.
     * @return List of skippedPages
     */
    public List<String> skippedPagesList() {
        List<String> skip = this.skippedPages.asList();
        return skip;
    }

    /**
     * foundPagesString takes the list of foundPages and adds it line by line to a 
     * string with a new line each link and the string is returned.
     * @return string of found page links
     */
    public String foundPagesString() {
        List<String> printTime = this.foundPagesList();
        String out = "";
        for(String page : printTime) {
            out += page + "\n";
        }
        return out;
    }

    /**
     * skippedPagesString takes the list of skippedPages and adds it line by line to a 
     * string with a new line each link and the string is returned.
     * @return string of skipped page links
     */
    public String skippedPagesString() {
        List<String> printTime = this.skippedPagesList();
        String out = "";
        for(String page : printTime) {
            out += page + "\n";
        }
        return out;
    }

    /**
     * validPageLink checks if the given pageFileName string ends with one of the 
     * correct "html" spellings and false if it starts incorrectly or otherwise.
     * @param pageFileName
     * @return true or false
     */
    public static boolean validPageLink(String pageFileName) {
        if (pageFileName.startsWith("http://")) {
            return false;
        } 
        if (pageFileName.startsWith("https://")) {
            return false;
        } 
        if (pageFileName.startsWith("file://")) {
            return false;
        } 
        if (pageFileName.startsWith("javascript:")) {
            return false;
        } 
        if (pageFileName.endsWith(".html")) {
            return true;
        }
        if (pageFileName.endsWith(".HTML")) {
            return true;
        }
        return false;
    }
}
