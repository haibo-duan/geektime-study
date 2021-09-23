 
create database gts01 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
grant all privileges on gts01.* to gts@’%’;
flush privileges;
create database gts02 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
grant all privileges on gts02.* to gts@’%’;
flush privileges;

