package MusicXMLOnline;

/**
 * Created by bouke on 26/05/2014.
 */
public class MusicXmlOnline {

    public static java.util.List<String> getDocuments() throws Exception {
        String [] args = new String[2];
        args[0] = "musicxmlonline";
        args[1] = "for $docs in collection('musicxmlonline')" +
                "  return fn:document-uri($docs)";
        Exist exist = new Exist();

        return exist.query(args);
    }

}
