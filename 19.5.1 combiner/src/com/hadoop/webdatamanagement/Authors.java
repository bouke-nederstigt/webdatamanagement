package com.hadoop.webdatamanagement;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by bouke on 14-6-14.
 * Mapreduce example for hadoop. Extracts some basic information from
 * a text file derived from the DBLP data set.
 */
public class Authors {

    /**
     * Mapper class -- takes a line from the input file and
     * extracts the string before the first tab (= author name)
     */
    public static class AuthorsMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text author = new Text();

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            /* Open a scanner object to parse the line */
            Scanner line = new Scanner(value.toString());
            line.useDelimiter(("\t"));
            author.set(line.next());
            context.write(author, one);
        }
    }

    /**
     * Combiner class. Used instead of CountReducer function from the book
     */
    public static class CountCombiner extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for(IntWritable val : values){
                count += val.get();
            }

            result.set(count);
            context.write(key, result);
        }
    }

    /**
     * Reducer class -- receives pairs (author name, <list of counts>)
     * and sums up the counts to get the number of publications per author
     */
    public static class CountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {


        public void reduce(Text key, IntWritable values, Context context) throws IOException, InterruptedException {

            context.write(key, values);
        }
    }
}
