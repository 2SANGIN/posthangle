package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit.StyledTextAction;
import javax.swing.text.rtf.RTFEditorKit;

import hjsi.posthangeul.window.PostHangeulApp;


public class FileOpenAction extends StyledTextAction {
  private static final long serialVersionUID = 4769790497914601143L;

  PostHangeulApp app;
  JFileChooser fileChooser;

  public FileOpenAction(PostHangeulApp app) {
    super("file-open");
    this.app = app;

    fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("파일 열기");;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JEditorPane editor = getEditor(e);

    if (app.getCurrentFile().exists()) {
      fileChooser.setCurrentDirectory(app.getCurrentFile().getAbsoluteFile());
      fileChooser.setSelectedFile(app.getCurrentFile());
    } else {
      fileChooser.setCurrentDirectory(new File("."));
    }

    int ret = fileChooser.showOpenDialog(null);

    if (ret == JFileChooser.APPROVE_OPTION) {
      try {
        File open = fileChooser.getSelectedFile();
        RTFEditorKit kit = new RTFEditorKit();
        FileInputStream in = new FileInputStream(open);
        StyledDocument doc = (StyledDocument) editor.getDocument();
        
        kit.read(in, doc, 0);
        editor.setText(doc.getText(0, doc.getLength() - 1));
        app.setCurrentFile(open);
        
      } catch (Exception e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      } 
    }

    editor.requestFocus();
  }
}
