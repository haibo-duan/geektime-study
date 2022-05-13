package com.dhb.mr;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.logging.log4j.util.Strings;


@Slf4j
public class MobileTrafficStatistics {

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("fs.hdfs.impl",org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        configuration.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName());

        Job job = Job.getInstance();
        job.setJarByClass(MobileTrafficStatistics.class);

        job.setMapperClass(MobileTrafficStatisticsMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(MapValueBean.class);

        job.setReducerClass(MobileTrafficStatisticsReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(1);

        String inputPath = args[0];
        String outputPath = args[1];

//        String inputPath = "hdfs://192.168.162.203:9000/test.dat";
//        String outputPath = "hdfs://192.168.162.203:9000/out";

        if(Strings.isNotBlank(inputPath) && Strings.isNotBlank(outputPath)) {
            FileSystem fs = FileSystem.get(configuration);
            FileInputFormat.addInputPath(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            if (fs.exists(new Path(outputPath))) {
                fs.delete(new Path(outputPath), true);
            }
            boolean b = job.waitForCompletion(true);
            if (b) {
                log.info("success~~~");
            } else {
                log.warn("fail~~~~");
            }
        }else {
            log.warn("inputPath or outputPath is empty!");
        }
    }
}
