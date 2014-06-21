package com.hadoop.movies;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by bouke on 15-6-14.
 * <p/>
 * Map and Reduce classes for movies.xml files
 */
public class Movies {

    public static class MoviesMapper extends Mapper<LongWritable, Text, Text, Text> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String xml = value.toString();
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

            ReadMovieXML readMovieXML = new ReadMovieXML();
            readMovieXML.readMovie(inputStream);

            context.write(new Text("1"), new Text(ReadMovieXML.director + "\t" + ReadMovieXML.title + "\t" + ReadMovieXML.year));

            Iterator it = readMovieXML.actors.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                String actorName = pairs.getKey().toString();
                String actorDetails = pairs.getValue().toString();
                context.write(new Text("2"), new Text(ReadMovieXML.title + "\t" + actorName + "\t" + actorDetails));
            }

        }
    }

    public static class MoviesReducer extends Reducer<LongWritable, Text, Text, Text> {

        private MultipleOutputs out;

        public void setup(Context context) {
            out = new MultipleOutputs(context);
        }

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text text : values) {

                Scanner line = new Scanner(text.toString());
                line.useDelimiter(("\t"));
                String newKey = line.next();
                String newValue = line.next() + "\t" + line.next();

                if (key.equals("1")) {
                    out.write("director", new Text(newKey), new Text(newValue));
                }
                if (key.equals("2")) {
                    out.write("title", new Text(newKey), new Text(newValue));
                }
            }
        }

        public void cleanup(Context context) throws IOException, InterruptedException {
            out.close();
        }
    }


}
