package be.tba.util.excel;

import org.apache.poi.ss.usermodel.Row;

public interface PaymentFileHandlerInterf
{
   public BankPayment parseRow(Row row);
   public boolean isValidRow(Row row);
}
