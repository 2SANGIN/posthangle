package hjsi.posthangeul.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class SwiftEditor extends JPanel {
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
}
