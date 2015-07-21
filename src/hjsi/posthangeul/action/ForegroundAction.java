package hjsi.posthangeul.action;


import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import hjsi.posthangeul.window.ColorPicker;

public class ForegroundAction extends StyledEditorKit.StyledTextAction {

  private static final long serialVersionUID = 6384632651737400352L;
  ColorPicker picker;
  private Color fg;


  public ForegroundAction() {
    super("foreground");
  }

  public void actionPerformed(ActionEvent e) {
    JTextPane editor = (JTextPane) getEditor(e);

    if (editor == null) {
      JOptionPane.showMessageDialog(null,
          "You need to select the editor pane before you can change the color.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    int p0 = editor.getSelectionStart();
    StyledDocument doc = getStyledDocument(editor);
    Element paragraph = doc.getCharacterElement(p0);
    AttributeSet as = paragraph.getAttributes();
    fg = StyleConstants.getForeground(as);
    if (fg == null) {
      fg = Color.BLACK;
    }

    if (picker == null) {
      picker = new ColorPicker();
    }

    picker.openColorPicker(fg);
    if (!picker.isCancelled()) {
      MutableAttributeSet attr = new SimpleAttributeSet();
      StyleConstants.setForeground(attr, picker.getPickedColor());
      setCharacterAttributes(editor, attr, false);
    }

    editor.requestFocus();
  }

}
