package hjsi.posthangeul.editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

/**
 * remove one line by ctrl + BackSpace
 */
public class RemoveLine {
	int lineNum;
	JTextPane parent;
	
	public RemoveLine(JTextPane textPane) {
		parent = textPane;
		parent.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if(ke.isControlDown()) {
						try {
							int start = Utilities.getRowStart(textPane, textPane.getCaretPosition());
							int end = Utilities.getRowEnd(textPane, textPane.getCaretPosition());
							textPane.getDocument().remove(start, end-start);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
	}
}
