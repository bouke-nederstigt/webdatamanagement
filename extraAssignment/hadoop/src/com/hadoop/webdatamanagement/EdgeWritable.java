package com.hadoop.webdatamanagement;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by ane on 6/18/14.
 */

/**
 * A writable IO type for edges
 */
public class EdgeWritable implements WritableComparable {
    //the relation types of the 3-way join (and a null relation)
    public static enum EDGE_TYPE {NONE, XY, YZ, XZ};
    //source node of edge (will be the smaller of the two)
    public long sourceVertex;
    //target node of the edge (will be the greater of the two)
    public long targetVertex;
    //relation type
    public EDGE_TYPE edgeType;

    public EdgeWritable() {

    }

    /**
     * Sets ends of the edge in appropriate order (source < target)
     * @param v1 first vertex
     * @param v2 second vertex
     */
    private void setEnds(long v1, long v2) {
        if (v1 > v2) {
            long tmp = v1;
            v1 = v2;
            v2 = tmp;
        }
        this.sourceVertex = v1;
        this.targetVertex = v2;
    }

    /**
     * Relation-agnostic constructor
     * @param v1 first vertex
     * @param v2 second vertex
     */
    public EdgeWritable(long v1, long v2) {
        setEnds(v1, v2);
        this.edgeType = EDGE_TYPE.NONE;
    }

    /**
     * Relation-specific constructor
     * @param v1 first vertex
     * @param v2 second vertex
     * @param r relation type
     */
    public EdgeWritable(long v1, long v2, EDGE_TYPE r) {
        setEnds(v1, v2);
        this.edgeType = r;
    }

    /**
     * Writes an edge to data
     * @param out output data
     * @throws IOException
     */
    public void write(DataOutput out) throws IOException {
        out.writeLong(sourceVertex);
        out.writeLong(targetVertex);
        out.writeBytes(edgeType.name());
    }

    /**
     * Reads an edge from data
     * @param in input data
     * @throws IOException
     */
    public void readFields(DataInput in) throws IOException {
        sourceVertex = in.readLong();
        targetVertex = in.readLong();
        edgeType = EDGE_TYPE.valueOf(in.readLine());
    }

    /**
     * An object is equal if both vertices and the relation types are equal
     * @param o object to be compared with
     * @return true or false
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EdgeWritable))
            return false;
        EdgeWritable w = (EdgeWritable) o;
        return (sourceVertex == w.sourceVertex) &&
                (targetVertex == w.targetVertex) &&
                (edgeType == w.edgeType);
    }

    /**
     * An object is compared, in order, by source, target and edgetype
     * @param o object to be compared with
     * @return true or false
     */
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

