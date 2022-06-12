import org.apache.spark.{SparkConf, SparkContext}

import org.apache.hadoop.conf.Configuration
import scala.collection.mutable.ListBuffer
import java.util.concurrent.Executors
import scala.concurrent.Await
import org.apache.spark.rdd.RDD
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path

object DistCp {

  def main(args: Array[String]): Unit = {
    type OptionMap = Map[Symbol, Any]
    if (args.length == 0) println("the args is null!")
    val arglist = args.toList


    def nextOption(map: OptionMap, list: List[String]): OptionMap = {
      def isSwitch(s: String) = (s(0) == '-')

      list match {
        case Nil => map
        case "-i" :: value => nextOption(map ++ Map('ignoreFailure -> 1), list.tail)
        case "-m" :: value :: tail =>
          nextOption(map ++ Map('maxconcurrency -> value.toInt), tail)
        case string :: Nil => nextOption(map ++ Map('outfile -> string), list.tail)
        case string :: tail => nextOption(map ++ Map('infile -> string), tail)
        case option :: opt2 :: tail if isSwitch(opt2) =>
          println("Unknown option " + option)
          sys.exit(1)
      }
    }

    val options = nextOption(Map(), arglist)
    println(options)

    val sourceFolder = String.valueOf(options(Symbol("infile")))
    val targetFolder = String.valueOf(options(Symbol("outfile")))
    val concurrency = (options(Symbol("maxconcurrency"))).toString.toInt
    val ignoreFailure = options(Symbol("ignoreFailure")).toString.toInt

    val sparkConf = new SparkConf().setAppName("bingbing").setMaster("local[1]")
    val sc = new SparkContext(sparkConf)
    val sb = new StringBuffer();
    var fileNames = new ListBuffer[String]()

    val conf = new Configuration();
    conf.set("fs.defaultFS", "hdfs://localhost:9000")

    traverseDir(conf, sourceFolder, fileNames);
    fileNames.foreach(
      fileName =>
        try {
          sc.textFile(fileName, concurrency).saveAsTextFile(fileName.replace(sourceFolder, targetFolder));
        } catch {
          case t: Throwable => t.printStackTrace()
            if (ignoreFailure == 0) {
              throw new Exception("failed to copy " + fileName)
            }
        })
  }

  def traverseDir(hdconf: Configuration, path: String, filePaths: ListBuffer[String]) {
    val files = FileSystem.get(hdconf).listStatus(new Path(path))
    files.foreach { fStatus => {
      if (!fStatus.isDirectory) {
        filePaths += fStatus.getPath.toString
      } else if (fStatus.isDirectory) {
        traverseDir(hdconf, fStatus.getPath.toString, filePaths)
      }
    }
    }
  }
}
