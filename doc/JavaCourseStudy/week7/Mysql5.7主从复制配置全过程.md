
mysql主从复制是最常见的高可用方式，通过主-从的方式，实现系统的高可用。在生产环境种，通常采用一主多从的方式，通过主库写数据，从库读数据，来提升系统的性能。
现在就演示一下，mysql5.7之下，如何配置主从复制。

# 1. 环境准备
两台安装好mysql数据库的服务器如下：

| 序号 | 服务器IP         | 主/从 | mysql版本 |
|:----|:----------------|:------|:---------|
| 1   | 192.168.161.114 | 主    | 5.7.34    |
| 2   | 192.168.161.115 | 从    | 5.7.34    |


# 2.主库配置
查看主库log配置：
```
mysql> show global variables like '%log%';
+--------------------------------------------+------------------------------------------+
| Variable_name                              | Value                                    |
+--------------------------------------------+------------------------------------------+
| back_log                                   | 300                                      |
| binlog_cache_size                          | 1048576                                  |
| binlog_checksum                            | CRC32                                    |
| binlog_direct_non_transactional_updates    | OFF                                      |
| binlog_error_action                        | ABORT_SERVER                             |
| binlog_format                              | MIXED                                    |
| binlog_group_commit_sync_delay             | 0                                        |
| binlog_group_commit_sync_no_delay_count    | 0                                        |
| binlog_gtid_simple_recovery                | ON                                       |
| binlog_max_flush_queue_time                | 0                                        |
| binlog_order_commits                       | ON                                       |
| binlog_row_image                           | FULL                                     |
| binlog_rows_query_log_events               | OFF                                      |
| binlog_stmt_cache_size                     | 32768                                    |
| binlog_transaction_dependency_history_size | 25000                                    |
| binlog_transaction_dependency_tracking     | COMMIT_ORDER                             |
| expire_logs_days                           | 30                                       |
| general_log                                | OFF                                      |
| general_log_file                           | /opt/mysql/data/m161p114.log             |
| innodb_api_enable_binlog                   | OFF                                      |
| innodb_flush_log_at_timeout                | 1                                        |
| innodb_flush_log_at_trx_commit             | 2                                        |
| innodb_locks_unsafe_for_binlog             | OFF                                      |
| innodb_log_buffer_size                     | 2097152                                  |
| innodb_log_checksums                       | ON                                       |
| innodb_log_compressed_pages                | ON                                       |
| innodb_log_file_size                       | 33554432                                 |
| innodb_log_files_in_group                  | 3                                        |
| innodb_log_group_home_dir                  | ./                                       |
| innodb_log_write_ahead_size                | 8192                                     |
| innodb_max_undo_log_size                   | 1073741824                               |
| innodb_online_alter_log_max_size           | 134217728                                |
| innodb_undo_log_truncate                   | OFF                                      |
| innodb_undo_logs                           | 128                                      |
| log_bin                                    | ON                                       |
| log_bin_basename                           | /opt/mysql/data/mysql-bin                |
| log_bin_index                              | /opt/mysql/data/mysql-bin.index          |
| log_bin_trust_function_creators            | OFF                                      |
| log_bin_use_v1_row_events                  | OFF                                      |
| log_builtin_as_identified_by_password      | OFF                                      |
| log_error                                  | /opt/mysql/log/mysql-error.log           |
| log_error_verbosity                        | 3                                        |
| log_output                                 | FILE                                     |
| log_queries_not_using_indexes              | OFF                                      |
| log_slave_updates                          | OFF                                      |
| log_slow_admin_statements                  | OFF                                      |
| log_slow_slave_statements                  | OFF                                      |
| log_statements_unsafe_for_binlog           | ON                                       |
| log_syslog                                 | OFF                                      |
| log_syslog_facility                        | daemon                                   |
| log_syslog_include_pid                     | ON                                       |
| log_syslog_tag                             |                                          |
| log_throttle_queries_not_using_indexes     | 0                                        |
| log_timestamps                             | UTC                                      |
| log_warnings                               | 2                                        |
| max_binlog_cache_size                      | 18446744073709547520                     |
| max_binlog_size                            | 1073741824                               |
| max_binlog_stmt_cache_size                 | 18446744073709547520                     |
| max_relay_log_size                         | 0                                        |
| relay_log                                  |                                          |
| relay_log_basename                         | /opt/mysql/data/m161p114-relay-bin       |
| relay_log_index                            | /opt/mysql/data/m161p114-relay-bin.index |
| relay_log_info_file                        | relay-log.info                           |
| relay_log_info_repository                  | FILE                                     |
| relay_log_purge                            | ON                                       |
| relay_log_recovery                         | OFF                                      |
| relay_log_space_limit                      | 0                                        |
| slow_query_log                             | ON                                       |
| slow_query_log_file                        | /opt/mysql/log/mysql-slow.log            |
| sql_log_off                                | OFF                                      |
| sync_binlog                                | 1                                        |
| sync_relay_log                             | 10000                                    |
| sync_relay_log_info                        | 10000                                    |
+--------------------------------------------+------------------------------------------+
73 rows in set (0.01 sec)
```
查看主库日志文件：
```
mysql> show master logs;
+------------------+-----------+
| Log_name         | File_size |
+------------------+-----------+
| mysql-bin.000001 |       177 |
| mysql-bin.000002 |       711 |
| mysql-bin.000003 |      1225 |
| mysql-bin.000004 |       177 |
| mysql-bin.000005 |       154 |
+------------------+-----------+
5 rows in set (0.00 sec)
```
查看主库server相关配置：
```
mysql> show global variables like '%server%';
+---------------------------------+--------------------------------------+
| Variable_name                   | Value                                |
+---------------------------------+--------------------------------------+
| character_set_server            | utf8                                 |
| collation_server                | utf8_general_ci                      |
| innodb_ft_server_stopword_table |                                      |
| server_id                       | 1                                    |
| server_id_bits                  | 32                                   |
| server_uuid                     | 10dfcf35-16c2-11ec-9238-5254003c7ee2 |
+---------------------------------+--------------------------------------+
6 rows in set (0.00 sec)
```
主库创建一个用于复制的用户：
```
mysql> create user 'repl'@'%' identified by '123456';
Query OK, 0 rows affected (0.00 sec)
```
将replication slave,replication client 授权到repl用户：
```
mysql> grant replication slave,replication client on *.* to 'repl'@'%';
Query OK, 0 rows affected (0.00 sec)
```
刷新权限：
```
mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)
```
查看主库status:
```
mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000005 |      767 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
```
可以看到，此时主库当前日志的最后一个文件为005，且position为767.

