package com.hadoop.webdatamanagement;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.*;

/**
 * Created by ane on 6/18/14.
 */

/**
 * Main class for counting triangles in Hadoop via Multi-way Join
 */
public class TriangleCounterInHadoop {

    //the default number of buckets to which the vertices will be hashed (2^3 reducers will be needed)
    private static int noBuckets = 2;

    /**
     * Mapper class for sending edges to reducers
     */
    public static class EdgeMapper extends Mapper<LongWritable, Text, LongWritable, EdgeWritable> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //parse input line
            Scanner line = new Scanner(value.toString());
            line.useDelimiter((" "));
            //the first long is the current vertex
            long v1 = line.nextLong(), v2;
            //the rest of the longs are the current vertex's neighbors
            while (line.hasNextLong()) {
                v2 = line.nextLong();
                //only send edge if vertices are in correct order
                if (v1 < v2) {
                    //compute hashes of vertices
                    long mod1 = v1 % noBuckets;
                    long mod2 = v2 % noBuckets;
                    long id;
                    //for each bucket value
                    for (int i = 0; i < noBuckets; i++) {
                        //send edge of type XY to reducer (h(v1), h(v2), i)
                        id = mod1 * noBuckets ^ 2 + mod2 * noBuckets + i;
                        context.write(new LongWritable(id), new EdgeWritable(v1, v2, EdgeWritable.EDGE_TYPE.XY));
                        //send edge of type YZ to reducer (i, h(v1), h(v2))
                        id = i * noBuckets ^ 2 + mod1 * noBuckets + mod2;
                        context.write(new LongWritable(id), new EdgeWritable(v1, v2, EdgeWritable.EDGE_TYPE.YZ));
                        //send edge of type XZ to reducer (h(v1), i, h(v2))
                        id = mod1 * noBuckets ^ 2 + i * noBuckets + mod2;
                        context.write(new LongWritable(id), new EdgeWritable(v1, v2, EdgeWritable.EDGE_TYPE.XZ));
                    }
                }
            }
        }
    }

    /**
     * Reducer class for counting triangles at each reducer
     */
    public static class EdgeReducer extends Reducer<LongWritable, EdgeWritable, LongWritable, LongWritable> {

        public void reduce(LongWritable key, Iterable<EdgeWritable> values, Context context) throws IOException, InterruptedException {
            /**
             *  Store edges in a map, by relation type
             */
            HashMap<EdgeWritable.EDGE_TYPE, TreeSet<EdgeWritable>> rels = new HashMap<EdgeWritable.EDGE_TYPE, TreeSet<EdgeWritable>>();
            for (EdgeWritable.EDGE_TYPE r : EdgeWritable.EDGE_TYPE.values()){
                rels.put(r, new TreeSet<EdgeWritable>());
            }
            for (EdgeWritable e : values) {
                TreeSet<EdgeWritable> edges = rels.get(e.edgeType);
                edges.add(new EdgeWritable(e.sourceVertex, e.targetVertex));
            }

            /**
             * Count triangles
             */
            long count = 0;
            for (EdgeWritable e1: rels.get(EdgeWritable.EDGE_TYPE.XY)) {
                for(EdgeWritable e2: rels.get(EdgeWritable.EDGE_TYPE.YZ)){
                    if(e1.targetVertex == e2.sourceVertex)
                        if(rels.get(EdgeWritable.EDGE_TYPE.XZ).contains(new EdgeWritable(e1.sourceVertex, e2.targetVertex)))
                            count++;
                }
            }

            /**
             * Store result
             */
            context.write(key, new LongWritable(count));
        }
    }

    public static void main(String[] args) throws Exception {
        /**
         * Load hadoop configuration
         */
        Configuration conf = new Configuration();

        /* More than 2 arguments expected */
        if (args.length < 2) {
            System.err.println("Usage: TriangleCounterInHadoop <in> <out> [<number_of_buckets>]");
            System.exit(2);
        }

        if (args.length > 2) {
            try {
                noBuckets = Integer.parseInt(args[2]);
            } catch (Exception e) {
                System.err.println("WARNING: Could not set number of buckets to " + args[2]);
            }

        }

        //define and submit job
        Job job = new Job(conf, "Triangle count");
        job.setJarByClass(TriangleCounterInHadoop.class);

        //define mapper, combiner and reducer
        job.setMapperClass(TriangleCounterInHadoop.EdgeMapper.class);
        job.setReducerClass(TriangleCounterInHadoop.EdgeReducer.class);

        //define output type
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(EdgeWritable.class);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(LongWritable.class);

        //set input and output
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
