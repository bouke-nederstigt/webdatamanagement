package com.hadoop.movies;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.File;

/**
 * Created by bouke on 15-6-14.
 *
 * MapReduce job for movies
 */
public class MoviesJob {

    public static void main(String[] args) throws Exception {
        /**
         * Load hadoop configuration
         */
        Configuration conf = new Configuration();
        conf.set("xmlinput.start", "<movie>");
        conf.set("xmlinput.end", "</movie>");

        /* Two arguments expected */
        if(args.length != 2){
            System.err.println("Usage: MoviesJob <in> <out>");
            System.exit(2);
        }

        //define and submit job
        Job job = new Job(conf, "Movies");
        job.setJarByClass(MoviesJob.class);

        //define mappers and reducers
        job.setMapperClass(Movies.MoviesMapper.class);
        job.setReducerClass(Movies.MoviesReducer.class);

        //set input type
        job.setInputFormatClass(XMLInputFormat.class);

        //define output type
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //define multiple outputs
        MultipleOutputs.addNamedOutput(job, "director", TextOutputFormat.class, Text.class, Text.class);
        MultipleOutputs.addNamedOutput(job, "title", TextOutputFormat.class, Text.class, Text.class);

        //set input and output
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}


