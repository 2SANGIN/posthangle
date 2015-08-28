package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

public class WordPopup extends JScrollPane implements ListCellRenderer<JLabel> {
   private class DeleteButton extends JLabel {
      private static final long serialVersionUID = -4207557892950479325L;
      private int index;

      DeleteButton(int index) {
         super("X");
         this.setIndex(index);
         setForeground(Color.LIGHT_GRAY);
         setFont(fontNanumGothic);
      }

      public int getIndex() {
         return index;
      }

      public void setIndex(int index) {
         this.index = index;
      }
   }

   private static final long serialVersionUID = 2407219411214780267L;
   static JLabel delButton = null;
   static Font fontNanumGothic = new Font("나눔고딕", Font.PLAIN, 12);

   JPanel wrapper = new JPanel();
   JList<JLabel> wordList = new JList<>(); // left
   JPanel deletePanel = new JPanel(); // right

   {
      /* 래퍼 설정 */
      wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
      wrapper.add(wordList);
      wrapper.add(Box.createHorizontalGlue());
      wrapper.add(deletePanel);
      this.getViewport().add(wrapper);

      /* 좌측 공간을 차지하는 단어 리스트 설정 */
      this.wordList.setFont(fontNanumGothic);
      this.wordList.setCellRenderer(this);

      /* 우측 공간을 차지하는 삭제 버튼 패널 설정 */
      deletePanel.setLayout(new BoxLayout(deletePanel, BoxLayout.Y_AXIS));
      deletePanel.setFont(fontNanumGothic);

      /* 공통으로 쓸 delButton 이미지 생성 */
      try {
         File resPath = new File("resources");
         Image img = ImageIO.read(new File(resPath, "btn_auto_del.png")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         delButton = new JLabel(new ImageIcon(img));
         delButton.setVerticalAlignment(SwingConstants.CENTER);
         delButton.setHorizontalAlignment(SwingConstants.CENTER);
         delButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void clearSelection() {
      this.wordList.clearSelection();
   }

   @Override
   public Component getListCellRendererComponent(JList<? extends JLabel> list, JLabel value,
         int index, boolean isSelected, boolean cellHasFocus) {
      value.setEnabled(list.isEnabled());
      value.setFont(list.getFont());

      if (isSelected) {
         value.setBackground(Color.CYAN);
         value.setForeground(Color.BLACK);
      } else if (cellHasFocus) {
         value.setBackground(list.getSelectionBackground());
         value.setForeground(list.getForeground());
      } else {
         value.setBackground(Color.WHITE);
         value.setForeground(Color.BLACK);
      }

      return value;
   }

   public int getListSize() {
      return this.wordList.getModel().getSize();
   }

   public int getSelectedIndex() {
      return this.wordList.getSelectedIndex();
   }

   public String getSelectedWord() {
      if (!isSelectionEmpty()) {
         return this.wordList.getModel().getElementAt(this.wordList.getSelectedIndex()).getText();
      }
      return null;
   }

   public boolean isSelectionEmpty() {
      return this.wordList.isSelectionEmpty();
   }

   public void moveSelection(int keyCode) {
      int index = 0;
      if (!isSelectionEmpty()) {
         if (keyCode == KeyEvent.VK_UP)
            index = (getSelectedIndex() + getListSize() - 1) % getListSize();
         else if (keyCode == KeyEvent.VK_DOWN)
            index = (getSelectedIndex() + 1) % getListSize();
      }
      setSelectedIndex(index);
   }

   public void setBackgroundColorAll(Color bg) {
      this.setBackground(bg);
      this.getViewport().setBackground(bg);
      this.getViewport().getView().setBackground(bg);
      this.wordList.setBackground(bg);
      this.deletePanel.setBackground(bg);
   }

   public void setSelectedIndex(int index) {
      this.wordList.setSelectedIndex(index);
   }

   public void setWordList(List<String> words) {
      Vector<JLabel> wordLabels = new Vector<>();
      deletePanel.removeAll();
      FontMetrics metric = getFontMetrics(fontNanumGothic);
      int i = 0;
      int maxWidth = 0;
      for (String str : words) {
         System.out.println(str);
         maxWidth = Math.max(maxWidth, metric.stringWidth(str) + metric.stringWidth(" X"));
         wordLabels.add(new JLabel(str));
         deletePanel.add(new DeleteButton(i));
         i++;
      }
      wordList.setListData(wordLabels);
      if (i > 0) {
         Dimension size = new Dimension();
         wordList.repaint();
         size.width = maxWidth;
         size.height = metric.getHeight() * i;
         wrapper.setSize(size);
         System.out.println(size);

         /* 스크롤패널 자체 너비 확장 */
         size.width += this.getInsets().left + this.getInsets().right;
         size.height += this.getInsets().top + this.getInsets().bottom;
         System.out.println(size);

         if (this.getVerticalScrollBar().isVisible())
            size.width += this.getVerticalScrollBar().getWidth();

         if (this.getHorizontalScrollBar().isVisible())
            size.height += this.getHorizontalScrollBar().getHeight();

         System.out.println(size);
         this.setSize(size.width, size.height);
      }
   }
}
