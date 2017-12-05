/*
 * TheBusinessAssistant b.v.b.a
 *
 */

package be.tba.util.exceptions;

import java.util.Collection;

/**
 * An instance of this class is thrown when a Value Object contains an invalid
 * value. It parameters can then be used with {@link java.text.MessageFormat
 * MessageFormat} to get right message. The message this exceptions contains is
 * not the text but the key to get the right text from it.
 * 
 * @author Yves Willems
 * @version $Revision: 1.1 $
 */
public class InvalidValueException extends Exception
{

    // -------------------------------------------------------------------------
    // Static
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Members
    // -------------------------------------------------------------------------

    private Object[] mParameters = new Object[0];

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor with a message handler and a list of parameters
     * 
     * @param pMessageHandler
     *            Handler to lookup the right message
     * @param pParameters
     *            One Parameter, array of parameters or a Collection of parameters
     *            or null
     */
    public InvalidValueException(String pMessageHandler, Object pParameters)
    {
        super(pMessageHandler);
        if (pParameters != null)
        {
            if (pParameters instanceof Collection)
            {
                mParameters = ((Collection) pParameters).toArray(new Object[0]);
            }
            else
            {
                if (pParameters instanceof Object[])
                {
                    mParameters = (Object[]) pParameters;
                }
                else
                {
                    mParameters = new Object[] { pParameters };
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * Returns the array of parameters coming along
     * 
     * @return Array of parameters which are always defined but can be empty
     */
    public Object[] getParameters()
    {
        return mParameters;
    }

    /**
     * Describes the instance and its content for debugging purpose
     * 
     * @return Using the one from the super class
     */
    public String toString()
    {
        return super.toString();
    }

    /**
     * Determines if the given instance is the same as this instance based on its
     * content. This means that it has to be of the same class ( or subclass ) and
     * it has to have the same content
     * 
     * @return Returns the equals value from the super class
     */
    public boolean equals(Object pTest)
    {
        return super.equals(pTest);
    }

    /**
     * Returns the hashcode of this instance
     * 
     * @return Hashcode of the super class
     */
    public int hashCode()
    {
        return super.hashCode();
    }

}
