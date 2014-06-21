package com.hadoop.movies;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;


/**
 * Created by Abhi on 19-6-14.
 */
public class ReadMovieXML extends DefaultHandler {
    public String summary;

    public String readMovieSummary(InputStream movie) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            DefaultHandler defaultHandler = new DefaultHandler() {
                boolean isSummary = false;
                String movieSummary = "";

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("summary")) {
                        isSummary = true;
                    }
                }

                public void characters(char ch[], int start, int length) throws SAXException {
                    if (isSummary == true) {
                        movieSummary += new String(ch, start, length);
                    }
                }

                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (qName.equalsIgnoreCase("summary")) {
                        isSummary = false;
                        summary = movieSummary;
                    }
                }
            };

            saxParser.parse(movie, defaultHandler);
            return summary;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
