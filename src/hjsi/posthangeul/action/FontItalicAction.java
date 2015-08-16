package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit.StyledTextAction;

public class FontItalicAction extends StyledTextAction {
  private static final long serialVersionUID = -1428340091100055456L;

  public FontItalicAction() {
    super("font-italic");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JEditorPane editor = getEditor(e);

    MutableAttributeSet attr = getStyledEditorKit(editor).getInputAttributes();
    boolean italic = !StyleConstants.isItalic(attr);
    SimpleAttributeSet sas = new SimpleAttributeSet();
    StyleConstants.setItalic(sas, italic);
    setCharacterAttributes(editor, sas, false);
    if (italic)
      ((JButton) e.getSource()).setBorder(new BevelBorder(BevelBorder.LOWERED));
    else
      ((JButton) e.getSource()).setBorder(new BevelBorder(BevelBorder.RAISED));

    editor.requestFocus();
  }
}
