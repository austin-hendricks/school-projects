import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/**
 * Utility class which generates a tag cloud as an HTML file from a given input
 * text, sorting words by decreasing counts and then alphabetically.
 *
 * @author Austin Hendricks
 *
 */
public final class TagCloudGeneratorStandardJava {

    /**
     * Compare {@code Integer}s in decreasing numerical order.
     */
    private static class CountOrder
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> count1,
                Map.Entry<String, Integer> count2) {
            int x = count2.getValue().compareTo(count1.getValue());
            // if entries have same values, check keys
            if (x == 0) {
                x = count2.getKey().compareTo(count1.getKey());
            }
            return x;
        }
    }

    /**
     * Compare {@code String}s in alphabetical order.
     */
    private static class WordOrder
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> word1,
                Map.Entry<String, Integer> word2) {
            int x = word1.getKey().compareTo(word2.getKey());
            // if keys are same, compare values
            if (x == 0) {
                x = word1.getValue().compareTo(word2.getValue());
            }

            return x;
        }
    }

    /**
     * String containing separators.
     */
    private static final String SEPARATORS = " \t\n\r,'-.!?|[]{}*&@#$%^_\";:/()`~";

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGeneratorStandardJava() {
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code SEPARATORS}) or "separator string" (maximal length string of
     * characters in {@code SEPARATORS}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection entries(SEPARATORS) = {}
     * then
     *   entries(nextWordOrSeparator) intersection entries(SEPARATORS) = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection entries(SEPARATORS) /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of entries(SEPARATORS)  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of entries(SEPARATORS))
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position) {
        assert text != null : "Violation of: text is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        // Check if starting pos is beginning of sep string
        boolean sep = false;
        if (SEPARATORS.indexOf(text.charAt(position)) != -1) {
            sep = true;
        }
        // Determine length of separator or non-separator string
        int endOfStr = position;
        if (sep) {
            // find end position of separator string.
            while (endOfStr < text.length()
                    && SEPARATORS.indexOf(text.charAt(endOfStr)) != -1) {
                endOfStr++;
            }
        } else {
            // find end position of non-separator string.
            while (endOfStr < text.length()
                    && SEPARATORS.indexOf(text.charAt(endOfStr)) == -1) {
                endOfStr++;
            }
        }
        // Return string containing entire next word or separator
        return text.substring(position, endOfStr);
    }

    /**
     * Returns a map containing each unique word, with all words converted to
     * lowercase, and its number of occurrences after each line from the input
     * file is read.
     *
     * @param in
     *            BufferedReader to read the input file
     * @ensures <pre>
     *          generateWordMap contains every unique word read from {@code in}
     *          and each word's respective counts </pre>
     * @return a map containing all words read and their counts
     */
    public static Map<String, Integer> generateWordMap(BufferedReader in)
            throws IOException {

        Map<String, Integer> wordCount = new TreeMap<String, Integer>();
        String s = in.readLine();
        // continually read lines and generate a map with every unique words and its count
        while (s != null) {

            int currLen = s.length();
            int currPos = 0;

            for (int i = currPos; i < currLen; i += 0) {
                String currWordOrSep = nextWordOrSeparator(s, i).toLowerCase();
                int currWordOrSepLen = currWordOrSep.length();

                // branch for when it is the first occurrence of a word
                if (SEPARATORS.indexOf(currWordOrSep.charAt(0)) < 0
                        && !wordCount.containsKey(currWordOrSep)) {

                    wordCount.put(currWordOrSep, 1);

                } else if (SEPARATORS.indexOf(currWordOrSep.charAt(0)) < 0) {

                    // branch for when a word is already in the Map
                    int currCount = wordCount.get(currWordOrSep);
                    currCount++;
                    wordCount.replace(currWordOrSep, currCount);
                }
                // makes sure i is set to the position of the next word/separator
                i += currWordOrSepLen;
            }
            // read the next line
            s = in.readLine();
        }

        return wordCount;
    }

    /**
     * Takes a map of words and counts, and first sorts it in decreasing
     * numerical order, then removes the top {@code n} entries and sorts them
     * alphabetically.
     *
     * @param wordMap
     *            Map containing unique words and the number of their
     *            occurrences
     * @param n
     *            number of words to be added to second SortingMachine
     * @requires words is not null and |words| > 0 and n > 0
     * @clears words
     * @ensures <pre>
     *          sort produces a List that has
     *          {@code n} elements with the highest counts from {@code words}
     *          sorted alphabetically and in order of decreasing counts. </pre>
     * @return a List with {@code n} values sorted alphabetically in order of
     *         decreasing counts.
     */
    public static List<Entry<String, Integer>> sort(
            Map<String, Integer> wordMap, int n) {
        assert n > 0 : "Violation of: n > 0";
        assert wordMap != null : "Violation of: words is not null";
        assert wordMap.size() > 0 : "Violation of: words is not null";

        Set<Entry<String, Integer>> entries = wordMap.entrySet();
        List<Entry<String, Integer>> wordList = new LinkedList<>();

        // collect all map entries into a linked list to allow for sorting
        for (Iterator<Entry<String, Integer>> it = entries.iterator(); it
                .hasNext();) {
            wordList.add(0, it.next());
        }

        // sort by decreasing word frequency (counts)
        Comparator<Entry<String, Integer>> countOrder = new CountOrder();
        Collections.sort(wordList, countOrder);

        // strip all words after (n-1)'th word from wordList
        while (wordList.size() > n) {
            wordList.remove(n);
        }

        // sort alphabetically A-Z
        Comparator<Entry<String, Integer>> alphabetOrder = new WordOrder();
        Collections.sort(wordList, alphabetOrder);

        // clear the rest of the word map
        wordMap.clear();

        return wordList;
    }

    /**
     * Writes all of the sorted words in the given {@code SortedSet} to the HTML
     * output. Completes the HTML output file upon method completion.
     *
     * @param inputFile
     *            {@code String} containing name of the input file
     * @param sortedWords
     *            {@code List} containing all words and their respective counts,
     *            sorted alphabetically in order of decreasing counts.
     * @param html
     *            {@code PrintWriter} that writes to the html output file.
     * @param minFontSize
     *            the smallest font size to be represented in the output
     * @param maxFontSize
     *            the largest font size to be represented in the output
     * @requires |sortedWords| > 0
     * @clears sortedWords
     * @ensures <pre>
     *          output file represented in {@code PrintWriter} html contains
     *          a full body and is complete with all tags closed, and
     *          |words in tag cloud| = $cloudSize, and
     *          for all w: word in tag cloud
     *              $words.containsKey(w) is true, and
     *              $words.getValue(w) determines font size of w in tag cloud,
     *          and output file contains paragraph equal to string:
     *          "Top {@code int} numWords words in {@code String}
     *          inputFile." </pre>
     */
    public static void populateCloud(String inputFile,
            List<Map.Entry<String, Integer>> sortedWords, PrintWriter html, 
            final int minFontSize, final int maxFontSize) {
        assert sortedWords.size() > 0 : "Violation of: |sortedWords| > 0";

        // determine max and min font sizes in the list of sorted words
        int minCounts = Integer.MAX_VALUE;
        int maxCounts = Integer.MIN_VALUE;
        for (Iterator<Entry<String, Integer>> it = sortedWords.iterator(); it
                .hasNext();) {
            Entry<String, Integer> entry = it.next();
            Integer val = entry.getValue();
            if (val > maxCounts) {
                maxCounts = val;
            } else if (val < minCounts) {
                minCounts = val;
            }
        }

        // begin html body in output file
        html.println("<body>");
        html.println("<h2>Top " + sortedWords.size() + " words in " + inputFile
                + ".txt</h2>");
        html.println("<hr>");
        html.println("<div class=\"cdiv\">");
        html.println("<p class=\"cbox\">");

        // add words into html file in sorted order
        for (int len = sortedWords.size(); len > 0; len--) {

            Map.Entry<String, Integer> p = sortedWords.get(0);
            sortedWords.remove(0);
            String word = p.getKey();
            int count = p.getValue();

            // calculate font size
            int fSize = calculateFontSize(word, count, minCounts, maxCounts, minFontSize, maxFontSize);

            // print info to html file
            html.print("<span style=\"cursor:default\" ");
            html.print("class=\"f" + fSize + "\" ");
            html.print("title=\"count: " + count + "\">");
            html.println(word + "</span>");
        }

        // close all remaining open html tags to end file
        html.println("</p>");
        html.println("</div>");
        html.println("</body>");
        html.println("</html>");
    }

    /**
     * Given a word and its number of counts, calculates the proper font size
     * that this word should have in the tag cloud. Uses maxCounts and minCounts
     * to perform a calculation weighted by {@code int} counts, producing an
     * output between the minimum font size (11) and maximum font size (48) that
     * is properly representative of the number of counts for this word in
     * relation to the rest of the words.
     *
     * @param word
     *            the word whose font-size is to be determined
     * @param count
     *            the number of occurances of the given word
     * @param maxCounts
     *            the maximum number of counts any word will have
     * @param minCounts
     *            the minimum number of counts any word will have
     * @param minFontSize
     *            the smallest font size to be represented in the output
     * @param maxFontSize
     *            the largest font size to be represented in the output
     * @return the optimal scaled font-size for this word within range 11-48
     * @requires word is not null and count > 0
     * @ensures <pre>
     *          11 <= calculateFontSize <= 48 and calculateFontSize is
     *          proportionate to the value of {@code int} counts weighted
     *          between {@code final int} minCounts and {@code final int}
     *          maxCounts. </pre>
     */
    public static int calculateFontSize(String word, int count, int minCounts,
            int maxCounts, final int minFontSize, final int maxFontSize) {
        assert word != null : "Violation of: word is not null";
        assert count > 0 : "Violation of: count is greater than 0.";

        final int shift = minFontSize - 1;

        // linear normalization to determine fSize
        int fSize = 1;
        if (count > minCounts) {
            int numerator = (maxFontSize - shift) * (count - minCounts);
            int denominator = maxCounts - minCounts;
            double quotient = numerator * 1.0 / denominator;
            fSize = (int) Math.ceil(quotient);
        }

        // output is in range [1, 38] -- shift so output will be in [11, 48].
        return fSize + shift;
    }

    /**
     * Writes the HTML header to the output file in given {@code PrintWriter}.
     * Uses numWords and inputFileName to generate HTML title element.
     *
     * @param inputFileName
     *            name of the input file
     * @param numWords
     *            number of words to be included in generated tag cloud
     * @param html
     *            PrintWriter that writes to the html output file
     * @requires numWords > 0 and inputFileName is not null
     * @ensures <pre>
     *          output file represented in {@code SimpleWriter} html begins
     *          with an html tag, followed by a complete header which
     *          establishes the css stylesheet as well as sets the title equal
     *          to the string: "Top {@code int} numWords words in {@code String}
     *          inputFileName" </pre>
     */
    public static void writeHeader(String inputFileName, int numWords,
            PrintWriter html) {
        assert numWords > 0 : "Violation of: numWords > 0";
        assert inputFileName != null : "Violation of: inputFileName is not null";

        final String stylesheet = "\"" + inputFileName + ".css\"";

        // open the html tag and begin header
        html.println("<!DOCTYPE html>");
        html.println("<html lang=\"en\">");
        html.println("<head>");
        html.println("<title>Top " + numWords + " words in " + inputFileName
                + ".txt</title>");

        // link to tagcloud.css to use as stylesheet for the html document
        html.println("<link href=" + stylesheet
                + " rel=\"stylesheet\" type=\"text/css\">");

        // close header
        html.println("</head>");
    }

    /**
     * Returns whether the given String can be parsed as an integer.
     *
     * @param input
     *            String to be parsed as int
     * @return true if input can be parsed as int, false otherwise
     * @requires input is not null
     * @ensures isInteger = true iff all characters in input are digits
     */
    private static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns both the name of the input file, and the BufferedReader
     * containing a stream of the input file specified by the user, and prints
     * newline to console.
     *
     * @param in
     *            {@code Scanner} reading System.in
     * @return String containing name of input file
     * @ensures <pre>
     *      if no IOException
     *          (getUserInputFile[0]) is type String and
     *          (getUserInputFile[1]) is type BufferedReader and
     *          (getUserInputFile[1]).isReady and a newline is printed to
     *          console
     *      else
     *          prints appropriate error message to console and
     *          closes Scanner and
     *          exits program
     *      </pre>
     */
    private static Object[] getUserInputFile(Scanner in) {
        Object[] userInput = new Object[2];

        // asks user for name of input file
        System.out.print(
                "Enter the name of an input file using a terminal name: ");
        String inFile = in.nextLine();
        userInput[0] = inFile;

        // try to open input file
        BufferedReader reader = null; // null initilization required for compilation
        try {
            reader = new BufferedReader(new FileReader(inFile));
        } catch (IOException e) {
            System.err.println("Error opening file: " + inFile);
            in.close();
            System.exit(1);
        }
        userInput[1] = reader;
        System.out.println();

        return userInput;
    }

    /**
     * Returns an array of Strings containing the name of the user-desired
     * output file, as well as a string representation of the user-desired cloud
     * size integer. Updates console by printing a newline at the end of the
     * operation.
     *
     * @param in
     *            {@code Scanner} reading System.in
     * @return array of String containing name of output file and valid integer
     *         string containing desired cloud size
     * @ensures getUserInputAndCloudSize[1] can be parsed to positive int
     */
    private static String[] getUserOutputAndCloudSize(Scanner in) {
        String[] userInput = new String[2];

        // asks user for name of output file
        System.out.print(
                "Enter the name of an output folder: ");
        userInput[0] = in.nextLine();
        System.out.println();

        // asks user for number of words to be generated
        System.out.print("Enter the number of words to be generated: ");
        String numWordsStr = in.nextLine();

        // continually ask user for input until valid input is given
        while (!isInteger(numWordsStr) || numWordsStr.contains("-")
                || numWordsStr.startsWith("0")) {
            System.out.println("Invalid input.");
            System.out.print("Enter the number of words to be generated: ");
            numWordsStr = in.nextLine();
        }
        userInput[1] = numWordsStr;
        System.out.println();

        return userInput;
    }

    /**
     * Returns the cloudSize based upon user-desired numWords and the actual
     * size of the map of unique words.
     *
     * @param userSize
     *            user-desired number
     * @param mapSize
     *            total amount of unique words
     * @return the smaller of the userSize and mapSize
     * @ensures <pre>
     *          if userSize < mapSize
     *              determineActualCloudSize = userSize
     *          else
     *              determineActualCloudSize = mapSize
     *          </pre>
     */
    private static int determineActualCloudSize(int userSize, int mapSize) {
        int cloudSize = userSize;
        if (userSize > mapSize) {
            // user number is too big -- continue instead with |wordMap|
            cloudSize = mapSize;
            System.out.println(
                    userSize + " exceeds number of unique words in file.");
            System.out.println(
                    "Continuing using " + cloudSize + " as cloud size.");
            System.out.println();
        }
        return cloudSize;
    }

    /**
     * Returns a PrintWriter containing the output file specified in outFile.
     * Prints error and terminates program if IOException occurs.
     *
     * @param outFile
     *            name of output file
     * @param inputReader
     *            input file bytestream
     * @return PrintWriter containing output file
     * @ensures <pre>
     *      if no IOException occurs
     *          (openOutputFile).isReady
     *      else
     *          prints appropriate error message to console and
     *          inputReader is tried to be closed and
     *          program is terminated with exit code 1
     *      </pre>
     */
    private static PrintWriter openOutputFile(String outFile,
            BufferedReader inputReader) {

        // null initialization required for compilation
        PrintWriter htmlWriter = null;

        try {
            htmlWriter = new PrintWriter(
                    new BufferedWriter(new FileWriter(outFile)));
        } catch (IOException e) {
            System.err.println("Error creating or opening file: " + outFile);
            try {
                inputReader.close();
            } catch (IOException e2) {
                System.err.println("Error closing input file");
            }
            System.exit(1);
        }

        return htmlWriter;
    }

    /**
     * Writes the CSS stylesheet to the output file in given {@code PrintWriter}.
     * Uses smallestFont and largestFont to generate all of the needed CSS rules.
     *
     * @param smallestFont
     *            smallest font to be represented in the output
     * @param largestFont
     *            largest font to be represented in the output
     * @param cssWriter
     *            PrintWriter that writes to the css output file
     * @requires smallestFont < largestFont and smallestFont > 0 and cssWriter is not null
     * @ensures <pre>
     *          output file represented in {@code SimpleWriter} cssWriter contains a fully 
     *          written, fully valid CSS stylesheet that represents html elements of all
     *          font sizes within the range of smallestFont and largestFont.
     *          </pre>
     */
    public static void generateCSS(int smallestFont, int largestFont, PrintWriter cssWriter) {
        cssWriter.write("body { padding: 10px; margin: 10px; background: #fff; color: #05e; "
        + "font-family: \"Arial\", Arial, Helvetica, sans-serif; }\n\n");
        cssWriter.write(".cbox { padding: 12px; background: #d5d5d5; width: 700px; }\n");
        cssWriter.write(".cdiv { margin-top: 0; padding-left: 7px; padding-right: 7px; }\n");
        cssWriter.write(".cdiv span { padding: 0px; margin: 3px; }\n");
        cssWriter.write(".cdiv span:hover { color: #fff; background: #05e; }\n\n");

        for (int i = smallestFont; i <= largestFont; i++) {
            cssWriter.write(".f" + i + " { font-size: " + i + "px; line-height: " + i + "px; }\n");
        }
        
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        final int smallestFont = 11;
        final int largestFont = 48;

        /*----------------------------------------------------------------------
         * Capture input file from user and open it.
         */

        Scanner in = new Scanner(System.in);
        Object[] userInputFile = getUserInputFile(in);

        // these casts cannot fail as ensured by getUserInputFile method
        String inFile = (String) userInputFile[0];
        BufferedReader inputReader = (BufferedReader) userInputFile[1];

        /*
         * ---------------------------------------------------------------------
         * Capture output file and user-desired cloud size, create output folder.
         */

        // grab name of text file from full file path (e.g "data/alice.txt" --> "alice")
        String txtName;
        int endPrePath = inFile.lastIndexOf("/") + 1;
        int fileTypeIndex = inFile.indexOf(".txt");
        txtName = inFile.substring(endPrePath, fileTypeIndex);
        
        String[] userPrefs = getUserOutputAndCloudSize(in);
        String outFolder = userPrefs[0];
        String styleFile = "output/" + outFolder + "/" + txtName + ".css";
        String outFile = "output/" + outFolder + "/" + txtName + ".html";
        String userNum = userPrefs[1];
        int numWords = Integer.parseInt(userNum);
        in.close();

        // create output folder
        File directory = new File("output/" + outFolder);
        if (directory.exists()) {
            System.err.println("Output directory \"" + outFolder + "\" already exists.");
            return;
        }
        directory.mkdirs();

        /*----------------------------------------------------------------------
         * Read input file contents into memory and determine actual cloud size.
         */

        // Read input file and generate word map.
        Map<String, Integer> wordMap;
        try {
            wordMap = generateWordMap(inputReader);
        } catch (IOException e) {
            System.err.println("Error reading input file");
            directory.delete();
            in.close();
            return;
        }

        // handle possibility of zero words in input file
        if (wordMap.size() <= 0) {
            System.err.println("Error: No words read from input file.");
            directory.delete();
            in.close();
            return;
        }

        // determine cloud size based on |wordMap| and numWords
        int cloudSize = determineActualCloudSize(numWords, wordMap.size());

        /*----------------------------------------------------------------------
         * Sort words, generate output file, and close resources.
         */

        // sort words by decreasing counts then alphabetically
        List<Map.Entry<String, Integer>> wordList = sort(wordMap, cloudSize);

        // try opening output html file
        PrintWriter htmlWriter = openOutputFile(outFile, inputReader);

        // write output to HTML file
        writeHeader(txtName, wordList.size(), htmlWriter);
        populateCloud(txtName, wordList, htmlWriter, smallestFont, largestFont);

        // try opening css file
        PrintWriter cssWriter = openOutputFile(styleFile, inputReader);

        // write css to style file
        generateCSS(smallestFont, largestFont, cssWriter);

        // close resources
        htmlWriter.close();
        cssWriter.close();
        try {
            inputReader.close();
        } catch (IOException e) {
            System.err.println("Error closing input file");
            return;
        }

        // signal end of program
        System.out.println("Finished writing to " + outFile);

    }

}
