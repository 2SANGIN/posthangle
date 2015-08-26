package hjsi.posthangeul.editor;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class WordManager {
   private Map<String, String> historyLast;
   private Map<String, Vector<String>> historyTop10;
   /**
    * 저장된 단어를 빈도 내림차순으로 정렬할 수 있도록 비교 방법을 가진 객체
    */
   private final Comparator<String> wordComparator =
         (o1, o2) -> (this.getWordCounter().get(o2).intValue()
               - this.getWordCounter().get(o1).intValue());

   private Map<String, Integer> wordCounter;

   {
      this.setWordCounter(new TreeMap<String, Integer>());
      this.historyTop10 = new TreeMap<String, Vector<String>>();
      this.historyLast = new TreeMap<String, String>();

      // test용
      this.countWord("안녕하세요");
      this.countWord("안녕못해");
      this.countWord("안녕이라고");
      this.countWord("제발말하지마");
      this.countWord("오빠차뽑았다");
   }

   public void addWordAsHistory(String inputWord, String selectedWord) {
      this.countWord(selectedWord);
      this.historyLast.put(inputWord, selectedWord);

      Vector<String> historyList;
      if (this.historyTop10.containsKey(inputWord))
         historyList = this.historyTop10.get(inputWord);
      else
         historyList = new Vector<String>(10);

      if (!historyList.contains(selectedWord))
         historyList.add(selectedWord);

      historyList.sort(this.getWordComparator());
      while (historyList.size() > 10)
         historyList.remove(historyList.size() - 1);
   }

   public void countWord(String inputWord) {
      if (inputWord.length() > 1) {
         // 완성된 글자가 아니면 wordCounter에 넣지 않음
         for (char ch : inputWord.toCharArray())
            if (AutoComplete.isKoreanAlphabet(ch))
               return;

         Integer count = this.getWordCounter().get(inputWord);
         if (count != null)
            count++;
         else
            count = 1;
         this.getWordCounter().put(inputWord.toString(), count);
         System.out.println("Counted Word: \"" + inputWord.toString() + "\", count: " + count);
      }
   }

   /**
    * @param inputWord
    * @return
    */
   public Vector<String> getMatchingWords(String inputWord) {
      Vector<String> matchingWords = new Vector<String>();
      for (String str : this.getWordCounter().keySet()) {
         /*
          * TODO 여기에 검색 알고리즘을 넣어야함!! 초성 검색이나, 단어 일부를 포함하는 경우 등을 포함하는 검색 알고리즘
          */
         if (str.length() < inputWord.length())
            continue;

         else if (str.contains(inputWord) || str.startsWith(inputWord) || (inputWord.length() == 0))
            matchingWords.add(str);

         // 초성을 포함하는지
         // matching words에는 완성된 글자만 들어가있음
         // inputWords에는 영어 / 초성 / 완성된한글 의 조합들로 구성

         else if (this.isKorean(str)) {
            int index = 0;
            for (char input : inputWord.toCharArray()) {
               // 초성만 입력
               if (this.isKoreanAlphabet(input)) {
                  if (input == this.getInitial(str.charAt(index))) {
                     if (matchingWords.contains(str) == false)
                        matchingWords.add(str);
                     index++;
                     continue;
                  }
                  matchingWords.remove(str);
                  break;
                  // 조합된 한글 입력
               } else if (this.isKorean(input)) {
                  // input은 종성이 있고 str은 종성이 없는 경우
                  if (this.hasFinal(input) != 0 && this.hasFinal(str.charAt(index)) == 0) {
                     // input의 종성이 str 다음 글자의 초성과 같을 경우
                     if (inputWord.length() < str.length()) {
                        if (this.getFinal(input) == this.getInitial(str.charAt(index + 1))) {
                           matchingWords.add(str);
                           continue;
                        }
                     }
                     matchingWords.remove(str);
                     break;
                  }
                  // 종성이 다른 경우
                  else if (this.hasFinal(input) != 0 && this.hasFinal(str.charAt(index)) != 0) {
                     if (this.getFinal(input) != this.getFinal(str.charAt(index))) {
                        matchingWords.remove(str);
                        break;
                     }
                     // 초성과 중성 모두 같아야
                  } else if (this.getInitial(input) == this.getInitial(str.charAt(index))
                        && this.getMedial(input) == this.getMedial(str.charAt(index))) {

                     if (matchingWords.contains(str) == false)
                        matchingWords.add(str);
                     index++;
                     continue;
                  }
                  matchingWords.remove(str);
                  break;
               }
            }

         }
      }
      matchingWords.sort(this.getWordComparator());
      return matchingWords;
   }

   /**
    * @return wordComparator
    */
   public Comparator<String> getWordComparator() {
      return this.wordComparator;
   }

   /**
    * @return the wordCounter
    */
   public Map<String, Integer> getWordCounter() {
      return this.wordCounter;
   }

   /**
    * 메모리에 저장된 단어를 카운트 수에 상관 없이 제거한다.
    * 
    * @param targetWord 제거할 단어
    */
   public void removeWord(String targetWord) {
      this.wordCounter.remove(targetWord);
   }

   /**
    * @param wordCounter the wordCounter to set
    */
   public void setWordCounter(Map<String, Integer> wordCounter) {
      this.wordCounter = wordCounter;
   }

   private char getFinal(char ch) {
      return PostIME.getFinalChar(PostIME.getFinalIndex(ch));
   }

   private char getInitial(char ch) {
      return PostIME.getInitialChar(PostIME.getInitialIndex(ch));
   }

   private char getMedial(char ch) {
      return PostIME.getMedialChar(PostIME.getMedialIndex(ch));
   }

   // if it has no final, return 0
   private int hasFinal(char ch) {
      return PostIME.getFinalIndex(ch);
   }

   private boolean isKorean(char ch) {
      return AutoComplete.isKorean(ch);
   }

   private boolean isKorean(String str) {
      return AutoComplete.isKorean(str);
   }

   private boolean isKoreanAlphabet(char ch) {
      return AutoComplete.isKoreanAlphabet(ch);
   }
}
