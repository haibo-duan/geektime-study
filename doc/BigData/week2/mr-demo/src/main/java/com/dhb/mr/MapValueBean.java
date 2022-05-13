package com.dhb.mr;

import lombok.Data;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Data
public class MapValueBean implements Writable {

    private long up;

    private long down;

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(up);
        dataOutput.writeLong(down);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        up = dataInput.readLong();
        down = dataInput.readLong();
    }

}
