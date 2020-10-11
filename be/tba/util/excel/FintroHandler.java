package be.tba.util.excel;

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FintroHandler implements PaymentFileHandlerInterf
{
   private static Logger log = LoggerFactory.getLogger(FintroHandler.class);

   // Fintro excel sheet collumns
   private static int ID = 0;
   private static int EXEC_DATE = 1;
   private static int VALUTA_DATE = 2;
   private static int AMOUNT = 3;
   private static int CUST_ACCOUNT = 5;
   private static int DETAILS = 6;

   public FintroHandler()
   {
      log.info("FintroHandler created");
   }
   
   @Override
   public BankPayment parseRow(Row row)
   {
      // Row row = sheet.getRow(i);
      BankPayment entry = new BankPayment();
      entry.id = row.getCell(ID).toString();
      /*
       * try 
       * { 
       * // try reading it as a date field. (!!! it is not clear here what the date format was on the computer importing the content) 
       * Date date = row.getCell(EXEC_DATE).getDateCellValue(); 
       * // serious hack I'm doing here. If the date column does not read in as a string, excel has formated this 
       * // cell as date but with a wrong (US) local setting. Therefore treat month as day and day as month!!!! 
       * SimpleDateFormat dt1 = new SimpleDateFormat("MM/dd/yyyy");
       * entry.payDate = dt1.format(date); 
       * } 
       * catch (Exception e) 
       * { 
       * log.error(" execution date date cell cannot be read as text. unknown cell format"); 
       * //log.error(e.getMessage(), e); 
       * entry.payDate = row.getCell(EXEC_DATE).toString(); 
       * } 
       * // log.info(entry.payDate);
       */

      entry.payDate = row.getCell(EXEC_DATE).toString();
      entry.valutaDate = row.getCell(VALUTA_DATE).toString();
      // entry.amount = row.getCell(AMOUNT).getNumericCellValue();
      entry.amount = Double.parseDouble(row.getCell(AMOUNT).toString());
      entry.accountNrCustomer = row.getCell(CUST_ACCOUNT).toString();
      entry.details = (row.getCell(DETAILS) == null ? "" : row.getCell(DETAILS).toString());
      // remove ' and " chars
      entry.details = entry.details.replace('\'', ' ');
      entry.details = entry.details.replace('\"', ' ');
      return entry;
   }

   @Override
   public boolean isValidRow(Row row)
   {
      // Column 'D' must be a double number
      try
      {
         Double.parseDouble(row.getCell(AMOUNT).toString());
         return true;
      }
      catch (Exception e)
      {
         log.info(e.getMessage());
      }
      return false;
   }

}
