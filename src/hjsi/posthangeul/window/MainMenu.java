package hjsi.posthangeul.window;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenu extends JMenuBar {
  String[] menuNames = {"File", "Help"};
  ArrayList<JMenu> menus;

  public MainMenu() {
    super();

    menus = new ArrayList<JMenu>();


    JMenu temp = new JMenu("File");
    temp.setMnemonic('F');
    temp.setBackground(Color.WHITE);
    temp.add(new JMenuItem("New"));
    temp.add(new JMenuItem("Open"));
    temp.addSeparator();
    temp.add(new JMenuItem("Save"));
    temp.add(new JMenuItem("Save as..."));

    menus.add(temp);

    for (JMenu menu : menus) {
      add(menu);
    }
  }
}
