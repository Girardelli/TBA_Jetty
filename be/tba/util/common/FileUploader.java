package be.tba.util.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.servlets.FileDownloadServlet;
import be.tba.session.SessionParmsInf;
import be.tba.util.exceptions.SystemErrorException;

@WebServlet("/upload")
@MultipartConfig
public class FileUploader implements SessionParmsInf
{
   private static Logger log = LoggerFactory.getLogger(FileUploader.class);
   /*
    * This class is used for file upload: from client to server. Constructor parses
    * the http request and makes the attributes available through the
    * getFormParameter([attribute name]) method. This method can be used before
    * starting the actual file transfer with the upload() method.
    * 
    * 
    */

   private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 200;
   private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 100;

   private String mUploadedFile;
   private List<FileItem> mItems;
   private UploadThread mUploadThread;
   private String mStorePath = "c:\\temp\\TBAuploads";

   public FileUploader(HttpServletRequest request) throws SystemErrorException
   {
      // Create a factory for disk-based file items
      DiskFileItemFactory factory = new DiskFileItemFactory();

      // Sets the size threshold beyond which files are written directly to
      // disk.
      factory.setSizeThreshold(MAX_MEMORY_SIZE);

      // Sets the directory used to temporarily store files that are larger
      // than the configured size threshold. We use temporary directory for
      // java
      factory.setRepository(new File("c:\\temp\\fileuploadtemp"));

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);

      // Set overall request size constraint
      upload.setSizeMax(MAX_REQUEST_SIZE);
      try
      {
         mItems = upload.parseRequest(request);
      }
      catch (FileUploadException e)
      {
         log.error(e.getMessage(), e);
         throw new SystemErrorException(e, "FileUploadException");
      }

   }

   public void setStoragePath(String path)
   {
      mStorePath = path;
   }

   public void upload(HttpServletRequest request) throws SystemErrorException
   {
      // Parse the request
      if (mItems == null)
      {
         throw new SystemErrorException("File upload parsing returned null.");
      }
      if (mItems.size() == 0)
      {
         throw new SystemErrorException("File upload parsing returned 0 file items");
      }

      Path path = Path.of(mStorePath);
      if (Files.notExists(path))
      {
         try
         {
            Files.createDirectories(path);
         }
         catch (IOException e)
         {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
            throw new SystemErrorException("Bestand kan niet worden opgeladen.");
         }
      }
      // log.info("#fileitems returned: " + mItems.size());
      mUploadThread = new UploadThread();
      mUploadThread.run();
      return;
   }

   public String getUploadedFileName()
   {
      return mUploadedFile;
   }

   public String waitTillFinished()
   {
      try
      {
         mUploadThread.join();
      }
      catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         log.error(e.getMessage(), e);
         log.error("File Upload thread join returned this exception on file: " + mUploadedFile);
      }
      return mUploadedFile;
   }

   public String getParameter(String name)
   {
      Iterator<FileItem> iter = mItems.iterator();
      while (iter.hasNext())
      {
         FileItem item = (FileItem) iter.next();
         if (item.isFormField() && item.getFieldName().equals(name))
         {
            return item.getString();
         }
      }
      log.info("Parameter not found: " + name);
      return null;
   }

   public void finalize()
   {
      if (mItems != null)
      {
         Iterator<FileItem> iter = mItems.iterator();
         while (iter.hasNext())
         {
            FileItem item = (FileItem) iter.next();
            // log.info("Deleting FileItem: " + item.getFieldName());
            item.delete();
         }
      }
      mItems = null;
   }

   private class UploadThread extends Thread
   {
      public UploadThread()
      {

      }

      public void run()
      {
         log.info("Upload thread started");
         FileItem uploadedFileItem = null;
         Iterator<FileItem> iter = mItems.iterator();
         while (iter.hasNext())
         {
            FileItem item = (FileItem) iter.next();
//            log.info("FileItem: " + item.getName());
//            log.info("fieldname: " + item.getFieldName());

            if (uploadedFileItem != null)
            {
               continue;
            }
            if (!item.isFormField())
            {
               String fileName = item.getName().substring(item.getName().lastIndexOf('\\') + 1);
               mUploadedFile = mStorePath + File.separator + fileName;
               // log.info("File name returned for fileupload: " + mUploadedFile);
               File uploadedFile = new File(mUploadedFile);

               // saves the file to upload directory
               try
               {
                  item.write(uploadedFile);
               }
               catch (Exception e)
               {
                  log.error(e.getMessage(), e);
                  log.error("File could not be stored on server: " + item.getFieldName());
                  continue;
               }
               uploadedFileItem = item;
               // log.info("File successful written to temp file: " + mUploadedFile);
            }
         }

      }

   }
}
