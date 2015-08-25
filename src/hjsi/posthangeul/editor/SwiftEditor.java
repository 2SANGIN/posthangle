package hjsi.posthangeul.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class SwiftEditor extends JPanel {
   private static final long serialVersionUID = 8037738001573590413L;
   AutoComplete autocomplete;
   GoToLine gotoline;
   TextLineNumber linenum;
   RemoveLine remove;
   JScrollPane scrollPane;
   SwitchLine switchLine;
   JTextPane textPane;
   UndoManager undo = new UndoManager();

   {
      textPane = new JTextPane();
      scrollPane = new JScrollPane(textPane);
      autocomplete = new AutoComplete(textPane);
      linenum = new TextLineNumber(textPane);
      gotoline = new GoToLine(textPane);
      remove = new RemoveLine(textPane);
      switchLine = new SwitchLine(textPane);
      linenum.setBackground(Color.WHITE);
      scrollPane.setRowHeaderView(linenum);

      /* redo undo */
      textPane.getDocument().addUndoableEditListener(new UndoableEditListener() {

         @Override
         public void undoableEditHappened(UndoableEditEvent e) {
            // TODO Auto-generated method stub
            undo.addEdit(e.getEdit());
         }
      });

      // undo action
      textPane.getActionMap().put("Undo", new AbstractAction("Undo") {
         private static final long serialVersionUID = -6318592051036637485L;

         @Override
         public void actionPerformed(ActionEvent evt) {
            // TODO Auto-generated method stub
            try {
               if (undo.canUndo())
                  undo.undo();
            } catch (CannotUndoException e) {

            }
         }
      });

      // ctrl-z to undo action
      textPane.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

      // redo action
      textPane.getActionMap().put("Redo", new AbstractAction("Redo") {

         @Override
         public void actionPerformed(ActionEvent evt) {
            // TODO Auto-generated method stub
            try {
               if (undo.canRedo()) {
                  undo.redo();
               }
            } catch (CannotRedoException e) {
            }
         }
      });

      // ctrl-y to redo action
      textPane.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

      setLayout(new BorderLayout());
      add(scrollPane);
   }

   public void addCaretListener(CaretListener listener) {
      textPane.addCaretListener(listener);
   }

   public String getFontFamily() {
      return textPane.getFont().getFamily();
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.swing.JComponent#requestFocus()
    */
   @Override
   public void requestFocus() {
      textPane.requestFocus();
   }

   public void setFont(String fontFamily, int size) {
      Font font = new Font(fontFamily, Font.PLAIN, size);
      textPane.setFont(font);
   }
}
