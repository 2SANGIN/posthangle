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
  private int oldCaretPos;

  private JList<String> listView;

  private StringBuffer inputWord;
  private int inputWordPos;
  
  private WordManager wordManager = new WordManager();

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

    listView = new JList<String>();
    listView.setSize(getSize());
    listView.setVisible(true);
    getViewport().add(listView);

    initInputWord();
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
      case KeyEvent.VK_ESCAPE:
        if (isShowing() == false) {
          initInputWord();
        }
        break;

      case KeyEvent.VK_BACK_SPACE:
        if (inputWordPos > 0)
          inputWord.deleteCharAt(inputWordPos - 1);
      case KeyEvent.VK_LEFT:
        if ((e.getModifiers() & KeyEvent.ALT_MASK) > 0)
          inputWordPos = 0;
        else
          inputWordPos = Math.max(0, inputWordPos - 1);
        break;

      case KeyEvent.VK_RIGHT:
        if ((e.getModifiers() & KeyEvent.ALT_MASK) > 0)
          inputWordPos = inputWord.length();
        else
          inputWordPos = Math.min(inputWordPos + 1, inputWord.length());
        break;

      case KeyEvent.VK_HOME:
        // TODO old, new 캐럿 위치 사이에 공백 있는지 검사해야함
        break;

      case KeyEvent.VK_END:
        inputWordPos = inputWord.length();
        break;

      case KeyEvent.VK_ENTER:
      case KeyEvent.VK_SPACE:
        inputWord.delete(inputWordPos, inputWord.length());
        wordManager.countWord(inputWord);
        initInputWord();
        break;
    }

    System.out.println(inputWordPos);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (e.isConsumed() == false) {
      char ch = e.getKeyChar();
      if ((0xAC00 <= ch && ch <= 0xD7A3) || (0x3131 <= ch && ch <= 0x318E)
          || (0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
        appendInputWord(e.getKeyChar());
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
    try {
      Vector<String> matchings = wordManager.getMatchingWords(inputWord);
      listView.setListData(matchings);
      refreshPopupLocation();
      setVisible(true);
    } catch (BadLocationException e1) {
      e1.printStackTrace();
    }
  }

  private void initInputWord() {
    inputWord = new StringBuffer();
    inputWordPos = 0;
  }

  private void appendInputWord(char ch) {
    inputWord.insert(inputWordPos, ch);
    inputWordPos++;
  }

  private boolean isKorean(CharSequence str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 0xAC00 || 0xD7A3 < ch)
        return false;
    }
    return true;
  }

  private boolean isKoreanAlphabet(CharSequence str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 0x3131 || 0x318E < ch)
        return false;
    }
    return true;
  }

  private boolean isEnglish(CharSequence str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if ((ch < 0x41 || 0x5A < ch) && (ch < 0x61 || 0x7A < ch))
        return false;
    }
    return true;
  }

  private boolean isNumber(CharSequence str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 0x30 || 0x39 < ch)
        return false;
    }
    return true;
  }
}
