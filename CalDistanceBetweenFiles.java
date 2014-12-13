import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CalDistanceBetweenFiles {

	public static final int NUM_OF_FILES = 2;
	public static final int SQUARED = 2;

	public static void main(String[] args) {

		double startTime = System.nanoTime();

		if (args.length != NUM_OF_FILES) {
			System.out.println("Usage: java ComputeDistanceBetween pathToFile1.txt pathTofile2.txt");
			return;
		}

		int docOneLineCount = 0;
		int docTwoLineCount = 0;
		int docOneTotalWordCount = 0;
		int docTwoTotalWordCount = 0;
		int docOneDistinctWordCount = 0;
		int docTwoDistinctWordCount = 0;

		String delimiters = "[^a-zA-Z0-9]+";
		String[] words;

		SortedSet<String> allWords = new TreeSet<String>();
		Map<String, Map<String, Integer>> wordMap = new HashMap<String, Map<String, Integer>>();

		for (int i = 0; i < NUM_OF_FILES; i++) {
			Path path = FileSystems.getDefault().getPath(args[i]);
			Charset charset = Charset.forName("US-ASCII");

			try (BufferedReader bufferedReader = Files.newBufferedReader(path, charset)) {
				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					words = line.split(delimiters);

					if (i == 0) {
						docOneLineCount++;
						docOneTotalWordCount += words.length;
					} else {
						docTwoLineCount++;
						docTwoTotalWordCount += words.length;
					}

					// For every word in the current line
					for (int k = 0; k < words.length; k++) {
						if (words[k].length() == 0) {
							continue;
						}
						Map<String, Integer> tokenMap = wordMap.get(words[k].toLowerCase());

						// Very first time the word has come up in either
						// document, make a new entry in wordMap
						if (tokenMap == null) {
							tokenMap = new HashMap<String, Integer>();
							tokenMap.put(args[i], 1);
							wordMap.put(words[k].toLowerCase(), tokenMap);
							allWords.add(words[k].toLowerCase());

							if (i == 0) {
								docOneDistinctWordCount++;
							} else {
								docTwoDistinctWordCount++;
							}

							// Not the first time the word has come up
						} else {

							// First time the word has come up for the current
							// document
							if (!tokenMap.containsKey(args[i])) {
								tokenMap.put(args[i], 1);
								wordMap.put(words[k].toLowerCase(), tokenMap);

								if (i == 0) {
									docOneDistinctWordCount++;
								} else {
									docTwoDistinctWordCount++;
								}

								// Not the first time the word has come up in
								// the current document
							} else {
								Integer currentFrequency = wordMap.get(words[k].toLowerCase()).get(args[i]);
								tokenMap.put(args[i], ++currentFrequency);
								wordMap.put(words[k].toLowerCase(), tokenMap);
							}
						}
					}

				} // end of while
			} catch (IOException e) {
				System.err.format("IOException: %s%n", e);
			}

			if (args[0].equals(args[1])) {
				docTwoLineCount = docOneLineCount;
				docTwoTotalWordCount = docOneTotalWordCount;
				docTwoDistinctWordCount = docOneDistinctWordCount;
				i = NUM_OF_FILES;
			}
		} // end of read files for loop

		Double vector = computeDistance(wordMap, allWords, args);
		double elapsedTime = (System.nanoTime() - startTime) / 1000000000;

		/*****/
		System.out.println(args[0] + " : Lines: " + docOneLineCount + ". Total words: " + docOneTotalWordCount
				+ ". Distinct words: " + docOneDistinctWordCount + ".");

		System.out.println(args[1] + " : Lines: " + docTwoLineCount + ". Total words: " + docTwoTotalWordCount
				+ ". Distinct words: " + docTwoDistinctWordCount + ".");

		System.out.printf("The distance between the documents is: %.6f radians.", vector);
		System.out.printf("\nTime elapsed: %.2f seconds.\n", elapsedTime);
		/*****/

// 		 Uncomment to see all the words and their frequencies. Requires a really big console output size. Or output to text file.
		 for (String word : allWords) {
		 System.out.println(word + " : " + wordMap.get(word).entrySet());
		 }
	}

	/** Computes dot product and other math stuff to find the "distance" between the two files.
	 * @param wordMap word frequency map
	 * @param allWords all words sorted set
	 * @param args command line args
	 * @return angle in radians between the two docs
	 */
	private static Double computeDistance(Map<String, Map<String, Integer>> wordMap, Set<String> allWords, String[] args) {
		double numerator = 0;
		double docOneDotProduct = 0;
		double docTwoDotProduct = 0;

		for (String word : allWords) {
			if (wordMap.get(word).containsKey(args[0]) && wordMap.get(word).containsKey(args[1])) {
				numerator += (wordMap.get(word).get(args[0]) * wordMap.get(word).get(args[1]));
				docOneDotProduct += Math.pow(wordMap.get(word).get(args[0]), SQUARED);
				docTwoDotProduct += Math.pow(wordMap.get(word).get(args[1]), SQUARED);
			} else if (wordMap.get(word).containsKey(args[0])) {
				docOneDotProduct += Math.pow(wordMap.get(word).get(args[0]), SQUARED);
			} else {
				docTwoDotProduct += Math.pow(wordMap.get(word).get(args[1]), SQUARED);
			}
		}
		return Math.acos(numerator / (Math.sqrt(docOneDotProduct) * Math.sqrt(docTwoDotProduct)));
	}
}
