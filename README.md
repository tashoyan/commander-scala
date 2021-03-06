# Commander Scala

A scalable command-line parser inspired by [commander.js](https://github.com/tj/commander.js).

Commander-scala is available on [Maven Central](http://mvnrepository.com/artifact/com.github.acrisci/commander_2.11).

```scala
libraryDependencies += "com.github.acrisci" % "commander_2.11" % "0.1.0"
```

## Option parsing

 Options with commander are defined with the `.option()` method, also serving as documentation for the options. The example below parses args and options from `args`, leaving remaining args as the `program.args` array which were not consumed by options. Options will be set on the `program` dynamically based on the camelcased form of the long opt.

```scala
import com.github.acrisci.commander.Program

var program = new Program()
  .version("0.0.1")
  .option("-p, --peppers", "Add peppers")
  .option("-P, --pineapple", "Add pineapple")
  .option("-b, --bbq-sauce", "Add bbq sauce")
  .option("-c, --cheese [type]", "Add the specified type of cheese [marble]", default="marble")
  .parse(args)

if (args.isEmpty)
  program.help

println("you ordered a pizza with:")
if (program.peppers)
  println("  - peppers")
if (program.pineapple)
  println("  - pineapple")
if (program.bbqSauce)
  println("  - bbq")
println("  - " + program.cheese + " cheese")
```

## Type Coercion

To coerce an option to a certain type, you can add a coercion function with the `fn` parameter. The coercion function should take the string given as input and coerce it to the type you want. Then you can then access the option on the Program as that type by providing a type tag.

```scala
val program = new Program()
  .version("0.0.1")
  .description("A program that can sum or multiply a list of numbers")
  .option("-o, --operation [operation]", "The operation to perform on the numbers [sum|multiply]", default="sum")
  .option("-n, --numbers <numbers>", "Comma-separated list of numbers", fn=_.split(",").map(_.toInt))
  .parse(args)

if (args.isEmpty)
  program.help()

if (program.operation.equals("sum")) {
  val sum = program.numbers[Array[Int]].sum
  println(s"the sum is $sum")
} else if (program.operation.equals("multiply")) {
  val product = program.numbers[Array[Int]].product
  println(s"the product is $product")
} else {
  println("Operation must be either 'sum' or 'multiply'")
  System.exit(1)
}
```

## Commands

You can define commands on your program like this:

```scala
var program = new Program()
  .version("0.0.1")
  .command(classOf[InstallPackages], "install [packages]", "Install the given packages")
  .command(classOf[SearchPackages], "search [query]", "Search for packages")
  .command(classOf[ListPackages], "list", "List packages")
  .parse(args)
```

If the name of the command is given as the first argument to the program, it will run the `main` method of the given class with the remaining arguments.

## Contributing

`commander-scala` is a work in progress.

Make issues on Github to report bugs or suggest new features. Let me know if you are working on something. Right now, I plan on staying as close to commander.js as is practical. However, I will change the api slightly where it makes sense given that Scala is very different from JavaScript. I also might add some features from other argument parsers that I like.

### To Do

Here are some things that need to be done.

* Set no help
* Code cleanup
* Test `--help` option and `help` command
* Cleanup program constructor args (debug mode for tests?)
* Promotion
* Command name duplicates
* Provide an action, options, and description for commands

## License

MIT (see LICENSE)

Copyright © 2015, Tony Crisci
