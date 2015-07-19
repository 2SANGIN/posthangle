package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import hjsi.posthangeul.action.BoldAction;
import hjsi.posthangeul.action.FontAction;
import hjsi.posthangeul.action.ForegroundAction;
import hjsi.posthangeul.action.ItalicAction;
import hjsi.posthangeul.editor.SwiftEditor;

public class PostHangeulApp {
  JFrame mainWindow;
  MainMenu menuBar;
  KeyCodeViewer keyCodeViewer;
  JPanel north = new JPanel();
  JButton bold = new JButton();
  JButton italic = new JButton();
  JButton color = new JButton();
  JComboBox<String> font = new JComboBox<String>();
  JButton size = new JButton();
  JButton btnKeyCode = new JButton("KeyCode Viewer");

  SwiftEditor editor = new SwiftEditor();

  public PostHangeulApp() {
    mainWindow = new JFrame("Post Hangeul");

    /* 메뉴바 생성 */
    menuBar = new MainMenu(mainWindow);
    mainWindow.setJMenuBar(menuBar);
    /* 메뉴바 생성 끝 */

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

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontNames = ge.getAvailableFontFamilyNames();
    for (int i = 0; i < fontNames.length; i++) {
      font.addItem(fontNames[i]);
    }
    font.getSelectedItem();
    font.addActionListener(formatTextAction);

    btnKeyCode.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        keyCodeViewer.setVisible(true);
      }
    });

    north.add(bold);
    north.add(italic);
    north.add(color);
    north.add(font);
    north.add(btnKeyCode);
    north.setLayout(new FlowLayout(FlowLayout.LEFT));

    mainWindow.getContentPane().add(north, BorderLayout.NORTH);
    mainWindow.getContentPane().add(editor, BorderLayout.CENTER);

    /* 메인프레임 생성 */
    mainWindow.setBounds(0, 0, 600, 480);
    mainWindow.setMinimumSize(new Dimension(600, 480));
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setLocationRelativeTo(null);
    mainWindow.setVisible(true);
    /* 메인프레임 생성 끝 */

    keyCodeViewer = new KeyCodeViewer(mainWindow, "KeyCode Viewer", false);
  }

  public static void main(String[] args) {
    new PostHangeulApp();
  }
}
