package hjsi.posthangeul.window;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;


public class Helper extends JPanel implements KeyListener {
   private static Image bi;

   private JButton close;
   private JTextPane editor;
   private JLabel text;

   public Helper(JTextPane textPane) {
      close = new JButton();
      text = new JLabel("Ctrl BackSpace로 전부 지우소!!");
      editor = textPane;
      try {
         bi = ImageIO.read(new File("resources", "close-icon.png")).getScaledInstance(20, 20,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

      close.setMargin(new Insets(0, 0, 0, 0));
      close.setIcon(new ImageIcon(bi));
      close.setBackground(new Color(238, 221, 130));
      close.setBorderPainted(false);
      close.setContentAreaFilled(false);

      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      add(text);
      add(Box.createHorizontalGlue());
      add(close);

      setLocation(0, editor.getSize().height);
      setOpaque(true);
      setSize(400, 40);
      setBackground(new Color(238, 221, 130));
      setVisible(false);

      editor.add(this);
      editor.addKeyListener(this);
      close.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            setVisible(false);
            editor.requestFocus();
         }
      });
   }

   @Override
   public void keyPressed(KeyEvent ke) {
      if (ke.getKeyCode() == KeyEvent.VK_F1) {
         if (ke.isControlDown() || ke.isAltDown()) {
            if (!isShowing()) {
               refreshLocation();
               setVisible(true);
            } else {
               setVisible(false);
            }
         }
      }
   }

   @Override
   public void keyReleased(KeyEvent e) {
      // TODO Auto-generated method stub

   }

   @Override
   public void keyTyped(KeyEvent e) {
      // TODO Auto-generated method stub
   }

   public void mouseClicked(MouseEvent e) {
      this.removeAll();
   }

   public void refreshLocation() {
      Point p = new Point(0, editor.getHeight() - this.getHeight());
      this.setLocation(p);
      repaint();
   }
}
