package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JEditorPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.StyledTextAction;

public class FontSizeAction extends StyledTextAction {
  private static final long serialVersionUID = -795689201716588660L;

  private ArrayList<Integer> fontSizes;

  public FontSizeAction(boolean isToIncrease, int[] fontSizes) {
    super("font-size");
    this.fontSizes = new ArrayList<Integer>();

    for (Integer size : fontSizes) {
      this.fontSizes.add(size);
    }

    this.fontSizes.sort(new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
        if (isToIncrease)
          return o1.compareTo(o2);
        else
          return o2.compareTo(o1);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JEditorPane editor = getEditor(e);

    StyledEditorKit kit = getStyledEditorKit(editor);
    MutableAttributeSet mas = kit.getInputAttributes();

    int fontSize = StyleConstants.getFontSize(mas);
    int index = fontSizes.indexOf(fontSize);

    if (index + 1 < fontSizes.size()) {
      fontSize = fontSizes.get(index + 1);

      SimpleAttributeSet sas = new SimpleAttributeSet();
      StyleConstants.setFontSize(sas, fontSize);
      setCharacterAttributes(editor, sas, false);
    }

    editor.requestFocus();
  }
}
