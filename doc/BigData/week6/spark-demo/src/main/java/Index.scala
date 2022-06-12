import org.apache.spark.{SparkConf, SparkContext}

object Index {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("bingbing").setMaster("local[1]")
    val sc = new SparkContext(sparkConf)
    //调用Spark读取文件API,读取文件内容
//    val url = getClass.getResource("index.txt");
    val wordRDD= sc.textFile("/haibo.duan/week6/index.txt")
      //使用flatMap进行分词后展开
      .flatMap {
        line =>
          //以.做分词需要加转义符
          val array = line.split("\\.", 2)
          val bookName = array(0)
          val result = array(1).replaceAll("\"","")
          result.split(" ").map(word => (bookName, word))
      }

    val kvRDD= wordRDD.map(kv => (kv._2, kv._1)).map((_, 1))
      .reduceByKey((x,y) => x + y)
      .map{case ((k,v),cnt) => (k,(v,cnt))}
      .groupByKey() //只分组不聚合
      .collect()
      .foreach(println)
  }
}
