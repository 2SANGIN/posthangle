package hjsi.posthangeul.editor;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * @author SANGIN
 *
 */
public class WordPopupList extends JList<WordItem>implements ListCellRenderer<JPanel> {
   @SuppressWarnings("javadoc")
   private static final long serialVersionUID = -3132671555329612391L;

   /**
    *
    */
   public WordPopupList() {
      this.setCellRenderer(this);
      this.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            int index = WordPopupList.this.locationToIndex(e.getPoint());
            WordItem item = WordPopupList.this.getModel().getElementAt(index);
            if (!item.isTargetRemoved())
               System.out.println(item.getText());
         }
      });
   }

   @Override
   public Component getListCellRendererComponent(JList<? extends JPanel> list, JPanel value,
         int index, boolean isSelected, boolean cellHasFocus) {

      value.setEnabled(list.isEnabled());
      value.setFont(list.getFont());

      if (isSelected) {
         value.setBackground(list.getSelectionBackground());
         value.setForeground(list.getSelectionForeground());
      } else if (cellHasFocus) {
         value.setBackground(list.getSelectionBackground());
         value.setForeground(list.getForeground());
      } else {
         value.setBackground(list.getBackground());
         value.setForeground(list.getForeground());
      }

      return value;
   }
}
