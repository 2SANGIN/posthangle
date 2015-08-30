package hjsi.posthangeul;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

/**
 * AppFont <br>
 * 2015. 8. 30.
 * 
 * @author SANGIN
 */
public class AppFont {
   /**
    * 나눔고딕
    */
   public static Font fontNanumGothic;

   /**
    * San-Serif 폰트
    */
   public static Font fontSans;

   static {
      try {
         /* font 리소스 불러옴 */
         File fp = new File("fonts/NanumGothic.ttf");
         fontNanumGothic = Font.createFont(Font.TRUETYPE_FONT, fp);
         fontSans = new Font("Sans", Font.PLAIN, 1);
      } catch (FontFormatException | IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * 나눔고딕 폰트를 반환
    *
    * @param size font point size
    * @return PLAIN font
    */
   public static Font getButtonFont(int size) {
      return getButtonFont(Font.PLAIN, size);
   }

   /**
    * 나눔고딕 폰트를 반환
    *
    * @param fontStyle Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD | Font.ITALIC
    * @return 주어진 스타일과 크기의 나눔고딕 폰트
    */
   public static Font getButtonFont(int fontStyle, int size) {
      return fontNanumGothic.deriveFont(fontStyle, size);

   }
}
