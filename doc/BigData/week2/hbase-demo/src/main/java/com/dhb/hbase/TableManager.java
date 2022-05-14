package com.dhb.hbase;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnPaginationFilter;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RandomRowFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SkipFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class TableManager {

    private static Configuration configuration;
    private static Connection conn = null;

    static {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "192.168.162.201,192.168.162.202,192.168.162.203");
        configuration.set("hbase.master", "192.168.162.203:60000");
        try {
            conn = ConnectionFactory.createConnection(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        createTable("test1");
    }

    public static void createTable(String tableName) throws Exception {
        System.out.println("start create table ......");
        TableName tName = TableName.valueOf(tableName);
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tName);

        Admin admin = conn.getAdmin();
        if (admin.tableExists(tName)) {
            System.out.println("table Exists!!!");
        } else {
            ColumnFamilyDescriptorBuilder nameColumn =
                    ColumnFamilyDescriptorBuilder.newBuilder("name".getBytes());
            ColumnFamilyDescriptorBuilder infoColumn = ColumnFamilyDescriptorBuilder.newBuilder("info".getBytes());
            ColumnFamilyDescriptorBuilder scoreColumn = ColumnFamilyDescriptorBuilder.newBuilder("score".getBytes());

            tableDescriptorBuilder.setColumnFamily(nameColumn.build());

            admin.createTable(tableDescriptorBuilder.build());
            System.out.println("create table ok.");
        }
        System.out.println("end create table ......");
    }

    /**
     * 创建表
     *
     * @param tableName 表名
     * @param familys   列族
     * @throws IOException
     */
    public void createTable(String tableName, String... familys) {
        try {
            Admin admin = conn.getAdmin();
            TableName tname = TableName.valueOf(tableName);
            if (admin.tableExists(tname)) {
                log.warn(tableName + "表已经存在,不能重复创建.");
            } else {
                TableDescriptorBuilder tdesc = TableDescriptorBuilder.newBuilder(tname);
                for (String family : familys) {
                    ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.of(family);
                    tdesc.setColumnFamily(cfd);
                }
                TableDescriptor desc = tdesc.build();
                admin.createTable(desc);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createTables(String[] tableNames, List<List<String>> familys) {
        try {
            Admin admin = conn.getAdmin();
            if (tableNames.length == familys.size()) {
                for (int i = 0; i < tableNames.length; i++) {
                    TableName tname = TableName.valueOf(tableNames[i]);
                    if (admin.tableExists(tname)) {
                        log.warn(tableNames[i] + "表已经存在,不能重复创建.");
                    } else {
                        TableDescriptorBuilder tdesc = TableDescriptorBuilder.newBuilder(tname);
                        for (String family : familys.get(i)) {
                            ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.of(family);
                            tdesc.setColumnFamily(cfd);
                        }
                        TableDescriptor desc = tdesc.build();
                        admin.createTable(desc);
                    }
                }
            } else {
                log.warn("每张表必须要有列族");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteTable(String tableName) {
        try {
            Admin admin = conn.getAdmin();
            TableName tName = TableName.valueOf(tableName);
            if (admin.tableExists(tName)) {
                admin.disableTable(tName);
                admin.deleteTable(tName);
                log.info("删除" + tableName + "表成功");
            } else {
                log.warn("需要删除的" + tableName + "表不存在");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteTables(String... tableNames) {
        try {
            Admin admin = conn.getAdmin();
            for (String tableName : tableNames) {
                TableName tName = TableName.valueOf(tableName);
                admin.disableTable(tName);
                admin.deleteTable(tName);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteFamily(String tableName, String family) {
        try {
            Admin admin = conn.getAdmin();
            admin.deleteColumnFamily(TableName.valueOf(tableName), Bytes.toBytes(family));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addFamily(String tableName, String family) {
        try {
            Admin admin = conn.getAdmin();
            ColumnFamilyDescriptor columnFamily = ColumnFamilyDescriptorBuilder.newBuilder(family.getBytes()).build();
            admin.addColumnFamily(TableName.valueOf(tableName), columnFamily);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addRow(String tableName, String rowKey, String family, String qualifier, String value) {
        try {
            Table table = (Table) conn.getTable(TableName.valueOf(tableName));
            //通过rowkey创建一个 put 对象
            Put put = new Put(Bytes.toBytes(rowKey));
            //在 put 对象中设置 列族、列、值
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
            //插入数据，可通过 put(List<Put>) 批量插入
            table.put(put);
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addrow(String tname, String family, Map<String, Object> params) {
        try {
            Table table = conn.getTable(TableName.valueOf(tname));
            Put put = new Put(params.get("row").toString().getBytes());
            for (Map.Entry<String, Object> m : params.entrySet()) {
                if (m.getKey().equals("row")) {
                    continue;
                }
                put.addColumn(family.getBytes(), m.getKey().getBytes(), m.getValue().toString().getBytes());
            }
            table.put(put);
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addrows(String tname, Map<String, Map<String, Object>> params) {
        try {
            Table table = conn.getTable(TableName.valueOf(tname));
            List<Put> listput = new ArrayList<>();
            for (Map.Entry<String, Map<String, Object>> map : params.entrySet()) {
                Put put = new Put(map.getKey().getBytes());
                String family = map.getValue().get("family").toString();
                for (Map.Entry<String, Object> m : map.getValue().entrySet()) {
                    if (m.getKey().equals("row")) {
                        continue;
                    }
                    put.addColumn(family.getBytes(), m.getKey().getBytes(), m.getValue().toString().getBytes());
                }
                listput.add(put);
            }
            table.put(listput);
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean deleteRow(String tname, String row, Map<String, Object> params) {
        TableName tableName = TableName.valueOf(tname);
        try {
            Table table = conn.getTable(tableName);
            Delete delete = new Delete(row.getBytes());
            if (params != null) {
                for (Map.Entry<String, Object> m : params.entrySet()) {
                    delete.addColumn(m.getKey().getBytes(), m.getValue().toString().getBytes());
                }
            }
            table.delete(delete);
            table.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void deleteRows(String tableName, String[] rows) {
        try {
            Table table = (Table) conn.getTable(TableName.valueOf(tableName));
            List<Delete> list = new ArrayList<Delete>();
            for (String row : rows) {
                Delete delete = new Delete(Bytes.toBytes(row));
                list.add(delete);
            }
            table.delete(list);
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteRows(String tname, Map<String, Object> params, String... rows) {
        try {
            Table table = conn.getTable(TableName.valueOf(tname));
            List<Delete> deletes = new ArrayList<Delete>();
            for (String row : rows) {
                Delete delete = new Delete(row.getBytes());
                if (params != null) {
                    for (Map.Entry<String, Object> m : params.entrySet()) {
                        delete.addColumn(m.getKey().getBytes(),
                                m.getValue().toString().getBytes());
                    }
                }
                deletes.add(delete);
            }
            table.delete(deletes);
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Map<String, Object> getRow(String tableName, String rowKey) {
        Map<String, Object> data = new HashMap<>();
        try {
            Table table = (Table) conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey)); //通过rowkey创建一个 get 对象
            Result result = table.get(get);
            if (!get.isCheckExistenceOnly()) {
                for (Cell cell : result.rawCells()) {
                    data.put("row", new String(CellUtil.cloneRow(cell)));
                    data.put("family", new String(CellUtil.cloneFamily(cell)));
                    data.put("qualifier", new String(CellUtil.cloneQualifier(cell)));
                    data.put("value", new String(CellUtil.cloneValue(cell)));
                }
            }
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

    public List<Map<String, Object>> getAllData(String tname) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        TableName tableName = TableName.valueOf(tname);
        try {
            Table table = conn.getTable(tableName);
            Set<byte[]> familyNames = table.getDescriptor().getColumnFamilyNames();
            for (byte[] familyName : familyNames) {
                ResultScanner rs = table.getScanner(familyName);
                Iterator<Result> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    Result r = iterator.next();
                    for (Cell cell : r.rawCells()) {
                        String family = Bytes.toString(cell.getFamilyArray(),
                                cell.getFamilyOffset(),
                                cell.getFamilyLength());
                        String qualifier = Bytes.toString(cell.getQualifierArray(),
                                cell.getQualifierOffset(),
                                cell.getQualifierLength());
                        String row = Bytes.toString(cell.getRowArray(),
                                cell.getRowOffset(),
                                cell.getRowLength());
                        String value = Bytes.toString(cell.getValueArray(),
                                cell.getValueOffset(),
                                cell.getValueLength());
                        Map map = new HashMap();
                        map.put("row", row);
                        map.put("family", family);
                        map.put("qualifier", qualifier);
                        map.put("value", value);
                        list.add(map);
                        log.info("row=" + row + ",family=" + family
                                + ",qualifier=" + qualifier + ",value=" + value);
                    }
                }
            }
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public void queryData(String tableName) {
        Table table = null;
        try {
            table = (Table) conn.getTable(TableName.valueOf(tableName));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Scan scan = new Scan();
        scan.setMaxResultSize(1000);
        //指定最多返回的Cell数目。用于防止一行中有过多的数据，导致OutofMemory错误。
//        scan.setBatch(1000);
        scan.withStartRow(Bytes.toBytes("row001"));
        scan.withStopRow(Bytes.toBytes("row010"));
        scan.addFamily(Bytes.toBytes("cf01"));
//        scan.addColumn(Bytes.toBytes("cf01"), Bytes.toBytes("name"));
        //该过滤器是随机选择一行的过滤器。参数 chance 是一个浮点值，介于0.1 和 1.0 之间。随机输出一半的行数据
        RandomRowFilter randomRowFilter = new RandomRowFilter(0.5f);
        //ColumnPrefixFilter ：用于列名 Qualifier 前缀过滤，即包含某个前缀的所有列名。
        ColumnPrefixFilter columnPrefixFilter = new ColumnPrefixFilter("bir".getBytes());

        byte[][] prefixes = new byte[][]{"author".getBytes(), "bookname".getBytes()};
        MultipleColumnPrefixFilter multipleColumnPrefixFilter = new MultipleColumnPrefixFilter(prefixes);

        //分页，PageFilter ：用于按行分页。
        PageFilter pageFilter = new PageFilter(3);//指定3行分一页

        //这是一种附加过滤器，其与 ValueFilter 结合使用，如果发现一行中的某一列不符合条件，那么整行就会被过滤掉。
        Filter skipFilter = new SkipFilter(columnPrefixFilter);

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes("李"));

        Filter columnPaginationFilter = new ColumnPaginationFilter(5, 15);

        Filter valueFilter = new ValueFilter(CompareOperator.EQUAL, new SubstringComparator("test"));
        Filter rowFilter1 = new RowFilter(CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes("row-3")));

        //时间戳过滤器
        List<Long> timestamp = new ArrayList<>();
        timestamp.add(1571438854697L);
        timestamp.add(1571438854543L);
        TimestampsFilter timestampsFilter = new TimestampsFilter(timestamp);

        List<Filter> filters = new ArrayList<Filter>();
        filters.add(pageFilter);
        filters.add(valueFilter);

        FilterList filter = new FilterList(FilterList.Operator.MUST_PASS_ALL, filters);
        scan.setFilter(filter);

        ResultScanner rs = null;
        try {
            rs = table.getScanner(scan);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (rs != null) {
            for (Result r : rs) {
                for (Cell cell : r.rawCells()) {
                    System.out.println(String.format("row:%s, family:%s, qualifier:%s, value:%s, timestamp:%s.",
                            Bytes.toString(cell.getRowArray(),
                                    cell.getRowOffset(),
                                    cell.getRowLength()),
                            Bytes.toString(cell.getFamilyArray(),
                                    cell.getFamilyOffset(),
                                    cell.getFamilyLength()),
                            Bytes.toString(cell.getQualifierArray(),
                                    cell.getQualifierOffset(),
                                    cell.getQualifierLength()),
                            Bytes.toString(cell.getValueArray(),
                                    cell.getValueOffset(),
                                    cell.getValueLength()),
                            cell.getTimestamp()));
                }
            }
            rs.close();
        }
    }

    public List<String> getQualifierValue(String tableName, String family, String qualifier) {
        List<String> list = new ArrayList<String>();
        TableName tName = TableName.valueOf(tableName);
        try {
            Table table = conn.getTable(tName);
            ResultScanner rs = table.getScanner(family.getBytes(), qualifier.getBytes());
            Iterator<Result> iterator = rs.iterator();
            while (iterator.hasNext()) {
                Result r = iterator.next();
                for (Cell cell : r.rawCells()) {
                    String value = Bytes.toString(cell.getValueArray(),
                            cell.getValueOffset(),
                            cell.getValueLength());
                    list.add(value);
                }
            }
            rs.close();
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }


}
