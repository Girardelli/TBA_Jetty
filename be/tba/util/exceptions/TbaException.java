package be.tba.util.exceptions;

public class TbaException extends Exception
{
   private static final long serialVersionUID = -7648733358069135056L;

   public TbaException(String pMessage)
   {
       super(pMessage);
   }
}
