package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;



public class AutoComplete extends JScrollPane implements KeyListener {
  private static final long serialVersionUID = 4592225249925286812L;

  private JTextPane editor;

  private JList<String> wordList;

  private Map<String, Integer> wordMap;

  private StringBuffer recentWord;
  private int recentWordPos;

  public AutoComplete(JTextPane textPane) {
    editor = textPane;
    editor.addKeyListener(this);
    editor.add(this);

    setVisible(false);
    setOpaque(true);
    setSize(100, 280);
    setBackground(Color.WHITE);
    EtchedBorder outer = new EtchedBorder(EtchedBorder.LOWERED);
    EmptyBorder inner = new EmptyBorder(2, 3, 2, 3);
    setBorder(new CompoundBorder(outer, inner));

    wordList = new JList<String>();
    wordList.setSize(getSize());
    wordList.setVisible(true);
    getViewport().add(wordList);

    wordMap = new TreeMap<String, Integer>();
    initRecentWord();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paint(java.awt.Graphics)
   */
  @Override
  public void paint(Graphics g) {
    super.paint(g);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        recentWordPos = Math.max(0, recentWordPos - 1);
        break;

      case KeyEvent.VK_RIGHT:
        recentWordPos = Math.min(recentWordPos + 1, recentWord.length());
        break;

      case KeyEvent.VK_ENTER:
      case KeyEvent.VK_SPACE:
        addWordList();
        break;
    }

    System.out.println(recentWordPos);

    /*
     * // 한글 ( 한글자 || 자음 , 모음 ) if ((0xAC00 <= c && c <= 0xD7A3) || (0x3131 <= c && c <= 0x318E)) {
     * System.out.println("k" + c); k++; } else if ((0x61 <= c && c <= 0x7A) || (0x41 <= c && c <=
     * 0x5A)) { // 영어 System.out.println("e:" + c); e++; } else if (0x30 <= c && c <= 0x39) { // 숫자
     * System.out.println("d" + c); d++; } else { System.out.println("z" + c); z = 0; }
     */
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (e.isConsumed() == false) {
      char ch = e.getKeyChar();

      // 한글 또는 영문인지 검사
      if ((0xAC00 <= ch && ch <= 0xD7A3) || (0x3131 <= ch && ch <= 0x318E)
          || (0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
        if (isShowing() == false) {
          showPopup();
        }
        appendRecentWord(e.getKeyChar());
      } else {
        setVisible(false);
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (e.isConsumed() == false) {
    }
  }

  private void refreshPopupLocation() throws BadLocationException {
    Rectangle anchor = editor.modelToView(editor.getCaretPosition());
    setLocation(anchor.x, anchor.y + anchor.height);
  }

  public void showPopup() {
    if (wordMap.size() > 0) {
      try {
        Set<String> temp = wordMap.keySet();
        wordList.setListData(new Vector<String>(temp));

        refreshPopupLocation();
        setVisible(true);
      } catch (BadLocationException e1) {
        e1.printStackTrace();
      }
    }
  }

  private void addWordList() {
    if (recentWord.length() > 0) {
      String wordKey = recentWord.toString();
      Integer wordCount = wordMap.get(wordKey);
      if (wordCount == null)
        wordCount = 1;
      else
        wordCount++;

      wordMap.put(wordKey, wordCount);
      initRecentWord();
    }
  }

  private void initRecentWord() {
    recentWord = new StringBuffer();
    recentWordPos = 0;
  }

  private void appendRecentWord(char ch) {
    recentWord.insert(recentWordPos, ch);
    recentWordPos++;
  }
}
