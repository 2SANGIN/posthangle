package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    * 글자 ch가 알파벳 대소문자에 해당하는지 검사한다.
    *
    * @param ch 검사할 글자
    * @return 영문 알파벳 소문자 혹은 대문자라면 true, 아니라면 false
    */
   public static boolean isAlphabet(char ch) {
      return !((ch < 0x41 || 0x5A < ch) && (ch < 0x61 || 0x7A < ch));
   }

   /**
    * 문장 전체가 알파벳 대소문자에 해당하는지 검사한다.
    *
    * @param str 검사할 문장
    * @return 영문 대소문자로 이루어진 문장이라면 true, 아니라면 false
    */
   public static boolean isEnglish(CharSequence str) {
      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (!isAlphabet(ch))
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
    * 현재 처리 중인 keyChar
    */
   private char keyChar;

   /**
    * 현재 처리 중인 keyCode
    */
   private int keyCode;

   /**
    * 자동완성 단어를 보여줄 스크롤 박스
    */
   private final JScrollPane popupBox;
   /**
    * 조립 중인 한글을 위한 버퍼 (1글자 무조건)
    */
   private StringBuffer uncommBuf;

   /**
    * 현재 입력하고 있는 단어 영역에 표시를 해준다.
    */
   private final JPanel wordBox;

   /**
    * 단어가 입력되기 시작한 caret position
    */
   private int wordStartedCaretPos;

   /**
    * 자동완성 단어를 삭제하게 해줄 버튼 리스트뷰
    */
   final JList<String> deleteList;

   /**
    * 자동완성 단어를 보여줄 스크롤 박스 안의 리스트뷰
    */
   final JList<String> popupList;

   /**
    * 사용자가 입력했던 단어들을 저장, 검색 등 관리해주는 매니저
    */
   final WordManager wordManager = new WordManager();


   // class initializer
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

      /* popupBox 스크롤패널 내부 리스트뷰 설정 */
      this.popupList = new JList<>();
      this.popupList.setVisible(true);

      /* popupBox 스크롤패널 내부 삭제 버튼 설정 */
      this.deleteList = new JList<>();
      this.deleteList.setVisible(true);
      this.deleteList.setForeground(Color.LIGHT_GRAY);
      this.deleteList.setFocusable(false);
      this.deleteList.setSelectionBackground(this.deleteList.getBackground());
      this.deleteList.setSelectionForeground(this.deleteList.getForeground());
      this.deleteList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            String wordToRemove = AutoComplete.this.popupList.getModel()
                  .getElementAt(AutoComplete.this.deleteList.getSelectedIndex());
            System.out.println(wordToRemove);
            AutoComplete.this.wordManager.removeWord(wordToRemove);
            AutoComplete.this.refreshWordList();
         }
      });
      this.deleteList.setCursor(new Cursor(Cursor.HAND_CURSOR));

      /* 팝업리스트(단어) 및 삭제버튼 리스트를 묶음 */
      JPanel listPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
      listPanel.setOpaque(true);
      listPanel.setBackground(textPane.getBackground());
      listPanel.add(this.popupList);
      listPanel.add(this.deleteList);

      /* scrollpane 설정 */
      this.popupBox = new JScrollPane(listPanel);
      this.popupBox.setVisible(false);
      this.popupBox.setOpaque(true);
      this.popupBox.setBackground(textPane.getBackground());
      this.popupBox.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
      this.popupBox.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

      /* 에디터 설정 */
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
      System.out.println(e.paramString());
      this.keyChar = e.getKeyChar();
      this.keyCode = e.getKeyCode();
      boolean isCtrlDown = (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
      boolean isAltDown = (e.getModifiers() & InputEvent.ALT_MASK) == InputEvent.ALT_MASK;
      boolean isShiftDown = (e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
      boolean isMetaDown = (e.getModifiers() & InputEvent.META_MASK) == InputEvent.META_MASK;

      System.out.println("alt: " + isAltDown + ", ctrl: " + isCtrlDown + ", shift: " + isShiftDown
            + ", meta: " + isMetaDown);

      if (!isCtrlDown && !isAltDown && this.processCharacterKeys()) {
         if (this.isShowingInputAssist()) {
            this.refreshInputAssist();
         } else {
            this.showInputAssist();
         }
      } else if (this.processArrowKeys(e)) {
         if (this.isShowingInputAssist() && !e.isConsumed()) {
            this.refreshInputAssist();
         }
      } else {
         switch (this.keyCode) {
            case KeyEvent.VK_ESCAPE:
               if (this.wordBox.isShowing()) {
                  if (this.popupList.getSelectedIndex() < 0)
                     this.hideInputAssist();
                  else
                     this.popupList.clearSelection();
               } else
                  this.initWordBuffers();
               break;

            case KeyEvent.VK_DELETE:
               this.deleteCommitted();
               this.refreshInputAssist();
               break;

            case KeyEvent.VK_BACK_SPACE:
               // TODO 지우기 전 단어 길이(px) 조사해서 지우고 난 뒤의 길이와 비교, 그 차이만큼 왼쪽으로 이동시켜서 보정해줘야함
               this.refreshInputAssist();
               this.backspaceCommitted();
               break;

            case KeyEvent.VK_ENTER:
               if (this.popupBox.isShowing()) {
                  // 단어를 입력 중인데 캐럿 위치가 단어의 끝이 아니라면 엔터를 입력시 줄바꿈을 하지 않고, 단어의 끝으로 캐럿을 위치시킨다.
                  this.moveCaretAfterWord(this.getWordToSearch());
                  e.consume();
               }
               //$FALL-THROUGH$
            case KeyEvent.VK_SPACE:
               if (!isCtrlDown && !isAltDown) {
                  this.wordManager.countWord(this.getWordToSearch());
                  this.initWordBuffers();
                  this.hideInputAssist();
               } else {
                  this.showInputAssist();
                  e.consume();
                  System.out.println("space!!");
               }
               break;
            case KeyEvent.VK_TAB:
               if (this.isShowingInputAssist()) {
                  int index = this.popupList.getSelectedIndex();
                  if (index < 0)
                     index = 0;
                  if (index < this.popupList.getModel().getSize()) {
                     String wordToReplace = this.popupList.getModel().getElementAt(index);
                     if (this.getWordToSearch().compareTo(wordToReplace) != 0) {
                        /* replace */
                        int length = this.getWordToSearch().length();
                        AttributeSet attrSet = this.editor.getInputAttributes();
                        try {
                           this.editor.getDocument().remove(this.wordStartedCaretPos, length);
                           this.editor.getDocument().insertString(this.wordStartedCaretPos,
                                 wordToReplace, attrSet);
                        } catch (BadLocationException e1) {
                           e1.printStackTrace();
                        }
                     }
                     /* count */
                     this.wordManager.countWord(wordToReplace);
                     this.moveCaretAfterWord(wordToReplace);
                     e.consume();
                  } else {
                     this.wordManager.countWord(this.getWordToSearch());
                  }
               }
               this.initWordBuffers();
               this.hideInputAssist();
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

      if (this.keyCode != KeyEvent.VK_ENTER && this.keyCode != KeyEvent.VK_SPACE)
         System.out.println(this.logString());
   }

   @Override
   public void keyReleased(KeyEvent e) {
      // System.out.println(e.paramString());
      this.keyChar = e.getKeyChar();
      this.keyCode = e.getKeyCode();

      System.out.println("the end of log\n===============\n");
   }

   @Override
   public void keyTyped(KeyEvent e) {
      // System.out.println(e.paramString());
      this.keyChar = e.getKeyChar();
      this.keyCode = e.getKeyCode();
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
         if (this.keyCode != KeyEvent.VK_LEFT)
            this.commBufPos++;
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
    * 조립 완성된 글자 버퍼에서 현재 commBufPos가 가리키는 위치의 뒷글자를 지운다.
    */
   private void deleteCommitted() {
      if (this.commBufPos < this.commBuf.length()) {
         this.commBuf.delete(this.commBufPos, this.commBufPos + 1);
      }
   }

   /**
    * @return this.editor.getCaretPosition();
    */
   private int getCaretPos() {
      int caretPos = this.editor.getCaretPosition();
      if (this.keyCode == KeyEvent.VK_LEFT)
         caretPos -= 1;
      else if (this.keyCode == KeyEvent.VK_RIGHT)
         caretPos += 1;
      return caretPos;
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
      return this.popupBox.isShowing() && this.wordBox.isShowing();
   }

   /**
    * 현재 에디터의 캐럿 위치를 입력 된 단어의 뒤로 위치시킨다.
    *
    * @param word 캐럿 위치의 기준이 되는 단어
    */
   private void moveCaretAfterWord(String word) {
      System.out.println("<<moveCaretAfterWord>>");
      this.editor.setCaretPosition(this.wordStartedCaretPos + word.length());
   }

   /**
    * 방향키를 입력 받으면 버퍼의 캐럿 및 기타 동작을 해당 방향키에 맞게 처리한다.
    *
    * @param e 위, 아래 방향키 처리 후 consume 시키기 위한 KeyEvent
    * @return 방향키를 처리했다면 true, 그 이외의 키는 false
    */
   private boolean processArrowKeys(KeyEvent e) {
      boolean isConsumed = true;
      switch (this.keyCode) {
         case KeyEvent.VK_LEFT:
            System.out.println("LEFT KEY PRESSED!");
            if (this.uncommBuf.length() <= 0)
               this.commBufPos--;
            else {
               this.commBufPos++;
            }
            if (this.commBufPos < 0) {
               this.initWordBuffers();
               this.hideInputAssist();
            }
            break;

         case KeyEvent.VK_RIGHT:
            System.out.println("RIGHT KEY PRESSED!");
            if (this.getWordToSearch().length() == this.commBufPos) {
               this.wordManager.countWord(this.getWordToSearch());
               this.initWordBuffers();
               this.hideInputAssist();
            } else {
               this.commBufPos++;
            }
            break;

         case KeyEvent.VK_UP:
            if (this.isShowingInputAssist()) {
               int index = this.popupList.getSelectedIndex();
               if (index < 0)
                  index = 0;
               else {
                  final int size = this.popupList.getModel().getSize();
                  index = (index + size - 1) % size;
               }
               this.popupList.setSelectedIndex(index);
               e.consume();
            } else {
               this.hideInputAssist();
               this.initWordBuffers();
            }
            break;

         case KeyEvent.VK_DOWN:
            if (this.isShowingInputAssist()) {
               int index = this.popupList.getSelectedIndex();
               if (index < 0)
                  index = 0;
               else {
                  final int size = this.popupList.getModel().getSize();
                  index = (index + 1) % size;
               }
               this.popupList.setSelectedIndex(index);
               e.consume();
            } else {
               this.hideInputAssist();
               this.initWordBuffers();
            }
            break;

         default:
            isConsumed = false;
      }
      return isConsumed;
   }

   /**
    * 한글이나 알파벳 입력에 대한 처리를 한다.
    *
    * @return 한글이나 알파벳에 대한 처리를 했으면 true, 그 이외의 키는 false
    */
   private boolean processCharacterKeys() {
      boolean isConsumed = false;
      if ((0xAC00 <= this.keyChar && this.keyChar <= 0xD7A3)
            || (0x3131 <= this.keyChar && this.keyChar <= 0x318E)) {
         // 한글의 경우이긴 하지만 들어올 일 없을 거임 (inputMethodTextChanged에서 걸러짐)
         isConsumed = true;
      } else if (isAlphabet(this.keyChar)) {
         this.appendCommitted(this.keyChar);
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
         Rectangle anchor = this.editor.modelToView(this.wordStartedCaretPos);
         this.popupBox.setLocation(anchor.x, anchor.y + anchor.height + 2);
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

      // 에디터의 현재 캐럿 위치로부터 입력단어에 해당하는 영역을 계산함
      try {
         System.out.print("Caret Pos: " + this.getCaretPos());
         System.out.println(", real caret pos: " + this.editor.getCaretPosition());
         // Rectangle rect = this.editor.modelToView(this.getCaretPos());
         Rectangle rect = this.editor.modelToView(this.wordStartedCaretPos);
         System.out.println(rect);
         rect.setSize(widthAll, rect.height);
         System.out.println(rect);
         this.wordBox.setBounds(rect);
      } catch (BadLocationException e) {
         e.printStackTrace();
      }
   }

   /**
    * 팝업박스와 워드박스를 보여준다. 팝업박스 리스트의 단어를 현재 입력된 단어 기준으로 다시 가져오고 위치를 재계산해서 에디터 내에 보여준다. 워드박스 또한 입력된 단어를
    * 감싸는 사각형 영역을 재계산해서 보여준다.
    */
   private void showInputAssist() {
      if (!this.wordBox.isShowing())
         this.wordStartedCaretPos = this.editor.getCaretPosition();
      this.refreshPopupLocation();
      if (this.refreshWordList() > 0) {
         this.popupBox.setVisible(true);
      }
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

   /**
    * 팝업박스 내에 보여줄 단어 리스트를 현재 입력단어 기준으로 다시 가져온다.
    *
    * @return 가져온 단어의 총 갯수를 반환한다.
    */
   int refreshWordList() {
      Vector<String> matchings = this.wordManager.getMatchingWords(this.getWordToSearch());
      Vector<String> deleteBtn = new Vector<>();
      int numRows = matchings.size();
      if (numRows <= 0) {
         this.popupBox.setVisible(false);
      } else {
         FontMetrics listMetrics = popupList.getGraphics().getFontMetrics();
         int maxWidth = 0;
         for (int i = 0; i < numRows; i++) {
            maxWidth = Math.max(maxWidth, listMetrics.stringWidth(matchings.get(i)));
            deleteBtn.add("X");
         }
         this.popupList.setListData(matchings);
         this.deleteList.setListData(deleteBtn);

         maxWidth += listMetrics.stringWidth("X");
         this.popupBox.setSize(maxWidth, listMetrics.getHeight());
      }
      return numRows;
   }
}
