package com.hadoop.movies;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.hadoop.tfidf.*;

/**
 * Created by Abhi on 19-6-14.
 *
 * Map and Reduce classes for movies.xml files
 */
public class Movies {
    /**
     * Mapper class - Reads the entire XML document and parses out the required text from the <summary>tags.
     */
    public final static int WORD_COUNT_POS = 0;
    public final static int TOTAL_NUM_OF_WORDS_POS = 1;

    public static class MoviesMapper extends Mapper<LongWritable, Text, Text, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String xml = value.toString();
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
            Text data = new Text();
            String summary = new String();
            Text val = new Text("");

            ReadMovieXML readMovieXML = new ReadMovieXML();
            summary = readMovieXML.readMovieSummary(inputStream);

            if (summary != null) {
                data.set(summary);
                context.write(data, val);
            }
        }
    }

    /**
     * Combiner class - Tokenizes all the words in the input data stream and then computes the word count for each.
     * This information along with the total word count is passed on to the reducer.
     */
    public static class MoviesCombiner extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text data, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String[] tokenizedTerms = data.toString().replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
            int totalWords = tokenizedTerms.length;
            Map tokens = new HashMap();

            // Create token map with count values
            for (int i = 0, j = 0; i < totalWords; i++) {
                if (!tokens.containsKey(tokenizedTerms[i].toLowerCase())) {
                    tokens.put(tokenizedTerms[i].toLowerCase(), 1);
                } else {
                    tokens.put(tokenizedTerms[i].toLowerCase(), Integer.parseInt(tokens.get(tokenizedTerms[i].toLowerCase()).toString()) + 1);
                }
            }

            Iterator it = tokens.entrySet().iterator();
            int wordCount = 0;
            Text keyVal = new Text();
            Text outputVal = new Text();

            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                wordCount = Integer.parseInt(pairs.getValue().toString());

                // Build output context string: key, wordCount, totalWords for every unique word
                keyVal.set(pairs.getKey().toString());
                outputVal.set(Integer.toString(wordCount) + "\t" + Integer.toString(totalWords));
                context.write(keyVal, outputVal);
                it.remove();
            }
        }
    }

    /**
     * Reducer class - Computes tf, idf and tf-idf values for each word in the document.
     */
    public static class MoviesReducer extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            TfIdf tfIdfCalc = new TfIdf();
            String output = "";
            Text val = new Text();
            int wordCount = 0;
            int totalWords = 0;

            for (Text v: values) {
                // Parse input string for word count and total number of words
                String[] tfAndIdf = v.toString().split("\t");
                wordCount = Integer.parseInt(tfAndIdf[WORD_COUNT_POS].toString());
                totalWords = Integer.parseInt(tfAndIdf[TOTAL_NUM_OF_WORDS_POS].toString());

                // Compute tf
                double tf = tfIdfCalc.tfCalculator(wordCount, totalWords);

                // Compute idf
                double idf = tfIdfCalc.idfCalculator(wordCount, totalWords);

                // Compute tf-idf
                double tfidf = tfIdfCalc.tfIdfCalculator(tf, idf);

                // Output context
                output = "count:" + Integer.toString(wordCount) + "\ttf:" + Double.toString(tf) + "\tidf:" + Double.toString(idf) + "\ttf-idf:" + Double.toString(tfidf);
                val.set(output);
                context.write(key, val);
            }
        }
    }
}
