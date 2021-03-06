/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.sqldata;

import java.io.Serializable;

/**
 * Base Data Container for all other Value Objects
 *
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */
public abstract class AbstractData implements Cloneable, Serializable
{

   /**
    *
    */
   private static final long serialVersionUID = 1L;

   /**
    * Returns a copy of itself. Is necessary because this method is protected
    * within java.lang.Object.
    *
    * @return Copy of this instance
    */
   public Object clone()
   {
      try
      {
         return super.clone();
      }
      catch (CloneNotSupportedException cnse)
      {
         // This never happens
         return null;
      }
   }

   public String toString()
   {
      StringBuilder str = new StringBuilder("{");

      str.append(getId() + ",");
      str.append(toNameValueString().toString());
      str.append('}');

      return (str.toString());
   }

   abstract public String toValueString();

   abstract public String toNameValueString();

   abstract public int getId();

   protected String escapeQuotes(String in)
   {
      if (in.indexOf('\'') >= 0)
      {
         return in.replace("'", "''");
      }
      return in;
   }

}
