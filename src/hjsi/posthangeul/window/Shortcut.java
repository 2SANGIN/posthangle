package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;

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

public class Shortcut extends JPanel implements CaretListener {
   public static BevelBorder LOWERED_BORDER = new BevelBorder(BevelBorder.LOWERED);
   public static BevelBorder RAISED_BORDER = new BevelBorder(BevelBorder.RAISED);

   private static final long serialVersionUID = -5056855136164482441L;
   private int buttonSize = 32;

   /* file processing buttons */
   JButton btnFileNew = new JButton();
   JButton btnFileOpen = new JButton();

   JButton btnFileSave = new JButton();
   JButton btnFileSaveAs = new JButton();
   /* font emphasis & decoration buttons */
   JButton btnFontBold = new JButton();
   /* font foreground & background color buttons */
   JButton btnFontColor = new JButton();

   JButton btnFontItalic = new JButton();
   JButton btnFontSizeDown = new JButton();
   JButton btnFontSizeUp = new JButton();
   JButton btnFontUnderline = new JButton();
   JButton btnHighlightBlue = new JButton();

   JButton btnHighlightRed = new JButton();
   JButton btnHighlightYellow = new JButton();
   JButton btnKeyCode = new JButton();
   JComboBox<String> comboFontFamily = new JComboBox<String>();

   JToolBar toolbar;
   JPanel topNavi;

