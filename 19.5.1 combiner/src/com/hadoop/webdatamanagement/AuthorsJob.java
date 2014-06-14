package com.hadoop.webdatamanagement;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.fs.Path;

import com.hadoop.webdatamanagement.Authors;

/**
 * Created by bouke on 14-6-14.
 *
 * Simpe MapReduce job: reads file containing authors and publications,
 * and produce each author with her publication count
 */


public class AuthorsJob {

    public static void main(String[] args) throws Exception {
        /**
         * Load hadoop configuration
         */
        Configuration conf = new Configuration();

        /* Two arguments expected */
        if(args.length != 2){
            System.err.println("Usage: AuthorsJob <in> <out>");
            System.exit(2);
        }

        //define and submit job
        Job job = new Job(conf, "Authors count");

        //define mapper, combiner and reducer
        job.setMapperClass(Authors.AuthorsMapper.class);
        job.setCombinerClass(Authors.CountCombiner.class);
        job.setReducerClass(Authors.CountReducer.class);

        //define output type
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //set input and output
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
