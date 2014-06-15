package com.hadoop.movies;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by bouke on 15-6-14.
 */
public class ReadMovieXML extends DefaultHandler {

    private static String directorTitle;
    private static List<String> actorTitle = new ArrayList<String>();

    public void readMovie(InputStream movie){
        try{
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            DefaultHandler defaultHandler = new DefaultHandler(){
                boolean title = false;
                boolean year = false;
                boolean director = false;
                boolean actor = false;

                boolean first_name = false;
                boolean last_name = false;
                boolean role = false;
                boolean birth_date = false;

                String dirFirst;
                String dirLast;

                String tempActorFirst;
                String tempActorLast;
                String tempActorBirth;
                String tempActorRole;

                String movieTitle;
                String movieYear;

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if(qName.equalsIgnoreCase("title")){
                        title = true;
                    }
                    if(qName.equalsIgnoreCase("director")){
                        director = true;
                    }
                    if(qName.equalsIgnoreCase("actor")){
                        actor = true;
                    }
                    if(qName.equalsIgnoreCase("first_name")){
                        first_name = true;
                    }
                    if(qName.equalsIgnoreCase("last_name")){
                        last_name = true;
                    }
                    if(qName.equalsIgnoreCase("role")){
                        role = true;
                    }
                    if(qName.equalsIgnoreCase("birth_date")){
                        birth_date = true;
                    }
                }

                public void characters(char ch[], int start, int length) throws SAXException{
                    if(title == true){
                        movieTitle = new String(ch, start, length);
                    }

                    if(director == true){
                        if(last_name == true){
                            dirLast = new String(ch, start, length);
                        }

                        if(first_name == true){
                            dirFirst = new String(ch, start, length);
                        }
                    }

                    if(actor == true){
                        if(last_name == true){
                            tempActorLast = new String(ch, start, length);
                        }

                        if(first_name == true){
                            tempActorFirst = new String(ch, start, length);
                        }

                        if(birth_date == true){
                            tempActorBirth = new String(ch, start, length);
                        }

                        if(role == true){
                            tempActorRole = new String(ch, start, length);
                        }
                    }
                }

                public void endElement(String uri, String localName, String qName) throws SAXException{
                    if(qName.equalsIgnoreCase("title")){
                        title = false;
                    }
                    if(qName.equalsIgnoreCase("director")){
                        ReadMovieXML.directorTitle = movieTitle + "\t" + dirFirst + " " + dirLast + "\t" + movieYear;
                        director = false;
                    }
                    if(qName.equalsIgnoreCase("actor")){
                        ReadMovieXML.actorTitle.add(movieTitle + "\t" + tempActorFirst + " " + tempActorLast + "\t" + tempActorBirth + "\t" + tempActorRole);
                        tempActorFirst = null;
                        tempActorLast = null;
                        tempActorRole = null;
                        tempActorBirth = null;
                        actor = false;
                    }
                    if(qName.equalsIgnoreCase("first_name")){
                        first_name = false;
                    }
                    if(qName.equalsIgnoreCase("last_name")){
                        last_name = false;
                    }
                    if(qName.equalsIgnoreCase("role")){
                        role = false;
                    }
                    if(qName.equalsIgnoreCase("birth_date")){
                        birth_date = false;
                    }
                }
            };

            saxParser.parse(movie, defaultHandler);

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
