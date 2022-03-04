import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.LinkedList;

/**
 * Glossary creator: generates a group of HTML files based on text file input
 * holding glossary terms and definitions.
 *
 * @author Austin Hendricks
 *
 */
public final class GlossaryMaker {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private GlossaryMaker() {
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Scanner consoleIn = new Scanner(System.in);

        // Get input file and destination folder from user.

        System.out.print("Enter name of input file including .txt extension: ");
        String fileName = consoleIn.nextLine();
        System.out.print("Enter a title for the glossary to be created (max 25 characters): ");
        String glossaryTitle = "";
        do {
            glossaryTitle = consoleIn.nextLine();
            if (glossaryTitle.length() > 25) {
                System.out.print("Invalid title - too many characters (max 25). Try again: ");
            }
        } while (glossaryTitle.length() > 25);
        String folder = "output/" + glossaryTitle;
        File dir = new File(folder);
        dir.mkdirs();

        /*
         * Open input file and read into Map, using terms as keys and
         * definitions as values.
         */

        Map<String, String> glossary = populateGlossary(fileName);

        // Generate alphabetical list of all terms in Map.

        Queue<String> terms = alphabetizeKeysToQueue(glossary);

        // Generate front page of glossary (index.html)

        HTMLGenerator.createFrontPage(terms, folder, glossaryTitle);

        // Create .html file for each term in glossary

        for (String term : terms) {
            HTMLGenerator.createTermPage(term, glossary, folder);
        }

        // Add CSS file to output directory

        // FileWriter css = new FileWriter(new File(folder + "/style.css"));
        // css.write("a { color: #444; } a:hover { color: red; } body { background-image: url(\"data/books2.jpg\"); background-repeat: no-repeat; background-size: cover; background-position: center; background-attachment: fixed; padding-left: 3vw; color: #444; font-family: Helvetica, Arial, sans-serif; font-size: calc(16px + 0.75vw); line-height: calc(1.1em + 0.5vw); } hr { width: 50%; text-align: left; margin-left: 0; } h1, h2, h3 { color: black; font-weight: normal; } h1 { font-size: calc(1.2em + 2vw); } h2 { font-size: calc(1em + 1.2vw); } h3 { font-size: calc(1em + 0.2vw); } .definition { width: 50%; } .term { color: red; } .title { color: black; }");
        // css.close();

        // Finished: Print success message.

        System.out.println("Success!");

        // Close input & output streams.

        consoleIn.close();
    }

    /**
     * Opens input file from {@code String} fileName and stores its contents
     * (several unique glossary terms + definitions) in the given {@code Map}.
     *
     * @param fileName
     *            name of file to read contents from
     * @return {@code Map} of terms and definitions from input file
     * @throws IOException
     *
     * @requires <pre>
     * [file named fileName exists but is not open, and has the
     *  format of one "term" (unique in the file) on first line, and one
     *  "definition" on following lines, with each term + definition pair
     *  separated by '\n'; the "term" must not contain whitespace]
     * </pre>
     *
     * @ensures <pre>
     * [populateGlossary contains term -> definition mapping from file
     * fileName with both term and definition in lowercase form]
     * </pre>
     */
    protected static Map<String, String> populateGlossary(String fileName) throws IOException {
        assert fileName != null : "Violation of: fileName is not null";

        /*
         * Initial set up
         */

        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
        Map<String, String> glossary = new LinkedHashMap<>();

        /*
         * Read lines from file into memory.
         */

        Queue<String> lines = new LinkedList<>();
        while (fileReader.ready()) {
            lines.add(fileReader.readLine());
        }

        /*
         * Translate lines from file into individual term-definition pairs, and
         * import into glossary.
         */

        while (lines.size() > 0) {
            // Get name of current term.
            String term = lines.remove().toLowerCase();

            // Get definition for current term.
            String definition = "";
            while (lines.peek() != null && !lines.peek().isBlank()) {
                definition = definition.concat(lines.remove()).toLowerCase();
            }

            // Input term-definition pair into glossary.
            glossary.put(term, definition);

            // Remove empty line
            lines.remove();
        }

        fileReader.close();

        return glossary;
    }

    /**
     * Reads all of the keys from a {@code Map} and feed them into a
     * {@code Queue} alphabetically.
     *
     * @param glossary
     *            {@code Map} to read keys from
     * @return {@code Queue} filled with names of each key in given {@code Map}.
     *
     * @requires <pre>
     * [map named "glossary" is not null and has at least 1 element, and the relation computed by
     * String.CASE_INSENSITIVE_ORDER.compare is a total preorder]
     * </pre>
     *
     * @ensures <pre>
     * alphabetizeKeysToQueue = [each pair.key from glossary ordered
     * alphabetically with case ignored (Aa's at top of queue, Zz's
     * at bottom)]
     * </pre>
     */
    protected static Queue<String> alphabetizeKeysToQueue(
            Map<String, String> glossary) {
        assert glossary != null : "Violation of: map is not null";
        assert glossary.size() > 0 : "Violation of: |glossary| > 0";

        Queue<String> q = new PriorityQueue<>(String.CASE_INSENSITIVE_ORDER);

        /*
         * Read each term name into Queue.
         */

        // Iterator over all pairs in the glossary.
        for (Map.Entry<String, String> entry : glossary.entrySet()) {
            // Add term name from each term-definition pair into queue.
            String term = entry.getKey();
            q.add(term);
        }

        return q;
    }

}
