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
      // 완성된 글자가 아니면 wordCounter에 넣지 않음
      for (char ch : inputWord.toCharArray())
        if (AutoComplete.isKoreanAlphabet(ch))
          return;

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

      // 초성을 포함하는지
      // matching words에는 완성된 글자만 들어가있음
      // inputWords에는 영어 / 초성 / 완성된한글 의 조합들로 구성

      else if (AutoComplete.isKorean(str)) {
        int index = 0;
        for (char input : inputWord.toCharArray()) {
          if (AutoComplete.isKoreanAlphabet(input)) {
            System.out.println("alphabet" + " " + input + ""
                + PostIME.getInitialChar(PostIME.getInitialIndex(str.charAt(index))));
            if (input == PostIME.getInitialChar(PostIME.getInitialIndex(str.charAt(index)))) {
              if (matchingWords.contains(str) == false)
                matchingWords.add(str);
              index++;
              continue;
            } else {
              matchingWords.remove(str);
              break;
            }
          } else if (AutoComplete.isKorean(input)) {
            if (PostIME.getInitialChar(PostIME.getInitialIndex(input)) == PostIME
                .getInitialChar(PostIME.getInitialIndex(str.charAt(index)))) {
              System.out.println(input + " " + str.charAt(index));
              if (matchingWords.contains(str) == false)
                matchingWords.add(str);
              index++;
              continue;
            } else {
              matchingWords.remove(str);
              break;
            }
          }
        }

      }
    }
    matchingWords.sort(wordComparator);
    return matchingWords;
  }
}
