# Introduction to Chisel for Bea ginners
Chisel3 for Beginners; How to begin coding with a hardware description language

## Target People for This Document
Beginners who have no experience with Chisel and Scala programming languages.
Almost hardware engineers are categorized in this type.

## What is Chisel?
Chisel is to generate HDL code from a high-level language, which is made with wrapping Scala language.
Scala is made on Java virtual machine.
The current version is Chisel3. Because Java is an object-oriented language, **class** in chisel code is converted to HDL, *Verilog-HDL*.

## Prerequisites

Prerequisites to run are;
- Java Runtime Environment (or JDK for running Scala)
- SBT (Simple Build Tool, to compile chisel code)

### Prerequisites: Common Installations for Users

- Java Runtime Environment (JRE)
  
  JRE is probably installed on your computer. You can check it through terminal;
```
  java -version
```
  If java does not exist on your computer, case of MacOS, you can install it through **homebrew**. Case of ubuntu, you can use **apt**.
 

- SBT

 SBT can also be installed with homebrew (mac) and apt (ubuntu).
 ```
 brew install sbt
 ```

## Installations

**NOTE**: Before installation, you need to check you are one of which user types;
  - **Ordinary** user (let us say simply *user*) who wants to compile their chisel code to generate HDL, so they want to use chisel as a common tool, they should use **released** chisel. 
  - **Deveropper** user (let us call a *developer*) who needs an advanced tool which is not yet released (ex. they might want **rc** version), and or want to customize the tools for their purpose.
  They might want **pre-released** chisel.


### Installation for Ordinary Users

  By using *template* of **build.sbt**, SBT invokes necessary **released** *tool software defined in the build.sbt* through the internet (**so, network connection is necessary**). Template is always upto-date, so ordinary users need not do manual installation works.
  Template is here;

  https://github.com/freechipsproject/chisel-template

By doing **copy and paste** the template's build.sbt into your project directory, you can do your compilation!


### Installation for Developper Users

To install chisel, etc, to your local computer, you need to compile the source code of chisel, treadle, firrtl, etc, and assemble these.
To compile, you need to do following command in the directory;
```
  sbt compile
```
You need assemble the compiled code to make runable code.
```
  sbt assembly
```
After this, you need to publish in order to invoke the tool from your local environment;

```
sbt publishLocal
```

- Stable Version of Recent Chisel
  
  Stable Recent Version: 3.5.0
  https://github.com/freechipsproject/chisel3/releases/tag/v3.5.0

- Tags for Recent Versions

  You can see the recent version here;
  https://github.com/freechipsproject/chisel3/tags


- iotesters

  iotesters is an old version of a verification tool which runs on Scala and can generate VCD file.

  https://github.com/freechipsproject/chisel-testers/tree/master/src/main/scala/chisel3/iotesters

- tester2 (chisel-testers)

  tester2 is the second generation of verification tool, currently, this is mainstream.

  https://github.com/freechipsproject/chisel-testers

- Coursier  
  
  This program based on Java is a utility such as for instalation.
  We can use the program to install registered API, let us see to install FIRRTL as an example.
  Coursier can be installed by;
```
  curl -fLo cs https://git.io/coursier-cli-linux &&
    chmod +x cs &&
    ./cs
```
  This command is for Linux.
  Program "cs" is generated on the current directory.
  After that we can install FIRRTL which is registered in Coursier, as follows;
```
  ./cs bootstrap edu.berkeley.cs::firrtl:1.3.2 --main-class firrtl.stage.FirrtlMain -o firrtl-1.3.2
```
 This command generates firrtl-1.3.2 (see *-o* option to define the name of generation).
 "firrtl:1.3.2" in the command selects the firrtl" with 1.3.2 version registered on the web-site.

## Project Directory Structure
Necessary project directory structure is as follows;
```
root--+--build.sbt
      |
      +--src--+--main--+--scala--"YOUR_SOURCE_CODES.scala"
              |
              +--test--+--scala--"YOUR_TEST_CODES.scala"

```
This is **strict constraint** by SBT, all chisel codes should belong to this directory structure.
If you do not take this directory structure, you will meet **ClassNotFound** error, SBT could not find a root directory.

## Compilation

At the compilation, SBT checks directory structure, so, you do not need to specify which file(s) should be compiled.
SBT traces a  *class-dependency* from top-class as a root.
This means that you need to specify a *root class*.

