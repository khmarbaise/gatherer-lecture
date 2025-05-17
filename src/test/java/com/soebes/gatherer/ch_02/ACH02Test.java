package com.soebes.gatherer.ch_02;

import org.junit.jupiter.api.Test;

import static com.soebes.gatherer.DataFactory.ALICE;
import static com.soebes.gatherer.DataFactory.ALICE_1_1;
import static com.soebes.gatherer.DataFactory.BOB;
import static com.soebes.gatherer.DataFactory.BOB_1_1;
import static com.soebes.gatherer.DataFactory.BOB_A;
import static com.soebes.gatherer.DataFactory.CHARLIE;
import static com.soebes.gatherer.DataFactory.CHARLIE_1_1;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS_SORTED;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static com.soebes.gatherer.DataFactory.PERSONS_WITH_MORE_FIELDS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ACH02Test {

  @Test
  void distinctWithNumber() {
    var result = INTEGER_NUMBERS.stream()
        .distinct()
        .toList();
    assertThat(result).containsExactly(100, 1, 10, 11, 5, 75, 78, 90);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithSortedNumber() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .distinct()
        .toList();
    assertThat(result).containsExactly(1, 5, 10, 11, 75, 78, 90, 100);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithPersons() {
    var result = PERSONS.stream()
        .distinct()
        .toList();
    assertThat(result).containsExactly(ALICE, BOB, CHARLIE, BOB_A);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithPersonsMoreFields() {
    var result = PERSONS_WITH_MORE_FIELDS.stream()
        .distinct()
        .toList();
    assertThat(result).containsExactly(ALICE_1_1, BOB_1_1, CHARLIE_1_1);
    System.out.println("result = " + result);
  }


}
