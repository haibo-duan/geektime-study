鉴于之前安装不带boost版本的mysql，由于boost头文件的问题，导致在编译boost的过程中出现各种各样的问题。在官网发现居然现在有在源码中带boost头文件的版本，那么现在就验证一下这个版本的安装过程。

# 1 安环境准备
## 1.1 操作系统
操作系统信息：
```
[root@m161p115 mysql]# lsb_release -a
LSB Version:	:core-4.1-amd64:core-4.1-noarch:cxx-4.1-amd64:cxx-4.1-noarch:desktop-4.1-amd64:desktop-4.1-noarch:languages-4.1-amd64:languages-4.1-noarch:printing-4.1-amd64:printing-4.1-noarch
Distributor ID:	CentOS
Description:	CentOS Linux release 7.9.2009 (Core)
Release:	7.9.2009
Codename:	Core
```
由于磁盘分区的原因，预计讲系统安装到/opt/mysql目录。
## 1.2 源码文件
下载带boost版本的mysql源码文件：
```
wget https://cdn.mysql.com/archives/mysql-5.7/mysql-boost-5.7.34.tar.gz
```
也可以到mysql官网选择所需要的版本对应的源码：
https://downloads.mysql.com/archives/community/
本文所选择的版本为：
Generic Linux (Architecture Independent), Compressed TAR Archive
Includes Boost Headers 5.7.34 。



## 1.3 依赖包准备
所有程序先升级：
yum upgrade -y
安装依赖的包：
yum -y install  readline-devel zlib-devel  cmake autoconf automake apr  apr-devel  apr-util apr-util-devel bison  bzip2-devel cpp fontconfig-devel freetype-devel  gcc gcc-c++  compat-dapl  compat-db-headers  compat-db47   compat-gcc-44 compat-gcc-44-c++  compat-glibc  compat-glibc-headers compat-libcap1 compat-libf2c-34  compat-libgfortran-41 compat-libtiff3 compat-openldap ncurses-devel

## 1.4 用户准备
创建用户及权限：
```
groupadd mysql
useradd -g mysql mysql
```
另外需要修改limits.conf

vim /etc/security/limits.conf
增加如下内容：
```
mysql soft nproc 16384
mysql hard nproc 16384
mysql soft nofile 65535
mysql hard nofile 65535
```
# 2.安装过程
## 2.1 cmake配置
在cmake中需要配置的参数如下所示。
```
cmake . -DCMAKE_INSTALL_PREFIX=/opt/mysql/ -DINSTALL_DATADIR=/opt/mysql/data -DSYSCONFDIR=/etc -DDEFAULT_CHARSET=utf8 -DDEFAULT_COLLATION=utf8_general_ci -DMYSQL_TCP_PORT=3306 -DMYSQL_USER=mysql -DWITH_MYISAM_STORAGE_ENGINE=1 -DWITH_INNOBASE_STORAGE_ENGINE=1 -DWITH_ARCHIVE_STORAGE_ENGINE=1 -DWITH_BLACKHOLE_STORAGE_ENGINE=1  -DWITH_MEMORY_STORAGE_ENGINE=1 -DDOWNLOAD_BOOST=1 -DWITH_BOOST=/opt/mysql/boost   -DWITH_MEMORY_STORAGE_ENGINE=1 -DMYSQL_USER=mysql
```
-DCMAKE_INSTALL_PREFIX 表示安装的目录。
-DINSTALL_DATADIR 数据文件的目录。
-DSYSCONFDIR 配置文件目录
-DWITH_BOOST boost下载的目录

上述目录参数可以根据自身情况进行调整。
需要注意的是，如果boost所在目录不存在boost的源码文件，那么在cmake的过冲中会下载 boost_1_59_0.tar.gz 到配置的目录/opt/mysql/boost。
如果下载不成功，会出现如下错误：
```
-- Downloading boost_1_59_0.tar.gz to /opt/mysql/boost
-- Download failed, error: 7;"Couldn't connect to server"
CMake Error at cmake/boost.cmake:201 (MESSAGE):
  You can try downloading
  http://sourceforge.net/projects/boost/files/boost/1.59.0/boost_1_59_0.tar.gz
  manually using curl/wget or a similar tool
Call Stack (most recent call first):
  CMakeLists.txt:522 (INCLUDE)

-- Configuring incomplete, errors occurred!
```
这种情况下，我们只需要将boost_1_59_0.tar.gz 手动复制到指定的目录重新执行cmake即可。

