package MusicXMLOnline;

import java.util.List;

/**
 * Created by bouke on 26/05/2014.
 */
public class MusicXmlOnline {

    /**
     *
     * @return list of filenames in db (also filenames on filesystem)
     * @throws Exception
     */
    public static java.util.List<String> getDocuments() throws Exception {
        String args = "for $docs in collection('musicxmlonline')" +
                "  return fn:document-uri($docs)";
        Exist exist = new Exist();

        List results = exist.query(args);
        for(int i = 0; i< results.size(); i++) {
            String split = (String) results.get(i);
            String[] parts = split.split("/");
            String fileName = parts[parts.length -1];
            fileName = fileName.substring(0, fileName.length() - 4);
            results.set(i, fileName);
        }

        return results;
    }

    /**
     * Retrieve songs by lyrics or title
     */
    public static java.util.List<String> searchLyrics(String input) throws Exception {

        //convert input to xQuery sequence
        String [] keywordsArray = input.split(" ");
        StringBuilder keywords = new StringBuilder("(");
        if(keywordsArray.length > 0){
            for (int i = 0; i<keywordsArray.length; i++){
                if(i == keywordsArray.length - 1){
                    keywords.append("'" + keywordsArray[i] + "'");
                }else{
                    keywords.append("'" + keywordsArray[i] + "',");
                }
            }
        }
        keywords.append(")");

        String query = "for $doc in collection('/musicxmlonline/?*.xml')" +
                "return if (every $keyword in " + keywords.toString() + " satisfies" +
                "   contains($doc/score-partwise//lyric/text, $keyword) or contains($doc/score-partwise/movement-title/text(), $keyword) )" +
                "then" +
                "   fn:document-uri($doc)" +
                "else ()";

        Exist exist = new Exist();
        List results = exist.query(query);

        if(results.size() != 0){
            for(int i = 0; i< results.size(); i++) {
                String split = (String) results.get(i);
                String[] parts = split.split("/");
                String fileName = parts[parts.length -1];
                fileName = fileName.substring(0, fileName.length() - 4);
                results.set(i, fileName);
            }
        }else{
            results = null;
        }

        return results;
    }

    /**
     * Get doc title
     */
    public static String getDocTitle(String document) throws Exception {
        String query = "for $doc in doc('" + document + ".xml')" +
                " return $doc/score-partwise/movement-title/text()";
        Exist exist = new Exist();
        List<String> result = exist.query(query);
        if(result.size() != 1){
            query = "for $doc in doc('" + document + ".xml')" +
                    " return $doc/score-partwise/work/work-title/text()";

            result = exist.query(query);
            if(result.size() != 1){
                result.add(0, "Unknown title in document: " + document);
            }
        }
        return result.get(0);
    }

    /**
     * Get lyrics to a song
     */
    public static String getLyrics(String doc) throws Exception{

       String query = "for $doc in doc('" + doc + ".xml')/score-partwise/part/measure/note" +
               " return <lyric>{$doc//lyric/text/text()} &#032; </lyric>";

        Exist exist = new Exist();
        List <String> results = exist.query(query);

        StringBuilder result = new StringBuilder();

        if(results.size() == 0){
            result.append("No lyrics available for this song");
        }else{
            for(int i = 0; i< results.size(); i ++){
                String lastChar = results.get(i).substring(results.get(i).length() - 1);
                result.append(results.get(i));
                if(lastChar == "." || lastChar == "!" || lastChar == "?"){
                result.append("<br />");
                }
            }
        }

        return result.toString();
    }
}