# 2.从库配置
查看从库的log配置：
```
mysql> show global variables like '%log%';
+--------------------------------------------+---------------------------------------+
| Variable_name                              | Value                                 |
+--------------------------------------------+---------------------------------------+
| back_log                                   | 300                                   |
| binlog_cache_size                          | 1048576                               |
| binlog_checksum                            | CRC32                                 |
| binlog_direct_non_transactional_updates    | OFF                                   |
| binlog_error_action                        | ABORT_SERVER                          |
| binlog_format                              | MIXED                                 |
| binlog_group_commit_sync_delay             | 0                                     |
| binlog_group_commit_sync_no_delay_count    | 0                                     |
| binlog_gtid_simple_recovery                | ON                                    |
| binlog_max_flush_queue_time                | 0                                     |
| binlog_order_commits                       | ON                                    |
| binlog_row_image                           | FULL                                  |
| binlog_rows_query_log_events               | OFF                                   |
| binlog_stmt_cache_size                     | 32768                                 |
| binlog_transaction_dependency_history_size | 25000                                 |
| binlog_transaction_dependency_tracking     | COMMIT_ORDER                          |
| expire_logs_days                           | 30                                    |
| general_log                                | OFF                                   |
| general_log_file                           | /opt/mysql/data/m161p115.log          |
| innodb_api_enable_binlog                   | OFF                                   |
| innodb_flush_log_at_timeout                | 1                                     |
| innodb_flush_log_at_trx_commit             | 2                                     |
| innodb_locks_unsafe_for_binlog             | OFF                                   |
| innodb_log_buffer_size                     | 2097152                               |
| innodb_log_checksums                       | ON                                    |
| innodb_log_compressed_pages                | ON                                    |
| innodb_log_file_size                       | 33554432                              |
| innodb_log_files_in_group                  | 3                                     |
| innodb_log_group_home_dir                  | ./                                    |
| innodb_log_write_ahead_size                | 8192                                  |
| innodb_max_undo_log_size                   | 1073741824                            |
| innodb_online_alter_log_max_size           | 134217728                             |
| innodb_undo_log_truncate                   | OFF                                   |
| innodb_undo_logs                           | 128                                   |
| log_bin                                    | ON                                    |
| log_bin_basename                           | /opt/mysql/data/mysql-bin             |
| log_bin_index                              | /opt/mysql/data/mysql-bin.index       |
| log_bin_trust_function_creators            | OFF                                   |
| log_bin_use_v1_row_events                  | OFF                                   |
| log_builtin_as_identified_by_password      | OFF                                   |
| log_error                                  | /opt/mysql/log/mysql-error.log        |
| log_error_verbosity                        | 3                                     |
| log_output                                 | FILE                                  |
| log_queries_not_using_indexes              | OFF                                   |
| log_slave_updates                          | OFF                                   |
| log_slow_admin_statements                  | OFF                                   |
| log_slow_slave_statements                  | OFF                                   |
| log_statements_unsafe_for_binlog           | ON                                    |
| log_syslog                                 | OFF                                   |
| log_syslog_facility                        | daemon                                |
| log_syslog_include_pid                     | ON                                    |
| log_syslog_tag                             |                                       |
| log_throttle_queries_not_using_indexes     | 0                                     |
| log_timestamps                             | UTC                                   |
| log_warnings                               | 2                                     |
| max_binlog_cache_size                      | 18446744073709547520                  |
| max_binlog_size                            | 1073741824                            |
| max_binlog_stmt_cache_size                 | 18446744073709547520                  |
| max_relay_log_size                         | 0                                     |
| relay_log                                  | slave-relay-bin                       |
| relay_log_basename                         | /opt/mysql/data/slave-relay-bin       |
| relay_log_index                            | /opt/mysql/data/slave-relay-bin.index |
| relay_log_info_file                        | relay-log.info                        |
| relay_log_info_repository                  | FILE                                  |
| relay_log_purge                            | ON                                    |
| relay_log_recovery                         | OFF                                   |
| relay_log_space_limit                      | 0                                     |
| slow_query_log                             | ON                                    |
| slow_query_log_file                        | /opt/mysql/log/mysql-slow.log         |
| sql_log_off                                | OFF                                   |
| sync_binlog                                | 1                                     |
| sync_relay_log                             | 10000                                 |
| sync_relay_log_info                        | 10000                                 |
+--------------------------------------------+---------------------------------------+
73 rows in set (0.01 sec)
```
查看从库的server配置：
```
mysql> show global variables like '%server%';
+---------------------------------+--------------------------------------+
| Variable_name                   | Value                                |
+---------------------------------+--------------------------------------+
| character_set_server            | utf8                                 |
| collation_server                | utf8_general_ci                      |
| innodb_ft_server_stopword_table |                                      |
| server_id                       | 2                                    |
| server_id_bits                  | 32                                   |
| server_uuid                     | 03cb46fa-16bc-11ec-9550-525400ea020a |
+---------------------------------+--------------------------------------+
6 rows in set (0.00 sec)
```
查看从库的master日志：
```
mysql> show master logs;
+------------------+-----------+
| Log_name         | File_size |
+------------------+-----------+
| mysql-bin.000001 |       177 |
| mysql-bin.000002 |       711 |
| mysql-bin.000003 |      1225 |
| mysql-bin.000004 |       177 |
| mysql-bin.000005 |       177 |
| mysql-bin.000006 |       154 |
+------------------+-----------+
6 rows in set (0.00 sec)
```
查看从库的status:
```
mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000006 |      154 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
```

