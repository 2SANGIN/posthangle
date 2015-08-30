package hjsi.posthangeul.window;

import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.text.Element;


public class Helper extends JPanel implements KeyListener {
   private static Image bi;

   private JButton close;
   private JTextPane editor;
   private JLabel text;

   private int BackSpaceCount;
   private int lineCount;
   private boolean isStart;

   public Helper(JTextPane textPane) {
      close = new JButton();
      text = new JLabel();
      editor = textPane;
      BackSpaceCount = 0;
      lineCount = 0;
      isStart = true;

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
      close.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

      // Ctrl + Space 자동완성 팝업
      if (isStart && editor.getDocument().getLength() > 0) {
         text.setText("Ctrl Space로 자동완성 팝업을 띄우실 수 있습니다.");
         setVisible(true);
         isStart = false;
      }

      // Ctrl + Backspace 한줄 삭제
      else if (ke.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
         BackSpaceCount++;
         if (BackSpaceCount > 5) {
            text.setText("Shift BackSpace로 해당줄을 삭제하실수 있습니다.");
            refreshLocation();
            setVisible(true);
            BackSpaceCount = 0;
         }
      }

      // Ctrl + L 라인 이동
      else if (ke.getKeyCode() == KeyEvent.VK_UP || ke.getKeyCode() == KeyEvent.VK_DOWN) {
         lineCount++;
         System.out.println("linecount " + lineCount);
         if (lineCount > 3) {
            text.setText("Ctrl L로 줄 이동을 하실수 있습니다.");
            refreshLocation();
            setVisible(true);
            lineCount = 0;
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

   private int getCurLine() {
      Element root = editor.getDocument().getDefaultRootElement();
      return root.getElementIndex(editor.getCaretPosition()) + 1; // starts from 0
   }
}
