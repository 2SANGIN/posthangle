package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import hjsi.posthangeul.window.PostHangeulApp;

public class FileSaveAction extends StyledEditorKit.StyledTextAction {
  private static final long serialVersionUID = 1278931309505020017L;

  PostHangeulApp app;
  boolean isSaveAs = false;
  JFileChooser fileChooser;

  public FileSaveAction(PostHangeulApp app, boolean isSaveAs) {
    super("file-save");
    this.app = app;
    this.isSaveAs = isSaveAs;

    fileChooser = new JFileChooser();
    if (isSaveAs)
      fileChooser.setDialogTitle("다른 이름으로 저장하기");
    else
      fileChooser.setDialogTitle("새로 저장하기");

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JEditorPane editor = getEditor(e);
    File toSave = null;

    if (isSaveAs || app.getCurrentFile().exists() == false) {
      if (app.getCurrentFile().exists())
        fileChooser.setCurrentDirectory(app.getCurrentFile().getAbsoluteFile());
      else
        fileChooser.setCurrentDirectory(new File("."));
      fileChooser.setSelectedFile(app.getCurrentFile());

      int ret = fileChooser.showSaveDialog(null);

      if (ret == JFileChooser.APPROVE_OPTION) {
        toSave = fileChooser.getSelectedFile();
      }
    } else {
      toSave = app.getCurrentFile();
    }

    if (toSave != null) {
      try {
    	  RTFEditorKit kit = new RTFEditorKit();
    	  StyledDocument doc = (StyledDocument) editor.getDocument();
    	  FileOutputStream out = new FileOutputStream(toSave);
    	  kit.write(out, doc, 0, doc.getEndPosition().getOffset());
//        BufferedWriter writer = new BufferedWriter(new FileWriter(toSave));
//        writer.write(editor.getText());
//        writer.close();
        app.setCurrentFile(toSave);
      } catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (BadLocationException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    }

    editor.requestFocus();
  }
}
