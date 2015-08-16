package hjsi.posthangeul.editor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class WordManager {
  private Map<String, Integer> wordCounter;
  private Map<String, Vector<String>> historyTop10;
  private Map<String, String> historyLast;

  public WordManager() {
    wordCounter = new TreeMap<String, Integer>();
    historyTop10 = new TreeMap<String, Vector<String>>();
    historyLast = new TreeMap<String, String>();
  }

  public void addWordAsHistory(CharSequence inputWord, String selectedWord) {
    historyLast.put(inputWord.toString(), selectedWord);
    countWord(inputWord);
  }

  public void countWord(CharSequence inputWord) {
    if (inputWord.length() > 1) {
      Integer count = wordCounter.get(inputWord.toString());
      if (count != null)
        count++;
      else
        count = 1;
      wordCounter.put(inputWord.toString(), count);
    }
  }
  
  public Vector<String> getMatchingWords(CharSequence inputWord) {
    Vector<String> matchingWords = new Vector<String>();
    for (String str : wordCounter.keySet()) {
      /*
       * TODO 여기에 검색 알고리즘을 넣어야함!!
       * 초성 검색이나, 단어 일부를 포함하는 경우 등을 포함하는 검색 알고리즘
       */
      if (str.contains(inputWord))
        matchingWords.add(str);
    }
    return matchingWords;
  }
}
