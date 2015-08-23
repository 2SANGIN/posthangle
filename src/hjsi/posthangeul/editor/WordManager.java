package hjsi.posthangeul.editor;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class WordManager {
   private Map<String, Integer> wordCounter;
   private Map<String, Vector<String>> historyTop10;
   private Map<String, String> historyLast;

   private final Comparator<String> wordComparator = new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
         return wordCounter.get(o2) - wordCounter.get(o1);
      }
   };

   public Comparator<String> getWordComparator() {
      return wordComparator;
   }

   public WordManager() {
      wordCounter = new TreeMap<String, Integer>();
      historyTop10 = new TreeMap<String, Vector<String>>();
      historyLast = new TreeMap<String, String>();
   }

   public void addWordAsHistory(String inputWord, String selectedWord) {
      countWord(selectedWord);
      historyLast.put(inputWord, selectedWord);

      Vector<String> historyList;
      if (historyTop10.containsKey(inputWord))
         historyList = historyTop10.get(inputWord);
      else
         historyList = new Vector<String>(10);

      if (!historyList.contains(selectedWord))
         historyList.add(selectedWord);

      historyList.sort(getWordComparator());
      while (historyList.size() > 10)
         historyList.remove(historyList.size() - 1);
   }

   public void countWord(String inputWord) {
      if (inputWord.length() > 1) {
         Integer count = wordCounter.get(inputWord);
         if (count != null)
            count++;
         else
            count = 1;
         wordCounter.put(inputWord.toString(), count);
         System.out.println("Counted Word: \"" + inputWord.toString() + "\", count: " + count);
      }
   }

   public Vector<String> getMatchingWords(String inputWord) {
      Vector<String> matchingWords = new Vector<String>();
      for (String str : wordCounter.keySet()) {
         /*
          * TODO 여기에 검색 알고리즘을 넣어야함!! 초성 검색이나, 단어 일부를 포함하는 경우 등을 포함하는 검색 알고리즘
          */
         if (str.contains(inputWord) || str.startsWith(inputWord) || (inputWord.length() == 0))
            matchingWords.add(str);
      }
      matchingWords.sort(wordComparator);
      return matchingWords;
   }
}
