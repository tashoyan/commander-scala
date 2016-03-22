package com.github.acrisci.commander

import org.scalatest.{Matchers, FlatSpec}

class TestProgram extends FlatSpec with Matchers{
  def testProgram: Program = {
    new Program(exitOnError=false)
      .version("1.0.0")
      .option("-p, --peppers", "Add peppers")
      .option("-o, --onions", "Add onions")
      .option("-a, --anchovies", "Add anchovies")
      .option("-b, --bbq-sauce <type>", "Add bbq sauce")
      .option("-c, --cheese [type]", "Add cheese", default="pepper jack")
      .option("-l, --olives [type]", "Add olives")
      .option("-L, --lettuce [type]", "Add lettuce", default="iceberg")
      .option("-P, --pickles [type]", "Add pickles")
      .option("-t, --tomatoes <type>", "Add tomatoes")
      .option("-n, --num [num]", "Number of pizzas", default=1, fn=(_.toInt))
  }

  "Program" should "throw errors" in {
    withClue("coercion to int should throw an exception when invalid number is given") {
      intercept[NumberFormatException] {
        // default program behavior should exit instead of throw the exception.
        // it throws the exception because exitOnError is false
        testProgram.parse(Array("-n", "invalid"))
      }
    }

    withClue("accessing a program option value that does not exist should error") {
      intercept[RuntimeException] {
        val program = testProgram.parse(Array())
        val notAnOptionValue = program.notAnOptionValue
      }
    }

    withClue("giving argument with missing required param should error") {
      intercept[ProgramParseException] {
        val program = testProgram.parse(Array("-t"))
      }
    }

    withClue("an unknown option should throw an error") {
      intercept[ProgramParseException] {
        val program = testProgram.parse(Array("--uknown-option"))
      }
    }

    withClue("a missing required option should throw an error") {
      intercept[ProgramParseException] {
        val program = new Program(exitOnError=false)
          .option("-r, --required-option", "A required option", required=true)
          .parse(Array())
      }
    }
  }

  "Program" should "parse arguments correctly" in {
    val fakeArgs = Array("-po", "unknown1", "--bbq-sauce=sweet", "--cheese", "cheddar", "-l", "black", "unknown2", "unknown3", "-n", "10")
    val program = testProgram.parse(fakeArgs)

    assertResult("1.0.0", "version should be set") { program.version }

    assertResult(true, "peppers was given in a combined short opt") { program.peppers }

    assertResult("sweet", "bbq sauce was given as a long opt with equals sign") { program.bbqSauce }

    assertResult("cheddar", "cheese was given as a long opt") { program.cheese }

    assertResult(true, "onions was given in a combined short opt") { program.onions }

    assertResult(false, "anchovies has no param and was not present") { program.anchovies }

    assertResult("black", "olives was given a param as a short opt") { program.olives }

    assertResult("iceberg", "lettuce was not given, but has a default") { program.lettuce }

    assertResult(null, "pickles was not given and has no default") { program.pickles }

    assertResult("java.lang.Integer", "num was coerced to an int") { program.num.getClass.getName }
    assertResult(10, "num should be parsed as an int") { program.num }

    assertResult(List("unknown1", "unknown2", "unknown3"),
                      "args should contain the unknown args") { program.args }
  }

  "Program" should "properly create the help string" in {
    val program = new Program()
      .version("1.0.0")
      .description("A test program")
      .option("-p, --peppers", "Add peppers")

    val helpString = """
Usage: TestProgram [options]

  A test program

  Options:

    -h, --help     output usage information
    -V, --version  output the version number
    -p, --peppers  Add peppers"""

    assertResult(helpString.trim, "program should have a useful help string") { program.helpInformation.trim }
  }
}