现在对从库进行配置：
```
mysql> change master to master_host='192.168.161.114',master_user='repl',master_password='123456',master_log_file='mysql-bin.000005',master_log_pos=767;
Query OK, 0 rows affected, 2 warnings (0.02 sec)
```
需要注意的是，master_log_file 与master_log_pos 均要指向主库对应的日志文件的最后一个文件。
此处的指向为前面第一部分中查看的日志的值master_log_file='mysql-bin.000005',master_log_pos=767。

# 3.启动从库
查看从库的status:
```

mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: 
                  Master_Host: 192.168.161.114
                  Master_User: repl
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000005
          Read_Master_Log_Pos: 767
               Relay_Log_File: slave-relay-bin.000001
                Relay_Log_Pos: 4
        Relay_Master_Log_File: mysql-bin.000005
             Slave_IO_Running: No
            Slave_SQL_Running: No
              Replicate_Do_DB: 
          Replicate_Ignore_DB: 
           Replicate_Do_Table: 
       Replicate_Ignore_Table: 
      Replicate_Wild_Do_Table: 
  Replicate_Wild_Ignore_Table: 
                   Last_Errno: 0
                   Last_Error: 
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 767
              Relay_Log_Space: 154
              Until_Condition: None
               Until_Log_File: 
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File: 
           Master_SSL_CA_Path: 
              Master_SSL_Cert: 
            Master_SSL_Cipher: 
               Master_SSL_Key: 
        Seconds_Behind_Master: NULL
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error: 
               Last_SQL_Errno: 0
               Last_SQL_Error: 
  Replicate_Ignore_Server_Ids: 
             Master_Server_Id: 0
                  Master_UUID: 
             Master_Info_File: /opt/mysql/data/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: 
           Master_Retry_Count: 86400
                  Master_Bind: 
      Last_IO_Error_Timestamp: 
     Last_SQL_Error_Timestamp: 
               Master_SSL_Crl: 
           Master_SSL_Crlpath: 
           Retrieved_Gtid_Set: 
            Executed_Gtid_Set: 
                Auto_Position: 0
         Replicate_Rewrite_DB: 
                 Channel_Name: 
           Master_TLS_Version: 
1 row in set (0.01 sec)

ERROR: 
No query specified
```
因为没有启动从节点的复制线程，这两个参数都为NO.
Slave_IO_Running: No
Slave_SQL_Running: No

