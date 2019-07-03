package Parser

import scala.util.parsing.combinator.JavaTokenParsers

abstract class Tree
case class Or(l: Tree, r: Tree) extends Tree
case class Juxt(l: Tree, r: Tree) extends Tree
case class Opt(n: Tree) extends Tree
case class Parens(n: Tree) extends Tree
case class Val(n: String) extends Tree
case class Any() extends Tree

  class Combinators extends JavaTokenParsers{

    override def skipWhitespace: Boolean = false

    //E  -> T | T '|' E
    //T  -> F | F T
    //F  -> A | A '?'
    //A  -> C | '(' E ')'
    def e: Parser[Tree] = t ~ "|" ~ e ^^ { case l ~ _ ~ r => Or(l, r) } | t
    def t: Parser[Tree] = f ~ t ^^ { case l ~ r => Juxt(l, r) } | f
    def f: Parser[Tree] = a ~ "?" ^^ { case n ~ _ => Opt(n) } | a
    def a: Parser[Tree] = "(" ~ e ~ ")" ^^ { case _ ~ n ~ _  => Parens(n) } | c
    def c: Parser[Tree] = letter | wild

    def letter: Parser[Val] = {"a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | "k" | "l" | "m" | "n" |
      "o" | "p" | "q" | "r" | "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z" | "A" | "B" | "C" | "D" | "E" | "F" |
      "G" | "H" | "I" | "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" | "S" | "T" | "U" | "V" | "W" | "X" |
      "Y" | "Z" | "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" | " " } ^^ { n => Val(n)}
    def wild: Parser[Any] = "." ^^ { n => Any()}
  }

  object Main extends Combinators {

    //matches the full String to the tree
    def eval(t: Tree, s: String): Boolean = t match {
      case Or(l, r) => eval(l, s) | eval(r, s)
      case Juxt(l, r) => if (s.length > 1) { optCase(l, r, s)}
                         else optSingle(l, r, s)
      case Opt(n) => eval(n, s) | s == ""
      case Parens(n) => eval(n, s)
      case Val(n) => s == n
      case Any() => s.length == 1
      case other => false
    }

    //handles optionals within juxtaposition
    def optCase(l: Tree, r: Tree, s: String): Boolean = l match {
      case Opt(n) => (evalPartial(n, s) & eval(r, matchPartial(n, s))) | eval(r, s)
      case other => evalPartial(l, s) & eval(r, matchPartial(l, s))
    }

    //handles optionals in juxtaposition at the end of the String
    def optSingle(l: Tree, r: Tree, s: String): Boolean = l match {
      case Opt(n) => (eval(n, s) & eval(r, "")) | eval(r, s)
      case other => eval(l, s) & eval(r, "")
    }

    //matches a subtree to the String and returns whatever String is leftover
    def matchPartial(t: Tree, s: String): String = t match {
      case Or(l, r) => if (evalPartial(l, s)) {matchPartial(l, s)}
                       else {matchPartial(r, s)}
      case Juxt(l, r) => matchPartial(r, matchPartial(l, s))
      case Opt(n) => if (evalPartial(n, s)) {matchPartial(n, s)}
                     else s
      case Parens(n) => matchPartial(n, s)
      case Val(n) => if (s.length > 1) {
        if (n == s.slice(0,1)) {s.substring(1)}
        else s
        }else ""
      case Any() => if (s.length > 1) {s.substring(1)}
                    else ""
      case other => ""
    }

    //matches a subtree to a portion of the String
    def evalPartial(t: Tree, s: String): Boolean = t match {
      case Or(l, r) => evalPartial(l, s) | evalPartial(r, s)
      case Juxt(l, r) => if (s.length > 1) { partOptCase(l, r, s)}
                         else partOptSingle(l, r, s)
      case Opt(n) => evalPartial(n, s) | s == ""
      case Parens(n) => evalPartial(n, s)
      case Val(n) => n == s.slice(0,1)
      case Any() => s.length >= 1
      case other => false
    }

    //handles optionals in a partial matching
    def partOptCase(l: Tree, r: Tree, s: String): Boolean = l match {
      case Opt(n) => (evalPartial(n, s) & evalPartial(r, matchPartial(n, s))) | evalPartial(r, s)
      case other => evalPartial(l, s) & evalPartial(r, matchPartial(l, s))
    }

    //handles optionals in a partial matching where the end of the string is reached
    def partOptSingle(l: Tree, r: Tree, s: String): Boolean = l match {
      case Opt(n) => (evalPartial(n, s) & eval(r, "")) | evalPartial(r, s)
      case other => evalPartial(l, s) & eval(r, "")
    }

    def main(args: Array[String]){

      val pattern:Tree = parseAll(e, scala.io.StdIn.readLine("Pattern? ")).get
      println(pattern)

      while(true) {
        val strMatch = scala.io.StdIn.readLine("String? ")
        if (strMatch equals "quit") return
        println(eval(pattern, strMatch))
      }
    }
  }
