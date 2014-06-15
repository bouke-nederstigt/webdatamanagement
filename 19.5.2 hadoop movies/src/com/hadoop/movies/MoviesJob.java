package com.hadoop.movies;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;

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
    }
}


