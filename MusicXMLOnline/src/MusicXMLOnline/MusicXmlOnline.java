package MusicXMLOnline;

/**
 * Created by bouke on 26/05/2014.
 */
public class MusicXmlOnline {

    public static java.util.List<String> getMessage() throws Exception {
        String [] args = new String[2];
        args[0] = "musicxmlonline";
        args[1] = "for $docs in collection('musicxmlonline')" +
                "  return $docs ";
        Exist exist = new Exist();

        return exist.query(args);
    }

    /**
     * Create ly from xml
     */
    public String xmlToLy(String pathToXml){
        return "Filepath";
    }

    /**
     * Create pdf from ly file
     * @return
     */
    public String createPDF(String pathToLy){
        return "Filepath";
    }

    /**
     * Create midi from ly
     */
    public String createMidi(String pathToLy){
        return "Filepaht";
    }
}
