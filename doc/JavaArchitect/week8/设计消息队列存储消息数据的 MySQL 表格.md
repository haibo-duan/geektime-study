【作业内容】设计消息队列存储消息数据的 MySQL 表格

【作业要求】

1.包括表名、字段、索引；

2.用文字描述设计思路和理由，例如：为什么设计某个索引？

3.一页 PPT 即可。


设计思路：
- 1.每个队列可以采用一张表来存储。表名格式 mq_msg_{qname}_{yyyyMMdd}
- 2.表可以采用ShardSphere按日期实现分库分表。以写入日器来实现分区表。
- 3.消息ID 和表的ID均满足单调递增，可以采用雪花算法实现，ShardSphere带了这个算法。
- 4.数据需要实现备份，根据需要实现一主多从。
- 5.需要添加一张队列的管理表,存储每个队列的生产者和消费者情况。mq_topic_{qname}_{yyyyMMdd}.这表也需要分库分表。

mq_msg_{qname}_{yyyyMMdd} 表结构：
- id  char(32) 主键，单调递增
- msg_id char(32) 单调递增
- msg_type char(10) 消息类型
- msg_content varchar(500) 消息内容
- create_time data_time 创建时间  分区字段
- is_valid  short 是否生效，假删除标识
- update_time  data_time 更新时间

mq_topic_{qname}_{yyyyMMdd} 表结构：
- id  char(32) 主键，单调递增
- msg_id char(32) 单调递增 与mq_msg_{qname} 关联
- producer_id int 生产者id
- consumer_id int 消费者id
- status short  状态 消费未消费
- consume_time 消费时间
- create_time data_time 创建时间  分区字段
- is_valid  short 是否生效，假删除标识
- update_time  data_time 更新时间



