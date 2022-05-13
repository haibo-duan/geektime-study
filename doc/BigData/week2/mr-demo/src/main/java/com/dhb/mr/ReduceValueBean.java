package com.dhb.mr;

import lombok.Data;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Data
public class ReduceValueBean implements Writable {

    long upSum;

    long downSum;

    long total;

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(upSum);
        dataOutput.writeLong(downSum);
        dataOutput.writeLong(total);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        upSum = dataInput.readLong();
        downSum = dataInput.readLong();
        total = dataInput.readLong();
    }
}
