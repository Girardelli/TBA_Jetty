package be.tba.util.file;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import be.tba.util.constants.Constants;
import be.tba.util.exceptions.SystemErrorException;

@WebServlet("/upload")
@MultipartConfig
public class FileUploader
{
    private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 200;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 100;
    
    private String mUploadedFile;
    private List<FileItem> mItems;

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
            FileItem uploadedFileItem = null;
            // Parse the request
            mItems = upload.parseRequest(request);
            if (mItems == null)
            {
                throw new SystemErrorException("File upload parsing returned null.");
            }
            if (mItems.size() == 0)
            {
                throw new SystemErrorException("File upload parsing returned 0 file items");
            }

            System.out.println("#fileitems returned: " + mItems.size());
            Iterator<FileItem> iter = mItems.iterator();
            while (iter.hasNext())
            {
                FileItem item = (FileItem) iter.next();
                System.out.println("FileItem: " + item.getName());
                if (uploadedFileItem != null)
                {
                    continue;
                }
                if (!item.isFormField())
                {
                    mUploadedFile = Constants.FILEUPLOAD_DIR + File.separator + item.getName();
                    System.out.println("File name returned for fileupload: " + mUploadedFile);
                    File uploadedFile = new File(mUploadedFile);
                    
                    // saves the file to upload directory
                    item.write(uploadedFile);
                    uploadedFileItem = item;
                    System.out.println("File successful written to temp file: " + mUploadedFile);
                }
            }
        }
        catch (FileUploadException ex)
        {
            ex.printStackTrace();
            throw new SystemErrorException(ex, "FileUploadException");
        }
        catch (SystemErrorException ex)
        {
            throw ex;
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new SystemErrorException(ex, "Exception");
        }

    }

    public String getUploadedFileName()
    {
        return mUploadedFile;
    }
    
    public String getFormParameter(String name)
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
        System.out.println("Parameter not found: " + name);
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
                System.out.println("Deleting FileItem: " + item.getFieldName());
                item.delete();
            }
        }
        mItems = null;
   }

}
