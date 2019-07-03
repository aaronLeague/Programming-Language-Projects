//Modification Author: Aaron B. League
//Code further modified from class code
//Schlegel, CSC344, Spring 2019

package Parser

  import scala.util.parsing.combinator._

  abstract class Tree
  case class Sum(l: Tree, r: Tree) extends Tree
  case class Prod(l: Tree, r: Tree) extends Tree
  case class Var(n: String) extends Tree
  case class Const(v: Int) extends Tree

  // E -> T + E | T
  // T -> Const | Var

  class RecursiveDescent {
    val constregex = "^[0-9]+".r
    val varregex = "^[A-Za-z]+".r

    var index = 0
    def parseE(in: String): Tree = {
      // We have to do T first.
      val pt = parseT(in)
      // Get what's left of the string
      val currStr = in.substring(index)
      if (currStr.length > 0 && currStr(0) == '+'){
        index+=1; // Advance past +
        Sum(pt, parseE(in))
      }
      else
        pt
    }

    def parseT(in: String): Tree = {
      // We have to do F first.
      val pt = parseF(in)
      // Get what's left of the string
      val currStr = in.substring(index)
      if (currStr.length > 0 && currStr(0) == '*'){
        index+=1; // Advance past +
        Prod(pt, parseT(in))
      }
      else
        pt
    }

    def parseF(in: String): Tree = {
      val currStr = in.substring(index)

      val consts = constregex.findAllIn(currStr)
      if (consts.hasNext){
        val const: String = consts.next()
        index += const.length()
        Const(const.toInt)
      }
      else {
        val vars = varregex.findAllIn(currStr)
        val varname = vars.next()
        index += varname.length()
        Var(varname)
      }
    }
  }

  class Combinators extends JavaTokenParsers{

    // E -> T + E | T
    // T -> Const | Var
    def e: Parser[Tree] = t ~ "+" ~ e ^^ { case l ~ _ ~ r => Sum(l, r) } | t
    def t: Parser[Tree] = f ~ "*" ~ t ^^ { case l ~ _ ~ r => Prod(l, r) } | f
    def f: Parser[Tree] = const | varname

    def const: Parser[Const] = "[0-9]+".r ^^ { str => Const(str.toInt) }
    def varname: Parser[Var] = "[A-Za-z]+".r ^^ { str => Var(str) }
  }

  object Main extends Combinators {
    type Environment = String => Int

    def eval(t: Tree, env: Environment): Int = t match {
      case Sum(l, r) => eval(l, env) + eval(r, env)
      case Prod(l, r) => eval(l, env) * eval(r, env)
      case Var(n)    =>; env(n)
      case Const(v)  => v
    }

    def main(args: Array[String]){
      val exp: Tree = Sum(Var("x"),Sum(Var("x"),Sum(Const(7),Var("y"))))
      val env: Environment = { case "x" => 5 case "y" => 7 }
      println(eval(exp, env))

      val rd = new RecursiveDescent()
      val exp2rd:Tree = rd.parseE("x+x*7+y*12")
      println(exp2rd)
      println(eval(exp2rd, env))

      val exp2c:Tree = parseAll(e, "x+x*7+y*12").get
      println(exp2c)
      println(eval(exp2c, env))
    }
  }

