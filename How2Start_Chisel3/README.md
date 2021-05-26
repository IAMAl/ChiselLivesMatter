# Introduction to Chisel for Biginners
Chisel3 for Beginners; How to start hardware description language

## Target People for This Document
Beginners who have not experience about Chisel and Scala programming languages. Almost hardware engineers are categorized in this type.

## What is Chisel?
Chisel is to generate HDL code from high-level language, which is made with wrapping Scala language. Scala is made on Java virtual machine. Current version is Chisel3. Because the Java is object-oriented language, **class** in chisel code is converted to HDL, *Verilog-HDL*.

## Prerequisites

Prerequisites to run are;
- Java Runtime Environment (or JDK for running Scala)
- SBT (Simple Built Tool, to compile chisel code)

## Installations

**NOTE**: Before installation, you need to check you are one of which user types;
  - **Ordinary** user (let us say simply *user*) who wants to compile their chisel code to generate HDL, so they want to use chisel as a common tool, they should use **released** chisel. 
  - **Deveropper** user (let us call a *developer*) who needs advanced tool which is not yet released (ex. they might want **rc** version), and or want to costomize the tools for their purpose. They might want **pre-released** chisel.

### Common Installations for Users

- Java Runtime Environment (JRE)
  
  JRE is probably installed in your computer. You can check it through terminal;
```
  java -version
```
  If java does not exist in your computer, case of MacOS, you can install through **homebrew**. Case of ubuntu, you can use **apt**. SBT is based on *open sourced* Java, not on Oracle's one, see;
  https://openjdk.java.net/  

- SBT (installation instructions)

  Installation instruction is well descrived in this web-page;
  https://github.com/freechipsproject/chisel3/wiki/Installation-Preparation  
  SBT's dependency calls openJDK at its installation, even if you have installed Oracle's Java at default setting.


### Installation for Ordinary Users

  By using *template* of **build.sbt**, SBT invokes necessary **released** *tool softwares defined in the build.sbt* through internet (**so, network connection is necessary**). Template is always upto-date, so ordinary users need not to manual installation works. Template is here;

  https://github.com/freechipsproject/chisel-template

By doing **copy and paste** the template's build.sbt into your project directory, you can do your compilation!


### Installation for Developper Users

To install chisel, etc to your local computer, you need to compile source code of chisel, treadle, firrtl, etc, and assemble these. These need doing following command in the directory;
```
  sbt compile
```
You need assembly the compiled code to make runable code.
```
  sbt assembly
```
After this, you need to publish in order to invoke the tool from your local environment;

```
sbt publishLocal
```

- Stable Version of Recent Chisel
  
  Stable Recent Version: 3.4.0
  https://github.com/freechipsproject/chisel3/releases/tag/v3.4.0

- Tags for Recent Versions

  You can see recent version at here;
  https://github.com/freechipsproject/chisel3/tags


- iotesters

  iotesters is old version of verification tool which run on Scala and can generate VCD file.

  https://github.com/freechipsproject/chisel-testers/tree/master/src/main/scala/chisel3/iotesters

- tester2 (chisel-testers)

  tester2 is second generation of verification tool, currently this is on a main stream.

  https://github.com/freechipsproject/chisel-testers

- Coursier  
  
  This program based on Java is utility such as installing. We can use the program to install registered API, let us see to install FIRRTL as an example. Coursier can be installed by;
```
  curl -fLo cs https://git.io/coursier-cli-linux &&
    chmod +x cs &&
    ./cs
```
  This command is for Linux. program "cs" is generated on current directory. After that we can install FIRRTL which is registered in Coursier, as follows;
```
  ./cs bootstrap edu.berkeley.cs::firrtl:1.3.2 --main-class firrtl.stage.FirrtlMain -o firrtl-1.3.2
```
 This command generates firrtl-1.3.2 (see *-o* option to define name of generation). "firrtl:1.3.2" in the command selects the firrtl" with 1.3.2 version registed in it.

## Project Directory Structure
Necessary project directory structure is as follows;
```
root--+--build.sbt
      |
      +--src--+--main--+--scala--"YOUR_SOURCE_CODES.scala"
              |
              +--test--+--scala--"YOUR_TEST_CODES.scala"

```
This is **strict constraint** by SBT, all chisel codes should belong to this directory structure. If you do not take this directory structure, you will meet **ClassNotFound** error, SBT could not find root file.

## Compilation

At the compilation, SBT checks directory structure, and so, you do not need to specify which file(s) should be compiled. SBT traces a  *class-dependency* from top-class as a root. This means that you need to specify *root class*.

### Before Your Compilation

You must decide following point before starting your compilation;
1. [Option] Project Name:  
  This name can be defined in build.sbt, "name"-field. You can see the name at the compilation, this helps that you work on what project. This is hint for that you have multiple cmopilation points to identify. 
2. Top *class* similar to HDL needs (see bellow):  
   At the compilation, you need to set **which class** is a *top* module of HDL. So, you can choose preferred class as top module of compiling HDL by specifying the name.
3. "YOUR_TEST_CODES.scala" (see bellow)  
   This is needed only for using **iotesters**.

### Top Class
Top class description bellow should be added to your a *top-class* file, or a file you want to test/generate HDL. Replace "ProjectName" with your project's name.
```
object ProjectNameMain extends App {
  chisel3.Driver.execute(args,()=>new ProjectName("_args_"))
}
```
where ```_args_``` are arguments you defined in your class as a succeeded parameter(s) (option). You can do naming this object's name ```ProjectNameMain```, freely. SBT invokes the object ```ProjectNameMain```, and work with the object. This description is needed starting to generate HDL and to test on Chisel.

