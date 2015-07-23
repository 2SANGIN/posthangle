package hjsi.posthangeul.action;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.text.StyledEditorKit.StyledTextAction;

import hjsi.posthangeul.window.PostHangeulApp;


public class OpenAction extends StyledTextAction {
  private static final long serialVersionUID = 4769790497914601143L;

  PostHangeulApp app;
  JFileChooser fileChooser;

  public OpenAction(PostHangeulApp app) {
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
      StringWriter strWriter = new StringWriter();

      try {
        int buf;
        File open = fileChooser.getSelectedFile();
        BufferedReader reader = new BufferedReader(new FileReader(open));

        while ((buf = reader.read()) != -1) {
          strWriter.write(buf);
        }
        reader.close();
        editor.setText(strWriter.toString());
        app.setCurrentFile(open);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    editor.requestFocus();
  }
}
