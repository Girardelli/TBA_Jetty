package be.tba.util.invoice;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * <b>IBAN</b> (International Bank Account Number) Check Digit
 * calculation/validation.
 * <p>
 * This routine is based on the ISO 7064 Mod 97,10 check digit calculation
 * routine.
 * <p>
 * The two check digit characters in a IBAN number are the third and fourth
 * characters in the code. For <i>check digit</i> calculation/validation the
 * first four characters are moved to the end of the code. So
 * <code>CCDDnnnnnnn</code> becomes <code>nnnnnnnCCDD</code> (where
 * <code>CC</code> is the country code and <code>DD</code> is the check digit).
 * For check digit calculation the check digit value should be set to zero (i.e.
 * <code>CC00nnnnnnn</code> in this example.
 * <p>
 * Note: the class does not check the format of the IBAN number, only the check
 * digits.
 * <p>
 * For further information see <a href=
 * "http://en.wikipedia.org/wiki/International_Bank_Account_Number">Wikipedia -
 * IBAN number</a>.
 */
public final class IBANCheckDigit implements Serializable
{

    private static final long serialVersionUID = -3600191725934382801L;

    /** Singleton IBAN Number Check Digit instance */
    public static final IBANCheckDigit IBAN_CHECK_DIGIT = new IBANCheckDigit();

    private static final long MAX = 999999999;

    private static final long MODULUS = 97;

    /**
     * Construct Check Digit routine for IBAN Numbers.
     */
    public IBANCheckDigit()
    {
    }

    /**
     * Validate the check digit of an IBAN code.
     * 
     * @param code
     *            The code to validate
     * @return <code>true</code> if the check digit is valid, otherwise
     *         <code>false</code>
     */
    public boolean isValid(String code)
    {
        if (code == null || code.length() < 5)
        {
            return false;
        }
        int modulusResult = calculateModulus(code);
        return (modulusResult == 1);
    }

    /**
     * Calculate the <i>Check Digit</i> for an IBAN code.
     * <p>
     * <b>Note:</b> The check digit is the third and fourth characters and and
     * should contain value "<code>00</code>".
     * 
     * @param code
     *            The code to calculate the Check Digit for
     * @return The calculated Check Digit as 2 numeric decimal characters, e.g. "42"
     * @throws CheckDigitException
     *             if an error occurs calculating the check digit for the specified
     *             code
     */
    public String calculateIBAN(String code) 
    {
        if (code == null || code.length() < 5)
        {
            System.out.println("Invalid Code length=" + (code == null ? 0 : code.length()));
            return null;
        }
        String reformattedCode = code.substring(4) + code.substring(0, 4);

        int modulusResult = calculateModulus(reformattedCode);
        int charValue = (98 - modulusResult);
        String checkDigit = Integer.toString(charValue);
        return (charValue > 9 ? checkDigit : "0" + checkDigit);
    }

    /*
     * 
     * N-1911nr693
     * 19110693
     * +++191/1000/69307+++
     */
    public String calculateOGM(String invoiceNr) 
    {
        // expects something like this: N-1710nr591
        
        if (invoiceNr == null)
        {
            System.out.println("input parm = null");
            return null;
        }
        
        if (invoiceNr.length() == 0)
        {
            return "";
        }
        else if (invoiceNr.length() != 8)
        {
            System.out.println("Invalid Code length (must be 8)=" + (invoiceNr == null ? "null" : invoiceNr.length()));
            return "";
        }
        
        String code = String.format("%s00%s", invoiceNr.substring(0, 4), invoiceNr.substring(4, 8));
        //System.out.println("Invoice nr: invoiceNr: " + invoiceNr + " --> " + code);
        int i = Integer.parseUnsignedInt(code);
        int y = i%97;
        y = (y == 97 ? 0 : y);
        DecimalFormat vCostFormatter = new DecimalFormat("#00");
        //        +++191/1000/69307+++
        //System.out.println("+++" + code.substring(0, 3) + "/" + code.substring(3, 7) + "/" + code.substring(7, 10) + vCostFormatter.format(y) + "+++");
        return "+++" + code.substring(0, 3) + "/" + code.substring(3, 7) + "/" + code.substring(7, 10) + vCostFormatter.format(y) + "+++";
    }

    
    /**
     * Calculate the modulus for a code.
     * 
     * @param code
     *            The code to calculate the modulus for.
     * @return The modulus value
     * @throws CheckDigitException
     *             if an error occurs calculating the modulus for the specified code
     */
    private int calculateModulus(String code) 
    {
        long total = 0;
        for (int i = 0; i < code.length(); i++)
        {
            int charValue = CharacterGetNumericValue.getNumericValue(code.charAt(i));
            if (charValue < 0 || charValue > 35)
            {
                System.out.println("Invalid Character[" + i + "] = '" + charValue + "'");
                return 0;
            }
            total = (charValue > 9 ? total * 100 : total * 10) + charValue;
            if (total > MAX)
            {
                total = total % MODULUS;
            }
        }
        return (int) (total % MODULUS);
    }


}
