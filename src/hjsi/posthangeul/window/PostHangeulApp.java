package hjsi.posthangeul.window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PostHangeulApp implements KeyListener {
  JFrame mainWindow;
  MainMenu menuBar;
  JPanel panel;
  char keyChar = 0;

  public PostHangeulApp() {
    mainWindow = new JFrame("Post Hangeul");

    mainWindow.setBounds(0, 0, 600, 480);
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setLocationRelativeTo(null);
    mainWindow.setVisible(true);

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
    mainWindow.getContentPane().add(panel);
    mainWindow.addKeyListener(this);

    menuBar = new MainMenu();
    mainWindow.setJMenuBar(menuBar);
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
    mainWindow.repaint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
   */
  @Override
  public void keyReleased(KeyEvent e) {
    mainWindow.repaint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
   */
  @Override
  public void keyTyped(KeyEvent e) {
    if (e.getKeyChar() == 27)
      mainWindow.dispose();
  }
}
