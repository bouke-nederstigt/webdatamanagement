package com.hadoop.movies;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.Attributes;

/**
 * Created by bouke on 15-6-14.
 *
 * Map and Reduce classes for movies.xml files
 */
public class Movies {

    public static class TitleActorMapper extends Mapper<LongWritable, Text, Text, Text> {

        private final static IntWritable one = new IntWritable(1);
        private Text line = new Text();

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String xml = value.toString();
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

            ReadMovieXML readMovieXML = new ReadMovieXML();
            readMovieXML.readMovie(inputStream);

            Iterator it = readMovieXML.actors.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pairs = (Map.Entry)it.next();
                String actorName = pairs.getKey().toString();
                String actorDetails = pairs.getValue().toString();
                context.write(new Text(ReadMovieXML.title), new Text(actorName +  "\t" + actorDetails));
            }

        }
    }

}