boost下载网址：
https://www.boost.org/users/download/#repository

## 2.2 make
make执行如下：
```
make 2>&1 |tee /root/makelog.log && make install
```
这个过程比较长，根据服务器的性能可能编译时间的长短也不同，基本上1个小时左右。
## 2.3  数据目录及权限
建立数据文件目录：
mkdir /opt/mysql/data
修改目录权限：
chown -R mysql:mysql /opt/mysql

## 2.4 配置文件设置
全局配置文件：/etc/my.cnf,设置如下内容：
```
[client]
port = 3306
socket = /opt/mysql/data/mysql.sock
[mysqld]
port = 3306
socket = /opt/mysql/data/mysql.sock
basedir = /opt/mysql
datadir = /opt/mysql/data
#pid-file = /opt/mysql/mysql.pid
user = mysql
bind-address = 0.0.0.0
server-id = 1
init-connect = ‘SET NAMES utf8’
character-set-server = utf8
#skip-name-resolve
#skip-networking
back_log = 300
max_connections = 1000
max_connect_errors = 6000
open_files_limit = 65535
table_open_cache = 128
max_allowed_packet = 4M
binlog_cache_size = 1M
max_heap_table_size = 8M
tmp_table_size = 16M
read_buffer_size = 2M
read_rnd_buffer_size = 8M
sort_buffer_size = 8M
join_buffer_size = 8M
key_buffer_size = 4M
thread_cache_size = 8
query_cache_type = 1
query_cache_size = 8M
query_cache_limit = 2M
ft_min_word_len = 4
log_bin = mysql-bin
binlog_format = mixed
expire_logs_days = 30
log_error = /opt/mysql/log/mysql-error.log
slow_query_log = 1
long_query_time = 1
slow_query_log_file = /opt/mysql/log/mysql-slow.log
performance_schema = 0
explicit_defaults_for_timestamp
#lower_case_table_names = 1
skip-external-locking
default_storage_engine = InnoDB
#default-storage-engine = MyISAM
innodb_file_per_table = 1
innodb_open_files = 500
innodb_buffer_pool_size = 64M
innodb_write_io_threads = 4
innodb_read_io_threads = 4
innodb_thread_concurrency = 0
innodb_purge_threads = 1
innodb_flush_log_at_trx_commit = 2
innodb_log_buffer_size = 2M
innodb_log_file_size = 32M
innodb_log_files_in_group = 3
innodb_max_dirty_pages_pct = 90
innodb_lock_wait_timeout = 120
bulk_insert_buffer_size = 8M
myisam_sort_buffer_size = 8M
myisam_max_sort_file_size = 10G
myisam_repair_threads = 1
interactive_timeout = 28800
wait_timeout = 28800
[mysqldump]
quick
max_allowed_packet = 16M
[myisamchk]
key_buffer_size = 8M
sort_buffer_size = 8M
read_buffer = 4M
write_buffer = 4M
```

