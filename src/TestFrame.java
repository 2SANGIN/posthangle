import javax.swing.JFrame;

public class TestFrame extends JFrame {
  public TestFrame() {
    super("Component develop frame");
    
    setBounds(100, 100, 500, 300);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
  }

  public static void main(String[] args) {
    new TestFrame();
  }

}
