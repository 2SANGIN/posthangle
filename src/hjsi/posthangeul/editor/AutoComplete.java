package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.AttributedCharacterIterator;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;



public class AutoComplete extends JScrollPane implements KeyListener, InputMethodListener {
  private static final long serialVersionUID = 4592225249925286812L;

  private JTextPane editor;

  private JList<String> listView;

  private PostIME ime = new PostIME();

  private boolean isComposition = false;
  private String lastInputWord;
  private StringBuffer inputWord;
  private int inputWordPos;

  private WordManager wordManager = new WordManager();

  public AutoComplete(JTextPane textPane) {
    editor = textPane;
    editor.addKeyListener(this);
    editor.addInputMethodListener(this);
    editor.add(this);

    /* scrollpane 설정 */
    setVisible(false);
    setOpaque(true);
    setSize(100, 280);
    setBackground(Color.WHITE);
    System.out.println(getInsets().toString());
    EtchedBorder outer = new EtchedBorder(EtchedBorder.LOWERED);
    setBorder(outer);
    System.out.println(getInsets().toString());

    /* scrollpane 내부 리스트뷰 설정 */
    listView = new JList<String>();
    listView.setSize(getSize());
    listView.setVisible(true);
    getViewport().add(listView);

    /* 기타 객체 생성 */
    lastInputWord = new String();
    initInputWord();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    System.out.println(e.paramString());
    char ch = e.getKeyChar();
    int code = e.getKeyCode();


    if ((0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
      appendInputWord(ch);
    }

    switch (code) {
      case KeyEvent.VK_ESCAPE:
        if (isShowing())
          setVisible(false);
        else
          initInputWord();
        break;

      case KeyEvent.VK_BACK_SPACE:
        backspaceInputWord();
        System.out.println(inputWord.toString());
        break;

      case KeyEvent.VK_ENTER:
      case KeyEvent.VK_SPACE:
        System.out.println(inputWord);
        if (inputWord.length() > 1) {
          wordManager.countWord(inputWord.toString());
        }
        initInputWord();
        break;

      /*
       * default: if (e.isConsumed() == false) { ch = e.getKeyChar(); // 한글 또는 영문인지 검사 if ((0xAC00
       * <= ch && ch <= 0xD7A3) || (0x3131 <= ch && ch <= 0x318E) || (0x61 <= ch && ch <= 0x7A) ||
       * (0x41 <= ch && ch <= 0x5A)) { if (isShowing() == false) { showPopup(); } } else {
       * setVisible(false); } }
       */
    }

  }

  @Override
  public void keyTyped(KeyEvent e) {
    // System.out.println(e.paramString());
  }

  @Override
  public void keyReleased(KeyEvent e) {
    System.out.println(e.paramString());

    // char ch = e.getKeyChar();
    // if (editor.getInputContext().isCompositionEnabled()) {
    // if (isEnglish(ch)) {
    // if ((e.getModifiers() & KeyEvent.SHIFT_MASK) > 0) {
    // if (ch == 'q' || ch == 'w' || ch == 'e' || ch == 'r' || ch == 't' || ch == 'o'
    // || ch == 'p')
    // ch = Character.toUpperCase(ch);
    // }
    // ime.processInput(ch);
    // ime.printUncommittedStack();
    // ime.printOutStack();
    // }
    // }

    /*
     * StringBuffer log = new StringBuffer(); for (int i = 0; i < inputWord.length(); i++) { if (i
     * == inputWordPos) log.append('|'); log.append(inputWord.charAt(i)); System.out.println(log);
     * System.out.println(inputWordPos); }
     */
  }


  private void refreshPopupLocation() throws BadLocationException {
    Rectangle anchor = editor.modelToView(editor.getCaretPosition());
    setLocation(anchor.x, anchor.y + anchor.height);
  }

  public void showPopup() {
    try {
      Vector<String> matchings = wordManager.getMatchingWords(inputWord.toString());
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

  private void overwriteInputWord(char ch) {
    if (inputWordPos >= 0 && inputWordPos < inputWord.length())
      inputWord.setCharAt(inputWordPos, ch);
    else {
      inputWord.append(ch);
    }
  }

  private void backspaceInputWord() {
    if (inputWordPos > 0) {
      inputWord.delete(inputWordPos - 1, inputWordPos);
      inputWordPos--;
    }
  }

  @Override
  public void inputMethodTextChanged(InputMethodEvent event) {
    System.out.println(event.paramString());
    AttributedCharacterIterator str = event.getText();
    if (str != null) {
      if (event.getCommittedCharacterCount() > 0) {
        System.out.print("committed : ");
        for (int i = 0; i < event.getCommittedCharacterCount(); i++) {
          System.out.print(str.setIndex(i));
        }
        System.out.print("\nuncommitted : ");
        for (int i = event.getCommittedCharacterCount(); i < str.getEndIndex(); i++) {
          System.out.print(str.setIndex(i));
        }
        overwriteInputWord(str.first());
        inputWordPos++;
      } else {
        System.out.print("uncommitted : " + str.last());
        overwriteInputWord(str.last());
      }

      System.out.println("\ncount " + str.getEndIndex());
    }


    System.out.println("inputword : " + inputWord.toString());
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
      if (!isEnglish(ch))
        return false;
    }
    return true;
  }

  private boolean isEnglish(char ch) {
    return !((ch < 0x41 || 0x5A < ch) && (ch < 0x61 || 0x7A < ch));
  }

  private boolean isNumber(CharSequence str) {
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 0x30 || 0x39 < ch)
        return false;
    }
    return true;
  }

  @Override
  public void caretPositionChanged(InputMethodEvent event) {
  }
}
