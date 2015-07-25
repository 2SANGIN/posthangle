package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit.StyledTextAction;

public class FontUnderlineAction extends StyledTextAction {
  private static final long serialVersionUID = -2332274754913249885L;

  public FontUnderlineAction() {
    super("font-underline");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JEditorPane editor = getEditor(e);

    MutableAttributeSet attr = getStyledEditorKit(editor).getInputAttributes();
    boolean toApply = !StyleConstants.isUnderline(attr);
    SimpleAttributeSet sas = new SimpleAttributeSet();
    StyleConstants.setUnderline(sas, toApply);
    setCharacterAttributes(editor, sas, false);

    editor.requestFocus();
  }
}
