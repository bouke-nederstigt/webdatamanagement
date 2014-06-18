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
public class TriangleCounterInHadoop {

    private static int noBuckets = 2;

    public static class EdgeMapper extends Mapper<LongWritable, Text, LongWritable, EdgeWritable> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            /* Open a scanner object to parse the line */
            Scanner line = new Scanner(value.toString());
            line.useDelimiter((" "));
            long v1 = line.nextLong(), v2;
            while (line.hasNextLong()) {
                v2 = line.nextLong();
                if (v1 < v2) {
                    long mod1 = v1 % noBuckets;
                    long mod2 = v2 % noBuckets;
                    long id;
                    for (int i = 0; i < noBuckets; i++) {
                        id = mod1 * noBuckets ^ 2 + mod2 * noBuckets + i;
                        context.write(new LongWritable(id), new EdgeWritable(v1, v2, EdgeWritable.EDGE_TYPE.XY));
                        id = i * noBuckets ^ 2 + mod1 * noBuckets + mod2;
                        context.write(new LongWritable(id), new EdgeWritable(v1, v2, EdgeWritable.EDGE_TYPE.YZ));
                        id = mod1 * noBuckets ^ 2 + i * noBuckets + mod2;
                        context.write(new LongWritable(id), new EdgeWritable(v1, v2, EdgeWritable.EDGE_TYPE.XZ));
                    }

                }
            }

        }
    }

    public static class EdgeReducer extends Reducer<LongWritable, EdgeWritable, LongWritable, LongWritable> {

        public void reduce(LongWritable key, Iterable<EdgeWritable> values, Context context) throws IOException, InterruptedException {
            HashMap<EdgeWritable.EDGE_TYPE, TreeSet<EdgeWritable>> rels = new HashMap<EdgeWritable.EDGE_TYPE, TreeSet<EdgeWritable>>();

            for (EdgeWritable.EDGE_TYPE r : EdgeWritable.EDGE_TYPE.values()){
                rels.put(r, new TreeSet<EdgeWritable>());
            }

            for (EdgeWritable e : values) {
                TreeSet<EdgeWritable> edges = rels.get(e.edgeType);
                edges.add(new EdgeWritable(e.sourceVertex, e.targetVertex));
            }

            long count = 0;
            for (EdgeWritable e1: rels.get(EdgeWritable.EDGE_TYPE.XY)) {
                for(EdgeWritable e2: rels.get(EdgeWritable.EDGE_TYPE.YZ)){
                    if(e1.targetVertex == e2.sourceVertex)
                        if(rels.get(EdgeWritable.EDGE_TYPE.XZ).contains(new EdgeWritable(e1.sourceVertex, e2.targetVertex)))
                            count++;
                }
            }
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
