package hjsi.posthangeul.editor;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;



public class AutoComplete extends JPopupMenu implements KeyListener {
  private static final long serialVersionUID = 4592225249925286812L;

  JTextPane textPane;
  Rectangle anchor;

  public AutoComplete(JTextPane textPane) {
    setOpaque(true);
    add(new JMenuItem("asd"));
    add(new JMenuItem("asd2"));
    this.textPane = textPane;
    this.textPane.addKeyListener(this);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // TODO Auto-generated method stub
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int modMask = e.getModifiers();
    int keyCode = e.getKeyCode();

    switch (e.getKeyCode()) {
      case KeyEvent.VK_SPACE:
        if ((modMask & InputEvent.CTRL_MASK) >= 1) {
          setVisible(true);
        }
        break;
    }

    if (isShowing()) {
      try {
        anchor = textPane.modelToView(textPane.getCaretPosition());
        show(textPane, anchor.x, anchor.y + (int) anchor.getHeight());
      } catch (BadLocationException e1) {
        e1.printStackTrace();
      }
    }
    textPane.requestFocus();
    textPane.setText(e.getKeyChar() + "");
  }
}
