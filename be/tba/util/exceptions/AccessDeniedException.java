/*
 * TheBusinessAssistant b.v.b.a
 *
 */

package be.tba.util.exceptions;

/**
 * @author Yves Willems
 * @version $Revision: 1.1 $
 */
public class AccessDeniedException extends Exception
{

    // -------------------------------------------------------------------------
    // Static
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Members
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor with a message of the exception
     * 
     * @param pMessage
     *            Message to further explain the exception
     */
    public AccessDeniedException(String pMessage)
    {
        super(pMessage);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

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
