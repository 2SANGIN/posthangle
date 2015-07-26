package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import hjsi.posthangeul.action.FileNewAction;
import hjsi.posthangeul.action.FileOpenAction;
import hjsi.posthangeul.action.FileSaveAction;
import hjsi.posthangeul.action.FontBoldAction;
import hjsi.posthangeul.action.FontFamilyAction;
import hjsi.posthangeul.action.FontForegroundAction;
import hjsi.posthangeul.action.FontHighlightAction;
import hjsi.posthangeul.action.FontItalicAction;
import hjsi.posthangeul.action.FontSizeAction;
import hjsi.posthangeul.action.FontUnderlineAction;

public class Shortcut extends JPanel {
  private static final long serialVersionUID = -5056855136164482441L;
  private int buttonSize = 32;

  /* file processing buttons */
  JButton btnFileNew = new JButton();
  JButton btnFileOpen = new JButton();
  JButton btnFileSave = new JButton();
  JButton btnFileSaveAs = new JButton();

  /* font emphasis & decoration buttons */
  JButton btnFontBold = new JButton();
  JButton btnFontItalic = new JButton();
  JButton btnFontUnderline = new JButton();
  JButton btnFontSizeUp = new JButton();
  JButton btnFontSizeDown = new JButton();

  /* font foreground & background color buttons */
  JButton btnFontColor = new JButton();
  JButton btnHighlightRed = new JButton();
  JButton btnHighlightBlue = new JButton();
  JButton btnHighlightYellow = new JButton();

  JComboBox<String> comboFontFamily = new JComboBox<String>();
  JButton btnKeyCode = new JButton();

