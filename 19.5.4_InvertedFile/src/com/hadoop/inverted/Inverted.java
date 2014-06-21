package com.hadoop.inverted;

import com.hadoop.movies.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Created by Abhi on 19-6-14.
 *
 * MapReduce job for movies
 */
public class Inverted {

    public static void main(String[] args) throws Exception {
        /* At least two arguments expected */
        if(args.length != 2){
            System.err.println("Usage: Inverted <in> <out>");
            System.exit(2);
        }
        /**
         * Load hadoop configuration (Only taking XML files that have a summary tag present)
         */
        Configuration conf = new Configuration();
        conf.set("xmlinput.start", "<movies>");
        conf.set("xmlinput.end", "</movies>");

        //define and submit job
        Job job = new Job(conf, "Movies");
        job.setJarByClass(Inverted.class);

        //define mappers, combiners and reducers
        job.setMapperClass(Movies.MoviesMapper.class);
        job.setCombinerClass(Movies.MoviesCombiner.class);
        job.setReducerClass(Movies.MoviesReducer.class);

        //set input type
        job.setInputFormatClass(XMLInputFormat.class);

        //define output type
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //set concatenated paths
        String paths = new String();
        for (int i = 0; i < args.length-1; i++) {
            if (i > 0) {
                paths += ",";
            }
            paths += args[i];
        }

        //set input and output
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPaths(job, paths);
        FileOutputFormat.setOutputPath(job, new Path(args[args.length-1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
