package hjsi.posthangeul.editor;

import java.awt.Color;
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

import javax.swing.JPanel;
import javax.swing.JTextPane;
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

   /**
    * 해당 글자가 문장부호(키보드 상의 쉼표, 마침표, 따옴표, 더하기 등과 같은 각종 특수문자) 여부를 검사한다.
    *
    * @param ch 검사할 글자
    * @return 문장부호에 해당하면 true, 아니라면 false
    */
   public static boolean isPunctuationMark(char ch) {
      String marks = "`~!@#$%^&*()_+=-[]{}\\|;':\"<>,./?";
      return marks.contains(Character.toString(ch));
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
    * 자동완성 단어 목록을 보여주는 팝업
    */
   final WordPopup wordPopup;

   /**
    * 현재 처리 중인 keyChar
    */
   private char keyChar;

   /**
    * 현재 처리 중인 keyCode
    */
   private int keyCode;

   /**
    * 현재 처리 중인 modifiers
    */
   private int modifiers;

   /**
    * Ctrl, Alt, Shift, Meta 키가 하나도 눌리지 않은 상태
    */
   boolean noMod;

   /**
    * Ctrl 눌린 상태
    */
   boolean modCtrl;

   /**
    * Alt 눌린 상태
    */
   boolean modAlt;

   /**
    * Shift 눌린 상태
    */
   boolean modShift;

   /**
    * Meta 눌린 상태
    */
   boolean modMeta;

   /**
    * Ctrl만 눌리거나 안 눌리거나 상관 없음
    */
   boolean modCtrlAllowed;

   /**
    * Alt만 눌리거나 안 눌리거나 상관 없음
    */
   boolean modAltAllowed;

   /**
    * Shift만 눌리거나 안 눌리거나 상관 없음
    */
   boolean modShiftAllowed;

   /**
    * Meta만 눌리거나 안 눌리거나 상관 없음
    */
   boolean modMetaAllowed;

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
      /* 버퍼 생성 */
      this.initWordBuffers();

      this.wordPopup = new WordPopup();
      this.wordPopup.setVisible(false);
      this.wordPopup.setSelectListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
               String word = AutoComplete.this.wordPopup.getSelectedItem();
               AutoComplete.this.replaceInputWord(word);
               AutoComplete.this.wordManager.countWord(word);
               AutoComplete.this.initWordBuffers();
               AutoComplete.this.hideInputAssist();
            }
         }
      });
      this.wordPopup.setDeleteListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            System.out.println(AutoComplete.this.wordPopup.getSelectedItem());
            AutoComplete.this.wordManager.removeWord(AutoComplete.this.wordPopup.getSelectedItem());
            AutoComplete.this.wordPopup.removeSelectedItem();
         }
      });

      /* 에디터 설정 */
      this.editor = textPane;
      this.editor.add(this.wordPopup);
      this.editor.add(this.wordBox);
      this.editor.repaint();

      this.editor.addKeyListener(this);
      this.editor.addInputMethodListener(this);
      this.editor.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            AutoComplete.this.hideInputAssist();
            AutoComplete.this.initWordBuffers();
            super.mouseClicked(e);
         }
      });
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

         if (str.getEndIndex() > 1) {
            this.wordManager.countWord(this.getWordInBox());
            this.initWordBuffers();
            this.hideInputAssist();
         } else {
            if (this.isShowingInputAssist()) {
               this.refreshInputAssist();
            } else
               this.showInputAssist();
         }
      } else {
         this.initUncommittedBuffer();
         System.out.println("it's null! \n");
      }
   }

   @Override
   public void keyPressed(KeyEvent e) {
      this.setProcessingKey(e);
      System.out.println(e.paramString());

      if (this.modShiftAllowed && isPunctuationMark(this.keyChar)) {
         if (this.isShowingInputAssist()) {
            /* caret이 입력 단어의 끝에 있을 때만 카운트 */
            if (this.commBufPos == this.getWordInBox().length())
               this.wordManager.countWord(this.getWordInBox());
            this.initWordBuffers();
            this.hideInputAssist();
         }
      } else if (this.modShiftAllowed && this.processCharacterKeys()) {
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
                  if (this.wordPopup.isSelectionEmpty())
                     this.hideInputAssist();
                  else
                     this.wordPopup.clearSelection();
               } else
                  this.initWordBuffers();
               break;

            case KeyEvent.VK_DELETE:
               this.deleteCommitted();
               this.refreshInputAssist();
               break;

            case KeyEvent.VK_BACK_SPACE:
               if (this.commBufPos <= 0 || this.modShiftAllowed) {
                  this.hideInputAssist();
                  this.initWordBuffers();
               } else {
                  this.backspaceCommitted();
                  this.refreshInputAssist();
               }
               break;

            case KeyEvent.VK_HOME:
            case KeyEvent.VK_PAGE_UP:
               if (!this.wordPopup.isSelectionEmpty()) {
                  this.wordPopup.setSelectedIndex(0);
                  this.wordPopup.gotoScroll();
                  e.consume();
               } else {
                  this.initWordBuffers();
                  this.hideInputAssist();
               }
               break;

            case KeyEvent.VK_END:
            case KeyEvent.VK_PAGE_DOWN:
               if (!this.wordPopup.isSelectionEmpty()) {
                  this.wordPopup.setSelectedIndex(this.wordPopup.getItemCount() - 1);
                  this.wordPopup.gotoScroll();
                  e.consume();
               } else {
                  this.initWordBuffers();
                  this.hideInputAssist();
               }
               break;

            case KeyEvent.VK_ENTER:
               if (this.noMod) {
                  /* 팝업 목록에 선택 된 단어가 있으면 그 단어로 대체 */
                  if (!this.wordPopup.isSelectionEmpty()) {
                     String word = this.wordPopup.getSelectedItem();
                     this.replaceInputWord(word);
                     this.wordManager.countWord(word);
                     e.consume();
                  }
                  /* 새로운 단어를 메모리에 저장 */
                  else if (this.getWordInBox().length() > 0) {
                     this.moveCaretAfterWord(this.getWordInBox());
                     this.wordManager.countWord(this.getWordInBox());
                     e.consume();
                  }
               }
               this.initWordBuffers();
               this.hideInputAssist();
               break;

            case KeyEvent.VK_SPACE:
               if (this.modShiftAllowed) {
                  /* caret이 입력 단어의 끝에 있을 때만 카운트 */
                  if (this.commBufPos == this.getWordInBox().length())
                     this.wordManager.countWord(this.getWordInBox());
                  this.initWordBuffers();
                  this.hideInputAssist();
               } else if (this.modCtrlAllowed) {
                  /* 팝업이 안 보이면 보여줘야지 */
                  if (!this.isShowingInputAssist()) {
                     this.showInputAssist();
                  }
                  /* 팝업이 보이면 현재 입력 중인 단어 뒤에 이어 붙여야지 */
                  else {
                     /* 입력 중인 단어가 있을 때만 대체하고 카운트 */
                     if (this.getWordToSearch().length() > 0) {
                        String word = this.wordPopup.getSelectedItem();
                        if (word == null)
                           word = this.wordPopup.getItemAt(0);
                        this.replaceInputWord(word);
                        this.wordManager.countWord(word);
                     }
                     this.initWordBuffers();
                     this.hideInputAssist();
                  }
                  e.consume();
               }
               break;
            case KeyEvent.VK_TAB:
               if (this.isShowingInputAssist()) {
                  if (this.wordPopup.isSelectionEmpty())
                     this.wordPopup.setSelectedIndex(0);
                  else {
                     String word = this.wordPopup.getSelectedItem();
                     this.replaceInputWord(word);
                     this.wordManager.countWord(word);
                     this.initWordBuffers();
                     this.hideInputAssist();
                  }
                  e.consume();
               }
               break;

            case KeyEvent.VK_CONTEXT_MENU:
               /* context menu key */
               if (!this.isShowingInputAssist())
                  this.showInputAssist();
               break;

            case KeyEvent.VK_INSERT:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_META:
            case 0x107: // 오른쪽 Ctrl 혹은 한자 키
               /* 특수 키만 눌린 경우는 입력도우미를 숨기지 않는다. */
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
         if (this.modCtrl) {
            this.commBuf.delete(0, this.commBufPos);
            this.commBufPos = 0;
            System.out.println(this.commBuf + ", " + this.commBufPos);
         } else {
            this.commBuf.delete(this.commBufPos - 1, this.commBufPos);
            this.commBufPos--;
         }
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
    * wordbox 안의 전체 단어를 가져온다.
    *
    * @return 워드박스 안의 단어
    */
   private String getWordInBox() {
      if (this.uncommBuf.length() > 0) {
         String caretLeft = this.commBuf.substring(0, this.commBufPos);
         String caretRight = this.commBuf.substring(this.commBufPos, this.commBuf.length());
         return caretLeft + this.uncommBuf.toString() + caretRight;
      }
      return this.commBuf.toString();
   }

   /**
    * 자동완성 단어 검색에 사용 될 단어를 완성글자 버퍼를 가져오거나 조립글자 버퍼까지 합쳐서 가져온다.
    *
    * @return 자동완성 검색에 사용 될 단어
    */
   private String getWordToSearch() {
      if (this.uncommBuf.length() > 0) {
         String caretLeft = this.commBuf.substring(0, this.commBufPos);
         return caretLeft + this.uncommBuf.toString();
      }
      return this.commBuf.substring(0, this.commBufPos);
   }

   /**
    * 조립글자 버퍼를 지우고 새 버퍼를 할당한다.
    */
   private void initUncommittedBuffer() {
      this.uncommBuf = new StringBuffer();
   }

   /**
    * @return 현재 입력도우미가 보여지고 있으면 true, 아니라면 false
    */
   private boolean isShowingInputAssist() {
      return this.wordPopup.isShowing() && this.wordBox.isShowing();
   }

   /**
    * 현재 에디터의 캐럿 위치를 입력 된 단어의 뒤로 위치시킨다.
    *
    * @param word 캐럿 위치의 기준이 되는 단어
    */
   private void moveCaretAfterWord(String word) {
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
            if (this.noMod) {
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
            } else {
               this.initWordBuffers();
               this.hideInputAssist();
            }
            break;

         case KeyEvent.VK_RIGHT:
            if (this.noMod) {
               System.out.println("RIGHT KEY PRESSED!");
               if (this.getWordInBox().length() == this.commBufPos) {
                  this.wordManager.countWord(this.getWordInBox());
                  this.initWordBuffers();
                  this.hideInputAssist();
               } else {
                  this.commBufPos++;
               }
            } else {
               this.initWordBuffers();
               this.hideInputAssist();
            }
            break;

         case KeyEvent.VK_UP:
         case KeyEvent.VK_DOWN:
            if (this.wordPopup.isShowing()) {
               this.wordPopup.moveSelection(this.keyCode);
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
      this.resizeWordBox();
      this.wordPopup.setWordList(this.wordManager.getMatchingWords(this.getWordToSearch()));
      this.wordPopup.repaint();
   }

   /**
    * 팝업박스의 위치를 에디터 내의 캐럿 위치를 기준으로 계산한다.
    */
   private void refreshPopupLocation() {
      try {
         Rectangle anchor = this.editor.modelToView(this.wordStartedCaretPos);
         this.wordPopup.setLocation(anchor.x, anchor.y + anchor.height + 2);
      } catch (BadLocationException e) {
         e.printStackTrace();
      }
   }

   /**
    * 워드박스의 위치를 에디터 내의 캐럿 위치를 기준으로 계산한다.
    */
   private void resizeWordBox() {
      // 현재 문단의 폰트 속성 조사
      AttributeSet attrSet = this.editor.getParagraphAttributes();
      String fontFamily = StyleConstants.getFontFamily(attrSet);
      int fontStyle = (StyleConstants.isBold(attrSet) ? Font.BOLD : Font.PLAIN)
            | (StyleConstants.isItalic(attrSet) ? Font.ITALIC : Font.PLAIN);
      int fontSize = StyleConstants.getFontSize(attrSet);

      // 조사된 속성으로 폰트 객체를 만들고, 입력단어의 길이를 측정함
      Font font = new Font(fontFamily, fontStyle, fontSize);
      FontMetrics metric = this.editor.getFontMetrics(font);
      int widthAll = metric.stringWidth(this.getWordInBox());

      // 에디터의 현재 캐럿 위치로부터 입력단어에 해당하는 영역을 계산함
      try {
         System.out.print("Caret Pos: " + this.getCaretPos());
         System.out.println(", real caret pos: " + this.editor.getCaretPosition());
         // Rectangle rect = this.editor.modelToView(this.getCaretPos());
         Rectangle rect = this.editor.modelToView(this.wordStartedCaretPos);
         // System.out.println(rect);
         rect.setSize(widthAll, rect.height);
         // System.out.println(rect);
         this.wordBox.setBounds(rect);
      } catch (BadLocationException e) {
         e.printStackTrace();
      }
   }

   /**
    * AutoComplete이 현재 눌린 키에 대한 전처리를 한다.
    *
    * @param e 현재 발생한 키 이벤트
    */
   private void setProcessingKey(KeyEvent e) {
      this.keyChar = e.getKeyChar();
      this.keyCode = e.getKeyCode();
      this.modifiers = e.getModifiers();
      this.noMod = this.modifiers == 0;
      this.modCtrl = (this.modifiers & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
      this.modAlt = (this.modifiers & InputEvent.ALT_MASK) == InputEvent.ALT_MASK;
      this.modShift = (this.modifiers & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
      this.modMeta = (this.modifiers & InputEvent.META_MASK) == InputEvent.META_MASK;
      this.modCtrlAllowed = !this.modAlt && !this.modShift && !this.modMeta;
      this.modAltAllowed = !this.modCtrl && !this.modShift && !this.modMeta;
      this.modShiftAllowed = !this.modCtrl && !this.modAlt && !this.modMeta;
      this.modMetaAllowed = !this.modCtrl && !this.modAlt && !this.modShift;

      System.out.println("alt: " + this.modAlt + ", ctrl: " + this.modCtrl + ", shift: "
            + this.modShift + ", meta: " + this.modMeta);
   }

   /**
    * 팝업박스와 워드박스를 보여준다. 팝업박스 리스트의 단어를 현재 입력된 단어 기준으로 다시 가져오고 위치를 재계산해서 에디터 내에 보여준다. 워드박스 또한 입력된 단어를
    * 감싸는 사각형 영역을 재계산해서 보여준다.
    */
   private void showInputAssist() {
      /* wordBox */
      if (!this.wordBox.isShowing())
         this.wordStartedCaretPos = this.editor.getCaretPosition();
      this.resizeWordBox();
      this.wordBox.setVisible(true);

      /* wordPopup */
      this.refreshPopupLocation();
      this.wordPopup.setWordList(this.wordManager.getMatchingWords(this.getWordToSearch()));
      if (this.wordPopup.getItemCount() > 0) {
         this.wordPopup.setVisible(true);
      }
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
    * 팝업박스와 워드박스를 보이지 않게 한다.
    */
   void hideInputAssist() {
      this.wordPopup.setVisible(false);
      this.wordBox.setVisible(false);
   }

   /**
    * 완성글자 버퍼를 지우고 새 버퍼를 할당한다. 완성글자 캐럿 포지션도 초기화하고, 조립글자 버퍼도 초기화한다.
    */
   void initWordBuffers() {
      this.commBuf = new StringBuffer();
      this.commBufPos = 0;
      this.initUncommittedBuffer();
   }

   /**
    * 현재 입력 중인 단어를 선택 된 단어로 대체한다.
    *
    * @param wordToReplace 입력 중인 단어를 대체할 단어. null을 입력하면 아무 일도 일어나지 않는다.
    */
   void replaceInputWord(String wordToReplace) {
      if (wordToReplace != null) {
         if (this.getWordToSearch().compareTo(wordToReplace) != 0) {
            /* replace */
            int length = this.getWordInBox().length();
            AttributeSet attrSet = this.editor.getInputAttributes();
            try {
               if (length > 0)
                  this.editor.getDocument().remove(this.wordStartedCaretPos, length);
               this.editor.getDocument().insertString(this.wordStartedCaretPos, wordToReplace,
                     attrSet);
            } catch (BadLocationException e1) {
               e1.printStackTrace();
            }
         }
         this.moveCaretAfterWord(wordToReplace);
      }
   }
}
