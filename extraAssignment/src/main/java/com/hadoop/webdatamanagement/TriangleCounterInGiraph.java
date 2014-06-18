package com.hadoop.webdatamanagement;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.log4j.Logger;

import java.io.IOException;

public class TriangleCounterInGiraph extends BasicComputation<
        LongWritable, LongWritable, NullWritable, LongWritable> {

    private static final Logger LOG =
            Logger.getLogger(TriangleCounterInGiraph.class);

    @Override
    public void compute(
            Vertex<LongWritable, LongWritable, NullWritable> vertex,
            Iterable<LongWritable> messages) throws IOException {

        if (getSuperstep() == 0) {
            for (Edge<LongWritable, NullWritable> edge : vertex.getEdges()) {
                if (edge.getTargetVertexId().compareTo(vertex.getId()) > 0) {
                    sendMessage(edge.getTargetVertexId(), vertex.getId());
                }
            }
        }

        if (getSuperstep() == 1) {
            for (LongWritable message: messages) {
                for (Edge<LongWritable, NullWritable> edge : vertex.getEdges()) {
                    if (edge.getTargetVertexId().compareTo(vertex.getId()) > 0) {
                        sendMessage(edge.getTargetVertexId(), message);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Vertex " + vertex.getId() +
                                    " sent " + message+ " to " + edge.getTargetVertexId());
                        }
                    }
                }
            }
        }

        if (getSuperstep() == 2) {
            for (LongWritable message: messages) {
                for (Edge<LongWritable, NullWritable> edge : vertex.getEdges()) {
                        sendMessage(edge.getTargetVertexId(), message);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Vertex " + vertex.getId() +
                                " sent " + message+ " to " + edge.getTargetVertexId());
                    }
                }
            }
        }

        if (getSuperstep() == 3) {
            Long count = 0L;
            for (LongWritable message: messages) {
                if(message.compareTo(vertex.getId()) == 0){
                    count ++;
                }
            }
            vertex.setValue(new LongWritable(count));
        }

        vertex.voteToHalt();
    }
}
