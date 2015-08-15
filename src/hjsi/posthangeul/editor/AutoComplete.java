package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;
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

  private int oldCaretPos;
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
    System.out.println(getInsets().toString());
    EtchedBorder outer = new EtchedBorder(EtchedBorder.LOWERED);
    EmptyBorder inner = new EmptyBorder(2, 3, 2, 3);
    setBorder(new CompoundBorder(outer, inner));
    System.out.println(getInsets().toString());

    wordList = new JList<String>();
    wordList.setSize(getSize());
    wordList.setVisible(true);
    getViewport().add(wordList);

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
    int oldPos = editor.getCaretPosition();
    int newPos;

    switch (e.getKeyCode()) {
      case KeyEvent.VK_ESCAPE:
        if (isShowing() == false) {
          initRecentWord();
        }
        break;

      case KeyEvent.VK_BACK_SPACE:
        if (recentWordPos > 0)
          recentWord.deleteCharAt(recentWordPos - 1);
      case KeyEvent.VK_LEFT:
        if ((e.getModifiers() & KeyEvent.ALT_MASK) > 0)
          recentWordPos = 0;
        else
          recentWordPos = Math.max(0, recentWordPos - 1);
        break;

      case KeyEvent.VK_RIGHT:
        if ((e.getModifiers() & KeyEvent.ALT_MASK) > 0)
          recentWordPos = recentWord.length();
        else
          recentWordPos = Math.min(recentWordPos + 1, recentWord.length());
        break;

      case KeyEvent.VK_HOME:
        recentWordPos = 0;
        break;

      case KeyEvent.VK_END:
        recentWordPos = recentWord.length();
        break;

      case KeyEvent.VK_ENTER:
      case KeyEvent.VK_SPACE:
        recentWord.delete(recentWordPos, recentWord.length());
        addWordList();
        break;
    }

    System.out.println(recentWordPos);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (e.isConsumed() == false) {
      char ch = e.getKeyChar();
      if ((0xAC00 <= ch && ch <= 0xD7A3) || (0x3131 <= ch && ch <= 0x318E)
          || (0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
        appendRecentWord(e.getKeyChar());
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (e.isConsumed() == false) {
      char ch = e.getKeyChar();
      // 한글 또는 영문인지 검사
      if ((0xAC00 <= ch && ch <= 0xD7A3) || (0x3131 <= ch && ch <= 0x318E)
          || (0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
        if (isShowing() == false) {
          showPopup();
        }
      } else {
        setVisible(false);
      }
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
    if (recentWord.length() > 1) {
      String wordKey = recentWord.toString();
      Integer wordCount = wordMap.get(wordKey);
      if (wordCount == null)
        wordCount = 1;
      else
        wordCount++;
      wordMap.put(wordKey, wordCount);
    }
    initRecentWord();
  }

  private void initRecentWord() {
    recentWord = new StringBuffer();
    recentWordPos = 0;
  }

  private void appendRecentWord(char ch) {
    recentWord.insert(recentWordPos, ch);
    recentWordPos++;
  }

  private boolean isKorean(String str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 0xAC00 || 0xD7A3 < ch)
        return false;
    }
    return true;
  }

  private boolean isKoreanAlphabet(String str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 0x3131 || 0x318E < ch)
        return false;
    }
    return true;
  }

  private boolean isEnglish(String str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if ((ch < 0x41 || 0x5A < ch) && (ch < 0x61 || 0x7A < ch))
        return false;
    }
    return true;
  }

  private boolean isNumber(String str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 0x30 || 0x39 < ch)
        return false;
    }
    return true;
  }
}
