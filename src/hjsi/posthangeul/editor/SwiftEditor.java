package hjsi.posthangeul.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import hjsi.posthangeul.editor.autocomplete.AutoComplete;
import hjsi.posthangeul.window.Helper;

public class SwiftEditor extends JPanel {
   private static final long serialVersionUID = 8037738001573590413L;
   AutoComplete autocomplete;
   GoToLine gotoline;
   Helper helper;
   TextLineNumber linenum;
   RemoveLine remove;
   JScrollPane scrollPane;
   SwitchLine switchLine;
   JTextPane textPane;
   UndoManager undo;

   {
      textPane = new JTextPane();
      scrollPane = new JScrollPane(textPane);
      autocomplete = new AutoComplete(textPane);
      linenum = new TextLineNumber(textPane);
      gotoline = new GoToLine(textPane);
      remove = new RemoveLine(textPane);
      undo = new UndoManager();
      switchLine = new SwitchLine(textPane);
      helper = new Helper(textPane);
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            helper.refreshLocation();
         }
      });

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


      // ctrl-t to tag action
      textPane.getActionMap().put("Tag", new AbstractAction("Tag") {

         @Override
         public void actionPerformed(ActionEvent e) {
            // if (!AudioPlayer.isRecording())
            // return
            Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            StyledDocument doc = textPane.getStyledDocument();
            Style tagAttr = doc.addStyle("tagAttr",
                  StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
            StyleConstants.setForeground(tagAttr, Color.BLUE);
            StyleConstants.setUnderline(tagAttr, true);


            // 태그 이름 입력
            String tagName = JOptionPane.showInputDialog(textPane, null, null);
            tagAttr.addAttribute("linkact", new ChatLinkListener(tagName));

            try {
               // 태그 삽입
               textPane.getDocument().insertString(textPane.getCaretPosition(), tagName, tagAttr);

               // 태그를 클릭 했을 경우
               textPane.addMouseListener(new MouseAdapter() {
                  public void mouseClicked(MouseEvent e) {
                     Element ele = doc.getCharacterElement(textPane.viewToModel(e.getPoint()));
                     AttributeSet as = ele.getAttributes();
                     ChatLinkListener fla = (ChatLinkListener) as.getAttribute("linkact");
                     if (fla != null) {
                        fla.execute();
                     }
                  }
               });
               textPane.addMouseMotionListener(new MouseAdapter() {
                  public void mouseMoved(MouseEvent e) {
                     Element ele = doc.getCharacterElement(textPane.viewToModel(e.getPoint()));
                     AttributeSet as = ele.getAttributes();
                     if (StyleConstants.isUnderline(as)) {
                        textPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
                     } else
                        textPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
                  }
               });
            } catch (BadLocationException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }
         }
      });
      textPane.getInputMap().put(KeyStroke.getKeyStroke("control T"), "Tag");


      setLayout(new BorderLayout());
      add(scrollPane);
   }

   class ChatLinkListener extends AbstractAction {
      private String textLink;

      ChatLinkListener(String textLink) {
         this.textLink = textLink;
      }

      protected void execute() {
         System.out.println("clikcedd");
      }

      public void actionPerformed(ActionEvent e) {
         execute();
      }
   }

   public void addCaretListener(CaretListener listener) {
      textPane.addCaretListener(listener);
   }

   public String getFontFamily() {
      return textPane.getFont().getFamily();
   }

   public JTextPane getTextPane() {
      return textPane;
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
