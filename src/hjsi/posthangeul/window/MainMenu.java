package hjsi.posthangeul.window;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class MainMenu extends JMenuBar {
  String[] menuNames = {"File", "Help"};
  ArrayList<JMenu> menus;

  public MainMenu() {
    super();

    menus = new ArrayList<JMenu>();
    JMenu temp = new JMenu("File");
    temp.setMnemonic('F');
    menus.add(temp);

    for (JMenu menu : menus) {
      add(menu);
    }
  }
}