  public Shortcut(PostHangeulApp app, int btnSize) {
    buttonSize = btnSize;

    setLayout(new BorderLayout());

    JPanel topNavi = new JPanel();
    topNavi.setLayout(new FlowLayout(FlowLayout.LEFT));

    JToolBar toolbar = new JToolBar();

    // add(topNavi, BorderLayout.NORTH);
    add(toolbar, BorderLayout.CENTER);

    Image image = null;
    File fpPath = new File("resources");

    /* set image to each buttons */
    try {
      // new file
      image = ImageIO.read(new File(fpPath, "file-new.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFileNew.setIcon(new ImageIcon(image));

      // open file
      image = ImageIO.read(new File(fpPath, "file-open.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFileOpen.setIcon(new ImageIcon(image));

      // save file
      image = ImageIO.read(new File(fpPath, "file-save.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFileSave.setIcon(new ImageIcon(image));

      // save as file
      image = ImageIO.read(new File(fpPath, "file-save-as.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFileSaveAs.setIcon(new ImageIcon(image));

      // font bold
      image = ImageIO.read(new File(fpPath, "font-bold.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFontBold.setIcon(new ImageIcon(image));

      // font italic
      image = ImageIO.read(new File(fpPath, "font-italic.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFontItalic.setIcon(new ImageIcon(image));

      // font underline
      image = ImageIO.read(new File(fpPath, "font-underline.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFontUnderline.setIcon(new ImageIcon(image));

      // font size up
      image = ImageIO.read(new File(fpPath, "font-size-up.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFontSizeUp.setIcon(new ImageIcon(image));

      // font size down
      image = ImageIO.read(new File(fpPath, "font-size-down.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnFontSizeDown.setIcon(new ImageIcon(image));

      // font highlight red
      image = ImageIO.read(new File(fpPath, "font-highlight-red.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnHighlightRed.setIcon(new ImageIcon(image));

      // font highlight blue
      image = ImageIO.read(new File(fpPath, "font-highlight-blue.png"))
          .getScaledInstance(buttonSize, buttonSize, Image.SCALE_AREA_AVERAGING);
      btnHighlightBlue.setIcon(new ImageIcon(image));

      // font highlight yellow
      image = ImageIO.read(new File(fpPath, "font-highlight-yellow.png"))
          .getScaledInstance(buttonSize, buttonSize, Image.SCALE_AREA_AVERAGING);
      btnHighlightYellow.setIcon(new ImageIcon(image));

      // keycode viewer
      image = ImageIO.read(new File(fpPath, "window-ascii.png")).getScaledInstance(buttonSize,
          buttonSize, Image.SCALE_AREA_AVERAGING);
      btnKeyCode.setIcon(new ImageIcon(image));
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    /* file processing buttons */
    btnFileNew.setOpaque(false);
    btnFileNew.setMargin(new Insets(0, 0, 0, 0));
    btnFileNew.addActionListener(new FileNewAction(app));

    btnFileOpen.setOpaque(false);
    btnFileOpen.setMargin(new Insets(0, 0, 0, 0));
    btnFileOpen.addActionListener(new FileOpenAction(app));

    btnFileSave.setOpaque(false);
    btnFileSave.setMargin(new Insets(0, 0, 0, 0));
    btnFileSave.addActionListener(new FileSaveAction(app, false));

    btnFileSaveAs.setOpaque(false);
    btnFileSaveAs.setMargin(new Insets(0, 0, 0, 0));
    btnFileSaveAs.addActionListener(new FileSaveAction(app, true));
    /* end of file processing buttons */

    /* font emphasis & decoration buttons */
    btnFontSizeUp.setOpaque(false);
    btnFontSizeUp.setMargin(new Insets(0, 0, 0, 0));
    btnFontSizeUp.addActionListener(new FontSizeAction(true, new int[] {9, 12, 16, 22}));

    btnFontSizeDown.setOpaque(false);
    btnFontSizeDown.setMargin(new Insets(0, 0, 0, 0));
    btnFontSizeDown.addActionListener(new FontSizeAction(false, new int[] {9, 12, 16, 22}));

    btnFontBold.setOpaque(false);
    btnFontBold.setMargin(new Insets(0, 0, 0, 0));
    btnFontBold.addActionListener(new FontBoldAction());

    btnFontItalic.setOpaque(false);
    btnFontItalic.setMargin(new Insets(0, 0, 0, 0));
    btnFontItalic.addActionListener(new FontItalicAction());

    btnFontUnderline.setOpaque(false);
    btnFontUnderline.setMargin(new Insets(0, 0, 0, 0));
    btnFontUnderline.addActionListener(new FontUnderlineAction());
    /* end of font emphasis & decoration buttons */

    /* font foreground & background color buttons */
    btnFontColor.setText("C");
    btnFontColor.setOpaque(false);
    btnFontColor.setMargin(new Insets(0, 0, 0, 0));
    btnFontColor.addActionListener(new FontForegroundAction());

    btnHighlightRed.setOpaque(false);
    btnHighlightRed.setMargin(new Insets(0, 0, 0, 0));
    btnHighlightRed.addActionListener(new FontHighlightAction(Color.RED));

    btnHighlightBlue.setOpaque(false);
    btnHighlightBlue.setMargin(new Insets(0, 0, 0, 0));
    btnHighlightBlue.addActionListener(new FontHighlightAction(Color.BLUE));

    btnHighlightYellow.setOpaque(false);
    btnHighlightYellow.setMargin(new Insets(0, 0, 0, 0));
    btnHighlightYellow.addActionListener(new FontHighlightAction(Color.YELLOW));
    /* end of font foreground & background color buttons */

    /* font family combobox */
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    LinkedList<String> fontFamilies = new LinkedList<String>();
    for (String fontFam : ge.getAvailableFontFamilyNames()) {
      fontFam = fontFam.trim();
      if (fontFam.startsWith("@") == false) {
        fontFamilies.add(fontFam);
        comboFontFamily.addItem(fontFam);
      }
    }
    String toApplyToDefault = null;
    for (String fontFam : app.defaultFontFamilies) {
      if (fontFamilies.contains(fontFam)) {
        toApplyToDefault = fontFam;
        break;
      }
    }
    if (toApplyToDefault != null) {
      comboFontFamily.setSelectedItem(toApplyToDefault);
      app.editor.setFont(toApplyToDefault, app.defaultFontSize);
    }
    comboFontFamily.setPreferredSize(new Dimension(80, 24));
    comboFontFamily.addActionListener(new FontFamilyAction());

    /* etc buttons... */
    btnKeyCode.setOpaque(false);
    btnKeyCode.setMargin(new Insets(0, 0, 0, 3));
    btnKeyCode.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        app.keyCodeViewer.setVisible(true);
      }
    });

    // topNavi.add(btnFileOpen);
    // topNavi.add(btnFileSave);
    // topNavi.add(btnFileSaveAs);
    // topNavi.add(btnFontBold);
    // topNavi.add(btnFontItalic);
    // topNavi.add(btnFontColor);
    // topNavi.add(comboFontFamily);
    // topNavi.add(btnKeyCode);

    toolbar.add(btnFileNew);
    toolbar.add(btnFileOpen);
    toolbar.add(btnFileSave);
    toolbar.add(btnFileSaveAs);
    toolbar.addSeparator();
    toolbar.add(btnFontBold);
    toolbar.add(btnFontItalic);
    toolbar.add(btnFontUnderline);
    toolbar.add(btnFontSizeUp);
    toolbar.add(btnFontSizeDown);
    toolbar.addSeparator();
    toolbar.add(btnFontColor);
    toolbar.add(btnHighlightRed);
    toolbar.add(btnHighlightBlue);
    toolbar.add(btnHighlightYellow);
    toolbar.addSeparator();
    toolbar.add(comboFontFamily);
    toolbar.add(btnKeyCode);
  }
}
