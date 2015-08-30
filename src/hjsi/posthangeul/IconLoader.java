/**
 * 2015. 8. 30.
 *
 */
package hjsi.posthangeul;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 * ButtonIconLoader <br>
 * 2015. 8. 30.
 *
 * @author SANGIN
 */
public class IconLoader {
   /**
    * 너무 특정한 경우에만 쓸 수 있으므로 삭제 대상
    *
    * @param imgDir 버튼 이미지가 들어있는 경로
    * @param buttonName 버튼 이미지의 이름 (접미사 및 확장자 제외)
    * @param btnSize 버튼 이미지 크기
    * @return 이미지 버튼 객체
    * @throws IOException if an error occurs during reading.
    */
   @Deprecated
   public static JToggleButton createImageToggleButton(File imgDir, String buttonName, int btnSize)
         throws IOException {
      String[] postfix = {"", "_rollover", "_selected", "_rollover_selected"};
      String extension = ".png";

      JToggleButton imgButton = new JToggleButton();
      imgButton.setName(buttonName);
      imgButton.setContentAreaFilled(false);
      imgButton.setBorder(BorderFactory.createEmptyBorder());

      Image[] images = new Image[postfix.length];

      for (int i = 0; i < postfix.length; i++) {
         File imgPath = new File(imgDir, buttonName + postfix[i] + extension);
         if (imgPath.exists()) {
            images[i] = ImageIO.read(imgPath).getScaledInstance(btnSize, btnSize,
                  Image.SCALE_AREA_AVERAGING);
         } else {
            System.out.println(imgPath.toString() + " was not found!");
         }
      }

      if (images[0] != null)
         imgButton.setIcon(new ImageIcon(images[0]));
      if (images[1] != null)
         imgButton.setRolloverIcon(new ImageIcon(images[1]));
      if (images[2] != null)
         imgButton.setSelectedIcon(new ImageIcon(images[2]));
      if (images[3] != null)
         imgButton.setRolloverSelectedIcon(new ImageIcon(images[3]));

      return imgButton;
   }


   /**
    * 주어진 <b>name</b>의 버튼 이미지를 <b>postfix</b>에 해당하는 이미지를 포함해서 모두 불러온다. <b>postfix</b>에 따로 주지 않아도
    * <b>name</b>에 해당하는 기본 이미지를 불러오고, 추가적으로 <b>postfix</b>에 주어진 접미사를 붙여서 이미지를 불러온다. 불러오는 이미지 파일의
    * 확장자는 <b>.png</b> 파일이다.<br>
    * <b>postfix</b>에는 기본 이미지 이름 뒤에 붙는 "pressed", "rollover" 등과 같은 문자열을 배열로 넣어주면 된다. <i>예를 들면,
    * "btn.png", "btn_pressed.png"의 파일을 불러오려고 한다면 name에는 "btn"을 넣어주고 postfix에는 "_pressed"를 넣어주면
    * 된다.</i>
    *
    * @param path 이미지가 들어있는 경로
    * @param name 이미지 이름
    * @param postfix 이미지 파일들의 접미사 배열. null이 주어지면 기본 파일에 대해서만 읽기를 시도한다.
    * @param size 이미지의 크기 (픽셀 단위)
    * @return 최소 1개 이상의 이미지 아이콘 객체 배열 (배열에 null을 포함한다.)
    * @throws IOException 파일 입출력 중 오류가 있거나 주어진 경로에 해당하는 파일이 없을 경우 발생한다.
    */
   public static ImageIcon[] loadIcons(File path, String name, String[] postfix, int size)
         throws IOException {
      String ext = ".png";
      ImageIcon[] icons;

      if (postfix != null) {
         icons = new ImageIcon[postfix.length + 1];
      } else {
         icons = new ImageIcon[1];
      }

      /*
       * 기본 형식의 이미지를 불러온다.
       */
      File absPath = new File(path, name + ext);
      if (absPath.exists()) {
         icons[0] = new ImageIcon(
               ImageIO.read(absPath).getScaledInstance(size, size, Image.SCALE_AREA_AVERAGING));
         System.out.println(absPath.toString() + " 파일을 로드했습니다.");
      } else {
         throw new FileNotFoundException(absPath.toString() + " 파일을 찾을 수 없습니다.");
      }

      /*
       * 접미사가 붙은 이미지들을 불러온다.
       */
      if (postfix != null) {
         for (int i = 1; i < icons.length; i++) {
            absPath = new File(path, name + postfix[i - 1] + ext);
            if (absPath.exists()) {
               icons[i] = new ImageIcon(ImageIO.read(absPath).getScaledInstance(size, size,
                     Image.SCALE_AREA_AVERAGING));
               System.out.println(absPath.toString() + " 파일을 로드했습니다.");
            } else {
               icons[i] = null;
               throw new FileNotFoundException(absPath.toString() + " 파일을 찾을 수 없습니다.");
            }
         }
      }

      return icons;
   }
}
