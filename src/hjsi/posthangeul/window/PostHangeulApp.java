package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class PostHangeulApp implements KeyListener {
  JFrame mainWindow;
  JTextPane textPane = new JTextPane();
  JScrollPane scrollPane = new JScrollPane(textPane);
  MainMenu menuBar;
  JDialog keyCodeViewer;
  JPanel panel;
  char keyChar = 0;

  JPanel north = new JPanel();

  JButton bold = new JButton();
  JButton italic = new JButton();
  JButton color = new JButton();
  JComboBox font = new JComboBox();
  JButton size = new JButton();

  public PostHangeulApp() {
    mainWindow = new JFrame("Post Hangeul");

    /*
     * 다이얼로그 생성
     * TODO 차후 옮길 예정
     */
    keyCodeViewer = new JDialog(mainWindow, "KeyCode Viewer", false);
    panel = new JPanel() {
      @Override
      public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 600, 480);
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf((int) keyChar), 300, 240);
        g.drawString(this.getBounds().toString(), 10, 20);
        g.drawString(mainWindow.getBounds().toString(), 10, 50);
      }
    };
    keyCodeViewer.getContentPane().add(panel);
    keyCodeViewer.addKeyListener(this);
    keyCodeViewer.setBounds(0, 0, 600, 480);
    keyCodeViewer.setLocationRelativeTo(null);
    /* 다이얼로그 생성 끝 */

    /* 메뉴바 생성 */
    menuBar = new MainMenu(mainWindow);
    menuBar.getMenuItem("KeyCode Viewer").addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        keyCodeViewer.setVisible(true);
      }
    });
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

    north.add(bold);
    north.add(italic);
    north.add(color);
    north.add(font);
    north.setLayout(new FlowLayout(FlowLayout.LEFT));

    mainWindow.getContentPane().add(north, BorderLayout.NORTH);
    mainWindow.getContentPane().add(scrollPane, BorderLayout.CENTER);

    /* 메인프레임 생성 */
    mainWindow.setBounds(0, 0, 600, 480);
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setLocationRelativeTo(null);
    mainWindow.setVisible(true);
    /* 메인프레임 생성 끝 */
    textPane.requestFocus();
  }

  public static void main(String[] args) {
    new PostHangeulApp();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
   */
  @Override
  public void keyPressed(KeyEvent e) {
    keyChar = e.getKeyChar();
    keyCodeViewer.repaint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
   */
  @Override
  public void keyReleased(KeyEvent e) {
    keyCodeViewer.repaint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
   */
  @Override
  public void keyTyped(KeyEvent e) {
    if (e.getKeyChar() == 27)
      keyCodeViewer.dispose();
  }
}