### How to Compile Your Code
Compiling on a terminal is simply as follows;
```
sbt 'run'
```
SBT will query which one you want to compile, then terminal notifies the list with number. You need to tell through number.

Or, you can specify top class which will be top-module of HDL.
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
- If you have multiple test codes, and you want do all tests, then simply do following;
```
  sbt test
```
- Test with tester2 on a terminal for particular class is simply as follows;
```
  sbt 'testOnly TestClassNameMain'
```
  where ```testOnly TestClassNameMain``` is your class made for testing (test bench)


### To dump VCD
Add this option;
```
-- -DwriteVcd=1
```
VCD file is stored into test_run_dir subfolder.

### YOUR_TEST_CODES.scala for iotesters
This file is needed to test your code on **iotesters** (**not tester2**). Set the test file name with ProjectNameMain.scala (replace "ProjectName" with your project's name). The file has to have bellow code at least;
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
where "ProjectName" in the code must be replaced with your project's name. We do **not** recommend to modify this template file for beginners.

## Constraint of Chisel

- **Signed Integer is Default**  
  If you need 32bit **unsigned** interger, you will meet an error if you take most significant bit. Java have only **signed** integer. You need to use of **BigInt()**. When you assign 32bit integer number to ```UInt``` primitive (eg. I/O, wire) then;
```
  Primitive := 0x80000000L.U
```
is simple way, where letaral ```L``` means *signed long*, and literal *U* specify to cast unsigned int (UInt).

## Small Tips

- **Hexadecimal Numerical Representation**  
  We often want to use hexadecimal number for coding. Leteral prefix ```0x``` can be used.

- **Use of Utilities**

  If you want to use utility function prepared already such as ```Log2()```, then add following import description in your code;
```
  import chisel3.util._
```
  Utilities are listed in;  
  https://www.chisel-lang.org/api/latest/index.html#chisel3.util.package

- **Displaying Hexadecimal Number on iotersters**  
  You can display hexadecimal number instead of decimal number with following option (**only** for iotesters);
```
  --display-base 16
```

- **Use of Chisel Operators**  
  Chisel's operators are listed in here;  
  https://www.chisel-lang.org/chisel3/operators.html  
  **Note** that chisel's data type and scala's data type is **different**. If you use Scala's variable as chisel's one, you need to *cast* explicitly.

- **Separate HDL file generation**  
  Chisel generates a single HDL file including all generated RTL module. You may want to generate every classes as separated files. Then you can do with following *SBT* option;

```
  --split-module
```

- **Reduction Operation across Vec**

  Let us see bellow declaration
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
  You may want to simplify coding, especially for redundant part and common part. Let us see following code;
```
  class datum (DataWidth: Int) extends Bundle {
    val valid = Bool()
    val data  = UInt(DataWidth.W)
  }
```

Class ```datum``` has to two primitives; ```valid``` (bool type) and ```data``` (unsigned DataWidth-bit integer). Then you can use the class as a bundled primitive. Then a register ```Datum``` with initialization can be written as follows;
```
  val Datum = RegInit(0.U.asTypeOf(new datum(DataWidth)))
```
  Both of valid and data "reg with init var"s in RTL is *zero cleared* (unsigned zero) by hardware "reset" signal. You can specify your preferred value. The reset (and also clock) is added automatically to the HDL.

- **Multi-Primitive**  
  You might want to have multiple instances of a class. ```Vec``` method help us to coding without redundant efforts.
  For example, ```Port()``` which defines I/O bundling several I/Os, we can define Num Ports as "port" like this;
```
  val io = IO(new Bundle {val port = Vec(Num, new Port(Width))})
```
  Then we can specify identical object such by ```io.port(3).xxx``` for fourth object. **NOTE** that ```3``` is **not** Chisel's data-type, it is Scala's data-type. So, you can combine this description with Scala's coding style (because of Chisel uses Scala language), such as a *for-loop*.

- **Multi-Instance with List**  
  When an adder ```Adder()``` is an instance class defining an adder logic circuit, then we can define "Num" adders like this;
```
  val ADD = List.fill(Num)(Module(new Adder(Width)))
```
  Then we can also specify identical object such as ```ADD(3)```.


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

  **Solution**: To Specify giving space, sett this in terminal shell setting (bash: .bashrc);
```
  export _JAVA_OPTIONS=-Xmx2048m
```
  This gives 2048MB (2GiB) for heap memory used in Java. Where ```Xmx``` denotes maximum size, JVM starts from ```Xms``` defined size, and allocates space on demand.
  

- [error] "*java.lang.ClassNotFoundException: ProjectName*"

  **Meaning**: There is no ProjectName top module in your source file(s). Or, you might not have correct directory structure.

  **Solution**: Check top module name. Or,check directory structure which is needed for SBT.

- [exception] "*CheckInitialization$RefNotInitializedException*"

  **Meaning**: If your code does have unknown state on the port "PortName" (or, wire) in switch statement, the exception might caused by FIRRTL's procedure. This is caused on io or wire, because these should have all statement clearlly. Implicit-state on these primitives make the error (so, *register* does not make the error because it can hold its value).

  **Solution**: You can temporally fix this issue by assigning "*DontCare*" literal which is reserved variable indicating "**Do not Care**". You can use it before assigning value to the port, like as follows;
```
  Port/Wire := DontCare
```  
**NOTE**: the ```DontCare``` works every where. If you use this assignment for other purpose then you will meet failed HDL generation.