### Before Your Compilation

You must decide following points before starting your compilation;
1. [Option] Project Name:  
  This name can be defined in build.sbt, "name"-field. You can see the name in the compilation, this helps that you work on what project.
  This is a hint that you have multiple compilation points to identify. 
2. Top *class* similar to HDL needs (see below):  
   At the compilation, you need to set **which class** is a *top* module of HDL.
   So, you can choose the preferred class as a top module for compiling HDL by specifying the name.
3. "YOUR_TEST_CODES.scala" (see bellow)  
   This is needed only for using **iotesters**.

### Top Class
The top class description below should be added to your a *top-class* file, or a file you want to test/generate HDL.
Replace "ProjectName" with your project's name.
```
object ProjectNameMain extends App {
  chisel3.Driver.execute(args,()=>new ProjectName("_args_"))
}
```
where ```_args_``` are arguments you defined in your class as a succeeded parameter(s) (option). You can do naming this object's name ```ProjectNameMain```, freely.
SBT invokes the object ```ProjectNameMain```, and works with the "root" object.
This description is needed to start generating HDL and to test on Chisel.

### How to Compile Your Code
Compiling on a terminal is simply as follows;
```
sbt 'run'
```
SBT will query which one you want to compile, then the terminal notifies the list with the number.
You need to tell through the number.

Or, you can specify the top class which will be a top module of HDL.
```
sbt 'runMain ProjectNameMain'
```
**NOTE**: replace "ProjectName" with your project's name, and if you have set package name ```package_name```;
```
sbt 'runMain package_name.ProjectNameMain'
```
The ```runMain``` invokes ```ProjectNameMain``` object and chisel works for this object.

### How to Test Your Code

- Test with ioterster on a terminal is simply as follows;
```
  sbt 'test:runMain ProjectNameMain'
```
- If you have multiple test codes, and you want do all tests, then simply do the following;
```
  sbt test
```
- Test with tester2 on a terminal for a particular class is simply as follows;
```
  sbt 'testOnly TestClassNameMain'
```
  where ```TestClassNameMain``` is your class made for testing (test bench)


### To dump VCD
Add this option;
```
-- -DwriteVcd=1
```
VCD file is stored in test_run_dir subfolder.

