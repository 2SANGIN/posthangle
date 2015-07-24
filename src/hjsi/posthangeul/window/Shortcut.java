package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import hjsi.posthangeul.FileNewAction;
import hjsi.posthangeul.action.BoldAction;
import hjsi.posthangeul.action.FileOpenAction;
import hjsi.posthangeul.action.FileSaveAction;
import hjsi.posthangeul.action.FontAction;
import hjsi.posthangeul.action.ForegroundAction;
import hjsi.posthangeul.action.ItalicAction;

public class Shortcut extends JPanel {
  private static final long serialVersionUID = -5056855136164482441L;

  JButton bold = new JButton();
  JButton italic = new JButton();
  JButton color = new JButton();
  JComboBox<String> font = new JComboBox<String>();
  JButton btnKeyCode = new JButton("KeyCode Viewer");

  JButton sizeUp = new JButton();
  JButton sizeDown = new JButton();

  JButton fileNew = new JButton();
  JButton fileOpen = new JButton();
  JButton fileSave = new JButton();
  JButton fileSaveAs = new JButton();

  public Shortcut(PostHangeulApp app) {
    setLayout(new BorderLayout());

    JPanel topNavi = new JPanel();
    topNavi.setLayout(new FlowLayout(FlowLayout.LEFT));

    JToolBar toolbar = new JToolBar();

    add(topNavi, BorderLayout.NORTH);
    add(toolbar, BorderLayout.CENTER);

    Image image = null;
    File fpPath = new File("resources");

    try {
      image = ImageIO.read(new File(fpPath, "file-new.png")).getScaledInstance(32, 32,
          Image.SCALE_AREA_AVERAGING);
      fileNew.setIcon(new ImageIcon(image));

      image = ImageIO.read(new File(fpPath, "file-open.png")).getScaledInstance(32, 32,
          Image.SCALE_AREA_AVERAGING);
      fileOpen.setIcon(new ImageIcon(image));

      image = ImageIO.read(new File(fpPath, "file-save.png")).getScaledInstance(32, 32,
          Image.SCALE_AREA_AVERAGING);
      fileSave.setIcon(new ImageIcon(image));

      image = ImageIO.read(new File(fpPath, "file-save-as.png")).getScaledInstance(32, 32,
          Image.SCALE_AREA_AVERAGING);
      fileSaveAs.setIcon(new ImageIcon(image));
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    /* file processing buttons */
    fileNew.setOpaque(false);
    fileNew.setContentAreaFilled(false);
    fileNew.setMargin(new Insets(0, 0, 0, 0));
    fileNew.addActionListener(new FileNewAction(app));

    fileOpen.setOpaque(false);
    fileOpen.setContentAreaFilled(false);
    fileOpen.setMargin(new Insets(0, 0, 0, 0));
    fileOpen.addActionListener(new FileOpenAction(app));

    fileSave.setOpaque(false);
    fileSave.setContentAreaFilled(false);
    fileSave.setMargin(new Insets(0, 0, 0, 0));
    fileSave.addActionListener(new FileSaveAction(app, false));

    fileSaveAs.setOpaque(false);
    fileSaveAs.setContentAreaFilled(false);
    fileSaveAs.setMargin(new Insets(0, 0, 0, 0));
    fileSaveAs.addActionListener(new FileSaveAction(app, true));
    /* end of file processing buttons */

    bold.setText("B");
    bold.setOpaque(false);
    bold.setContentAreaFilled(false);

    italic.setText("I");
    italic.setOpaque(false);
    italic.setContentAreaFilled(false);

    color.setText("C");
    color.setOpaque(false);
    color.setContentAreaFilled(false);

    Action boldAction = new BoldAction();
    boldAction.putValue(Action.NAME, "Bold");
    bold.addActionListener(boldAction);

    Action italicAction = new ItalicAction();
    italicAction.putValue(Action.NAME, "Italic");
    italic.addActionListener(italicAction);

    Action foregroundAction = new ForegroundAction();
    foregroundAction.putValue(Action.NAME, "Color");
    color.addActionListener(foregroundAction);

    Action formatTextAction = new FontAction();
    formatTextAction.putValue(Action.NAME, "Font");
    font.addActionListener(formatTextAction);

    btnKeyCode.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        app.keyCodeViewer.setVisible(true);
      }
    });

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontNames = ge.getAvailableFontFamilyNames();
    for (int i = 0; i < fontNames.length; i++) {
      font.addItem(fontNames[i]);
    }
    font.getSelectedItem();
    font.addActionListener(formatTextAction);

    // topNavi.add(fileOpen);
    // topNavi.add(fileSave);
    // topNavi.add(fileSaveAs);
    // topNavi.add(bold);
    // topNavi.add(italic);
    topNavi.add(color);
    topNavi.add(font);
    topNavi.add(btnKeyCode);

    toolbar.add(fileNew);
    toolbar.add(fileOpen);
    toolbar.add(fileSave);
    toolbar.add(fileSaveAs);
    toolbar.addSeparator();
    toolbar.add(bold);
    toolbar.add(italic);
  }
}


class VerticalBar extends JComponent {
  private static final long serialVersionUID = 7438238662160423918L;

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paint(java.awt.Graphics)
   */
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.drawRect(0, 0, 2, 30);
  }

}
