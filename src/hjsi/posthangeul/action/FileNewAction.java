package hjsi.posthangeul.action;

import hjsi.posthangeul.window.PostHangeulApp;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.text.StyledEditorKit.StyledTextAction;

public class FileNewAction extends StyledTextAction {
   private static final long serialVersionUID = 620247404113872945L;

   PostHangeulApp app;

   public FileNewAction(PostHangeulApp app) {
      super("file-new");
      this.app = app;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      FileSaveAction save = new FileSaveAction(app, false);
      save.askSave(e);
      app.getWindow().dispose();

      app = new PostHangeulApp();
      app.setCurrentFile(new File("제목 없음.rtf"));

      app.getEditor().requestFocus();
   }
}