### YOUR_TEST_CODES.scala for iotesters
This file is needed to test your code on **iotesters** (**not tester2**). Set the test file name with ProjectNameMain.scala (replace "ProjectName" with your project's name). The file has to have below code at least;
```
import chisel3._

object ProjectNameMain extends App {
  iotesters.Driver.execute(args, () => new ProjectName) {
    c => new ProjectNameUnitTester(c)
  }
}

object ProjectNameRepl extends App {
  iotesters.Driver.executeFirrtlRepl(args, () => new ProjectName)
}
```
where "ProjectName" in the code must be replaced with your project's name. We do **not** recommend modifying this template file for beginners.

## Constraint of Chisel

- **Signed Integer is Default**  
  If you need 32-bit **unsigned** integer, you will meet an error if you take the most significant bit.
  Java has only **signed** integer.
  You need to use of **BigInt()**.
  When you assign 32-bit integer number to ```UInt``` primitive (eg. I/O, wire, reg, etc) then;
```
  Primitive := 0x80000000L.U
```
is the simple way, where litaral ```L``` means *signed long*, and literal *U* specify to cast unsigned int (UInt).

## Small Tips

- **Hexadecimal Numerical Representation**  
  We often want to use a hexadecimal number for coding.
  You can use literal prefix ```0x```.

- **Use of Utilities**

  If you want to use a utility function prepared already such as ```Log2()```, then add the following import description in your code;
```
  import chisel3.util._
```
  Utilities are listed in;  
  https://www.chisel-lang.org/api/latest/index.html#chisel3.util.package

- **Displaying Hexadecimal Number on iotersters**  
  You can display hexadecimal numbers instead of decimal numbers with following option (**only** for iotesters);
```
  --display-base 16
```

- **Use of Chisel Operators**  
  Chisel's operators are listed here;  
  https://www.chisel-lang.org/chisel3/operators.html  
  **Note** that chisel's data type and scala's data type are **different**.
  If you use Scala's variable as chisel's one, you need to *cast* explicitly.

- **Separate HDL file generation**  
  Chisel generates a single HDL file including all generated RTL modules.
  You may want to generate every class (modules in HDL) as separated files.
  Then you can do with following *SBT* option;

```
  --split-module
```

- **Reduction Operation across Vec**

  Let us see below declaration
```
  val hoge = Vec(Size, Bool())
```
And we want to do OR-reduction. There are two ways;
1. Cast to a UInt and use orR;
```
  hoge.asUInt.orR
```
orR is OR-reduction. Chisel's reductions are listed in here;  
  https://www.chisel-lang.org/chisel3/operators.html 

2. Use Scala's reduction method;
```
  hoge.reduce(_ || _)
```

- **Bundle and Initialization of Bundled Primitive**  
  You may want to simplify coding, especially for a redundant part and a common part. Let us see the following code;
```
  class datum (DataWidth: Int) extends Bundle {
    val valid = Bool()
    val data  = UInt(DataWidth.W)
  }
```

Class ```datum``` has to two primitives; ```valid``` (bool type) and ```data``` (unsigned DataWidth-bit integer).
Then you can use the class as a bundled primitive.
A register ```Datum``` with initialization can be written as follows;
```
  val Datum = RegInit(0.U.asTypeOf(new datum(DataWidth)))
```
  Both valid and data "reg with init var"s in RTL is *zero cleared* (unsigned zero) by hardware "reset" signal.
  You can specify your preferred initial value.
  The reset (and also the clock) is added automatically to the HDL.

- **Multi-Primitive**  
  You might want to have multiple instances of a class.
  ```Vec``` method helps us to code without redundant efforts.
  For example, ```Port()``` which defines I/O bundling several I/Os, we can define Num ports as a "port" like this;
```
  val io = IO(new Bundle {val port = Vec(Num, new Port(Width))})
```
  Then we can specify identical objects such by ```io.port(3).xxx``` for the fourth object, for example.
  **NOTE** that ```3``` is **not** Chisel's data type, it is Scala's data type.
  So, you can combine this description with Scala's coding style (because Chisel uses Scala language), such as a *for-loop*.

- **Multi-Instance with List**  
  When an adder ```Adder()``` is an instance class defining an adder logic circuit, then we can define "Num" adders like this;
```
  val ADD = List.fill(Num)(Module(new Adder(Width)))
```
  Then we can also specify identical objects such as ```ADD(3)```.


## Error Messages
- [error] "*OutOfMemory* (memory space)"

  **Meaning**: SBT (and thus Java Virtual Machine; JVM) needs more memory space.

  **Solution**: To Specify giving space, add this option at compilation;
```
  -mem 4096
```
  This gives 4GiB space in terms of MiB.

- [error] "*OutOfMemory* (heap space)"

  **Meaning**: SBT (and thus Java Virtual Machine; JVM) needs more heap memory space in Java.

  **Solution**: To Specify giving space, set below in terminal shell setting (bash: .bashrc);
```
  export _JAVA_OPTIONS=-Xmx2048m
```
  This gives 2048MB (2GiB) for heap memory used in Java.
  Where ```Xmx``` denotes a maximum size, JVM starts from ```Xms``` defines a size, and allocates space on demand.
  

- [error] "*java.lang.ClassNotFoundException: ProjectName*"

  **Meaning**: There is no ```ProjectName``` top module in your source file(s). Or, you might not have the correct directory structure.

  **Solution**: Check top module name.
  Or, check the directory structure which is needed for SBT.

- [exception] "*CheckInitialization$RefNotInitializedException*"

  **Meaning**: If your code does have an unknown state on the port "PortName" (or, wire) in a switch statement, the exception might caused by FIRRTL's procedure.
  This is caused on io or wire because these should have all statements clearly. Implicit-state on these primitives make the error (so, *register* does not make the error because it can hold a value).

  **Solution**: You can temporally fix this issue by assigning "*DontCare*" literal which is a reserved variable indicating "**Do not Care**". You can use it before assigning value to the port, as follows;
```
  Port/Wire := DontCare
```  
**NOTE**: the ```DontCare``` works everywhere.
If you use this assignment for other purposes then you can meet failed HDL generation.

## FIRRTL
To generate Verilog-File through firrtl, you can do it with;
```
firrtl -i InputFileName.fir -o OutputFileName.v -X verilog
```
```-i``` and ```-o``` options specify input and output files, respectively.
While this example inputs fir file, protobuf (*.pb) file also can be fed.