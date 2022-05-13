package com.dhb.mr;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

@Slf4j
public class MobileTrafficStatisticsMap extends Mapper<LongWritable, Text, Text, MapValueBean> {

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, MapValueBean>.Context context) throws IOException, InterruptedException {
        log.debug("key is :" + key);
        log.debug("value is :" + value);
        String[] strings = value.toString().trim().split("\t");
        if (strings.length >= 11) {
            MapValueBean mapValueBean = new MapValueBean();
            mapValueBean.setUp(Long.parseLong(strings[8]));
            mapValueBean.setDown(Long.parseLong(strings[9]));

            log.debug("====================================" + mapValueBean.toString());
            context.write(new Text(strings[1]), mapValueBean);
        }
    }
}
