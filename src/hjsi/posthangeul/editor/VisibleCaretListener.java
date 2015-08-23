package hjsi.posthangeul.editor;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class VisibleCaretListener implements CaretListener
{
	private int visiblePixels;

	/**
	 *  Convenience constructor to create a VisibleCaretListener using
	 *  the default value for visible pixels, which is set to 2.
	 */
	public VisibleCaretListener()
	{
		this(2);
	}

	/**
	 *  Create a VisibleCaretListener.
	 *
	 *  @param pixels the number of visible pixels after the caret.
	 */
	public VisibleCaretListener(int visiblePixels)
	{
		setVisiblePixels( visiblePixels );
	}

	/**
	 *  Get the number of visble pixels displayed after the Caret.
	 *
	 *  @return the number of visible pixels after the caret.
	 */
	public int getVisiblePixels()
	{
		return visiblePixels;
	}

	/**
	 *  Control the number of pixels that should be visible in the viewport
	 *  after the caret position.
	 *
	 *  @param pixels the number of visible pixels after the caret.
	 */
	public void setVisiblePixels(int visiblePixels)
	{
		this.visiblePixels = visiblePixels;
	}
//
//	Implement CaretListener interface
//
	public void caretUpdate(final CaretEvent e)
	{
		//  Attempt to scroll the viewport to make sure Caret is visible

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
   				try
   				{
       				JTextComponent component = (JTextComponent)e.getSource();
       				int position = component.getCaretPosition();
       				Rectangle r = component.modelToView(position);
       				r.x += visiblePixels;
       				component.scrollRectToVisible(r);
   				}
   				catch(Exception ble) {}
			}
		});
	}
}