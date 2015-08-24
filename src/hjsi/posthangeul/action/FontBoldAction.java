package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

/**
 * 글꼴 강조 효과를 적용하는 액션
 *
 * @author SANGIN
 */
public class FontBoldAction extends StyledEditorKit.StyledTextAction {
   @SuppressWarnings("javadoc")
   private static final long serialVersionUID = 9174670038684056758L;

   /**
    * 글꼴 강조 효과를 적용하는 액션 객체 생성
    */
   public FontBoldAction() {
      super("font-bold"); //$NON-NLS-1$
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      JEditorPane editor = this.getEditor(e);
      if (editor != null) {
         StyledEditorKit kit = this.getStyledEditorKit(editor);
         MutableAttributeSet attr = kit.getInputAttributes();
         boolean bold = (StyleConstants.isBold(attr)) ? false : true;
         SimpleAttributeSet sas = new SimpleAttributeSet();
         StyleConstants.setBold(sas, bold);
         this.setCharacterAttributes(editor, sas, false);
         if (bold)
            ((JButton) e.getSource()).setBorder(new BevelBorder(BevelBorder.LOWERED));
         else
            ((JButton) e.getSource()).setBorder(new BevelBorder(BevelBorder.RAISED));

         editor.requestFocus();
      }
   }
}
