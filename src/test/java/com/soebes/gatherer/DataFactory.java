package com.soebes.gatherer;

import java.util.List;

public record DataFactory() {

  public static Person ALICE = new Person("Alice", 25);
  public static Person BOB = new Person("Bob", 30);
  public static Person CHARLIE = new Person("Charlie", 26);
  public static Person BOB_A = new Person("BobA", 30);
  public static List<Person> PERSONS = List.of(
      ALICE,
      BOB,
      CHARLIE,
      ALICE,
      BOB_A
  );

  public static final List<Integer> INTEGER_NUMBERS =
      List.of(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);

  public static final List<Integer> INTEGER_NUMBERS_SORTED =
      List.of(1, 5, 10, 11, 11, 75, 78, 90, 100, 100);

  public static final List<List<Integer>> LIST_OF_LIST =
      List.of(
        List.of(1,2),
        List.of(3,4),
        List.of(5,6),
        List.of(7,8)
      );

  public static final PersonWithMoreFields ALICE_1_1 = new PersonWithMoreFields("Alice", 25, "First");
  public static final PersonWithMoreFields ALICE_1_2 = new PersonWithMoreFields("Alice", 25, "Second");
  public static final PersonWithMoreFields BOB_1_1 = new PersonWithMoreFields("Bob", 30, "First");
  public static final PersonWithMoreFields BOB_1_2 = new PersonWithMoreFields("Bob", 30, "Second");
  public static final PersonWithMoreFields CHARLIE_1_1 = new PersonWithMoreFields("Charlie", 26, "First");
  public static final List<PersonWithMoreFields> PERSONS_WITH_MORE_FIELDS = List.of(
      ALICE_1_1,
      BOB_1_1,
      CHARLIE_1_1,
      ALICE_1_2,
      BOB_1_2
  );

}
