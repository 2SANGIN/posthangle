package hjsi.posthangeul.editor;

import java.util.Stack;

public class PostIME {
  private static final int BASE_CODE = 0xAC00;
  private static final int FACTOR_INITIAL = 588;
  private static final int FACTOR_MEDIAL = 28;
  private static final int FACTOR_FINAL = 1;

  private static final char[] INITIAL_LIST = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ',
      'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

  private static final char[] MEDIAL_LIST = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ',
      'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};

  private static final char[] FINAL_LIST = {'\0', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ',
      'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

  public static char assemble(int initial, int medial, int fin) {
    return (char) (BASE_CODE + initial * FACTOR_INITIAL + medial * FACTOR_MEDIAL + fin);
  }

  public static int getInitialIndex(char hangeul) {
    return (hangeul - BASE_CODE) / FACTOR_INITIAL;
  }

  public static int getMedialIndex(char hangeul) {
    return (hangeul - BASE_CODE) % FACTOR_INITIAL / FACTOR_MEDIAL;
  }

  public static int getFinalIndex(char hangeul) {
    return (hangeul - BASE_CODE) % FACTOR_MEDIAL;
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

  public static boolean isConsonant(char keyCode) {
    return (0x3131 <= keyCode && keyCode <= 0x314E);
  }

  public static boolean isVowel(char keyCode) {
    return (0x314F <= keyCode && keyCode <= 0x318E);
  }

  private static final int CONSONANT = 0;
  private static final int VOWEL = 1;

  private enum IME_STATE {
    START, INITIAL, SINGLE_MEDIAL, DOUBLE_MEDIAL, SINGLE_FINAL, DOUBLE_FINAL, END1, END2
  }

  private static IME_STATE state = IME_STATE.START;
  private static char uncommittedChar;
  private static char prevKey;
  private static char keyChar;

  public static Stack<Character> uncommittedStack = new Stack<>();
  public static Stack<Character> keyStack = new Stack<>();
  public static Stack<IME_STATE> inStackState = new Stack<>();
  public static Stack<Character> outStack = new Stack<>();

  private static boolean tryDoubleMedial() {
    char dMedialTbl[][] = {{'ㅗ', 'ㅏ', 'ㅘ'}, {'ㅗ', 'ㅐ', 'ㅙ'}, {'ㅗ', 'ㅣ', 'ㅚ'}, {'ㅜ', 'ㅓ', 'ㅝ'},
        {'ㅜ', 'ㅔ', 'ㅞ'}, {'ㅜ', 'ㅣ', 'ㅟ'}, {'ㅡ', 'ㅣ', 'ㅢ'}};
    /* 이전에 입력된 키와 현재 입력된 키를 겹모음 테이블에서 검색한다. */
    for (char[] vowel : dMedialTbl) {
      if (vowel[0] == prevKey && vowel[1] == keyChar) {
        keyChar = vowel[2];
        return true;
      }
    }
    return false;
  }

  private static boolean tryDoubleFinal() {
    char dFinalTbl[][] = {{'ㄱ', 'ㅅ', 'ㄳ'}, {'ㄴ', 'ㅈ', 'ㄵ'}, {'ㄴ', 'ㅎ', 'ㄶ'}, {'ㄹ', 'ㄱ', 'ㄺ'},
        {'ㄹ', 'ㅁ', 'ㄻ'}, {'ㄹ', 'ㅂ', 'ㄼ'}, {'ㄹ', 'ㅅ', 'ㄽ'}, {'ㄹ', 'ㅌ', 'ㄾ'}, {'ㄹ', 'ㅍ', 'ㄿ'},
        {'ㄹ', 'ㅎ', 'ㅀ'}, {'ㅂ', 'ㅅ', 'ㅄ'}};
    /* 이전에 입력된 키와 현재 입력된 키를 겹받침 테이블에서 찾아서 있는지를 검사한다. */
    for (char[] fin : dFinalTbl) {
      if (fin[0] == prevKey && fin[1] == keyChar) {
        keyChar = fin[2];
        return true;
      }
    }
    return false;
  }

  public static char assemble(int index, int factor) {
    uncommittedChar = (char) (uncommittedChar / factor * factor); // 뒷자리 버림
    uncommittedChar = (char) (uncommittedChar + index * factor);
    return uncommittedChar;
  }

  public static char disassemble(int factor) {
    uncommittedChar = (char) (uncommittedChar / factor * factor);
    return uncommittedChar;
  }

  private static int searchInitialIndex(char keyChar) {
    for (int i = 0; i < INITIAL_LIST.length; i++) {
      if (INITIAL_LIST[i] == keyChar)
        return i;
    }
    return -1;
  }

  private static int searchMedialIndex(char keyChar) {
    for (int i = 0; i < MEDIAL_LIST.length; i++) {
      if (MEDIAL_LIST[i] == keyChar)
        return i;
    }
    return -1;
  }

  private static int searchFinalIndex(char keyChar) {
    for (int i = 1; i < FINAL_LIST.length; i++) {
      if (FINAL_LIST[i] == keyChar)
        return i;
    }
    return -1;
  }

  /**
   * @param ch 입력된 키
   * @return 한 글자의 조합이 끝나면 true를, 계속 조합중이면 false를, 완성된 글자의 코드는 입력 스택의 가장 마지막에서 구할 수 있다.
   */
  public static boolean hanAutomata2(char ch) {
    keyChar = ch; // 작업할 keyCode로 복사

    /*
     * 입력된 낱자가 어떤 형태인가를 규정하고 이전의 한글입력상태와 조합된 코드를 얻어오는 오토마타의 작업준비 부분
     */
    int chKind;
    boolean canBeFinal = false;
    if (isVowel(ch)) {
      chKind = VOWEL; /* 모음 */
    } else {
      chKind = CONSONANT; /* 그 이외에는 자음 */
      /* 쌍디귿, 쌍비읍, 쌍지읒은 받침으로 올 수 없다. */
      if (!(ch == 'ㄸ' || ch == 'ㅃ' || ch == 'ㅉ'))
        canBeFinal = true;
    }
    /*
     * 만일 한글이 입력되고 있다면 이전에 저장된 스택에서 조립 중인 코드와 입력된 글쇠를 가져온다.
     */
    if (state != IME_STATE.START) {
      uncommittedChar = uncommittedStack.pop();
      prevKey = keyStack.pop();
    }
    /* 한글 입력이 처음일 경우 */
    else {
      uncommittedChar = BASE_CODE;
      prevKey = 0;
    }

    /*
     * 이전의 한글이 조합된 상태(uncommittedChar)와 현재 입력된 글자가 어떤 형태인가에 따라 조합상태를 다시 결정짓는 오타마타 주요 부분
     */
    switch (state) {
      case START: // 한글입력이 처음이고
        if (chKind == CONSONANT) // 자음이 들어왔다면
          state = IME_STATE.INITIAL; // 초성 상태로

        else // 모음이 들어왔다면
          state = IME_STATE.SINGLE_MEDIAL; // 중성 상태로
        break;

      case INITIAL: // 초성이 입력이 되었고
        if (chKind == VOWEL) // 모음이 들어왔다면
          state = IME_STATE.SINGLE_MEDIAL; // 중성 상태로

        else // 2벌식에는 복초성을 두번으로 나누어 입력할 수 없다. 따라서 초성이 들어온 상태에서 초성이 다시 들어온다면 당연히 글자조합을 끝을 낸다.
          state = IME_STATE.END1;
        break;

      case SINGLE_MEDIAL: // 중성이 입력이 되어있고
        /* 종성이 될 수 있는 놈이라면 */
        if (canBeFinal)
          state = IME_STATE.SINGLE_FINAL; // 종성 상태로 전이

        /* 중성이 들어왔다면 겹모음을 이룰 수 있는 지를 검사해서 겹모음이 될 수 있으면 복중성상태로 간다. */
        else if (tryDoubleMedial())
          state = IME_STATE.DOUBLE_FINAL;

        /* 이도 저도 아니 면 끝으로 간다. */
        else
          state = IME_STATE.END1;
        break;

      case DOUBLE_MEDIAL: // 겹모음을 이루었고
        /* 종성이 될 수 있는 놈이라면 */
        if (canBeFinal)
          state = IME_STATE.SINGLE_FINAL; // 종성상태로 ...

        /* 모음이나 종성이 될 수 없는 자음은 겹모음상태에서 받아들일 수 없다. */
        else
          state = IME_STATE.END1;
        break;

      case SINGLE_FINAL:// 종성을 이루고 있고
        /* 자음이 들어와서 겹자음을 이룰 수 있는 놈이라면 복종성상태로 간다. */
        if (chKind == CONSONANT && tryDoubleFinal())
          state = IME_STATE.DOUBLE_FINAL;

        /* 만일 모음이 들어온다면 끝2로 간다. */
        else if (chKind == VOWEL)
          state = IME_STATE.END2;

        /* 복종성을 이룰 수 없는 자음이라면 끝으로 간다. */
        else
          state = IME_STATE.END1;
        break;

      case DOUBLE_FINAL: // 복종성을 이루고 있고
        /* 모음이 들어왔다면 당연히 끝2로 */
        if (chKind == VOWEL)
          state = IME_STATE.END2;

        /* 그외에는 안보고도 끝1로 */
        else
          state = IME_STATE.END1;
        break;

      default:
        break;
    }
    /*
     * 한글낱자를 조합해서 코드를 얻는 곳이다. 부분적으로 뒤에서는 스택조정도 한다. 2벌식에서는 초성, 종성이 따로 없으므로 자음은 모조리 초성으로 가정하고 종성에는
     * 쌍디귿, 씽비읍, 쌍지읒 만이 올 수 없다. 따라서 처음에 초성으로 들어왔으므로 초성에 대응 하는 종성코드가 기술되어 있는 테이블에서 인덱스로 찾아야 한다. 복종성은
     * 이미 앞에서 알맞는 종성코드로 변환되었으므로 이런 과정이 필요없다.
     */
    switch (state) {
      case INITIAL: /* 초성코드 처리 */
        uncommittedChar = assemble(searchInitialIndex(keyChar), FACTOR_INITIAL);
        break;

      case SINGLE_MEDIAL: /* 중성코드는 복중성과 같이 처리된다. */
      case DOUBLE_MEDIAL: /* 복중성 코드 처리 */
        // uncommittedChar = (uncommittedChar & 0xFC1F) | ((keyChar - 0xA0) << 5);
        break;

      case SINGLE_FINAL: /* 종성코드는 초성->종성 테이블에서 변환되어야 한다. */
        // keyChar = Cho2Jong[keyChar - 0x82];
      case DOUBLE_FINAL: /* 복종성 및 종성 코드 처리 */
        // uncommittedChar = (uncommittedChar & 0xFFE0) | (keyChar - 0xC0);
        break;

      case END1:
        outStack.push(ch);// 현재낱자 하나만 다음으로 넘긴다.
        return true;

      case END2:
        /* 현재 낱자를 먼저 출력스택에 넣고 이전 글쇠를 꺼집어 내서 출력 스택에 집어넣는다. */
        outStack.push(ch);
        outStack.push(prevKey);

        /* 입력스택에서 하나를 빼갔으므로 스택포인터 조정 */
        uncommittedStack.pop();
        inStackState.pop();
        keyStack.pop();
        return true; /* 1은 완료되었음을 의미 */

      default:
        state = IME_STATE.START;
        break;
    }

    inStackState.push(state);
    uncommittedStack.push(uncommittedChar);
    keyStack.push(ch);

    /* 아직 한글 조립 중 */
    return false;
  }
}
