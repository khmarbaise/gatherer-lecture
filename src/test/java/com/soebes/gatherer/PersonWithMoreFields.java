package com.soebes.gatherer;

import java.util.Objects;

/**
 * @implNote Explicitly do {@code NOT} add {@link PersonWithMoreFields#marker}
 * into account while creating the {@link PersonWithMoreFields#hashCode()} and
 * {@link PersonWithMoreFields#equals(Object)}.
 */
public final class PersonWithMoreFields {
  private final String name;
  private final int age;
  private final String marker;

  public PersonWithMoreFields(String name, int age, String marker) {
    this.name = name;
    this.age = age;
    this.marker = marker;
  }

  public String name() {
    return name;
  }

  public int age() {
    return age;
  }

  public String marker() {
    return marker;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PersonWithMoreFields that)) {
      return false;
    }
    return Objects.equals(this.name, that.name)
           && Objects.equals(this.age, that.age);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age);
  }

  @Override
  public String toString() {
    return "PersonWithMoreFields[" +
           "name=" + name + ", " +
           "age=" + age + "," +
           "marker=" + marker + "]";
  }

}
