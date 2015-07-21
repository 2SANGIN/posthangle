package hjsi.posthangeul.window;

import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import hjsi.posthangeul.action.BoldAction;
import hjsi.posthangeul.action.FontAction;
import hjsi.posthangeul.action.ForegroundAction;
import hjsi.posthangeul.action.ItalicAction;

public class Shortcut extends JPanel {
  JButton bold = new JButton();
  JButton italic = new JButton();
  JButton color = new JButton();
  JComboBox<String> font = new JComboBox<String>();
  JButton btnKeyCode = new JButton("KeyCode Viewer");

  JButton sizeUp = new JButton();
  JButton sizeDown = new JButton();

  public Shortcut(PostHangeulApp owner) {
    bold.setText("B");
    bold.setOpaque(false);
    bold.setContentAreaFilled(false);

    italic.setText("I");
    italic.setOpaque(false);
    italic.setContentAreaFilled(false);

    color.setText("C");
    color.setOpaque(false);
    color.setContentAreaFilled(false);

    Action boldAction = new BoldAction();
    boldAction.putValue(Action.NAME, "Bold");
    bold.addActionListener(boldAction);

    Action italicAction = new ItalicAction();
    italicAction.putValue(Action.NAME, "Italic");
    italic.addActionListener(italicAction);

    Action foregroundAction = new ForegroundAction();
    foregroundAction.putValue(Action.NAME, "Color");
    color.addActionListener(foregroundAction);

    Action formatTextAction = new FontAction();
    formatTextAction.putValue(Action.NAME, "Font");
    font.addActionListener(formatTextAction);

    btnKeyCode.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        owner.keyCodeViewer.setVisible(true);
      }
    });

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontNames = ge.getAvailableFontFamilyNames();
    for (int i = 0; i < fontNames.length; i++) {
      font.addItem(fontNames[i]);
    }
    font.getSelectedItem();
    font.addActionListener(formatTextAction);

    add(bold);
    add(italic);
    add(color);
    add(font);
    add(btnKeyCode);
    setLayout(new FlowLayout(FlowLayout.LEFT));
  }
}
