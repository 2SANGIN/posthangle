package hjsi.posthangeul.editor;

import java.util.Map;
import java.util.TreeMap;

public class WordManager {
  private Map<String, Integer> wordMap;
  private Map<String, String> historyMap;

  public WordManager() {
    wordMap = new TreeMap<String, Integer>();
    historyMap = new TreeMap<String, String>();
  }
}
