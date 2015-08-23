package hjsi.posthangeul.editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Scanner;

import javax.swing.JTextPane;

public class SwitchLine {
	JTextPane parent;
	int curLine;
	int targetLine;

	public SwitchLine(JTextPane textPane) {
		parent = textPane;
		parent.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_UP) {
					if (ke.isAltDown()) {
						curLine = parent.getCaretPosition();
						targetLine = curLine - 1;
						parent.setCaretPosition(setcursor(targetLine));

					}
				}

				else if (ke.getKeyCode() == KeyEvent.VK_KP_DOWN) {
					if (ke.isAltDown()) {
						curLine = parent.getCaretPosition();
						targetLine = curLine + 1;

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

}
