package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

class PartitioningByTest {

  static final List<AgeRange> AGE_RANGES = List.of(
      new AgeRange(0, 17),
      new AgeRange(18, 20),
      new AgeRange(21, 29),
      new AgeRange(30, 39),
      new AgeRange(40, 49),
      new AgeRange(50, 59),
      new AgeRange(60, 69),
      new AgeRange(70, 100)
  );

  // EEID,Full Name,Job Title,Department,Business Unit,Gender,Ethnicity,Age,Hire Date,Annual Salary,Bonus %,Country,City,Exit Date
  //E02387,Emily Davis,Sr. Manger,IT,Research & Development,Female,Black,55,4/8/2016,"$141,604 ",15% ,United States,Seattle,10/16/2021
  record Employee(String eeid, String name, String title, String department, String businessUnit, String gender,
                  String ethnicity, int age, LocalDate hireDate, int annualSalary, float bonus, String country,
                  String city, LocalDate exitDate) {
  }

  record Line(String line) {

  }

  /*
EEID,Full Name,Job Title,Department,Business Unit,Gender,Ethnicity,Age,Hire Date,Annual Salary,Bonus %,Country,City,Exit Date
E02387,Emily Davis,Sr. Manger,IT,Research & Development,Female,Black,55,4/8/2016,"$141,604 ",15% ,United States,Seattle,10/16/2021
   */
  Integer parseSalary(String salaryString) {
    var cleanedUpSalaryString = salaryString.substring(2, salaryString.length() - 1).trim();
    try {
      return NumberFormat.getNumberInstance(Locale.US).parse(cleanedUpSalaryString).intValue();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  Float parseBonus(String bonusString) {
    var cleanup = bonusString.trim().substring(0, bonusString.trim().length() - 1);
    return Float.parseFloat(cleanup);
  }

  LocalDate parseExitDate(String exitDateString) {
    if (exitDateString.trim().isEmpty()) {
      return null;
    }
    return LocalDate.parse(exitDateString, DateTimeFormatter.ofPattern("M/d/yyyy"));
  }

  Employee convertLineIntoEmployee(Line line) {
    var split = line.line().split(",(?=([^\"]|\"[^\"]*\")*$)");
    Integer salary = parseSalary(split[9]);
    Float bonus = parseBonus(split[10]);

    var exitDate = (LocalDate) null;
    if (split.length == 14) {
      exitDate = parseExitDate(split[13]);
    }
    return new Employee(split[0],
        split[1],
        split[2],
        split[3],
        split[4],
        split[5],
        split[6],
        Integer.parseInt(split[7]),
        LocalDate.parse(split[8], DateTimeFormatter.ofPattern("M/d/yyyy")),
        salary,
        bonus,
        split[11],
        split[12],
        exitDate
    );
  }

  List<Employee> convertToEmployee(List<Line> lines) {
    return lines.stream()
        .map(this::convertLineIntoEmployee)
        .toList();
  }

  List<Person> convertToPerson(List<Employee> employees) {
    return employees.stream()
        .map(employee -> new Person(employee.name(), employee.age())).toList();
  }

  private final Predicate<String> emptyLine = l -> l.trim().isEmpty();

  List<Line> readLinesFromFile(Path csvFile) throws IOException {
    //We have to read the file in UTF_16!
    try (Stream<String> lines = Files.lines(csvFile, StandardCharsets.UTF_16)) {
      return lines.filter(not(emptyLine)).map(Line::new).toList();
    }
  }

  List<Person> getPersons() throws IOException {
    var employeeSampleData = Path.of("src/test/resources/EmployeeSampleData.csv");
    var lines = readLinesFromFile(employeeSampleData).stream().skip(1).toList();
    return convertToPerson(convertToEmployee(lines));
  }

  //TODO: Change age into birthday and calculate age accordingly.
  record Person(String name, int age) {
  }

  record AgeRange(int from, int to) {
    static Comparator<AgeRange> comparator() {
      return Comparator.comparingInt(AgeRange::from).thenComparingInt(AgeRange::to);
    }
  }

  static Predicate<AgeRange> personInRange(Person person) {
    return ar -> (person.age() >= ar.from()) && (person.age() <= ar.to());
  }

  @Test
  void partitionByAgeRange() throws IOException {
    var persons = getPersons();

    HashMap<AgeRange, List<Person>> ageRangeList = persons.stream().collect(
        HashMap::new,
        (acc, person) -> {
          var aRange = AGE_RANGES.stream()
              .filter(personInRange(person))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("Person does not fit in any given range person:%s".formatted(person)));
          acc.computeIfAbsent(aRange, _ -> new ArrayList<>()).add(person);
        },
        HashMap::putAll);

    ageRangeList.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey(AgeRange.comparator()))
        .forEach(entry -> {
          System.out.println(entry.getKey());
          entry.getValue()
              .stream()
              .sorted(Comparator.comparing(Person::name))
              .forEachOrdered(p -> System.out.printf("  %s%n", p));
        });
  }

  @Test
  void partitionByAgeRangeViaGroupingBy() throws IOException {
    var persons = getPersons();

    Map<AgeRange, List<Person>> collect = persons
        .parallelStream()
        .collect(Collectors.groupingBy(person ->
            AGE_RANGES.stream()
                .filter(personInRange(person))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("The Person %s does not fit in any given range.".formatted(person)))
        ));
    collect.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey(AgeRange.comparator()))
        .forEachOrdered(entry -> {
          System.out.println(entry.getKey());
          entry.getValue()
              .stream()
              .sorted(Comparator.comparing(Person::name))
              .forEachOrdered(p -> System.out.println("  " + p));
        });

  }

}
