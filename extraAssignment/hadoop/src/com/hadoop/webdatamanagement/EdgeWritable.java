package com.hadoop.webdatamanagement;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by ane on 6/18/14.
 */
public class EdgeWritable implements WritableComparable {
    public static enum EDGE_TYPE {NONE, XY, YZ, XZ};
    public long sourceVertex;
    public long targetVertex;
    public EDGE_TYPE edgeType;

    public EdgeWritable() {

    }

    private void setEnds(long v1, long v2) {
        if (v1 > v2) {
            long tmp = v1;
            v1 = v2;
            v2 = tmp;
        }
        this.sourceVertex = v1;
        this.targetVertex = v2;
    }

    public EdgeWritable(long v1, long v2) {
        setEnds(v1, v2);
        this.edgeType = EDGE_TYPE.NONE;
    }

    public EdgeWritable(long v1, long v2, EDGE_TYPE r) {
        setEnds(v1, v2);
        this.edgeType = r;
    }

    public void write(DataOutput out) throws IOException {
        out.writeLong(sourceVertex);
        out.writeLong(targetVertex);
        out.writeBytes(edgeType.name());
    }

    public void readFields(DataInput in) throws IOException {
        sourceVertex = in.readLong();
        targetVertex = in.readLong();
        edgeType = EDGE_TYPE.valueOf(in.readLine());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EdgeWritable))
            return false;
        EdgeWritable w = (EdgeWritable) o;
        return (sourceVertex == w.sourceVertex) &&
                (targetVertex == w.targetVertex) &&
                (edgeType == w.edgeType);
    }

    @Override
    public int compareTo(Object o) {
        EdgeWritable w = (EdgeWritable) o;
        if (sourceVertex != w.sourceVertex)
            return new Long(sourceVertex).compareTo(w.sourceVertex);
        if (targetVertex != w.targetVertex)
            return new Long(targetVertex).compareTo(w.targetVertex);
        return edgeType.compareTo(w.edgeType);
    }

    @Override
    public String toString() {
        return "(" + sourceVertex + "," + targetVertex + "){" + edgeType + "}";
    }
}

