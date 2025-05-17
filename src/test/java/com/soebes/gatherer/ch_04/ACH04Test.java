package com.soebes.gatherer.ch_04;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static com.soebes.gatherer.DataFactory.LIST_OF_LIST;

/**
 * flatMap(..)
 */
class ACH04Test {

  @Test
  void flatMap() {
    var result = LIST_OF_LIST.stream()
        .flatMap(Collection::stream)
        .toList();
    System.out.println("result = " + result);
  }


}
