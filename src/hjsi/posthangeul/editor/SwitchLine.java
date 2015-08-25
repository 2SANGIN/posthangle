package hjsi.posthangeul.editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Utilities;


public class SwitchLine {
   JTextPane parent;
   String cur, change;

   public SwitchLine(JTextPane textPane) {
      parent = textPane;
      parent.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
               if (ke.isAltDown()) {
                  try {
                     if (getCurLine() == getLastLine())
                        return;
                     parent.select(getStartPos(), getEndPos());
                     cur = parent.getSelectedText();
                     parent.setCaretPosition(getEndPos() + 1);
                     
                     parent.select(getStartPos(), getEndPos());
                     change = parent.getSelectedText();
                     parent.replaceSelection(cur);
                     
                     parent.setCaretPosition(getStartPos() - 1);
                     parent.select(getStartPos(), getEndPos());
                     parent.replaceSelection(change);
                     
                     parent.setCaretPosition(getEndPos() + 1);
                     parent.setCaretPosition(getEndPos());
                  } catch (BadLocationException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               }
            }
            else if (ke.getKeyCode() == KeyEvent.VK_UP) {
               if (ke.isAltDown()) {
                  try {
                     if (getCurLine() == 1)
                        return;
                     parent.select(getStartPos(), getEndPos());
                     cur = parent.getSelectedText();
                     parent.setCaretPosition(getStartPos() - 1);
                     
                     parent.select(getStartPos(), getEndPos());
                     change = parent.getSelectedText();
                     parent.replaceSelection(cur);
                     
                     parent.setCaretPosition(getEndPos() + 1);
                     parent.select(getStartPos(), getEndPos());
                     parent.replaceSelection(change);
                     
                     parent.setCaretPosition(getStartPos() - 1);
                     parent.setCaretPosition(getEndPos());
                  } catch (BadLocationException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               }
            }
         }
      });

   }

   private int getStartPos() throws BadLocationException {
      return Utilities.getRowStart(parent, parent.getCaretPosition());
   }
   
   private int getEndPos() throws BadLocationException {
      return Utilities.getRowEnd(parent, parent.getCaretPosition());
   }
   
   private int getCurLine() {
      Element root = parent.getDocument().getDefaultRootElement();
      return root.getElementIndex(parent.getCaretPosition()) + 1; // starts from 0
   }
   
   private int getLastLine() {
      Element root = parent.getDocument().getDefaultRootElement();
      return root.getElementIndex(parent.getDocument().getLength()) + 1;
   }

}
