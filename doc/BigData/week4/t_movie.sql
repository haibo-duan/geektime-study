-- 建t_movie_dhb表
CREATE TABLE `t_movie_dhb`(
    `movie_id` bigint COMMENT '电影id',
    `movie_name` string COMMENT '电影名字',
    `movie_type` string COMMENT '电影类型')
ROW FORMAT SERDE 
    'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ( 
    'field.delim'='::') -- 按::进行分隔，如果数据本身是别的分隔符，按具体情况选择，例如：\t
STORED AS INPUTFORMAT 
    'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT 
    'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
    '/haibo.duan/week4/movies' -- hdfs文件路径
TBLPROPERTIES (
    'bucketing_version'='2',
    'transient_lastDdlTime'='1648533877');
