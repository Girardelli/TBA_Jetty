package be.tba.util.file;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import be.tba.util.exceptions.SystemErrorException;

public class FileUploader
{
    private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 2;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024;

    public static String upload(String uploadFolder, HttpServletRequest request) throws SystemErrorException
    {
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Sets the size threshold beyond which files are written directly to
        // disk.
        factory.setSizeThreshold(MAX_MEMORY_SIZE);

        // Sets the directory used to temporarily store files that are larger
        // than the configured size threshold. We use temporary directory for
        // java
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Set overall request size constraint
        upload.setSizeMax(MAX_REQUEST_SIZE);

        try
        {
            File uploadedFile = null;
            String filePath = null;
            // Parse the request
            List<FileItem> items = upload.parseRequest(request);
            if (items == null || items.size() == 0)
            {
                throw new SystemErrorException("File could not be uploaded. No file items found.");
            }

            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext())
            {
                FileItem item = (FileItem) iter.next();
                if (uploadedFile != null)
                {
                    System.out.println("Only 1 file per upload supported. Deleting this one");

                    item.delete();
                    continue;
                }
                if (!item.isFormField())
                {
                    String fileName = new File(item.getName()).getName();
                    filePath = uploadFolder + File.separator + fileName;
                    uploadedFile = new File(filePath);
                    
                    // saves the file to upload directory
                    item.write(uploadedFile);
                    System.out.println("File successful written to temp file: " + filePath);
                }
                else
                {
                    System.out.println("uploaded item is a FormField.");
                    item.delete();
                    
                }
            }
            return filePath;

        }
        catch (FileUploadException ex)
        {
            throw new SystemErrorException(ex, "FileUploadException");
        }
        catch (Exception ex)
        {
            throw new SystemErrorException(ex, "Exception");
        }

    }

    public static void clearTempFiles(FileItem item)
    {
        if (item != null)
        {
            item.delete();
        }
    }

}
