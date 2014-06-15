package com.hadoop.movies;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.jar.Attributes;

/**
 * Created by bouke on 15-6-14.
 *
 * Map and Reduce classes for movies.xml files
 */
public class Movies {

    class TitleActorMapper extends Mapper<LongWritable, Text, Text, Text> {

        public void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        }
    }
}
