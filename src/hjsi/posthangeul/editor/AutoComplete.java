package hjsi.posthangeul.editor;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;



public class AutoComplete extends JPopupMenu{
	private static final long serialVersionUID = 4592225249925286812L;
	
	public AutoComplete(JTextPane textPane) {
		setOpaque(true);
		
		textPane.addKeyListener(new KeyAdapter() {
	    	public void keyPressed(KeyEvent e) {
	    	if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
	    		add(new JMenuItem("asd"));
	    		add(new JMenuItem("asd2"));
	    		try {
					Rectangle caret = textPane.modelToView(textPane.getCaretPosition());
					show(textPane, caret.x, caret.y + (int)caret.getHeight());
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
			else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				removeAll();
	    	}
	    		
		});
	}

}
