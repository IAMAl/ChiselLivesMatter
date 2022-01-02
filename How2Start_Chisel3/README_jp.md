# 初めてのChisel
Chisel使用にあたっての事前準備紹介です。

## この記事の対象者
Chiselに興味があるけど使ったことがなく、とりあえず触ってみたい方。Scalaスクリプト言語で高位記述でハードウェア記述を体験してみたい方。

## Chiselって何?
ChiselはHDL(Hardware Description Language;　ハードウェア記述言語)を生成するスクリプト言語です。Java系のScalaスクリプト言語を基にしていて、文法規則も基本それに従いますが、Chiselではハードウェア記述のためのプリミティブなクラスや文法が追加されています。Scalaコーディングライクにハードウェア設計ができる特徴を持ちます。現在のChiselのバージョンはChisel3で、リリースバージョンは3.5が最新です。Scalaはオブジェクト指向プログラミング言語ですので、それに従ってChiselではハードウェアを**クラス**として定義してそれを基にHDLを生成します。

## 事前準備

予め必要なものは以下のとおりです；
- Java Runtime Environment (or JDK, Scala実行のための大元の言語)
- SBT (Simple Built Tool, Chisel(Scala)実行のための環境)
- インターネットワーク環境

### 一般的なインストール手順

初めに事前準備のソフトについて説明します。
- Java Runtime Environment (JRE)
  
    おそらくJREはPCにデフォルトでインストールされていると思いますが、念のために次のコマンドをターミナルで試してみてください。
```
  java -version
```
バージョン情報とともに実行されない場合、インストールされていません。Macであれば***homebrew*を使用してインストールできます。Ubuntuの場合、**apt**を使用してインストールできます。次にインストールするSBTはオープンソースJavaなのでOracle社のものではなくOpenJDKの方が必要です。

https://openjdk.java.net/  

- SBT

    インストール方法は次のWebサイトに記載されています。
    https://github.com/freechipsproject/chisel3/wiki/Installation-Preparation
    SBTはインストールの際にOpenJDKを呼ぶのでデフォルトでOracle社のものがインストールされている場合、別途先に説明したJREをインストールしてください。

## インストール

**注意**: インストール前にChisel利用にあたり自分がどちらの利用者か確認してください。どちらの利用者かで適切なインストール方法が異なります。
    - **一般**利用者：chisel記述したプログラムからHDLを生成する目的で使用したい。Chiselは一般的なもので良いので**リリース**されているchiselを使用すれば十分です。ローカルにインストールしなくても利用できます。その代わりネットワーク環境が必要です。
    - **開発**利用者：最新のChiselバージョンが必要（例えば**rc**バージョンを使いたいなど）だったり、ローカル環境で開発環境を帰結したい方、Chiselソース高度に触れてカスタマイズしたい方。おそらく**プリリリース**されているchiselを使用したいのではないでしょうか。

### 一般利用者向けのインストール方法

ChiselはSBTで実行する際に**build.sbt**という実行するバージョン指定などが記述された環境ファイルであり、これを参照します。このファイルには**リリース**バージョンの一般的な定義が記載されており、SBT実行時にMavenサーバにアクセスしますのでネットワーク環境は必須です。build.sbtファイルを更新すれば常に最新の環境でChiselを使用できます。予めテンプレートのbuild.sbtが用意されていますので定期的にこのテンプレートを更新すれば良いだけです。
テンプレートはここにあります：

    https://github.com/freechipsproject/chisel-template

**コピー＆ペースト**をプロジェクトディレクトリで行えば良いだけです。これでChiselの環境は整いました。コーディングしてコンパイルしてみてください！


### 開発利用者向けのインストール方法

ローカルコンピュータにインストールしたい場合、Chiselなどのツールをコンパイルしてインストールする必要があります。インストールまでの手順は次のとおりです。
```
  sbt compile
```
コンパイル後に実行可能なファイルにします。
```
  sbt assembly
```
そしてローカル環境で使用できるようにします。

```
sbt publishLocal
```


- Coursier  
  
Coursierを使ってインストールすることもできます。FIRRTLを例に説明します。次のコマンドでcurlを使用してCoursierをローカル環境にインストールします。
```
  curl -fLo cs https://git.io/coursier-cli-linux &&
    chmod +x cs &&
    ./cs
```
このコマンドで実行可能なCoursierがファイル名"cs"として生成されます（もちろんCourssierが事前にインストールされていればこのステップをスキップできます）。この後にFIRRTLをインストールします。
```
  ./cs bootstrap edu.berkeley.cs::firrtl:1.3.2 --main-class firrtl.stage.FirrtlMain -o firrtl-1.3.2
```
Coursierに登録されているバージョンに限定されることに留意してください。上記コマンドでは```firrtl:1.3.2```でバージョンを指定して、```-o```オプションでインストールするFIRRTLファイル名を指定しています。



## テスト環境

- 安定バージョン
Stable Recent Version: 3.5.0
```
https://github.com/freechipsproject/chisel3/releases/tag/v3.4.0
```
- タグ情報
```
https://github.com/freechipsproject/chisel3/tags
```

- iotesters

iotestersは旧テスト用ツールです。
```
https://github.com/freechipsproject/chisel-testers/tree/master/src/main/scala/chisel3/iotesters
```
- tester2 (chisel-testers)
```
https://github.com/freechipsproject/chisel-testers
```

