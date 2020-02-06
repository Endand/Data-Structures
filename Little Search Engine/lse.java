package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f); //keeps track of duplicates with key
		noiseWords = new HashSet<String>(100,2.0f); //array w/ no duplicate
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		if (docFile == null) {
			throw new FileNotFoundException("File not found");
		}
		File doc= new File(docFile);
		Scanner sc = new Scanner(doc); //scan doc file
		HashMap<String,Occurrence> loadkw = new HashMap<String,Occurrence>(); //create HashMap 
		while (sc.hasNext()) { //traverse through words
			String test=sc.next().trim();
			String keyword = getKeyword(test); //check if word is keyword
			if (keyword == null) { //if it doesn't pass the keyword test, move on
				continue;
			}
			if(!loadkw.containsKey(keyword)) { //if it's the first occurrence
				Occurrence occ=  new Occurrence(docFile,1); //create new occurrence object with 1
				loadkw.put(keyword,occ); //put into HashMap
			}
			else{ //if it's not the first occurrence
				loadkw.get(keyword).frequency++; //frequency++
			}
		}
		sc.close();
		return loadkw;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 * 
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for(String word:kws.keySet()) { //traverse through HashMap
			ArrayList<Occurrence> listOfWord = keywordsIndex.get(word);
			if(listOfWord==null) { //new occurrence
				listOfWord= new ArrayList<Occurrence>(); //create first
				listOfWord.add(kws.get(word)); //add new item
			}
			else {
				listOfWord.add(kws.get(word)); //add new item
			}
			
			insertLastOccurrence(listOfWord); //sort
			keywordsIndex.put(word, listOfWord); //put modified list back in Master HashMap
	
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/*
		 * 1.check if every letter is alphabetic 
		 * 2.strip
		 * 3. lowercase
		 * 4. check if it's noiseword
		 * 5. return result
		 */
		if(word.equals("")||word.equals(".")||word.equals(",")||word.equals(":")||word.equals(";")||word.equals("!")||word.equals("?")) {
			word="";
		}
		while(word.length()>1&&(word.charAt(word.length()-1)=='.'||word.charAt(word.length()-1)==','||word.charAt(word.length()-1)=='?'||word.charAt(word.length()-1)==':'||word.charAt(word.length()-1)==';'||word.charAt(word.length()-1)=='!')) {
			word=word.substring(0,word.length()-1);
		
		}
		if(word.equals("")) {return null;}
		for(int i=0;i<word.length();i++) {
			if(!Character.isLetter(word.charAt(i))) {
				return null;
			}
		}
		word=word.toLowerCase();
		if(noiseWords.contains(word)) {
			return null;
		}
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		ArrayList<Integer> mp = new ArrayList<Integer>(); //
		Occurrence toInsert= occs.get(occs.size()-1);
		int last= occs.size()-1;
		if (occs.size() <= 1) {
			return null;
		}
		int lo=0;
		int mid=0;
		int hi=occs.size()-2;
		while(lo<=hi) {
			mid=(lo+hi)/2;
			mp.add(mid);
			if(occs.get(mid).frequency==toInsert.frequency) { //same frequency
				occs.add(mid+1, toInsert);
				last++;
				occs.remove(last);
				return mp;
			}
			if(occs.get(mid).frequency>toInsert.frequency) {
				lo=mid+1;
			}
			if(occs.get(mid).frequency<toInsert.frequency) {
				hi=mid-1;
			}
		}
		//out of the while loop
		if(toInsert.frequency>occs.get(mid).frequency) {
			occs.add(mid, toInsert);
			last++;
		}
		if(toInsert.frequency<occs.get(mid).frequency) {
			occs.add(mid+1, toInsert);
			last++;
		}
		occs.remove(last);
		return mp;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
		
			mergeKeywords(kws);
		
		}
		sc.close();
	
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<Occurrence> occ1= keywordsIndex.get(kw1.toLowerCase());	
		ArrayList<Occurrence> occ2= keywordsIndex.get(kw2.toLowerCase());	
		if(occ1==null && occ2==null) {
		return null;}
		ArrayList<String> top5 = new ArrayList<String>(5);
		if(occ1!=null && occ2==null) { //if kw2 is empty
			for(int doc=0; doc<top5.size();doc++) { //copy occ1 upto 5 items
				if(doc<occ1.size()) {
					top5.add(occ1.get(doc).document);
				}
			}
		}
		if(occ1==null && occ2!=null) { //if kw1 is empty
			for(int doc=0; doc<top5.size();doc++) {
				if(doc<occ2.size()) {
					top5.add(occ2.get(doc).document);
				}
			}
		}
		if(occ1!=null && occ2!=null) { //if they are both not empty
			int ptr1=0,ptr2=0;
			while(top5.size() != 5) {
			if(ptr1 < occ1.size() && ptr2 < occ2.size()) {
			
				int c= occ1.get(ptr1).frequency-occ2.get(ptr2).frequency;
				if(c>=0) { //occ1 is greater than occ2 || frequency is same
					if (!top5.contains(occ1.get(ptr1).document)) {
						top5.add(occ1.get(ptr1).document);
						ptr1++;
					}
					else if(!top5.contains(occ2.get(ptr2).document)) {
						top5.add(occ2.get(ptr2).document);
						ptr1++;
						ptr2++;
					}
					else {
						ptr1++;
						ptr2++;
					}
				}
				if(c<0) { //occ2 is greater than occ2
					if (!top5.contains(occ2.get(ptr2).document)) {
						top5.add(occ2.get(ptr2).document);
						ptr2++;
					}
					else if(!top5.contains(occ1.get(ptr1).document)) {
						top5.add(occ1.get(ptr1).document);
						ptr1++;
						ptr2++;
					}
					else {
						ptr1++;
						ptr2++;
					}
				}
			}
			
			else if (ptr1 < occ1.size() && ptr2 >= occ2.size()) {
				if(!top5.contains(occ1.get(ptr1).document)) {
				top5.add(occ1.get(ptr1).document);
				ptr1++;}
				else ptr1++;
			}
			else if (ptr1 >= occ1.size() && ptr2 < occ2.size()) {
				if(!top5.contains(occ2.get(ptr2).document)) {
				top5.add(occ2.get(ptr2).document);
				ptr2++;}
				else ptr2++;
			}
			
			else {
			
				break;
			}
		}
		}
		return top5;
	}
}