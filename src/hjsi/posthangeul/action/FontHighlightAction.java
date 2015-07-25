package hjsi.posthangeul.action;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit.StyledTextAction;

public class FontHighlightAction extends StyledTextAction {
  private static final long serialVersionUID = -2245364930013165488L;
  private Color highlightColor;

  public FontHighlightAction(Color bg) {
    super("font-highlight");
    highlightColor = bg;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JEditorPane editor = getEditor(e);

    MutableAttributeSet attr = getStyledEditorKit(editor).getInputAttributes();
    boolean isAlreadyApplied = StyleConstants.getBackground(attr) == highlightColor;
    SimpleAttributeSet sas = new SimpleAttributeSet();
    if (isAlreadyApplied)
      StyleConstants.setBackground(sas, editor.getBackground()); // remove highlight
    else
      StyleConstants.setBackground(sas, highlightColor);
    setCharacterAttributes(editor, sas, false);

    editor.requestFocus();
  }
}
