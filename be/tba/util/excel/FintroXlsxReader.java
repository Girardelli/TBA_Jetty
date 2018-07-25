package be.tba.util.excel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Collection;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.pbx.PbxLogPoller;
import be.tba.util.data.FintroPayment;

final public class FintroXlsxReader
{
    final static Logger sLogger = LoggerFactory.getLogger(PbxLogPoller.class);

    // Fintro excel sheet collumns
    private static int ID = 0;
    private static int EXEC_DATE = 1;
    private static int VALUTA_DATE = 2;
    private static int AMOUNT = 3;
    private static int CUST_ACCOUNT = 5;
    private static int DETAILS = 6;
    
    private FintroXlsxReader(File input)
    {
    }
    
    static public Map<String, Collection<FintroPayment>> readPayments(String input)
    {
        OPCPackage pkg = null;
        XSSFWorkbook wb;
        File inputFile = new File(input);
        
        Map<String, Collection<FintroPayment>> mPaymentsMap = new HashMap<String, Collection<FintroPayment>>();

        try
        {
            sLogger.info("FintroXlsxReader constructor");
            
            
         // XSSFWorkbook, File
            pkg = OPCPackage.open(inputFile);
            wb = new XSSFWorkbook(pkg);
            Sheet sheet = wb.getSheetAt(0);
            //for (int i = 1; i < 10; ++i)
            
            // Decide which rows to process
            int rowStart = 1;
            int rowEnd = Math.min(1500, sheet.getLastRowNum());
            sLogger.info("last collumn: "+ sheet.getLastRowNum());
            int i;
            for (i = rowStart; i < rowEnd; i++) 
            {
               Row row = sheet.getRow(i);
               if (row == null) 
               {
                  // This whole row is empty
                  // Handle it as needed
                  continue;
               }
               if (row.getCell(ID, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) == null)
               {
                   continue;
               }

               //Row row = sheet.getRow(i);
               FintroPayment entry = new FintroPayment();
               entry.id = row.getCell(ID).toString();
               entry.executionDate= row.getCell(EXEC_DATE).toString();
               entry.valutaDate= row.getCell(VALUTA_DATE).toString();
               entry.amount= row.getCell(AMOUNT).getNumericCellValue();
               entry.accountNrCustomer= row.getCell(CUST_ACCOUNT).toString();
               entry.details= row.getCell(DETAILS).toString();
               
               if (entry.amount > 0)
               {
                   Collection<FintroPayment> customerPaymentList = mPaymentsMap.get(entry.accountNrCustomer);
                   if (customerPaymentList == null)
                   {
                       customerPaymentList = new Vector<FintroPayment>();
                       mPaymentsMap.put(entry.accountNrCustomer, customerPaymentList);
                   }
                   customerPaymentList.add(entry);
                   sLogger.info("entry added: size=" + mPaymentsMap.size() + " ##" + entry.toString());
               }
               else
               {
                   sLogger.info("outgoing payment!! " + entry.toString());
               }
                       
            }
        }
        catch (IOException | InvalidFormatException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (pkg != null)
                    pkg.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mPaymentsMap;
    }
    
}
