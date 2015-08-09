package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

public class FontBoldAction extends StyledEditorKit.StyledTextAction {
  private static final long serialVersionUID = 9174670038684056758L;

  public FontBoldAction() {
    super("font-bold");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JEditorPane editor = getEditor(e);
    if (editor != null) {
      StyledEditorKit kit = getStyledEditorKit(editor);
      MutableAttributeSet attr = kit.getInputAttributes();
      boolean bold = (StyleConstants.isBold(attr)) ? false : true;
      SimpleAttributeSet sas = new SimpleAttributeSet();
      StyleConstants.setBold(sas, bold);
      setCharacterAttributes(editor, sas, false);
      if (bold)
        ((JButton) e.getSource()).setBorder(new BevelBorder(BevelBorder.LOWERED));
      else
        ((JButton) e.getSource()).setBorder(new BevelBorder(BevelBorder.RAISED));
    }
    editor.requestFocus();
  }
}
