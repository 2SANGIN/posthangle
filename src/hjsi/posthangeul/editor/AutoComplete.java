package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.AttributedCharacterIterator;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;



/**
 * 단어 자동완성 클래스
 *
 * @author SANGIN
 */
public class AutoComplete implements KeyListener, InputMethodListener {
   /**
    * @param ch 검사할 글자
    * @return 영문 알파벳 소문자 혹은 대문자라면 true, 아니라면 false
    */
   public static boolean isEnglish(char ch) {
      return !((ch < 0x41 || 0x5A < ch) && (ch < 0x61 || 0x7A < ch));
   }

   public static boolean isEnglish(CharSequence str) {
      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (!isEnglish(ch))
            return false;
      }
      return true;
   }

   public static boolean isKorean(char ch) {
      if (ch < 0xAC00 || 0xD7A3 < ch)
         return false;
      return true;
   }

   public static boolean isKorean(CharSequence str) {
      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (ch < 0xAC00 || 0xD7A3 < ch)
            return false;
      }
      return true;
   }

   /**
    * 해당 글자가 한글 자모에 해당하는지 검사한다.
    *
    * @param ch 검사할 글자
    * @return 자음 혹은 모음이라면 true, 아니라면 false
    */
   public static boolean isKoreanAlphabet(char ch) {
      if (ch < 0x3131 || 0x318E < ch)
         return false;
      return true;
   }

   private static boolean isNumber(CharSequence str) {
      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (ch < 0x30 || 0x39 < ch)
            return false;
      }
      return true;
   }

   /**
    * 텍스트 에디터의 캐럿 포지션을 별도로 관리한다. 다른 메소드에서 캐럿의 최신 위치를 사용하기 위함.
    */
   private int caretPos = 0;

   /**
    * 조립이 끝나 완성된 한글 버퍼
    */
   private StringBuffer commBuf;

   /**
    * 완성된 한글 버퍼의 가상 캐럿 위치
    */
   private int commBufPos;

   /**
    * 자동완성 기능이 동작하게 될 대상 텍스트 에디터
    */
   private final JTextPane editor;

   /**
    * 자동완성 단어를 보여줄 스크롤 박스
    */
   private final JScrollPane popupBox;

   /**
    * 자동완성 단어를 보여줄 스크롤 박스 안의 리스트뷰
    */
   private final JList<String> popupList;

   /**
    * 조립 중인 한글을 위한 버퍼 (1글자 무조건)
    */
   private StringBuffer uncommBuf;

   /**
    * 현재 자동완성 입력도우미가 화면에 보여지고 있는가를 나타낸다.
    */
   private boolean visibleInputAssist = false;

   /**
    * 현재 입력하고 있는 단어 영역에 표시를 해준다.
    */
   private final JPanel wordBox;

   /**
    * 사용자가 입력했던 단어들을 저장, 검색 등 관리해주는 매니저
    */
   private final WordManager wordManager = new WordManager();

   {
      this.wordBox = new JPanel();
      this.wordBox.setVisible(false);
      this.wordBox.setOpaque(false);
      this.wordBox.setBorder(new LineBorder(Color.RED, 1));
   }

   /**
    * @param textPane 자동완성 기능을 이용할 텍스트 에디터
    */
   public AutoComplete(JTextPane textPane) {
      /* 기타 객체 생성 */
      this.initWordBuffers();

      /* scrollpane 내부 리스트뷰 설정 */
      this.popupList = new JList<>();
      this.popupList.setVisible(true);

      /* scrollpane 설정 */
      this.popupBox = new JScrollPane(this.popupList);
      this.popupBox.setVisible(false);
      this.popupBox.setOpaque(true);
      this.popupBox.setSize(100, 280);
      this.popupBox.setBackground(Color.WHITE);
      this.popupBox.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

      this.editor = textPane;
      this.editor.addKeyListener(this);
      this.editor.addInputMethodListener(this);
      this.editor.add(this.popupBox);
      this.editor.add(this.wordBox);
   }

   @Override
   public void caretPositionChanged(InputMethodEvent event) {}

   @Override
   public void inputMethodTextChanged(InputMethodEvent event) {
      // System.out.println(event.paramString());
      this.setCaretPos(this.editor.getCaretPosition()); // 일단 에디터의 캐럿 위치와 일치시킴
      AttributedCharacterIterator str = event.getText();

      System.out.println("IME Event Occur!");
      if (str != null) {
         if (event.getCommittedCharacterCount() > 0) {
            this.appendCommitted(str.first());
            this.initUncommittedBuffer();
            System.out.println("committed : " + this.commBuf.charAt(this.commBufPos - 1));
         } else if (str.getEndIndex() > 0) {
            this.updateUncommitted(str.last());
            System.out.println("uncommitted : " + this.uncommBuf);
         } else {
            this.initUncommittedBuffer();
            System.out.println("uncommitted : " + this.uncommBuf);
         }
         System.out.println(this.logString());
      } else {
         this.initUncommittedBuffer();
         System.out.println("it's null! \n");
      }

      if (this.isShowingInputAssist()) {
         this.refreshInputAssist();
      } else
         this.showInputAssist();
   }

   @Override
   public void keyPressed(KeyEvent e) {
      char ch = e.getKeyChar();
      int code = e.getKeyCode();
      this.setCaretPos(this.editor.getCaretPosition()); // 일단 에디터의 캐럿 위치와 일치시킴

      if (this.processCharacterKeys(ch)) {
         if (this.isShowingInputAssist()) {
            this.refreshInputAssist();
         } else {
            this.showInputAssist();
         }
      } else if (this.processArrowKeys(code)) {
         if (this.isShowingInputAssist()) {
            this.refreshInputAssist();
         }
      } else {
         switch (code) {
            case KeyEvent.VK_ESCAPE:
               if (this.isShowingInputAssist())
                  this.hideInputAssist();
               else
                  this.initWordBuffers();
               break;

            case KeyEvent.VK_BACK_SPACE:
               this.backspaceCommitted();
               break;

            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
               this.wordManager.countWord(this.commBuf.toString());
               this.initWordBuffers();
               this.hideInputAssist();
               break;

            case KeyEvent.VK_TAB:
               // TODO 현재 선택된 단어를 replace한다!
               // 카운트도 증가시킨다!
               break;

            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_ALT:
               // 아무것도 안 한다. default로 가기 전 최후의 필터링.
               break;

            default:
               this.hideInputAssist(); // 기타 엉뚱한 키가 들어오면 팝업박스를 가린다.
               System.out.println(e.paramString()); // 뭐가 들어왔는지 확인은 해야지
         }
      }

      if (code != KeyEvent.VK_ENTER && code != KeyEvent.VK_SPACE)
         System.out.println(this.logString());
   }

   @Override
   public void keyReleased(KeyEvent e) {
      // System.out.println(e.paramString());
   }

   @Override
   public void keyTyped(KeyEvent e) {
      // System.out.println(e.paramString());
   }

   /**
    * @return 버퍼에 관련한 로그 문자열
    */
   public String logString() {
      StringBuffer logStr = new StringBuffer();
      logStr.append("-----------------------------------------\n");
      logStr.append(" logString() \n");
      logStr.append("-----------------------------------------\n");
      logStr.append("  commBuf: \"" + this.commBuf.toString() + "\"\n");
      logStr.append("uncommBuf: \"" + this.uncommBuf.toString() + "\"\n");
      logStr.append("wordToSearch: \"" + this.getWordToSearch() + "\"\n");
      logStr.append("length: " + this.getWordToSearch().length() + "\n");
      logStr.append("buf caret pos: " + this.commBufPos + "\n");
      logStr.append("-----------------------------------------\n");
      if (this.getWordToSearch().length() > 0)
         return logStr.toString();
      return "";
   }

   /**
    * 조립 완성된 글자 버퍼에 글자를 추가한다.
    *
    * @param ch 추가할 글자
    */
   private void appendCommitted(char ch) {
      if (this.commBufPos > this.commBuf.length())
         this.commBuf.append(ch);
      else {
         this.commBuf.insert(this.commBufPos, ch);
         this.commBufPos++;
         this.setCaretPos(this.getCaretPos() + 1);
      }
   }

   /**
    * 조립 완성된 글자 버퍼에서 현재 commBufPos가 가리키는 위치의 글자를 지운다.
    */
   private void backspaceCommitted() {
      if (this.commBufPos > 0) {
         this.commBuf.delete(this.commBufPos - 1, this.commBufPos);
         this.commBufPos--;
      }
   }

   /**
    * @return the caretPos
    */
   private int getCaretPos() {
      return this.caretPos;
   }

   /**
    * 자동완성 단어 검색에 사용 될 단어를 완성글자 버퍼를 가져오거나 조립글자 버퍼까지 합쳐서 가져온다.
    *
    * @return 자동완성 검색에 사용 될 단어
    */
   private String getWordToSearch() {
      if (this.uncommBuf.length() > 0)
         return this.commBuf.toString() + this.uncommBuf.toString();
      return this.commBuf.toString();
   }

   /**
    * 팝업박스와 워드박스를 보이지 않게 한다.
    */
   private void hideInputAssist() {
      this.visibleInputAssist = false;
      this.popupBox.setVisible(false);
      this.wordBox.setVisible(false);
   }

   /**
    * 조립글자 버퍼를 지우고 새 버퍼를 할당한다.
    */
   private void initUncommittedBuffer() {
      this.uncommBuf = new StringBuffer();
   }

   /**
    * 완성글자 버퍼를 지우고 새 버퍼를 할당한다. 완성글자 캐럿 포지션도 초기화하고, 조립글자 버퍼도 초기화한다.
    */
   private void initWordBuffers() {
      this.commBuf = new StringBuffer();
      this.commBufPos = 0;
      this.initUncommittedBuffer();
   }

   /**
    * @return 현재 입력도우미가 보여지고 있으면 true, 아니라면 false
    */
   private boolean isShowingInputAssist() {
      return this.visibleInputAssist;
   }

   /**
    * 방향키를 입력 받으면 버퍼의 캐럿 및 기타 동작을 해당 방향키에 맞게 처리한다.
    *
    * @param keyCode 가상 키코드
    * @return 방향키를 처리했다면 true, 그 이외의 키는 false
    */
   private boolean processArrowKeys(int keyCode) {
      boolean isConsumed = false;
      if (keyCode == KeyEvent.VK_LEFT) {
         System.out.println("left key pressed!");
         this.setCaretPos(this.getCaretPos() - 1);
         this.commBufPos--;
         if (this.commBufPos < 0) {
            this.initWordBuffers();
            this.hideInputAssist();
         }
         isConsumed = true;
      } else if (keyCode == KeyEvent.VK_RIGHT) {
         System.out.println("right key pressed!");
         if (this.getWordToSearch().length() == this.commBufPos) {
            this.wordManager.countWord(this.getWordToSearch());
            this.initWordBuffers();
            this.hideInputAssist();
         } else {
            this.setCaretPos(this.getCaretPos() + 1);
            this.commBufPos++;
         }
         isConsumed = true;
      }
      return isConsumed;
   }

   /**
    * 한글이나 알파벳 입력에 대한 처리를 한다.
    *
    * @param keyChar 글자 코드 값
    * @return 한글이나 알파벳에 대한 처리를 했으면 true, 그 이외의 키는 false
    */
   private boolean processCharacterKeys(char keyChar) {
      boolean isConsumed = false;
      if ((0xAC00 <= keyChar && keyChar <= 0xD7A3) || (0x3131 <= keyChar && keyChar <= 0x318E)) {
         // 한글의 경우이긴 하지만 들어올 일 없을 거임 (inputMethodTextChanged에서 걸러짐)
         isConsumed = true;
      } else if ((0x61 <= keyChar && keyChar <= 0x7A) || (0x41 <= keyChar && keyChar <= 0x5A)) {
         this.appendCommitted(keyChar);
         isConsumed = true;
      }
      return isConsumed;
   }

   /**
    * 워드박스의 위치, 크기를 재계산하고 단어 리스트를 다시 가져온다.
    */
   private void refreshInputAssist() {
      this.refreshWordList();
      this.refreshWordBox();
   }

   /**
    * 팝업박스의 위치를 에디터 내의 캐럿 위치를 기준으로 계산한다.
    */
   private void refreshPopupLocation() {
      try {
         Rectangle anchor = this.editor.modelToView(this.getCaretPos());
         this.popupBox.setLocation(anchor.x, anchor.y + anchor.height);
      } catch (BadLocationException e) {
         e.printStackTrace();
      }
   }

   /**
    * 워드박스의 위치를 에디터 내의 캐럿 위치를 기준으로 계산한다.
    */
   private void refreshWordBox() {
      // 현재 문단의 폰트 속성 조사
      AttributeSet attrSet = this.editor.getParagraphAttributes();
      String fontFamily = StyleConstants.getFontFamily(attrSet);
      int fontStyle = (StyleConstants.isBold(attrSet) ? Font.BOLD : Font.PLAIN)
            | (StyleConstants.isItalic(attrSet) ? Font.ITALIC : Font.PLAIN);
      int fontSize = StyleConstants.getFontSize(attrSet);

      // 조사된 속성으로 폰트 객체를 만들고, 입력단어의 길이를 측정함
      Font font = new Font(fontFamily, fontStyle, fontSize);
      FontMetrics metric = this.editor.getFontMetrics(font);
      String wordToSearch = this.getWordToSearch();
      int widthAll = metric.stringWidth(wordToSearch);
      int widthAfterCaret = 0;
      if (!wordToSearch.isEmpty() && wordToSearch.length() != this.commBufPos) {
         String endStr = wordToSearch.substring(this.commBufPos);
         widthAfterCaret = metric.stringWidth(endStr);
         System.out.println("endStr: \"" + endStr + "\"");
      }

      // 에디터의 현재 캐럿 위치로부터 입력단어에 해당하는 영역을 계산함
      try {
         System.out.println("Text Length: " + this.editor.getText().length() + ", Caret Pos: "
               + this.getCaretPos() + ", real caret pos: " + this.editor.getCaretPosition());
         System.out
               .println("magic caret: " + this.editor.getCaret().getMagicCaretPosition() + " \n");
         Rectangle rect;
         Point pos = this.editor.getCaret().getMagicCaretPosition();
         if (pos != null) {
            rect = new Rectangle(pos.x, pos.y, 0, 20);
         } else {
            if (false)
               rect = this.editor.modelToView(this.getCaretPos());
            rect = new Rectangle(0, 0, 0, 0);
         }
         System.out.println("x: " + rect.x + ", widthAll: " + widthAll + ", widthAfterCaret: "
               + widthAfterCaret);
         rect.translate(-(widthAll - widthAfterCaret), 0);
         rect.setSize(widthAll, rect.height);
         this.wordBox.setBounds(rect);
      } catch (BadLocationException e) {
         e.printStackTrace();
      }
   }

   /**
    * 팝업박스 내에 보여줄 단어 리스트를 현재 입력단어 기준으로 다시 가져온다.
    */
   private void refreshWordList() {
      Vector<String> matchings = this.wordManager.getMatchingWords(this.getWordToSearch());
      this.popupList.setListData(matchings);
   }

   /**
    * @param caretPos the caretPos to set
    */
   private void setCaretPos(int caretPos) {
      if (caretPos >= 0 && caretPos <= this.editor.getText().length())
         this.caretPos = caretPos;
   }

   /**
    * 팝업박스와 워드박스를 보여준다. 팝업박스 리스트의 단어를 현재 입력된 단어 기준으로 다시 가져오고 위치를 재계산해서 에디터 내에 보여준다. 워드박스 또한 입력된 단어를
    * 감싸는 사각형 영역을 재계산해서 보여준다.
    */
   private void showInputAssist() {
      this.visibleInputAssist = true;
      this.refreshWordList();
      this.refreshPopupLocation();
      this.popupBox.setVisible(true);
      this.refreshWordBox();
      this.wordBox.setVisible(true);
   }

   /**
    * 자바 IME로부터 조립 글자를 받아서 조립글자 버퍼에 덮어쓴다.
    *
    * @param ch IME에서 조립 중인 글자
    */
   private void updateUncommitted(char ch) {
      if (this.uncommBuf.length() > 0)
         this.uncommBuf.setCharAt(0, ch);
      else {
         this.uncommBuf.append(ch);
      }
   }
}
