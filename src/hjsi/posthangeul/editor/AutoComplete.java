package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.Font;
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



public class AutoComplete implements KeyListener, InputMethodListener {
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

   public static boolean isKoreanAlphabet(char ch) {
      if (ch < 0x3131 || 0x318E < ch)
         return false;
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
   private JTextPane editor;

   private JScrollPane popupBox;

   private JList<String> popupList;

   /**
    * 조립 중인 한글을 위한 버퍼 (1글자 무조건)
    */
   private StringBuffer uncommBuf;

   /**
    * 현재 입력하고 있는 단어 영역에 표시를 해준다.
    */
   private final JPanel wordBox;

   private final WordManager wordManager = new WordManager();

   {
      this.wordBox = new JPanel();
      this.wordBox.setVisible(false);
      this.wordBox.setOpaque(false);
      this.wordBox.setBorder(new LineBorder(Color.RED, 2));
   }

   public AutoComplete(JTextPane textPane) {
      /* 기타 객체 생성 */
      this.initWordBuffers();

      /* scrollpane 내부 리스트뷰 설정 */
      this.popupList = new JList<String>();
      this.popupList.setVisible(true);

      /* scrollpane 설정 */
      this.popupBox = new JScrollPane(this.popupList);
      this.popupBox.setVisible(false);
      this.popupBox.setOpaque(true);
      this.popupBox.setSize(100, 280);
      this.popupBox.setBackground(Color.WHITE);
      System.out.println(this.popupBox.getInsets().toString());
      EtchedBorder outer = new EtchedBorder(EtchedBorder.LOWERED);
      this.popupBox.setBorder(outer);
      System.out.println(this.popupBox.getInsets().toString());

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
         System.out.println(this.logString() + "\n----\n");
      } else {
         this.initUncommittedBuffer();
         System.out.println("it's null!\n----\n");
      }

      if (this.popupBox.isShowing())
         this.updatePopup();
      else
         this.showPopup();
      this.showWordBox();
   }

