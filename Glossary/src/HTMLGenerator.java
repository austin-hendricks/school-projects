import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Queue;

/**
 * Helper class for GlossaryMaker.java to generate necessary HTML code. Created
 * to separate HTML code from main Java code to improve readability. This class
 * includes all methods related to printing html code to the html output files.
 *
 * @author Austin Hendricks
 *
 */
public final class HTMLGenerator {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private HTMLGenerator() {
    }

    /**
     * Prints the header of the HTML file with CSS style options.
     *
     * @param html
     *            PrintWriter writing to html file
     * @param title
     *            title of page
     * @requires <pre>
     * [html.isOpen and html is not null]
     * </pre>
     * @ensures <pre>
     * printHeader = [proper HTML code for header and style
     * options for page named by param "title"]
     * </pre>
     */
    private static void printHeader(PrintWriter html, String title) {
        assert html != null : "Violation of: html is not null";
        html.println("<!DOCTYPE html>");
        html.println("<html lang=\"en\">");
        html.println("<head>");
        html.println("<title>" + title + "</title>");
        html.println("<link rel=\"stylesheet\" href=\"../style/style.css\">");
    }

    /**
     * Prints closing tags for body and html.
     *
     * @param html
     *            PrintWriter to html file
     * @requires <pre> [html.isOpen and html is not null] </pre>
     * @ensures <pre>
     * printFooter = [proper body and html HTML closing tags printed to .html file
     * open in PrintWriter "html"]
     * </pre>
     */
    private static void printFooter(PrintWriter html) {
        assert html != null : "Violation of: html is not null";
        html.println("</body>");
        html.println("</html>");
    }

    /**
     * Generates the front page of the glossary as an HTML file. This will be
     * named index.html
     *
     * @param terms
     *            {@code Queue} containing all the glossary terms alphabetically
     * @param folder
     *            {@code String} name of folder to store output file in
     * @param title
     *            {@code String} containing the glossary title
     * @throws FileNotFoundException
     *            This will virtually never happen due to the nature of the 
     *            PrintWriter class.
     *
     * @requires <pre>Parameter "folder" already exists</pre>
     *
     * @ensures <pre>
     * createFrontPage = attractive html document named index.html
     * with links to each term in {@code Queue} terms and with title as
     * "title" parameter.
     * </pre>
     */
    protected static void createFrontPage(Queue<String> terms, String folder,
            String title) throws FileNotFoundException {
        String file = folder + "/index.html";
        PrintWriter html = new PrintWriter(file);

        // Print header of html file with style information

        printHeader(html, title);

        // Print top part of the page

        html.println("<body>");
        html.print("<h1 class=\"title\">" + title + "</h1>");
        html.println("<hr>");

        // Print all the terms with their respective links

        html.println("<h3>Terms</h3>");
        html.println("<ul>");
        for (String term : terms) {
            html.print("<li><a href=\"" + term + ".html\">");
            html.println(term + "</a></li>");
        }
        html.println("</ul>");

        // Print end of html file and close

        printFooter(html);

        html.close();

    }

    /**
     * Generates the individual term page as an HTML file. This will be named
     * term.html.
     *
     * @param term
     *            {@code String} name of the glossary term
     * @param glossary
     *            {@code Map} of all glossary terms and definitions
     * @param folder
     *            {@code String} name of folder to store output file in
     * @throws FileNotFoundException   
     *            This will virtually never happen due to the nature of the
     *            PrintWriter class.
     *
     * @requires <pre>
     * glossary.hasKey(term), folder is name of existing folder
     * in filesystem, and glossary is not null.
     * </pre>
     *
     * @ensures <pre>
     * createTermPage = [.html file named "term.html" containing
     * term in bold red italics with definition underneath ending
     * in a single period, with any word in definition which is also
     * a term itself linking to that term's page, with link back to
     * index.html at bottom.]
     * </pre>
     */
    protected static void createTermPage(String term,
            Map<String, String> glossary, String folder) throws FileNotFoundException {
        assert glossary != null : "Violation of: glossary is not null";
        assert glossary.containsKey(term) : "Violation of: term is key in glossary";

        String file = folder + "/" + term + ".html";
        PrintWriter html = new PrintWriter(file);

        // Print header and style info
        printHeader(html, term);

        // Print term at top of page
        html.println("<body>");
        html.print("<h2 class=\"term\">" + term + "</h2>");

        // Print definition
        html.print("<blockquote class=\"definition\">");
        html.print(formatDefinition(term, glossary));
        html.println("</blockquote>");

        // Print bottom of html file
        html.println("<hr>");
        html.println("<p>Return to <a href=\"index.html\">index</a></p>");

        printFooter(html);
        html.close();

    }

