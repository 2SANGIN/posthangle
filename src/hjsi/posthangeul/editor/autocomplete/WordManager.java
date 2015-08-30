package hjsi.posthangeul.editor.autocomplete;

import java.io.File;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import hjsi.posthangeul.Database.DBManager;
import hjsi.posthangeul.editor.PostIME;
import hjsi.posthangeul.window.PostHangeulApp;

public class WordManager {
   private Map<String, String> historyLast;
   private Map<String, Vector<String>> historyTop10;
   /**
    * 저장된 단어를 빈도 내림차순으로 정렬할 수 있도록 비교 방법을 가진 객체
    */
   private final Comparator<String> wordComparator =
         (key1, key2) -> (this.getWordCounter().get(key2).intValue()
               - this.getWordCounter().get(key1).intValue());

   /**
    * 단어와 카운트를 가진 맵
    */
   private Map<String, Integer> wordCounter;

   /**
    * DB 관리 객체
    */
   private final DBManager dbManager;

   {
      File dbPath = new File(PostHangeulApp.appPath, "userdic.db");
      this.dbManager = new DBManager(dbPath.toString());

      try {
         this.setWordCounter(this.dbManager.loadAllWords());
      } catch (SQLException e) {
         e.printStackTrace();
      }
      this.historyTop10 = new TreeMap<>();
      this.historyLast = new TreeMap<>();
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

   @SuppressWarnings("boxing")
   public void countWord(String inputWord) {
      if (inputWord.length() > 1) {
         // 완성된 글자가 아니면 wordCounter에 넣지 않음
         for (char ch : inputWord.toCharArray())
            if (AutoComplete.isKoreanAlphabet(ch))
               return;

         Integer count = this.getWordCounter().get(inputWord);
         if (count != null) {
            count++;
            /* db 단어 카운트 갱신 */
            try {
               this.dbManager.updateCount(inputWord);
            } catch (SQLException e) {
               e.printStackTrace();
            }
         } else {
            count = 1;
            /* db에 단어 삽입 */
            try {
               this.dbManager.insertWord(inputWord);
            } catch (SQLException e) {
               e.printStackTrace();
            }
         }
         this.getWordCounter().put(inputWord.toString(), count);
         System.out.println("Counted Word: \"" + inputWord.toString() + "\", count: " + count);
      }
   }

   /**
    * @param inputWord
    * @return
    */
   public Vector<String> getMatchingWords(String inputWord) {
      Vector<String> matchingWords = new Vector<>();
      for (String str : this.getWordCounter().keySet()) {
         if (str.length() < inputWord.length())
            continue;

         else if (str.startsWith(inputWord) || (inputWord.length() == 0))
            matchingWords.add(str);

         // 초성을 포함하는지
         // matching words에는 완성된 글자만 들어가있음
         // inputWords에는 영어 / 초성 / 완성된한글 의 조합들로 구성

         else if (this.isKorean(str)) {
            // index 문자열 한 글자씩 쪼개서 비교
            for (int index = 0; index < inputWord.length(); index++) {
               char input = inputWord.charAt(index);
               System.out.println("input length" + inputWord.length());

               // 초성만 입력
               if (this.isKoreanAlphabet(input)) {
                  // 1. 저장된 문자열의 초성과 다른 경우, 그 다음 글자는 볼 필요도 없음
                  if (input != this.getInitial(str.charAt(index))) {
                     if (matchingWords.contains(str))
                        matchingWords.remove(str);
                     System.out.println("case 1");
                     break;
                  }
                  if (matchingWords.contains(str) == false)
                     matchingWords.add(str);
               }

               // 초성+중성 혹은 초성+중성+종성
               else if (this.isKorean(input)) {
                  // 2. 초성끼리 비교해서 다르면 그 다음 글자는 볼 필요도 없음
                  if (this.getInitial(input) != this.getInitial(str.charAt(index))) {
                     System.out.println("case 2");
                     if (matchingWords.contains(str))
                        matchingWords.remove(str);
                     break;
                  }

                  // 3. 중성끼리 비교해서 다르면 그 다음 글자는 볼 필요도 없음
                  else if (this.getMedial(input) != this.getMedial(str.charAt(index))) {
                     System.out.println("case 3");
                     if (matchingWords.contains(str))
                        matchingWords.remove(str);
                     break;
                  }

                  // 4. 둘다 종성이 있는데 다르면 그 다음 글자는 볼 필요도 없음
                  else if (this.hasFinal(input) != 0 && this.hasFinal(str.charAt(index)) != 0
                        && this.getFinal(input) != this.getFinal(str.charAt(index))) {
                     System.out.println("case 4");
                     if (matchingWords.contains(str))
                        matchingWords.remove(str);
                     break;
                  }

                  // 5. input은 종성이 있고 str은 종성이 없는 경우
                  else if (this.hasFinal(input) != 0 && this.hasFinal(str.charAt(index)) == 0) {
                     // input의 종성이 str 다음 글자의 초성과 다른 경우
                     if (inputWord.length() < str.length()) {
                        if (this.getFinal(input) != this.getInitial(str.charAt(index + 1))) {
                           System.out.println("case 5");
                           if (matchingWords.contains(str))
                              matchingWords.remove(str);
                           break;
                        }
                     }
                  }
                  if (matchingWords.contains(str) == false)
                     matchingWords.add(str);
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
      try {
         // db에서 제거
         this.dbManager.deleteWord(targetWord);
      } catch (SQLException e) {
         e.printStackTrace();
      }
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

   /**
    * if it has no final, return 0
    */
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
