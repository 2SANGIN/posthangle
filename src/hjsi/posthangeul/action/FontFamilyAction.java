package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

public class FontFamilyAction extends StyledEditorKit.StyledTextAction {
  private static final long serialVersionUID = 584531387732416339L;

  public FontFamilyAction() {
    super("font-family");
  }

  @SuppressWarnings("unchecked")
  public void actionPerformed(ActionEvent e) {
    JTextPane editor = (JTextPane) getEditor(e);

    /*
     * if (editor != null) { int p0 = editor.getSelectionStart(); StyledDocument doc =
     * getStyledDocument(editor); Element paragraph = doc.getCharacterElement(p0); AttributeSet as =
     * paragraph.getAttributes();
     * 
     * family = (String) editor.getSelectedText(); family = (String) ((JComboBox<String>)
     * e.getSource()).getSelectedItem();
     * 
     * MutableAttributeSet attr = null; if (editor != null) { StyledEditorKit kit =
     * getStyledEditorKit(editor); attr = kit.getInputAttributes();
     * StyleConstants.setFontFamily(attr, family); setCharacterAttributes(editor, attr, false); } }
     */

    if (e.getSource() instanceof JComboBox) {
      String family = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();
      SimpleAttributeSet sas = new SimpleAttributeSet();
      StyleConstants.setFontFamily(sas, family);

      int beforeSelectionStart = editor.getSelectionStart();
      int beforeSelectionEnd = editor.getSelectionEnd();
      editor.setSelectionStart(0);
      editor.setSelectionEnd(editor.getText().length());

      // apply font family
      setCharacterAttributes(editor, sas, false);

      editor.setSelectionStart(beforeSelectionStart);
      editor.setSelectionEnd(beforeSelectionEnd);
    }

    editor.requestFocus();
  }
}