## 2.5 初始化数据库
数据库初始化：
cd /opt/mysql
执行如下命令
```
bin/mysqld --initialize --basedir=/opt/mysql --datadir=/opt/mysql/data --user=mysql
```
输出：
```
[root@m161p115 mysql]# bin/mysqld --initialize --basedir=/opt/mysql --datadir=/opt/mysql/data --user=mysql
2021-09-16T07:02:10.595294Z 0 [Warning] TIMESTAMP with implicit DEFAULT value is deprecated. Please use --explicit_defaults_for_timestamp server option (see documentation for more details).
2021-09-16T07:02:11.334506Z 0 [Warning] InnoDB: New log files created, LSN=45790
2021-09-16T07:02:11.436298Z 0 [Warning] InnoDB: Creating foreign key constraint system tables.
2021-09-16T07:02:11.495939Z 0 [Warning] No existing UUID has been found, so we assume that this is the first time that this server has been started. Generating a new UUID: 03cb46fa-16bc-11ec-9550-525400ea020a.
2021-09-16T07:02:11.497713Z 0 [Warning] Gtid table is not ready to be used. Table 'mysql.gtid_executed' cannot be opened.
2021-09-16T07:02:11.850389Z 0 [Warning] CA certificate ca.pem is self signed.
2021-09-16T07:02:12.011559Z 1 [Note] A temporary password is generated for root@localhost: 3WNMQz646/h2
```
这个过程会输出root用户的默认密码。
## 2.6 设置service服务
操作如下：
```
cd /opt/mysql
cp support-files/mysql.server /etc/init.d/mysqld
chmod +x /etc/init.d/mysqld
chkconfig --add mysqld
```
## 2.7 环境变量配置
增加mysql环境变量：
```
echo -e ‘\n\n export PATH=/opt/mysql/bin:$PATH\n’ >> /etc/profile && source /etc/profile
```
## 2.8 启动mysql
service启动mysql 
```
service mysqld start
```
出现如下错误：
```
Starting MySQL.2021-09-16T07:03:47.342039Z mysqld_safe error: log-error set to '/opt/mysql/log/mysql-error.log', however file don't exists. Create writable for user 'mysql'.
The server quit without updating PID file (/opt/mysql/data/[FAILED].pid).
```
这个文件需要手动创建并修改权限：
```
mkdir /opt/mysql/log
touch /opt/mysql/log/mysql-error.log
chown -R mysql:mysql /opt/mysql/log
```
这之后可以方便的通过service对mysql进行启动了：
```
[root@m161p115 mysql]# service mysqld start
Starting MySQL.                                            [  OK  ]
[root@m161p115 mysql]# service mysqld stop
Shutting down MySQL.                                       [  OK  ]
```

# 3 配置用户及密码
## 3.1 root密码修改
如果默认的root密码无法登陆，则需要修改root密码。
首先通过配置my.cnf ,再mysqld服务中增加配置：
```
skip-grant-tables
```
这样再次启动mysql,将不会验证用户权限。
```
[root@m161p115 etc]# mysql
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.34-log Source distribution

Copyright (c) 2000, 2021, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
4 rows in set (0.00 sec)

mysql> use mysql;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> 
```
select user,host,authentication_string,password_expired from user;

可以发现，除root用户外，还存在一个mysql.sys用户。此时root用户password_expired状态为Y。为已过期状态。密码字段变成了authentication_string 。
```
mysql> select user,host,authentication_string,password_expired from user;
+---------------+-----------+-------------------------------------------+------------------+
| user          | host      | authentication_string                     | password_expired |
+---------------+-----------+-------------------------------------------+------------------+
| root          | localhost | *674DEBABE67AD3F4FC501B805BE09C0CA2FCB514 | Y                |
| mysql.session | localhost | *THISISNOTAVALIDPASSWORDTHATCANBEUSEDHERE | N                |
| mysql.sys     | localhost | *THISISNOTAVALIDPASSWORDTHATCANBEUSEDHERE | N                |
+---------------+-----------+-------------------------------------------+------------------+
3 rows in set (0.00 sec)
```
将密码修改为mysql:
```
mysql> update user set authentication_string=password('mysql') where user='root';
Query OK, 1 row affected, 1 warning (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 1

mysql> flush privileges;
Query OK, 0 rows affected (0.01 sec)
```
这样将my.cnf中的skip-grant-tables注释掉，重启即可。
```
[root@m161p115 etc]# mysql -uroot -pmysql
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.34-log

Copyright (c) 2000, 2021, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> 
```
## 3.2 1820异常处理
如果出现1820异常：
```
mysql> create database gts DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ERROR 1820 (HY000): You must reset your password using ALTER USER statement before executing this statement.
mysql> 

```
解决办法：
```
mysql> SET PASSWORD = PASSWORD('mysql');
Query OK, 0 rows affected, 1 warning (0.00 sec)
mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)
```
之后重新登陆即可。

# 4.创建新数据库
现在创建一个gts的数据库，密码为mysql。
```
create database gts DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
grant all privileges on gts.* to gts@’%.%.%.%’ identified by ‘mysql’;
flush privileges;
```
执行过程：
```
mysql> create database gts DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
Query OK, 1 row affected (0.00 sec)
mysql> grant all privileges on gts.* to gts@'%.%.%.%' identified by 'mysql';
Query OK, 0 rows affected, 1 warning (0.01 sec)
mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)

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

mysql> 

```
数据库gts创建完毕。如下即可登陆：
```
[root@m161p115 etc]# mysql -ugts -pmysql -h127.0.0.1
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 5
Server version: 5.7.34-log

Copyright (c) 2000, 2021, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> 

```
