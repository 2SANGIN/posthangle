package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

public class ItalicAction extends StyledEditorKit.StyledTextAction {
  private static final long serialVersionUID = -1428340091100055456L;

  public ItalicAction() {
    super("font-italic");
  }

  @Override
  public String toString() {
    return "Italic";
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JEditorPane editor = getEditor(e);
    if (editor != null) {
      StyledEditorKit kit = getStyledEditorKit(editor);
      MutableAttributeSet attr = kit.getInputAttributes();
      boolean italic = (StyleConstants.isItalic(attr)) ? false : true;
      SimpleAttributeSet sas = new SimpleAttributeSet();
      StyleConstants.setItalic(sas, italic);
      setCharacterAttributes(editor, sas, false);
    }
    editor.requestFocus();
  }
}