    /**
     * Formats a word so that a file extension can be concatenated to the end of
     * it properly. Takes a {@code String}, removes any trailing punctuation or
     * whitespace, and removes plurality if word is found as a key in the given
     * {@code Map} in its singular form.
     *
     * @param word
     *            Word to properly format
     * @param glossary
     *            Map for comparing word to keys
     * @return word without punctuation characters, and singular if #word is
     *         plural and given {@code Map} has key matching #word in its
     *         singular form.
     *
     * @requires <pre>
     * {@code Map} "glossary" is not null, and {@code String}
     * "word" is not blank.
     * </pre>
     *
     * @ensures <pre>
     * makeLinkable = [#word - trailing whitespace or
     * punctuation, and in singular form if found as key in {@code Map}
     * that way.]
     * </pre>
     */
    protected static String makeLinkable(String word,
            Map<String, String> glossary) {
        assert glossary != null : "Violation of: glossary is not null";
        assert !word.isBlank() : "Violation of: word is not blank";

        // Strip word of any unwanted punctuation characters
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (Character.isAlphabetic(word.charAt(i))) {
                sb.append(word.charAt(i));
            }
        }
        String linkableWord = sb.toString().toLowerCase();

        // If word is plural, but appears in Map as singular, make singular.
        if (linkableWord.endsWith("s") && glossary
                .containsKey(linkableWord.substring(0, linkableWord.length() - 1))) {
            linkableWord = linkableWord.substring(0, linkableWord.length() - 1);
        }

        return linkableWord;
    }

    /**
     * Determines if the given {@code String} is a key in the given {@code Map}
     * in any form.
     *
     * @param word
     * @param glossary
     * @return true if glossary.hasKey(word in any form)
     *
     * @requires <pre>glossary is not null, and word is not blank</pre>
     *
     * @ensures <pre>
     * isTerm = [true if glossary.hasKey(word in any form),
     * false otherwise]
     * </pre>
     */
    protected static boolean isTerm(String word, Map<String, String> glossary) {
        assert glossary != null : "Violation of: glossary is not null";
        assert !word.isBlank() : "Violation of: word is not blank";

        boolean isTerm = false;

        // Strip any spaces or special characters.
        String updatedWord = makeLinkable(word, glossary).toLowerCase();

        // Check if word appears as a key in Map in its singular or plural form.
        if (glossary.containsKey(updatedWord) || glossary.containsKey(updatedWord + "s")
                || (updatedWord.endsWith("s") && glossary.containsKey(
                        updatedWord.substring(0, updatedWord.length() - 1)))) {
            isTerm = true;
        }

        return isTerm;
    }

    /**
     * Provides some of the logic for createTermPage method. Prints definition
     * of given term to a string, while creating a html link to any word in the
     * definition which exists as its own term in the glossary.
     *
     * @param term
     *            the term whose definition is to be printed.
     * @param glossary
     *            the {@code Map} containing all terms with their definitions
     * @return definition of given term formatted into html code, linking to any
     *         term in map that exists in that definition. End with a period.
     *
     * @requires <pre>
     * glossary is not null, and term is not blank.
     * </pre>
     *
     * @ensures <pre>
     * formatDefinition = definition of given term is printed to a
     * string on a single line, ending with a period, and having each
     * word in definition that is also a term in given glossary
     * printed embedded in a html link to that term's page.
     * </pre>
     */
    protected static String formatDefinition(String term,
            Map<String, String> glossary) {
        assert glossary != null : "Violation of: glossary is not null";
        assert !term.isBlank() : "Violation of: term is not blank";

        String definition = glossary.get(term);

        // Get rid of trailing periods and / or commas at end of definition
        while (definition.endsWith(".") || definition.endsWith(",")) {
            definition = definition.substring(0, definition.length() - 1);
        }

        // Split definition upon spaces into words with puntuations
        String[] words = definition.split(" ");

        // Build formatted definition, link any words that are also terms in glossary
        StringBuilder bldr = new StringBuilder();
        for (String word : words) {

            // check if current word is a glossary term and behave accordingly
            if (isTerm(word, glossary)) {
                String wordLinkable = makeLinkable(word, glossary);
                bldr.append("<a href=\"" + wordLinkable + ".html\">");

                // make sure any immediate punctuation after word is conserved
                String punc = "";
                if (word.endsWith(",")) {
                    String wordCopy = word;
                    word = wordCopy.substring(0, wordCopy.length() - 1);
                    punc = ",";
                } else if (word.endsWith(".")) {
                    String wordCopy = word;
                    word = wordCopy.substring(0, wordCopy.length() - 1);
                    punc = ".";
                }
                bldr.append(word);
                bldr.append("</a>");
                bldr.append(punc);

            } else {
                // if not term in glossary, just print the word
                bldr.append(word);
            }

            // print space between each word
            bldr.append(" ");
        }

        // Remove trailing space and end with a period.
        bldr.deleteCharAt(bldr.length() - 1);
        bldr.append(".");
        return bldr.toString();

    }

}
