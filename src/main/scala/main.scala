/**
 * Created by roman on 25/09/14.
 */

object main {
  def main(args: Array[String]): Unit = {
    val reader = new Reader
    val lines, cols = reader.load(args(0), args(1))
  }
}