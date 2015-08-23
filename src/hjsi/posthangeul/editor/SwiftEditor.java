package hjsi.posthangeul.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretListener;


public class SwiftEditor extends JPanel {
  private static final long serialVersionUID = 8037738001573590413L;
  JTextPane textPane;
  JScrollPane scrollPane;
  AutoComplete autocomplete;
  TextLineNumber linenum;
  GoToLine gotoline;
  RemoveLine remove;
  SwitchLine switchLine;

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
    
    setLayout(new BorderLayout());
    add(scrollPane);
  }

  public void addCaretListener(CaretListener listener) {
    textPane.addCaretListener(listener);
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

  public String getFontFamily() {
    return textPane.getFont().getFamily();
  }

  public void setFont(String fontFamily, int size) {
    Font font = new Font(fontFamily, Font.PLAIN, size);
    textPane.setFont(font);
  }
}
