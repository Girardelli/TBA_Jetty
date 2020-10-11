package be.tba.util.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BankVanBredaHandler implements PaymentFileHandlerInterf
{
   private static Logger log = LoggerFactory.getLogger(BankVanBredaHandler.class);

   // Fintro excel sheet collumns
   
   private static int EXEC_DATE = 0;
   private static int VALUTA_DATE = 1;
   private static int UITTREKSELNR = 2;
   private static int TRANSACTIONNR = 3;
   private static int AMOUNT = 4;
   private static int CUST_ACCOUNT = 8;
   private static int NAME = 7;
   private static int MESSAGE = 9;

   public BankVanBredaHandler()
   {
      log.info("BankVanBredaHandler created");
   }
   
   @Override
   public BankPayment parseRow(Row row)
   {
      // Row row = sheet.getRow(i);
      BankPayment entry = new BankPayment();
      String uittrekselNr = stripDotPart(row.getCell(UITTREKSELNR).toString());
      String transactionNr = stripDotPart(row.getCell(TRANSACTIONNR).toString());
      entry.id = uittrekselNr + "-" + transactionNr;

      entry.payDate = row.getCell(EXEC_DATE).toString();
//      row.getCell(VALUTA_DATE).setCellType(CellType.STRING);
      entry.valutaDate = row.getCell(VALUTA_DATE).toString();
      // entry.amount = row.getCell(AMOUNT).getNumericCellValue();
      String amount = row.getCell(AMOUNT).toString();
      entry.amount = Double.parseDouble(BankPayment.normalizeAmount(amount));
      entry.accountNrCustomer = row.getCell(CUST_ACCOUNT).toString();
      
      String name = (row.getCell(NAME) == null ? "" : row.getCell(NAME).toString());
      String message = (row.getCell(MESSAGE) == null ? "" : row.getCell(MESSAGE).toString());
      
      entry.details = name + "  " + message;
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
         String amount = row.getCell(AMOUNT).toString();
         amount = BankPayment.normalizeAmount(amount);
         Double.parseDouble(amount);
         return true;
      }
      catch (Exception e)
      {
         log.info(e.getMessage());
      }
      return false;
   }

   
   private String stripDotPart(String valuaStr)
   {
      if (valuaStr.contains("."))
      {
         valuaStr = valuaStr.substring(0, valuaStr.indexOf('.'));
      }
      return valuaStr;
   }
}