## プロジェクトのディレクトリ構造
Chiselを使用した開発にあたってScala制約の洗礼を受けます。代表的なものが**ディレクトリ構造の制約**です。
```
root--+--build.sbt
      |
      +--src--+--main--+--scala--"YOUR_SOURCE_CODES.scala"
              |
              +--test--+--scala--"YOUR_TEST_CODES.scala"

```
この構造にしないとHDL生成時にトップファイルを見つけることができず**ClassNotFound**エラーになります。


## コンパイル

コンパイルにあたって、SBTはプロジェクトディレクトリ構造に従ってそのディレクトリ内ををチェックします。SBTはトップモジュールを認識しそれに紐ずくファイル群をトレースしてクラス間依存を確認します。このため、トップモジュール（クラス）はどれかを指定してあげる必要があります。


### コンパイルの前に

コンパイルにあたって次の事項を定義しておく必要があります。
1. [オプション] **プロジェクト名**:
プロジェクト名は開発するプロジェクトのアイデンティティです。固有の名前を定義することをお勧めします。この名前は```build.sbt```ファイルに定義することで、実行中のスクリプトが何かを認識できるようになります。従って複数管理する場合ヒントになります。
2. **トップクラス**
HDLと同じくトップモジュール（クラス）はどれかを指定します。
どのクラスがトップかを指定するにあたり、適宜固有な名前をクラスに付与してください。
3. "YOUR_TEST_CODES.scala"
**iotesters**で使用します。

## トップクラス
トップクラスの記述はトップクラスのファイルに次の構文を付与すれば問題なくChiselはチェックできコンパイルに移行することができます。"ProjectName"を自分のプロジェクトの名前に置き換えてください。

```
object ProjectNameMain extends App {
  chisel3.Driver.execute(args,()=>new ProjectName("_args_"))
}
```

```_args_``` はそのトップモジュール（クラス）が継承している引数を表現していますので、クラスで引数を使用していなければ不要ですが使用している場合間接的でも良いので引数を指定しておく必要があります。

## コンパイル方法
ターミナルでコンパイルすることになると思いますが次のコマンドでコンパイルできます。
```
sbt 'run'
```
SBTはコンパイルにあたってどうしたいかを質問してきます。可能なオプションから番号を指定してコンパイルを行います。
あるいは、直接的に先に定義した```ProjectNameMain```を指定してコンパイルをすることもできます。

```
sbt 'runMain ProjectNameMain'
```
パッケージ名```package_name```を予め指定している場合（複数の同じクラスが存在していてそれを区別するためにパッケージ名を使用している）、パッケージ名も指定してコンパイルすることをお勧めします。
```
sbt 'runMain package_name.ProjectNameMain'
```

```runMain```は```ProjectNameMain```を呼び出しコンパイルを行い、HDLを生成します。

### コードのテスト

- iotesterを使ってターミナル上でのテストする場合は次のコマンドを実行します
```
  sbt 'test:runMain ProjectNameMain'
```
```ProjectNameMain```の部分は先に説明したとおりです。

- 複数のテストを行い時は次のようにテストを指示します。
```
  sbt test
```
```
  sbt 'testOnly TestClassNameMain'
```
```TestClassNameMain```はテストコードのクラスを指定します。


### VCDのダンプ
VCDファイルをダンプできますので波形を確認できます。全てのプリミティブをダンプしてしまうので、規模や時間が長い場合はファイルサイズに注意が必要です。
```
-- -DwriteVcd=1
```
VCDは```test_run_dir```ディレクトリにあります。

## Chiselの制約

- **符号付き整数がデフォルト**  
  Java系言語は符号付き整数のみ対応しています。32bit **符号なし**整数を使う場合、テスト上、最上ビットは符号ビットとして扱われます。また代入捜査においてもエラーとして扱われます。 32ビット符号無し```UInt```のプリミティブ (eg. I/O, wire) に最上位ビットを立てたい場合、
```
  Primitive := 0x80000000L.U
```
が簡単な方法です。リテラル ```L``` は *signed long*、で リテラル *U* で符号無し整数(UInt)へキャストしています。

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
  Both of valid and data "reg with init var"s in RTL is *zero cleared* (unsigned zero) by hardware "reset" signal. You can specify your preferred initial value. The reset (and also clock) is added automatically to the HDL.

- **Multi-Primitive**  
  You might want to have multiple instances of a class. ```Vec``` method help us to coding without redundant efforts.
  For example, ```Port()``` which defines I/O bundling several I/Os, we can define Num Ports as a "port" like this;
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

  **原因**: SBT (Java Virtual Machine; JVM) はメモリを必要としています。

  **対処**: SBTにさらにメモリを割り当てます。SBT実行時に次のオプションをしています。
```
  -mem 4096
```
これで4GiB割り当てることができます（MiB単位です）.

- [error] "*OutOfMemory* (heap space)"

  **原因**: SBT (Java Virtual Machine; JVM) はヒープメモリを必要としています。

  **対処**: 環境変数を設定します。ターミナルで下記を実行するかシェル設定ファイルへ記述します。
```
  export _JAVA_OPTIONS=-Xmx2048m
```
これによりヒープスペースとして2GiB割り当てます（MiB単位）。```Xmx```は最大割り当てサイズで、JVMは```Xms```で定義されているサイズから必要に応じてこの最大サイズまで動的に確保します。
  
- [error] "*java.lang.ClassNotFoundException: ProjectName*"

  **原因**: ProjectNameのトップクラス（モジュール）がプロジェクトファイルに記述されていないときに発生します。あるいはプロジェクトのディレクトリ構造が先に説明した構造になっていない場合も発生します。

  **対処**: トップクラス（モジュール）の名前を確認してください。あるいはディレクトリ構造も併せて確認してください。
