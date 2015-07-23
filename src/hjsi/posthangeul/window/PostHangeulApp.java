package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;

import hjsi.posthangeul.editor.SwiftEditor;

public class PostHangeulApp {
  JFrame mainWindow;
  KeyCodeViewer keyCodeViewer;
  Shortcut shortcuts;

  SwiftEditor editor;

  File currentFile = new File("제목 없음.txt");

  public PostHangeulApp() {
    mainWindow = new JFrame();
    mainWindow.setTitle("Post Hangeul - " + this.currentFile.getName());

    /* create menu */
    shortcuts = new Shortcut(this);
    mainWindow.getContentPane().add(shortcuts, BorderLayout.NORTH);

    /* add editor */
    editor = new SwiftEditor();
    mainWindow.getContentPane().add(editor, BorderLayout.CENTER);

    /* create main window */
    mainWindow.setBounds(0, 0, 600, 480);
    mainWindow.setMinimumSize(new Dimension(600, 480));
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setLocationRelativeTo(null);
    mainWindow.setVisible(true);

    keyCodeViewer = new KeyCodeViewer(mainWindow, "KeyCode Viewer", false);
    editor.requestFocus();
  }

  public static void main(String[] args) {
    // try {
    // UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
    // } catch (UnsupportedLookAndFeelException e) {
    // e.printStackTrace();
    // }
    new PostHangeulApp();
  }

  /**
   * @return the currentFile
   */
  public File getCurrentFile() {
    return this.currentFile;
  }

  /**
   * @param currentFile the currentFile to set
   */
  public void setCurrentFile(File currentDocument) {
    this.currentFile = currentDocument;
    mainWindow.setTitle("Post Hangeul - " + this.currentFile.getName());
  }
}
