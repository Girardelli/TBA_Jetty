/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.data;

import java.io.Serializable;

/**
 * Base Data Container for all other Value Objects
 * 
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */

final public class AccountMapData implements Serializable
{
    /**
    * 
    */
    private static final long serialVersionUID = 10007L;

    public AccountMapData(String name, boolean is3W, boolean isMailEnabled, short invoiceType)
    {
        this.name = name;
        this.is3W = is3W;
        this.isMailEnabled = isMailEnabled;
        this.invoiceType = invoiceType;
    }

    public String name;

    public boolean is3W;

    public boolean isMailEnabled;

    public short invoiceType;

    public String toString()
    {
        return new String("AccountMapData: name:" + name + " is3W:" + (is3W ? "yes" : "no") + " isMailEnabled:" + (isMailEnabled ? "yes" : "no"));
    }

}