现在启动主从复制：
```
mysql> start slave;
Query OK, 0 rows affected (0.00 sec)
```
再次查看从库节点的状态：
```
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.161.114
                  Master_User: repl
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000005
          Read_Master_Log_Pos: 767
               Relay_Log_File: slave-relay-bin.000002
                Relay_Log_Pos: 320
        Relay_Master_Log_File: mysql-bin.000005
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB: 
          Replicate_Ignore_DB: 
           Replicate_Do_Table: 
       Replicate_Ignore_Table: 
      Replicate_Wild_Do_Table: 
  Replicate_Wild_Ignore_Table: 
                   Last_Errno: 0
                   Last_Error: 
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 767
              Relay_Log_Space: 527
              Until_Condition: None
               Until_Log_File: 
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File: 
           Master_SSL_CA_Path: 
              Master_SSL_Cert: 
            Master_SSL_Cipher: 
               Master_SSL_Key: 
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error: 
               Last_SQL_Errno: 0
               Last_SQL_Error: 
  Replicate_Ignore_Server_Ids: 
             Master_Server_Id: 1
                  Master_UUID: 10dfcf35-16c2-11ec-9238-5254003c7ee2
             Master_Info_File: /opt/mysql/data/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind: 
      Last_IO_Error_Timestamp: 
     Last_SQL_Error_Timestamp: 
               Master_SSL_Crl: 
           Master_SSL_Crlpath: 
           Retrieved_Gtid_Set: 
            Executed_Gtid_Set: 
                Auto_Position: 0
         Replicate_Rewrite_DB: 
                 Channel_Name: 
           Master_TLS_Version: 
1 row in set (0.00 sec)

ERROR: 
No query specified
```
可以看到这两个参数都为yes.
Slave_IO_Running: Yes
Slave_SQL_Running: Yes。

