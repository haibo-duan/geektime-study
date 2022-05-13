package com.dhb.mr;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

@Slf4j
public class MobileTrafficStatisticsReduce extends Reducer<Text, MapValueBean, Text, Text> {


    @Override
    protected void reduce(Text key, Iterable<MapValueBean> values, Reducer<Text, MapValueBean, Text, Text>.Context context) throws IOException, InterruptedException {
        long upSum = 0;
        long downSum = 0;
        long total = 0;
        for (MapValueBean mapValueBean : values) {
            log.info("mapValueBean is :"+mapValueBean.toString());
            upSum += mapValueBean.getUp();
            downSum += mapValueBean.getDown();
            total += (mapValueBean.getUp() + mapValueBean.getDown());
        }
        StringBuffer sb = new StringBuffer();
        sb.append(upSum);
        sb.append("\t");
        sb.append(downSum);
        sb.append("\t");
        sb.append(total);
        log.info("key out is :" + key);
        log.info("value out is :" + sb.toString());
        context.write(new Text(key.toString()), new Text(sb.toString()));
    }
}
