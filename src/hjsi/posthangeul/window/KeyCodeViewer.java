package hjsi.posthangeul.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class KeyCodeViewer extends JDialog implements KeyListener {
  private static final long serialVersionUID = -9204219135323776333L;
  private int keyChar;
  private int keyCode;

  public KeyCodeViewer(Frame owner, String title, boolean modal) {
    super(owner, title, modal);

    addKeyListener(this);

    setContentPane(new JPanel() {
      private static final long serialVersionUID = -3334243621995118959L;

      @Override
      public void paint(Graphics g) {
        super.paint(g);
        String strKeyCode = String.valueOf(keyChar) + " : " + KeyEvent.getKeyText(keyCode);
        int strWidth = g.getFontMetrics().stringWidth(strKeyCode);
        int strHeight = g.getFontMetrics().getHeight() / 2;
        g.drawString(strKeyCode, (getWidth() - strWidth) / 2, (getHeight() + strHeight) / 2);
      }
    });
    getContentPane().setBackground(Color.WHITE);
    setBounds(owner.getX() + owner.getWidth(), owner.getY(), owner.getWidth() / 3,
        owner.getHeight() / 3);
    setMinimumSize(new Dimension(50, 150));
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
   */
  @Override
  public void keyPressed(KeyEvent e) {
    System.out.println(e.toString());
    keyChar = e.getKeyChar();
    keyCode = e.getKeyCode();
    repaint();
  }

  @Override
  public void keyReleased(KeyEvent e) {
    System.out.println(e.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
   */
  @Override
  public void keyTyped(KeyEvent e) {
    System.out.println(e.toString());
    if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
      dispose();
  }
}
