package hjsi.posthangeul.editor;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class SwiftEditor extends JPanel {
  private static final long serialVersionUID = 8037738001573590413L;
  JTextPane textPane;
  JScrollPane scrollPane;

  {
    textPane = new JTextPane();
    scrollPane = new JScrollPane(textPane);
    setLayout(new BorderLayout());
    add(scrollPane);
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
