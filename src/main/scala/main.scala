import scala.io.Source

/**
 * Created by roman on 25/09/14.
 */

object main {
  def main(args: Array[String]): Unit = {
    val table = Source.fromFile(args(0)).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
    val meta = table.filter(_.length<=3)
    val data = table.filter(_.length>3)
    val dataset = new Dataset(data, meta)
    dataset.getMergedTable
    dataset.makeFile(args(1))
  }
}