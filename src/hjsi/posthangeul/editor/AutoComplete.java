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
    System.out.println(e.toString());
    char ch = e.getKeyChar();
    if ((0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
      appendInputWord(ch);
    }

    switch (e.getKeyCode()) {
      case KeyEvent.VK_ESCAPE:
        if (isShowing())
          setVisible(false);
        break;

      case KeyEvent.VK_BACK_SPACE:
        if (isComposition == false) {
          backspaceInputWord();
        }
        break;

      case KeyEvent.VK_ENTER:
      case KeyEvent.VK_SPACE:
        try {
          int i = editor.getCaretPosition() - 1;
          if (i > 0) {
            String text = editor.getText(0, i);
            while (i > 0) {
              ch = text.charAt(--i);
              if (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r')
                break;
              else
                inputWord.insert(0, ch);
            }

            if (lastInputWord.compareTo(inputWord.toString()) != 0) {
              lastInputWord = inputWord.toString();
              wordManager.countWord(inputWord.toString());
            }
          }
        } catch (BadLocationException e1) {
          e1.printStackTrace();
        }
        initInputWord();
        break;

      default:
        if (e.isConsumed() == false) {
          ch = e.getKeyChar();
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
  }

  @Override
  public void keyTyped(KeyEvent e) {
    System.out.println(e.toString());
  }

  @Override
  public void keyReleased(KeyEvent e) {
    System.out.println(e.toString());

    StringBuffer log = new StringBuffer();
    for (int i = 0; i < inputWord.length(); i++) {
      if (i == inputWordPos)
        log.append('|');
      log.append(inputWord.charAt(i));
      System.out.println(log);
      System.out.println(inputWordPos);
    }
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
    if (inputWord.length() > 0)
      inputWord.setCharAt(inputWordPos - 1, ch);
    else
      inputWord.append(ch);
  }

  private void backspaceInputWord() {
    if (inputWordPos - 1 >= 0) {
      inputWord.deleteCharAt(--inputWordPos);
    }
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

  @Override
  public void inputMethodTextChanged(InputMethodEvent event) {
    System.out.println(event.toString());
    AttributedCharacterIterator str = event.getText();
    if (str != null) {
      if (event.getCommittedCharacterCount() > 0) {
        overwriteInputWord(str.first());
        appendInputWord(str.last());
        isComposition = false;
      } else {
        overwriteInputWord(str.last());
        isComposition = true;
      }
    }
  }

  @Override
  public void caretPositionChanged(InputMethodEvent event) {}
}
