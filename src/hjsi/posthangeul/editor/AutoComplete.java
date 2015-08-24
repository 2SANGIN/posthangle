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
      wordBox = new JPanel();
      wordBox.setVisible(false);
      wordBox.setOpaque(false);
      wordBox.setBorder(new LineBorder(Color.RED, 2));
   }

   public AutoComplete(JTextPane textPane) {
      /* 기타 객체 생성 */
      initWordBuffers();

      /* scrollpane 내부 리스트뷰 설정 */
      popupList = new JList<String>();
      popupList.setVisible(true);

      /* scrollpane 설정 */
      popupBox = new JScrollPane(popupList);
      popupBox.setVisible(false);
      popupBox.setOpaque(true);
      popupBox.setSize(100, 280);
      popupBox.setBackground(Color.WHITE);
      System.out.println(popupBox.getInsets().toString());
      EtchedBorder outer = new EtchedBorder(EtchedBorder.LOWERED);
      popupBox.setBorder(outer);
      System.out.println(popupBox.getInsets().toString());

      editor = textPane;
      editor.addKeyListener(this);
      editor.addInputMethodListener(this);
      editor.add(popupBox);
      editor.add(wordBox);
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
            appendCommitted(str.first());
            initUncommittedBuffer();
            System.out.println("committed : " + commBuf.charAt(commBufPos - 1));
         } else if (str.getEndIndex() > 0) {
            updateUncommitted(str.last());
            System.out.println("uncommitted : " + uncommBuf);
         } else {
            initUncommittedBuffer();
            System.out.println("uncommitted : " + uncommBuf);
         }
         System.out.println(logString() + "\n----\n");
      } else {
         initUncommittedBuffer();
         System.out.println("it's null!\n----\n");
      }

      if (popupBox.isShowing())
         updatePopup();
      else
         showPopup();
      showWordBox();
   }

   @Override
   public void keyPressed(KeyEvent e) {
      // System.out.println(e.paramString());
      char ch = e.getKeyChar();
      int code = e.getKeyCode();

      switch (code) {
         case KeyEvent.VK_ESCAPE:
            if (popupBox.isShowing())
               popupBox.setVisible(false);
            else
               initWordBuffers();
            break;

         case KeyEvent.VK_LEFT:
            commBufPos--;
            break;

         case KeyEvent.VK_RIGHT:
            System.out.println("right key pressed!");
            if (getWordToSearch().length() == commBufPos) {
               wordManager.countWord(getWordToSearch());
               initWordBuffers();
               popupBox.setVisible(false);
            } else {
               commBufPos++;
            }
            break;

         case KeyEvent.VK_BACK_SPACE:
            backspaceCommitted();
            break;

         case KeyEvent.VK_ENTER:
         case KeyEvent.VK_SPACE:
            popupBox.setVisible(false);
            wordManager.countWord(commBuf.toString());
            initWordBuffers();
            break;

         default:
            if ((0xAC00 <= ch && ch <= 0xD7A3) || (0x3131 <= ch && ch <= 0x318E)) {
               // 한글의 경우이긴 하지만 들어올 일 없을 거임 (inputMethodTextChanged에서 걸러짐)
            } else if ((0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
               appendCommitted(ch);
               if (popupBox.isShowing() == false) {
                  showPopup();
               }
            } else {
               popupBox.setVisible(false);
            }
      }

      if (popupBox.isShowing())
         updatePopup();

      if (code != KeyEvent.VK_ENTER && code != KeyEvent.VK_SPACE)
         System.out.println(logString());

      editor.invalidate();
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
      logStr.append("  commBuf: \"" + commBuf.toString() + "\"\n");
      logStr.append("uncommBuf: \"" + uncommBuf.toString() + "\"\n");
      logStr.append("wordToSearch: \"" + getWordToSearch() + "\"\n");
      logStr.append("length: " + getWordToSearch().length() + "\n");
      logStr.append("buf caret pos: " + commBufPos + "\n");
      if (getWordToSearch().length() > 0)
         return logStr.toString();
      else
         return "";
   }

   private void appendCommitted(char ch) {
      if (commBufPos > commBuf.length())
         commBuf.append(ch);
      else {
         commBuf.insert(commBufPos, ch);
         commBufPos++;
      }
   }

   private void backspaceCommitted() {
      if (commBufPos > 0) {
         commBuf.delete(commBufPos - 1, commBufPos);
         commBufPos--;
      }
   }

   private Rectangle getWordBoxBounds() throws BadLocationException {
      // 현재 문단의 폰트 속성 조사
      AttributeSet attrSet = editor.getParagraphAttributes();
      String fontFamily = StyleConstants.getFontFamily(attrSet);
      int fontStyle = StyleConstants.isBold(attrSet) ? Font.BOLD : Font.PLAIN;
      fontStyle |= StyleConstants.isItalic(attrSet) ? Font.ITALIC : Font.PLAIN;
      int fontSize = StyleConstants.getFontSize(attrSet);

      // 조사된 속성으로 폰트 객체를 만들고, 입력단어의 길이를 측정함
      Font font = new Font(fontFamily, fontStyle, fontSize);
      int width = editor.getFontMetrics(font).stringWidth(getWordToSearch());

      // 에디터의 현재 캐럿 위치로부터 입력단어에 해당하는 영역을 계산함
      Rectangle rect = editor.modelToView(editor.getCaretPosition());
      rect.translate(-width, 0);
      rect.setSize(width, rect.height);
      return rect;
   }

   private String getWordToSearch() {
      if (uncommBuf.length() > 0)
         return commBuf.toString() + uncommBuf.toString();
      else
         return commBuf.toString();
   }

   private void initUncommittedBuffer() {
      uncommBuf = new StringBuffer();
   }

   private void initWordBuffers() {
      commBuf = new StringBuffer();
      commBufPos = 0;
      initUncommittedBuffer();
   }

   private boolean isEnglish(char ch) {
      return !((ch < 0x41 || 0x5A < ch) && (ch < 0x61 || 0x7A < ch));
   }

   private boolean isEnglish(CharSequence str) {
      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (!isEnglish(ch))
            return false;
      }
      return true;
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
      Rectangle anchor = editor.modelToView(editor.getCaretPosition());
      popupBox.setLocation(anchor.x, anchor.y + anchor.height);
   }

   private void showPopup() {
      try {
         updatePopup();
         refreshPopupLocation();
         popupBox.setVisible(true);
      } catch (BadLocationException e1) {
         e1.printStackTrace();
      }
   }

   private void showWordBox() {
      try {
         wordBox.setBounds(getWordBoxBounds());
         wordBox.setVisible(true);
      } catch (BadLocationException e) {
         wordBox.setVisible(false);
         e.printStackTrace();
      }
   }

   private void updatePopup() {
      Vector<String> matchings = wordManager.getMatchingWords(getWordToSearch());
      popupList.setListData(matchings);
   }

   private void updateUncommitted(char ch) {
      if (uncommBuf.length() > 0)
         uncommBuf.setCharAt(0, ch);
      else {
         uncommBuf.append(ch);
      }
   }
}
