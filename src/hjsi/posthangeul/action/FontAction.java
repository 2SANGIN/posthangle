package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class FontAction extends StyledEditorKit.StyledTextAction {
  private static final long serialVersionUID = 584531387732416339L;
  private String family;

  public FontAction() {
    super("Font");
  }

  public String toString() {
    return "Font";
  }

  public void actionPerformed(ActionEvent e) {
    JTextPane editor = (JTextPane) getEditor(e);
    if (editor != null) {
      int p0 = editor.getSelectionStart();
      StyledDocument doc = getStyledDocument(editor);
      Element paragraph = doc.getCharacterElement(p0);
      AttributeSet as = paragraph.getAttributes();

      family = (String) editor.getSelectedText();
      family = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();

      MutableAttributeSet attr = null;
      if (editor != null) {
        StyledEditorKit kit = getStyledEditorKit(editor);
        attr = kit.getInputAttributes();
        StyleConstants.setFontFamily(attr, family);
        setCharacterAttributes(editor, attr, false);
      }
      editor.requestFocus();
    }
  }
}
