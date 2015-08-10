package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ColorPicker extends JDialog {
	private static final long serialVersionUID = 1830407727031054052L;
	JPanel buttons = new JPanel();
	JButton accept = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JColorChooser colorChooser = new JColorChooser();
	Color pickedColor;

	public ColorPicker() {
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (pickedColor != colorChooser.getColor())
					pickedColor = colorChooser.getColor();
				else
					pickedColor = null;
				dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pickedColor = null;
				dispose();
			}
		});

		buttons.add(accept);
		buttons.add(cancel);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(colorChooser, BorderLayout.CENTER);
		getContentPane().add(buttons, BorderLayout.SOUTH);
		setModal(true);
		pack();
	}

	public void openColorPicker(Color defaultColor) {
		colorChooser.setColor(defaultColor);
		setVisible(true);
	}

	/**
	 * @return the cancelled
	 */
	public boolean isCancelled() {
		return pickedColor == null;
	}

	/**
	 * @return the pickedColor
	 */
	public Color getPickedColor() {
		return pickedColor;
	}
}
