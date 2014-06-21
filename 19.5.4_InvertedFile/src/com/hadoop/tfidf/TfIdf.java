package com.hadoop.tfidf;

import java.lang.*;

/**
 * Created by Abhi on 6/20/14.
 */
public class TfIdf {
    public double tfCalculator(int wordCount, int totalWords) {
        return (double)((double)wordCount / (double)totalWords);
    }

    public double idfCalculator(int wordCount, int totalWords) {
        return (Math.log(totalWords / wordCount));
    }

    public double tfIdfCalculator(double tf, double idf) {
        return tf * idf;
    }
}
