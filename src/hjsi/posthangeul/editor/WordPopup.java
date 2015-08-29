package hjsi.posthangeul.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * WordPopup <br>
 * 단어 목록을 보여주는 스크롤 패널 <br>
 * 2015. 8. 28.
 *
 * @author SANGIN
 */
public class WordPopup extends JScrollPane {
   @SuppressWarnings("javadoc")
   private static final long serialVersionUID = 2407219411214780267L;
   /**
    * 버튼을 위한 폰트
    */
   private static Font fontNanumGothic;

   static {
      /* font 리소스 불러옴 */
      File fp = new File("fonts/NanumGothic.ttf");
      try {
         fontNanumGothic = Font.createFont(Font.TRUETYPE_FONT, fp).deriveFont(Font.PLAIN, 16);
      } catch (FontFormatException | IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * word 패널과 xbtn 패널을 묶어주는 wrapper 패널
    */
   private JPanel wrapper = new JPanel();

   /**
    * 아이템
    */
   private ArrayList<JPanel> items = new ArrayList<>();

   /**
    * 현재 선택된 항목의 인덱스
    */
   private int selectedIndex = -1;

   /**
    * 현재 호버링된 항목의 인덱스
    */
   private int hoverIndex = -1;

   /**
    * 현재 선택된 단어, X버튼의 배경색
    */
   private Color selectedBgColor = new Color(128, 220, 220, 155);

   /**
    * 현재 선택된 단어의 전경색
    */
   private Color selectedFgColor = Color.BLACK;

   /**
    * 현재 호버링된 단어, X버튼의 배경색
    */
   private Color hoverBgColor = new Color(128, 220, 220, 60);

   /**
    * 현재 호버링된 단어의 전경색
    */
   private Color hoverFgColor = Color.BLACK;

   /**
    * 선택되지 않은 기본 배경색
    */
   private Color normalBgColor = Color.WHITE;

   /**
    * 선택되지 않은 단어의 기본 전경색
    */
   private Color normalFgColor = Color.BLACK;

   /**
    * 팝업이 보여줄 최소 행 수
    */
   private int minRowCount = 5;
   /**
    * 팝업이 보여줄 최대 행 수
    */
   private int maxRowCount = 15;

   /**
    * 팝업 자체의 패딩
    */
   private Insets padding;

   /**
    * 선택 이벤트 리스너
    */
   private MouseListener selectListener;

   /**
    * 삭제 이벤트 리스너
    */
   private MouseListener deleteListener;

   /**
    * 단어 목록을 보여주는 팝업 객체를 생성한다.
    */
   public WordPopup() {
      /* wrapper 패널 설정 */
      this.wrapper.setLayout(new BoxLayout(this.wrapper, BoxLayout.Y_AXIS));
      this.wrapper.setBackground(Color.WHITE);

      /* viewport 설정 */
      this.setViewportView(this.wrapper);

      /* scrollpanel 자체 설정 */
      this.padding = this.getInsets();
      this.setSize(300 + this.padding.left + this.padding.right, 220);
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      this.getVerticalScrollBar().addAdjustmentListener(e -> WordPopup.this.wrapper.repaint());
   }

   /**
    * 선택 상태를 해제한다.
    */
   public void clearSelection() {
      if (this.selectedIndex > -1) {
         if (this.selectedIndex == this.hoverIndex)
            this.hoverItem(this.hoverIndex);
         else
            this.deselectItem(this.selectedIndex);
      }
      this.selectedIndex = -1;
   }

   /**
    * 인덱스에 해당하는 단어를 가져온다.
    *
    * @param index 가져올 단어의 인덱스
    * @return 인덱스에 해당하는 단어가 있으면 String 객체, 없으면 null
    */
   public String getItemAt(int index) {
      if (index >= 0 && index < this.getItemCount())
         return this.getLabel(index).getText();
      return null;
   }

   /**
    * 단어 목록의 갯수를 반환한다.
    *
    * @return 단어의 갯수 >= 0
    */
   public int getItemCount() {
      return this.items.size();
   }

   /**
    * 현재 선택된 인덱스를 가져온다.
    *
    * @return 선택된 상태라면 인덱스 >= 0, 선택되어 있지 않다면 -1
    */
   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   /**
    * 선택되어 있는 단어를 반환한다.
    *
    * @return 선택된 단어가 있으면 String 객체, 아니라면 null
    */
   public String getSelectedItem() {
      return this.getItemAt(this.getSelectedIndex());
   }

   /**
    * 현재 선택된 아이템이 화면에 보이도록 스크롤바 위치 갱신
    */
   public void gotoScroll() {
      JPanel selected = this.items.get(this.getSelectedIndex());
      int yTop = selected.getY();
      int yBottom = selected.getY() + selected.getHeight();
      int posTop = this.getVerticalScrollBar().getValue();
      int posBottom = posTop + this.getViewport().getHeight();

      if (yTop < posTop)
         this.getVerticalScrollBar().setValue(yTop);
      else if (yBottom > posBottom)
         this.getVerticalScrollBar().setValue(posTop - (posBottom - yBottom));
   }

   /**
    * 현재 아무 단어도 선택되어 있지 않는지 검사한다.
    *
    * @return 선택된 단어가 없으면 <b>true</b>, 아니라면 <b>false</b>
    */
   public boolean isSelectionEmpty() {
      return this.selectedIndex == -1;
   }

   /**
    * 방향키에 입력에 따라 선택인덱스 이동을 처리한다.
    *
    * @param keyCode 방향키 코드 값 (<b>KeyEvent.VK_UP</b> or <b>KeyEvent.VK_DOWN</b>)
    * @throws InvalidParameterException keyCode가 방향키 왼쪽, 오른쪽 이외일 경우
    */
   public void moveSelection(int keyCode) throws InvalidParameterException {
      int index = 0;
      if (!this.isSelectionEmpty()) {
         if (keyCode == KeyEvent.VK_UP)
            index = (this.getSelectedIndex() + this.getItemCount() - 1) % this.getItemCount();
         else if (keyCode == KeyEvent.VK_DOWN)
            index = (this.getSelectedIndex() + 1) % this.getItemCount();
         else
            throw new InvalidParameterException("올바른 매개변수가 아닙니다.");
      }
      if (this.getItemCount() > 0) {
         this.setSelectedIndex(index);
         this.gotoScroll();
      } else
         this.clearSelection();
   }

   /**
    * 보여줄 내용에 맞춰 팝업의 크기를 조절한다.
    */
   public void pack() {
      /* 세로 길이 계산 */
      int h = this.getRowHeight()
            * Math.max(this.minRowCount, Math.min(this.maxRowCount, this.getItemCount()));

      /* 내용물 바깥의 요소 크기 계산 */
      h += this.padding.left + this.padding.right;
      this.setSize(this.getWidth(), h);
   }

   /**
    * 선택 된 아이템을 제거함
    */
   public void removeSelectedItem() {
      if (this.selectedIndex > -1) {
         this.wrapper.remove(this.selectedIndex);
         this.items.remove(this.selectedIndex);
         this.setVisible(false);
         if (this.getItemCount() > 0)
            this.setVisible(true);
      }
   }

   /**
    * 단어 삭제를 처리할 리스너를 등록한다.
    *
    * @param listener 클릭 이벤트를 구현한 리스너 혹은 어댑터 객체
    */
   public void setDeleteListener(MouseListener listener) {
      this.deleteListener = listener;
   }

   /**
    * 선택된 인덱스를 주어진 매개변수로 설정한다. 새로 선택된 아이템의 배경색도 갱신된다.
    *
    * @param index index >= 0, index < 단어 목록의 갯수 미만으로 입력해야 한다.
    * @throws IndexOutOfBoundsException index가 0 미만, 혹은 단어 목록의 size 이상
    */
   public void setSelectedIndex(int index) throws IndexOutOfBoundsException {
      if (index >= 0 && index < this.getItemCount()) {
         if (this.selectedIndex == this.hoverIndex)
            this.hoverItem(this.hoverIndex);
         else
            this.deselectItem(this.selectedIndex);
         this.selectedIndex = index;
         this.selectItem(this.selectedIndex);
      } else
         throw new IndexOutOfBoundsException("주어진 매개변수가 리스트의 범위를 벗어남.");
   }

   /**
    * 단어 선택을 처리할 리스너를 등록한다.
    *
    * @param listener 클릭 이벤트를 구현한 리스너 혹은 어댑터 객체
    */
   public void setSelectListener(MouseListener listener) {
      this.selectListener = listener;
   }

   @Override
   public void setVisible(boolean aFlag) {
      this.clearSelection();
      this.clearHover();
      super.setVisible(aFlag);
   }

   /**
    * 팝업 목록에 단어를 설정한다.
    *
    * @param words 팝업 목록에 넣을 단어 벡터리스트
    */
   public void setWordList(Vector<String> words) {
      this.clearSelection();
      this.clearHover();
      this.items.clear();
      for (String word : words) {
         this.items.add(this.createWordItem(word));
      }
      this.wrapper.removeAll();
      for (JPanel item : this.items) {
         this.wrapper.add(item);
      }
   }

   /**
    * 단어 라벨을 생성해서 반환한다.
    *
    * @param text 단어
    * @return word 라벨
    */
   private JLabel createWord(String text) {
      JLabel word = new JLabel(text);
      word.setFont(fontNanumGothic);
      word.setForeground(this.normalFgColor);
      // word.addMouseListener(new MouseAdapter() {
      // @Override
      // public void mouseEntered(MouseEvent e) {
      // int y = ((JLabel) e.getSource()).getParent().getY();
      // WordPopup.this.setHoverIndex((y / WordPopup.this.getRowHeight()));
      // }
      //
      // @Override
      // public void mouseExited(MouseEvent e) {
      // WordPopup.this.clearHover();
      // }
      // });
      return word;
   }

   /**
    * WordItem을 생성한다.
    *
    * @param text 단어 텍스트
    * @return 단어와 삭제 버튼으로 구성된 한 패널을 반환한다.
    */
   private JPanel createWordItem(String text) {
      JPanel item = new JPanel();
      item.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      item.setBackground(this.normalBgColor);
      item.setBorder(new EmptyBorder(2, 3, 2, 3));
      item.setLayout(new BoxLayout(item, BoxLayout.X_AXIS));
      item.add(this.createWord(text));
      item.add(Box.createHorizontalGlue());
      item.add(this.createXButton());
      item.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            int y = ((JPanel) e.getSource()).getY();
            WordPopup.this.setSelectedIndex((y / WordPopup.this.getRowHeight()));
            // System.out.println(WordPopup.this.getSelectedWord());
         }

         @Override
         public void mouseEntered(MouseEvent e) {
            int y = ((JPanel) e.getSource()).getY();
            WordPopup.this.setHoverIndex((y / WordPopup.this.getRowHeight()));
         }

         @Override
         public void mouseExited(MouseEvent e) {
            WordPopup.this.clearHover();
         }
      });
      if (this.selectListener != null)
         item.addMouseListener(this.selectListener);
      return item;
   }

   /**
    * 삭제용 버튼을 생성해서 반환한다.
    *
    * @return x button 라벨
    */
   private JLabel createXButton() {
      JLabel xbtn = new JLabel("X");
      xbtn.setFont(fontNanumGothic);
      xbtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
      xbtn.setForeground(Color.LIGHT_GRAY);
      xbtn.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            int y = ((JLabel) e.getSource()).getParent().getY();
            WordPopup.this.setSelectedIndex((y / WordPopup.this.getRowHeight()));
         }

         @Override
         public void mouseEntered(MouseEvent e) {
            int y = ((JLabel) e.getSource()).getParent().getY();
            WordPopup.this.setHoverIndex((y / WordPopup.this.getRowHeight()));
            xbtn.setForeground(Color.RED);
         }

         @Override
         public void mouseExited(MouseEvent e) {
            WordPopup.this.clearHover();
            xbtn.setForeground(Color.LIGHT_GRAY);
         }
      });
      if (this.deleteListener != null)
         xbtn.addMouseListener(this.deleteListener);
      return xbtn;
   }

   /**
    * 호버 상태를 해제한다.
    */
   protected void clearHover() {
      if (this.hoverIndex > -1) {
         if (this.hoverIndex == this.selectedIndex)
            this.selectItem(this.selectedIndex);
         else
            this.deselectItem(this.hoverIndex);
      }
      this.hoverIndex = -1;
   }

   /**
    * 주어진 인덱스의 아이템의 색상을 선택 해제 된 일반 색상으로 한다.
    *
    * @param index 선택해제할 인덱스 >= 0, 인덱스 < itemCount
    * @return 선택 해제 된 아이템 혹은 null
    */
   protected String deselectItem(int index) {
      if (index >= 0 && index < this.getItemCount()) {
         this.items.get(index).setBackground(this.normalBgColor);
         this.getLabel(index).setForeground(this.normalFgColor);
         this.wrapper.repaint();
         return this.getItemAt(index);
      }
      return null;
   }

   /**
    * @return the hoverIndex
    */
   protected int getHoverIndex() {
      return this.hoverIndex;
   }

   /**
    * 인덱스에 해당하는 단어 JLabel 객체를 가져온다.
    *
    * @param index 가져올 라벨의 인덱스
    * @return 인덱스에 해당하는 라벨이 있으면 JLabel 객체, 없으면 null;
    */
   protected JLabel getLabel(int index) {
      if (index >= 0 && index < this.getItemCount())
         return (JLabel) this.items.get(index).getComponent(0);
      return null;
   }

   /**
    * 단어 목록의 한 개의 행 높이를 반환한다.
    *
    * @return 단어 목록의 한 행의 높이, 목록이 없으면 0
    */
   protected int getRowHeight() {
      if (this.getItemCount() > 0)
         return this.items.get(0).getHeight();
      return 0;
   }

   /**
    * 주어진 인덱스의 아이템의 색상을 hover 색상으로 한다.
    *
    * @param index 선택해제할 인덱스 >= 0, 인덱스 < itemCount
    * @return 마우스오버 된 아이템 혹은 null
    */
   protected String hoverItem(int index) {
      if (index >= 0 && index < this.getItemCount()) {
         this.items.get(index).setBackground(this.hoverBgColor);
         this.getLabel(index).setForeground(this.hoverFgColor);
         this.wrapper.repaint();
         return this.getItemAt(index);
      }
      return null;
   }

   /**
    * 주어진 인덱스의 아이템의 색상을 선택 된 일반 색상으로 한다.
    *
    * @param index 선택해제할 인덱스 >= 0, 인덱스 < itemCount
    * @return 선택 된 아이템 혹은 null
    */
   protected String selectItem(int index) {
      if (index >= 0 && index < this.getItemCount()) {
         this.items.get(index).setBackground(this.selectedBgColor);
         this.getLabel(index).setForeground(this.selectedFgColor);
         this.wrapper.repaint();
         return this.getItemAt(index);
      }
      return null;
   }

   /**
    * 마우스가 올라와있는 인덱스를 설정한다.
    *
    * @param index the hoverIndex to set
    * @throws IndexOutOfBoundsException index가 0 미만, 혹은 단어 목록의 size 이상
    */
   protected void setHoverIndex(int index) throws IndexOutOfBoundsException {
      if (index >= 0 && index < this.getItemCount()) {
         if (this.hoverIndex == this.selectedIndex)
            this.deselectItem(this.hoverIndex);
         if (index == this.selectedIndex)
            this.selectItem(index);
         else
            this.hoverItem(index);
         this.hoverIndex = index;
      } else
         throw new IndexOutOfBoundsException("주어진 매개변수가 리스트의 범위를 벗어남.");
   }
}
