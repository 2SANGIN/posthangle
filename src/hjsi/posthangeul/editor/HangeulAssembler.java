package hjsi.posthangeul.editor;

public class HangeulAssembler {
  private static final int BASE_CODE = 0xAC00;
  private static final int INITIAL_FACTOR = 588;
  private static final int MEDIAL_FACTOR = 28;

  private static final char[] INITIAL_LIST = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ',
      'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

  private static final char[] MEDIAL_LIST = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ',
      'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};

  private static final char[] FINAL_LIST = {'\0', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ',
      'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

  public static char assemble(int initial, int medial, int fin) {
    return (char) (BASE_CODE + initial * INITIAL_FACTOR + medial * MEDIAL_FACTOR + fin);
  }

  public static int getInitialIndex(char hangeul) {
    return (hangeul - BASE_CODE) / INITIAL_FACTOR;
  }

  public static int getMedialIndex(char hangeul) {
    return (hangeul - BASE_CODE) % INITIAL_FACTOR / MEDIAL_FACTOR;
  }

  public static int getFinalIndex(char hangeul) {
    return (hangeul - BASE_CODE) % INITIAL_FACTOR % MEDIAL_FACTOR;
  }

  public static char getInitialChar(int initialIndex) {
    return INITIAL_LIST[initialIndex];
  }

  public static char getMedialChar(int medialIndex) {
    return MEDIAL_LIST[medialIndex];
  }

  public static char getFinalChar(int finalIndex) {
    return FINAL_LIST[finalIndex];
  }

  {
    /*
     * // 한글 ( 한글자 || 자음 , 모음 ) if ((0xAC00 <= c && c <= 0xD7A3) || (0x3131 <= c && c <= 0x318E)) {
     * System.out.println("k" + c); k++; } else if ((0x61 <= c && c <= 0x7A) || (0x41 <= c && c <=
     * 0x5A)) { // 영어 System.out.println("e:" + c); e++; } else if (0x30 <= c && c <= 0x39) { // 숫자
     * System.out.println("d" + c); d++; } else { System.out.println("z" + c); z = 0; }
     */

    /* 알파벳 범위 */
    for (int i = 0x61; i <= 0x7a; i++)
      System.out.print(String.format("%c ", i));
    System.out.println();
    for (int i = 0x41; i <= 0x5a; i++)
      System.out.print(String.format("%c ", i));
    System.out.println();

    /* 한글 자모 범위 */
    for (int i = 0x3131; i <= 0x318e; i++)
      System.out.print(String.format("%c ", i));
    System.out.println();

    /* 한글 완성된 글자 범위 */
    int total = 0xd7a3 - 0xac00 + 1;
    int cnt = 0;
    for (int i = 0; i < total; i++) {
      System.out.print(String.format("%c", 0xAC00 + i));
      if (++cnt % (total / 19) == 0)
        System.out.println();
    }
    System.out.println("총 한글 글자 수 : " + total + "자");
  }
}
