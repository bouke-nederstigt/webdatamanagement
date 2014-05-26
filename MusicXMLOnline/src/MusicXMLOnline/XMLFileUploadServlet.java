package MusicXMLOnline;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


/**
 * Created by bouke on 26/05/2014.
 */
public class XMLFileUploadServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        java.io.PrintWriter pw = response.getWriter();

        if(isMultipart){
            try{
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List items = upload.parseRequest(request);
                Iterator iter = items.iterator();
                while (iter.hasNext()){
                    FileItem item = (FileItem) iter.next();
                    if(!item.isFormField()){
                        String fileName = item.getName();
                        String destFilePath = getServletContext().getRealPath("/upload/" + fileName);
                        File uploadedFile = new File(destFilePath);
                        item.write(uploadedFile);

                        //Exist exist = new Exist();
                        //String[] args = new String[]{"musicxmlonline", fileName};
                        //request.setAttribute("message", exist.addDocument(args));
                    }
                }
                //request.setAttribute("message", "File has been uploaded successfully" );
            }catch(Exception e){
                e.printStackTrace();
                request.setAttribute("message", "Unable to upload file:" + e.getMessage());
            }
            getServletContext().getRequestDispatcher("/handleUpload.jsp").forward(request, response);

        }
    }
}