需要注意的是，并不是所有的库操作都要同步到从库，因此，可以在主库的配置上增加如下配置:  
```
# 不同步哪些数据库
binlog-ignore-db = mysql
binlog-ignore-db = test
binlog-ignore-db = information_schema

# 只同步哪些数据库，除此之外，其他不同步
binlog-do-db = gts
``` 
这样可以将需要同步的数据库进行指定。

# 4.主从不一致问题
不小心在从库上执行了写操作，这样导致从库的Slave_SQL_Running停止：
```
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.161.114
                  Master_User: repl
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000006
          Read_Master_Log_Pos: 509
               Relay_Log_File: slave-relay-bin.000005
                Relay_Log_Pos: 367
        Relay_Master_Log_File: mysql-bin.000006
             Slave_IO_Running: Yes
            Slave_SQL_Running: No
              Replicate_Do_DB: 
          Replicate_Ignore_DB: 
           Replicate_Do_Table: 
       Replicate_Ignore_Table: 
      Replicate_Wild_Do_Table: 
  Replicate_Wild_Ignore_Table: 
                   Last_Errno: 1008
                   Last_Error: Error 'Can't drop database 'gts'; database doesn't exist' on query. Default database: 'gts'. Query: 'drop database gts'
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 154
              Relay_Log_Space: 1095
              Until_Condition: None
               Until_Log_File: 
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File: 
           Master_SSL_CA_Path: 
              Master_SSL_Cert: 
            Master_SSL_Cipher: 
               Master_SSL_Key: 
        Seconds_Behind_Master: NULL
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error: 
               Last_SQL_Errno: 1008
               Last_SQL_Error: Error 'Can't drop database 'gts'; database doesn't exist' on query. Default database: 'gts'. Query: 'drop database gts'
  Replicate_Ignore_Server_Ids: 
             Master_Server_Id: 1
                  Master_UUID: 10dfcf35-16c2-11ec-9238-5254003c7ee2
             Master_Info_File: /opt/mysql/data/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: 
           Master_Retry_Count: 86400
                  Master_Bind: 
      Last_IO_Error_Timestamp: 
     Last_SQL_Error_Timestamp: 210917 20:18:56
               Master_SSL_Crl: 
           Master_SSL_Crlpath: 
           Retrieved_Gtid_Set: 
            Executed_Gtid_Set: 
                Auto_Position: 0
         Replicate_Rewrite_DB: 
                 Channel_Name: 
           Master_TLS_Version: 
1 row in set (0.00 sec)
```

解决办法,如果主从不同步，可以通过如下方法强行跳过：
```
mysql> stop slave;
Query OK, 0 rows affected (0.00 sec)

mysql> set global sql_slave_skip_counter=1;
Query OK, 0 rows affected (0.00 sec)

mysql> start slave;
Query OK, 0 rows affected (0.00 sec)
```
强行跳过之后，主从就能一致了。

现在在主库上创建一个gts数据库，从库上就能同步到了。
主库执行如下语句：
```
create database gts DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
```
从库下就能查询到了：
```
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| gts                |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.01 sec)
```

这说明，从库上要设置为只读：
设置：read_only = ON,但是此限制对拥有SUPER权限 的用户均无效。
阻止所有用户的方法,在从库上执行如下语句：
```
mysql> flush tables with read lock;
Query OK, 0 rows affected (0.00 sec)

```

至此，mysql的主从复制配置完成。
