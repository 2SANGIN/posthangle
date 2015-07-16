package hjsi.posthangeul.window;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenu extends JMenuBar {
  ArrayList<JMenu> menus;
  JMenu menuFile;
  JMenu menuEdit;
  JMenu menuWindow;
  JMenu menuHelp;

  public MainMenu() {
    super();

    menus = new ArrayList<JMenu>();

    menuFile = new JMenu("File");
    menuFile.setMnemonic('F');
    menuEdit = new JMenu("Edit");
    menuEdit.setMnemonic('E');
    menuWindow = new JMenu("Window");
    menuWindow.setMnemonic('W');
    menuHelp = new JMenu("Help");
    menuHelp.setMnemonic('H');

    menuFile.add(new JMenuItem("New"));
    menuFile.add(new JMenuItem("Open"));
    menuFile.addSeparator();
    menuFile.add(new JMenuItem("Save"));
    menuFile.add(new JMenuItem("Save as..."));

    menuEdit.add(new JMenuItem("Undo"));
    menuEdit.add(new JMenuItem("Redo"));
    menuEdit.addSeparator();
    menuEdit.add(new JMenuItem("Cut"));
    menuEdit.add(new JMenuItem("Copy"));
    menuEdit.add(new JMenuItem("Paste"));
    menuEdit.add(new JMenuItem("Delete"));

    menuWindow.add(new JMenuItem("KeyCode Viewer"));
    menuWindow.add(new JMenuItem("Preference"));

    menuHelp.add(new JMenuItem("Help"));
    menuHelp.add(new JMenuItem("About"));

    menus.add(menuFile);
    menus.add(menuEdit);
    menus.add(menuWindow);
    menus.add(menuHelp);

    for (JMenu menu : menus) {
      add(menu);
    }
  }
}
