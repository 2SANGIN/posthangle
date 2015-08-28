package hjsi.posthangeul.editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.Utilities;


/**
 * Go to line by ctrl + 'L'
 */
public class GoToLine {
   int lineNum;
   JTextPane editor;

   public GoToLine(JTextPane textPane) {
      editor = textPane;
      editor.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_L) {
               if (ke.isControlDown()) {
                  String str = JOptionPane.showInputDialog(editor, null, null);

                  try {
                     lineNum = Integer.parseInt(str);
                     editor.setCaretPosition(setcursor(lineNum));
                     editor.setCaretPosition(Utilities.getRowEnd(editor, editor.getCaretPosition()));
                  } catch (Exception e) {
                     editor.setCaretPosition(textPane.getDocument().getLength());
                  }
               }
            }
         }
      });
   }

   public int setcursor(int newlineno) {
      int pos = 0;
      int i = 0;
      String line = "";
      Scanner sc = new Scanner(editor.getText());
      while (sc.hasNextLine()) {
         line = sc.nextLine();
         i++;
         if (newlineno > i) {
            pos = pos + line.length() + 1;
         }
      }
      sc.close();
      return pos;
   }

   public int getLineNum() {
      return lineNum;
   }
}
