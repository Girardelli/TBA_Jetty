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
final public class RegisterData implements Serializable
{
   /**
   * 
   */
   private static final long serialVersionUID = 10004L;

   private String mCode = "";

   private String mUserId = "";

   private String mPassword = "";

   private String mFullName = "";

   private String mEmail = "";

   public void setCode(String code)
   {
      mCode = code;
   }

   public String getCode()
   {
      return mCode;
   }

   public void setUserId(String userId)
   {
      mUserId = userId;
   }

   public String getUserId()
   {
      return mUserId;
   }

   public void setPassword(String password)
   {
      mPassword = password;
   }

   public String getPassword()
   {
      return mPassword;
   }

   public void setFullName(String name)
   {
      mFullName = name;
   }

   public String getFullName()
   {
      return mFullName;
   }

   public void setEmail(String email)
   {
      mEmail = email;
   }

   public String getEmail()
   {
      return mEmail;
   }

   public String toString()
   {
      return new String("Code:" + mCode + " UserId:" + mUserId + " name:" + mFullName);
   }
}