   public Shortcut(PostHangeulApp app, int btnSize) {
      buttonSize = btnSize;

      setLayout(new BorderLayout());

      topNavi = new JPanel();
      topNavi.setLayout(new FlowLayout(FlowLayout.LEFT));

      toolbar = new JToolBar();
      toolbar.setFloatable(false);

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
         btnFileNew.setBorder(RAISED_BORDER);

         // open file
         image = ImageIO.read(new File(fpPath, "file-open.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnFileOpen.setIcon(new ImageIcon(image));
         btnFileOpen.setBorder(RAISED_BORDER);

         // save file
         image = ImageIO.read(new File(fpPath, "file-save.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnFileSave.setIcon(new ImageIcon(image));
         btnFileSave.setBorder(RAISED_BORDER);

         // save as file
         image = ImageIO.read(new File(fpPath, "file-save-as.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnFileSaveAs.setIcon(new ImageIcon(image));
         btnFileSaveAs.setBorder(RAISED_BORDER);

         // font size up
         image = ImageIO.read(new File(fpPath, "font-size-up.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnFontSizeUp.setIcon(new ImageIcon(image));
         btnFontSizeUp.setBorder(RAISED_BORDER);

         // font size down
         image = ImageIO.read(new File(fpPath, "font-size-down.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnFontSizeDown.setIcon(new ImageIcon(image));
         btnFontSizeDown.setBorder(RAISED_BORDER);

         // font bold
         image = ImageIO.read(new File(fpPath, "font-bold.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnFontBold.setIcon(new ImageIcon(image));
         btnFontBold.setBorder(RAISED_BORDER);

         // font italic
         image = ImageIO.read(new File(fpPath, "font-italic.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnFontItalic.setIcon(new ImageIcon(image));
         btnFontItalic.setBorder(RAISED_BORDER);

         // font underline
         image = ImageIO.read(new File(fpPath, "font-underline.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnFontUnderline.setIcon(new ImageIcon(image));
         btnFontUnderline.setBorder(RAISED_BORDER);

         // font highlight red
         image = ImageIO.read(new File(fpPath, "font-highlight-red.png"))
               .getScaledInstance(buttonSize, buttonSize, Image.SCALE_AREA_AVERAGING);
         btnHighlightRed.setIcon(new ImageIcon(image));
         btnHighlightRed.setBorder(RAISED_BORDER);

         // font highlight blue
         image = ImageIO.read(new File(fpPath, "font-highlight-blue.png"))
               .getScaledInstance(buttonSize, buttonSize, Image.SCALE_AREA_AVERAGING);
         btnHighlightBlue.setIcon(new ImageIcon(image));
         btnHighlightBlue.setBorder(RAISED_BORDER);

         // font highlight yellow
         image = ImageIO.read(new File(fpPath, "font-highlight-yellow.png"))
               .getScaledInstance(buttonSize, buttonSize, Image.SCALE_AREA_AVERAGING);
         btnHighlightYellow.setIcon(new ImageIcon(image));
         btnHighlightYellow.setBorder(RAISED_BORDER);

         // keycode viewer
         image = ImageIO.read(new File(fpPath, "window-ascii.png")).getScaledInstance(buttonSize,
               buttonSize, Image.SCALE_AREA_AVERAGING);
         btnKeyCode.setIcon(new ImageIcon(image));
         btnKeyCode.setBorder(RAISED_BORDER);
      } catch (IOException e1) {
         e1.printStackTrace();
      }

      /* file processing buttons */
      btnFileNew.setOpaque(false);
      btnFileNew.setMargin(new Insets(0, 0, 0, 0));
      btnFileNew.addActionListener(new FileNewAction(app));
      btnFileNew.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F1"), "New File");
      btnFileNew.getActionMap().put("New File", new FileNewAction(app));

      btnFileOpen.setOpaque(false);
      btnFileOpen.setMargin(new Insets(0, 0, 0, 0));
      btnFileOpen.addActionListener(new FileOpenAction(app));
      btnFileOpen.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"), "Open File");
      btnFileOpen.getActionMap().put("Open File", new FileOpenAction(app));

      btnFileSave.setOpaque(false);
      btnFileSave.setMargin(new Insets(0, 0, 0, 0));
      btnFileSave.addActionListener(new FileSaveAction(app, false));
      btnFileSave.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F3"), "Save File");
      btnFileSave.getActionMap().put("Save File", new FileSaveAction(app, false));

      btnFileSaveAs.setOpaque(false);
      btnFileSaveAs.setMargin(new Insets(0, 0, 0, 0));
      btnFileSaveAs.addActionListener(new FileSaveAction(app, true));
      btnFileSaveAs.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F4"), "SaveAs File");
      btnFileSaveAs.getActionMap().put("SaveAs File", new FileSaveAction(app, true));
      /* end of file processing buttons */

      /* font size buttons */
      btnFontSizeUp.setOpaque(false);
      btnFontSizeUp.setMargin(new Insets(0, 0, 0, 0));
      btnFontSizeUp.addActionListener(new FontSizeAction(true, new int[] {9, 12, 16, 22}));
      btnFontSizeUp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "Size Up");
      btnFontSizeUp.getActionMap().put("Size Up", new FontSizeAction(true, new int[] {9, 12, 16, 22}));

      btnFontSizeDown.setOpaque(false);
      btnFontSizeDown.setMargin(new Insets(0, 0, 0, 0));
      btnFontSizeDown.addActionListener(new FontSizeAction(false, new int[] {9, 12, 16, 22}));
      btnFontSizeDown.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F6"), "Size Down");
      btnFontSizeDown.getActionMap().put("Size Down", new FontSizeAction(false, new int[] {9, 12, 16, 22}));
      /* end of font size buttons */

      /* font emphasis buttons */
      btnFontBold.setOpaque(false);
      btnFontBold.setMargin(new Insets(0, 0, 0, 0));
      btnFontBold.addActionListener(new FontBoldAction());

      btnFontItalic.setOpaque(false);
      btnFontItalic.setMargin(new Insets(0, 0, 0, 0));
      btnFontItalic.addActionListener(new FontItalicAction());

      btnFontUnderline.setOpaque(false);
      btnFontUnderline.setMargin(new Insets(0, 0, 0, 0));
      btnFontUnderline.addActionListener(new FontUnderlineAction());

      btnFontColor.setText("C");
      btnFontColor.setOpaque(false);
      btnFontColor.setMargin(new Insets(0, 0, 0, 0));
      btnFontColor.addActionListener(new FontForegroundAction());

      JButton[] buttonsGroup =
            new JButton[] {btnHighlightRed, btnHighlightBlue, btnHighlightYellow};

      btnHighlightRed.setOpaque(false);
      btnHighlightRed.setMargin(new Insets(0, 0, 0, 0));
      btnHighlightRed.addActionListener(new FontHighlightAction(buttonsGroup, Color.RED));

      btnHighlightBlue.setOpaque(false);
      btnHighlightBlue.setMargin(new Insets(0, 0, 0, 0));
      btnHighlightBlue.addActionListener(new FontHighlightAction(buttonsGroup, Color.BLUE));

      btnHighlightYellow.setOpaque(false);
      btnHighlightYellow.setMargin(new Insets(0, 0, 0, 0));
      btnHighlightYellow.addActionListener(new FontHighlightAction(buttonsGroup, Color.YELLOW));
      /* end of font emphasis buttons */

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
      }
      comboFontFamily.setPreferredSize(new Dimension(80, 24));
      comboFontFamily.addActionListener(new FontFamilyAction());
      /* end of font family combobox */

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

      btnFileNew.setToolTipText("새 파일(F1)");
      btnFileOpen.setToolTipText("파일 열기(F2)");
      btnFileSave.setToolTipText("저장(F3)");
      btnFileSaveAs.setToolTipText("다른 이름으로 저장(F4)");
      
      btnFontSizeUp.setToolTipText("글자 크게(F5)");
      btnFontSizeDown.setToolTipText("글자 작게(F6)");

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

   @Override
   public Component add(Component comp) {
      toolbar.add(comp);
      return comp;
   }

   @Override
   public void caretUpdate(CaretEvent e) {
      JTextPane pane = (JTextPane) e.getSource();
      AttributeSet attr;
      if (pane.getText().length() == pane.getCaretPosition())
         attr = pane.getInputAttributes();
      else
         attr = pane.getCharacterAttributes();

      boolean isBold = StyleConstants.isBold(attr);
      boolean isItalic = StyleConstants.isItalic(attr);
      boolean isUnderline = StyleConstants.isUnderline(attr);
      Color highlightColor = StyleConstants.getBackground(attr);

      if (isBold)
         btnFontBold.setBorder(LOWERED_BORDER);
      else
         btnFontBold.setBorder(RAISED_BORDER);

      if (isItalic)
         btnFontItalic.setBorder(LOWERED_BORDER);
      else
         btnFontItalic.setBorder(RAISED_BORDER);

      if (isUnderline)
         btnFontUnderline.setBorder(LOWERED_BORDER);
      else
         btnFontUnderline.setBorder(RAISED_BORDER);

      btnHighlightRed.setBorder(RAISED_BORDER);
      btnHighlightBlue.setBorder(RAISED_BORDER);
      btnHighlightYellow.setBorder(RAISED_BORDER);
      if (highlightColor == Color.RED)
         btnHighlightRed.setBorder(LOWERED_BORDER);
      else if (highlightColor == Color.BLUE)
         btnHighlightBlue.setBorder(LOWERED_BORDER);
      else if (highlightColor == Color.YELLOW)
         btnHighlightYellow.setBorder(LOWERED_BORDER);
   }

   public String getFontFamily() {
      return comboFontFamily.getSelectedItem().toString();
   }
}
