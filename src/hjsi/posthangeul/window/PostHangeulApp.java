package hjsi.posthangeul.window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PostHangeulApp implements KeyListener {
  JFrame mainWindow;
  MainMenu menuBar;
  JDialog keyCodeViewer;
  JPanel panel;
  char keyChar = 0;

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

    // TODO 고칠 곳
    // mainWindow.getContentPane().add(here);

    /* 메인프레임 생성 */
    mainWindow.setBounds(0, 0, 600, 480);
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setLocationRelativeTo(null);
    mainWindow.setVisible(true);
    /* 메인프레임 생성 끝 */
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
