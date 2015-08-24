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

   /**
    * 조립 중인 한글을 위한 버퍼 (1글자 무조건)
    */
   private StringBuffer uncommBuf;
   /**
    * 조립이 끝나 완성된 한글 버퍼
    */
   private StringBuffer commBuf;
   private int commBufPos;

   private JTextPane editor;
   private JList<String> listView;

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
      initWordBuffers();
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

      if (isShowing())
         updatePopup();
      else
         showPopup();
   }

   /**
    * 방향키를 입력 받으면 버퍼의 캐럿을 해당 방향키에 맞게 처리한다.
    * 
    * @param keyCode
    */
   private void processArrowKeys(int keyCode) {

   }

   /**
    * 현재 입력하고 있는 단어 영역에 표시를 해준다. 단어가 DB에 저장되거나 ESC 키 입력 등으로 취소되어 표시가 사라지는 것도 이 함수에서 처리한다.
    */
   private void drawWordRect() {

   }

   @Override
   public void keyPressed(KeyEvent e) {
      // System.out.println(e.paramString());
      char ch = e.getKeyChar();
      int code = e.getKeyCode();

      switch (code) {
         case KeyEvent.VK_ESCAPE:
            if (isShowing())
               setVisible(false);
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
            } else {
               commBufPos++;
            }
            break;

         case KeyEvent.VK_BACK_SPACE:
            backspaceCommitted();
            break;

         case KeyEvent.VK_ENTER:
         case KeyEvent.VK_SPACE:
            setVisible(false);
            wordManager.countWord(commBuf.toString());
            initWordBuffers();
            break;

         default:
            if ((0xAC00 <= ch && ch <= 0xD7A3) || (0x3131 <= ch && ch <= 0x318E)) {
               // 한글의 경우이긴 하지만 들어올 일 없을 거임 (inputMethodTextChanged에서 걸러짐)
            } else if ((0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)) {
               appendCommitted(ch);
               if (isShowing() == false) {
                  showPopup();
               }
            } else {
               setVisible(false);
            }
      }

      if (isShowing())
         updatePopup();

      if (code != KeyEvent.VK_ENTER && code != KeyEvent.VK_SPACE)
         System.out.println(logString());
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

   public void showPopup() {
      try {
         updatePopup();
         refreshPopupLocation();
         setVisible(true);
      } catch (BadLocationException e1) {
         e1.printStackTrace();
      }
   }


   public void updatePopup() {
      Vector<String> matchings = wordManager.getMatchingWords(getWordToSearch());
      listView.setListData(matchings);
   }

   private void appendCommitted(char ch) {
      commBuf.insert(commBufPos, ch);
      commBufPos++;
   }

   private void backspaceCommitted() {
      if (commBufPos > 0) {
         commBuf.delete(commBufPos - 1, commBufPos);
         commBufPos--;
      }
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

   private void refreshPopupLocation() throws BadLocationException {
      Rectangle anchor = editor.modelToView(editor.getCaretPosition());
      setLocation(anchor.x, anchor.y + anchor.height);
   }

   private void updateUncommitted(char ch) {
      if (uncommBuf.length() > 0)
         uncommBuf.setCharAt(0, ch);
      else {
         uncommBuf.append(ch);
      }
   }
}
