import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.expressions.SubqueryExpression
import org.apache.spark.sql.catalyst.plans.logical.{LogicalPlan, Project, Sort}
import org.apache.spark.sql.catalyst.rules._
case class MyPushDown(spark: SparkSession) extends Rule[LogicalPlan] {

  private def removeTopLevelSort(plan: LogicalPlan): LogicalPlan = {
    plan match {
      case Sort(_, _, child) => child
      case Project(fields, child) => Project(fields, removeTopLevelSort(child))
      case other => other
    }
  }

  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    case Sort(_, _, child) => {
      logWarning("*************************************************************")
      print("custom MyPushDown")
      child
    }
    case other => {
      logWarning("*************************************************************")
      print("custom MyPushDown")
      logWarning(s"Optimization batch is excluded from the MyPushDown optimizer")
      other
    }
  }
}