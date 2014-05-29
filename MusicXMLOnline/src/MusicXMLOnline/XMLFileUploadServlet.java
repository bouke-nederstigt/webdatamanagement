package MusicXMLOnline;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by bouke on 26/05/2014.
 */
public class XMLFileUploadServlet extends javax.servlet.http.HttpServlet {

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        java.io.PrintWriter pw = response.getWriter();


        if (isMultipart) {
            try {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List items = upload.parseRequest(request);
                Iterator iter = items.iterator();

                String destFilePath = null;
                String fileName = null;
                String uploadPath = null;

                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (!item.isFormField()) {
                        fileName = item.getName();
                        uploadPath = getServletContext().getRealPath("/upload/");
                        destFilePath = getServletContext().getRealPath("/upload/" + fileName);
                        File uploadedFile = new File(destFilePath);
                        item.write(uploadedFile);
                    }
                }

                //add document to eXist db
                Exist exist = new Exist();
                String[] args = new String[3];
                args[0] = "db/musicxmlonline";
                args[1] = destFilePath;
                args[2] = fileName;
                request.setAttribute("message", exist.addDocument(args));

                //immediately create pdf and MIDI files
                String ly = this.xmlToLy(destFilePath, destFilePath.substring(0, destFilePath.length() - 4) + ".ly");
                if(ly == null){
                    request.setAttribute("pathToLy", destFilePath.substring(0, destFilePath.length() - 4) + ".ly");
                }


                String pdfMidi = this.createPdfMidi(destFilePath.substring(0, destFilePath.length() - 4) + ".ly", uploadPath);
                if(pdfMidi == null){
                    request.setAttribute("createdOutput", "Created PDF and Midi files");
                }

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("message", "Unable to upload file:" + e.getMessage());
            }
            getServletContext().getRequestDispatcher("/handleUpload.jsp").forward(request, response);

        }
    }

    /**
     * Create ly from xml
     *
     * @return null on success
     */
    public String xmlToLy(String pathToXml, String outputPath) throws IOException {
        List<String> result = this.executeCmd("musicxml2ly --output=" + outputPath + " -m " + pathToXml);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    /**
     * Create pdf and MIDI file from ly file
     *
     * @return null on success
     */
    public String createPdfMidi(String pathToLy, String outputPath) throws IOException {
        List<String> result = this.executeCmd("lilypond --output=" + outputPath + " " + pathToLy);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    /**
     * Execute cmd line command
     *
     * @param cmd that needs to bee executed
     */
    private List<String> executeCmd(String cmd) throws IOException {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("cmd /c " + cmd);

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line = null;
            List<String> returnValue = new ArrayList<String>();

            while ((line = input.readLine()) != null) {
                returnValue.add(line);
            }

            int exitVal = pr.waitFor();
            if (exitVal != 0) {
                returnValue.add("Exited with error code " + exitVal);
            }
            //  List<String> returnValue = new ArrayList<String>();
            //  returnValue.add("cmd /c " + cmd);
            return returnValue;

        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        return null;
    }
}