   @Override
   public void keyPressed(KeyEvent e) {
      // System.out.println(e.paramString());
      char ch = e.getKeyChar();
      int code = e.getKeyCode();

      switch (code) {
         case KeyEvent.VK_ESCAPE:
            if (this.popupBox.isShowing())
               this.popupBox.setVisible(false);
            else
               this.initWordBuffers();
            break;

         case KeyEvent.VK_LEFT:
            this.commBufPos--;
            break;

         case KeyEvent.VK_RIGHT:
            System.out.println("right key pressed!");
            if (this.getWordToSearch().length() == this.commBufPos) {
               this.wordManager.countWord(this.getWordToSearch());
               this.initWordBuffers();
               this.popupBox.setVisible(false);
            } else {
               this.commBufPos++;
            }
            break;

         case KeyEvent.VK_BACK_SPACE:
            this.backspaceCommitted();
            break;

         case KeyEvent.VK_ENTER:
         case KeyEvent.VK_SPACE:
            this.popupBox.setVisible(false);
            this.wordManager.countWord(this.commBuf.toString());
            this.initWordBuffers();
            break;

         default:
            if ((0xAC00 <= ch && ch <= 0xD7A3) || (0x3131 <= ch && ch <= 0x318E)) {
               // 한글의 경우이긴 하지만 들어올 일 없을 거임 (inputMethodTextChanged에서 걸러짐)
            } else if ((0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
               this.appendCommitted(ch);
               if (this.popupBox.isShowing() == false) {
                  this.showPopup();
               }
            } else {
               this.popupBox.setVisible(false);
            }
      }

      if (this.popupBox.isShowing())
         this.updatePopup();

      if (code != KeyEvent.VK_ENTER && code != KeyEvent.VK_SPACE)
         System.out.println(this.logString());

      this.editor.invalidate();
   }

   @Override
   public void keyReleased(KeyEvent e) {
      // System.out.println(e.paramString());
   }

   @Override
   public void keyTyped(KeyEvent e) {
      // System.out.println(e.paramString());
   }

   public String logString() {
      StringBuffer logStr = new StringBuffer();
      logStr.append("  commBuf: \"" + this.commBuf.toString() + "\"\n");
      logStr.append("uncommBuf: \"" + this.uncommBuf.toString() + "\"\n");
      logStr.append("wordToSearch: \"" + this.getWordToSearch() + "\"\n");
      logStr.append("length: " + this.getWordToSearch().length() + "\n");
      logStr.append("buf caret pos: " + this.commBufPos + "\n");
      if (this.getWordToSearch().length() > 0)
         return logStr.toString();
      else
         return "";
   }

   private void appendCommitted(char ch) {
      if (this.commBufPos > this.commBuf.length())
         this.commBuf.append(ch);
      else {
         this.commBuf.insert(this.commBufPos, ch);
         this.commBufPos++;
      }
   }

   private void backspaceCommitted() {
      if (this.commBufPos > 0) {
         this.commBuf.delete(this.commBufPos - 1, this.commBufPos);
         this.commBufPos--;
      }
   }

   private Rectangle getWordBoxBounds() throws BadLocationException {
      // 현재 문단의 폰트 속성 조사
      AttributeSet attrSet = this.editor.getParagraphAttributes();
      String fontFamily = StyleConstants.getFontFamily(attrSet);
      int fontStyle = StyleConstants.isBold(attrSet) ? Font.BOLD : Font.PLAIN;
      fontStyle |= StyleConstants.isItalic(attrSet) ? Font.ITALIC : Font.PLAIN;
      int fontSize = StyleConstants.getFontSize(attrSet);

      // 조사된 속성으로 폰트 객체를 만들고, 입력단어의 길이를 측정함
      Font font = new Font(fontFamily, fontStyle, fontSize);
      int width = this.editor.getFontMetrics(font).stringWidth(this.getWordToSearch());

      // 에디터의 현재 캐럿 위치로부터 입력단어에 해당하는 영역을 계산함
      Rectangle rect = this.editor.modelToView(this.editor.getCaretPosition());
      rect.translate(-width, 0);
      rect.setSize(width, rect.height);
      return rect;
   }

   private String getWordToSearch() {
      if (this.uncommBuf.length() > 0)
         return this.commBuf.toString() + this.uncommBuf.toString();
      else
         return this.commBuf.toString();
   }


   private void initUncommittedBuffer() {
      this.uncommBuf = new StringBuffer();
   }

   private void initWordBuffers() {
      this.commBuf = new StringBuffer();
      this.commBufPos = 0;
      this.initUncommittedBuffer();
   }

   private boolean isNumber(CharSequence str) {
      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (ch < 0x30 || 0x39 < ch)
            return false;
      }
      return true;
   }

   /**
    * 방향키를 입력 받으면 버퍼의 캐럿을 해당 방향키에 맞게 처리한다.
    *
    * @param keyCode
    */
   private void processArrowKeys(int keyCode) {

   }

   private void refreshPopupLocation() throws BadLocationException {
      Rectangle anchor = this.editor.modelToView(this.editor.getCaretPosition());
      this.popupBox.setLocation(anchor.x, anchor.y + anchor.height);
   }

   private void showPopup() {
      try {
         this.updatePopup();
         this.refreshPopupLocation();
         this.popupBox.setVisible(true);
      } catch (BadLocationException e1) {
         e1.printStackTrace();
      }
   }

   private void showWordBox() {
      try {
         this.wordBox.setBounds(this.getWordBoxBounds());
         this.wordBox.setVisible(true);
      } catch (BadLocationException e) {
         this.wordBox.setVisible(false);
         e.printStackTrace();
      }
   }

   private void updatePopup() {
      Vector<String> matchings = this.wordManager.getMatchingWords(this.getWordToSearch());
      this.popupList.setListData(matchings);
   }

   private void updateUncommitted(char ch) {
      if (this.uncommBuf.length() > 0)
         this.uncommBuf.setCharAt(0, ch);
      else {
         this.uncommBuf.append(ch);
      }
   }
}
