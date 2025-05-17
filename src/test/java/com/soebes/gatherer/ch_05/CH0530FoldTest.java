package com.soebes.gatherer.ch_05;

import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * fold - stateful, inherently ordered, N:1 operation.
 * Usage of {@code reduce} is not guaranteed to be executed in an ordered way.
 *
 * @see Stream#reduce(BinaryOperator)
 * @see Stream#reduce(Object, BinaryOperator)
 * @see Stream#reduce(Object, BiFunction, BinaryOperator)
 * @see Gatherers#fold(Supplier, BiFunction)
 * @see <a href="https://bugs.openjdk.org/browse/JDK-8133680">https://bugs.openjdk.org/browse/JDK-8133680 Discussion about fold/foldLeft/reduce</a>
 * @see <a href="https://bugs.openjdk.org/browse/JDK-8292845">https://bugs.openjdk.org/browse/JDK-8292845 Discussion about fold/foldLeft/reduce</a>
 */
class CH0530FoldTest {

  @Test
  void reduceDecimal() {
    Integer[] digits = {1, 7, 2, 9};
    int number = Stream.of(digits)
        //Hint: Non sequential
        .reduce(0, (x, y) -> x * 10 + y);

    System.out.println("number = " + number);
  }

  @Test
  void foldWithEmptyStream() {
    int number = Stream.<Integer>of()
        //Hint: Gatherer.fold is a foldLeft
        .gather(Gatherers.fold(() -> 0, (x, y) -> x * 10 + y))
        .findFirst()
        .orElse(9999999);

    System.out.println("number = " + number);
  }

  @Test
  void foldResultInDecimal() {
    Integer[] digits = {1, 7, 2, 9};
    int number = Stream.of(digits)
        .gather(Gatherers.fold(() -> 0, (x, y) -> x * 10 + y))
        .findFirst()
        .orElse(0);

    System.out.println("number = " + number);
  }

  @Test
  void foldResultingInBinary() {
    Integer[] digits = {1, 0, 1, 0};
    int number = Stream.of(digits)
        .gather(Gatherers.fold(() -> 0, (acc, y) -> acc * 2 + y))
        .findFirst()
        .orElse(0);

    System.out.println("number = " + Integer.toBinaryString(number));
    System.out.println("number = " + number);
  }

  @Test
  void reduceResultingInDecimal() {
    Integer[] digits = {1, 0, 1, 0};
    int number = Stream.of(digits)
        .reduce(0, (acc, y) -> acc * 2 + y);

    System.out.println("number = " + Integer.toBinaryString(number));
    System.out.println("number = " + number);
  }

  @Test
  void reduceFromStringResultingInDecimal() {
    String binary = "111";
    int result = binary.codePoints()
        .reduce(0, (acc, y) -> acc * 2 + (y == '0' ? 0 : 1));

    System.out.println("result = " + result);
  }

  @Test
  void reduceFromStringWithSwitchExpressionResultingInDecimal() {
    String binary = "11110000";
    int number = binary.codePoints()
        .reduce(0, (acc, y) -> acc * 2 + switch (y) {
          case '0' -> 0;
          case '1' -> 1;
          default -> throw new IllegalStateException("Unexpected value: " + y);
        });

    System.out.println("number = " + Integer.toBinaryString(number));
    System.out.println("number = " + number);
  }

  @Test
  void convertFromBinaryToDecimal() {
    String binary = "11110000";

    int result = 0;
    for (int i = 0; i < binary.length(); i++) {
      result = result * 2 + (binary.charAt(i) - '0');
    }
    System.out.println("result = " + result);
  }
  @Test
  void convertFromBinaryToDecimalUseJDK() {
    String binary = "11110000";

    int result = 0;
    for (int i = 0; i < binary.length(); i++) {
      result = result * 2 + Character.digit(binary.codePointAt(i), 2);
    }
    System.out.println("result = " + result);
  }

  @Test
  void convertFromBinaryToDecimalUsingJDK() {
    String binary = "11110000";
    var result = Integer.parseInt(binary, 2);
    System.out.println("result = " + result);
  }
}
