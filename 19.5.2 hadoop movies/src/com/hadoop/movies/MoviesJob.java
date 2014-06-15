package com.hadoop.movies;

import com.hadoop.combiner.Authors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

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

        //define mappers
        job.setMapperClass(Movies.TitleActorMapper.class);

        //set input type
        job.setInputFormatClass(XMLInputFormat.class);

        //define output type
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);


        //set input and output
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}


