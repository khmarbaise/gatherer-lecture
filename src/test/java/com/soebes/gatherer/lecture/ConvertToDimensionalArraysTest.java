package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.util.stream.Gatherers.windowFixed;
import static org.assertj.core.api.Assertions.assertThat;

class ConvertToDimensionalArraysTest {

  @Test
  void convertTo2DArray() {
    int[] flatArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    IntStream flatStream = Arrays.stream(flatArray);
    int rows = 3;
    int cols = 3;

    int[][] expectedArray2D = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
    int[][] resultArray2D = new int[rows][cols];
    int[] collectedArray = flatStream.toArray();

    for (int i = 0; i < rows; i++) {
      System.arraycopy(collectedArray, i * cols, resultArray2D[i], 0, cols);
    }

    assertThat(resultArray2D).isEqualTo(expectedArray2D);
  }

  @Test
  void convertingViaWindow() {
    int[] flatArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    var array = Arrays.stream(flatArray)
        .mapToObj(Integer::valueOf)
        .gather(windowFixed(3)).toArray();
    System.out.println("Arrays.toString(array) = " + Arrays.toString(array));
  }
}
