package hjsi.posthangeul.editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;

public class GoToLine {
	int lineNum;
	JTextPane parent;

	public GoToLine(JTextPane textPane) {
		parent = textPane;
		parent.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_L) {
					if (ke.isShiftDown()) {
						String str = JOptionPane.showInputDialog(parent, null, null);

						try {
							lineNum = Integer.parseInt(str);
							parent.setCaretPosition(0);
							parent.setCaretPosition(setcursor(lineNum));
						} catch (Exception e) {
							parent.setCaretPosition(textPane.getDocument().getLength());
						}
					}
				}
			}
		});
	}

	public int setcursor(int newlineno) {
		int pos = 0;
		int i = 0;
		String line = "";
		Scanner sc = new Scanner(parent.getText());
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			i++;
			if (newlineno > i) {
				pos = pos + line.length() + 1;
			}
		}
		sc.close();
		return pos;
	}

	public int getLineNum() {
		return lineNum;
	}
}
