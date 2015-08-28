package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class WordItem extends JPanel {
   JLabel word;
   JLabel delButton;
   boolean remove = false;

   public WordItem(String word) {
      this.word = new JLabel(word);
      this.delButton = new JLabel("X");
      this.delButton.setForeground(Color.LIGHT_GRAY);
      this.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            WordItem.this.remove = true;
         }

         @Override
         public void mouseEntered(MouseEvent e) {
            WordItem.this.delButton.setForeground(Color.RED);
         }

         @Override
         public void mouseExited(MouseEvent e) {
            WordItem.this.delButton.setForeground(Color.LIGHT_GRAY);
         }
      });

      this.add(this.word);
      this.add(this.delButton);
   }

   public String getText() {
      return this.word.getText();
   }

   public boolean isTargetRemoved() {
      return this.remove;
   }
}
