package be.tba.util.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Tools
{
   static public String spaces2underscores(String aName)
   {
      String name = aName.replace(' ', '_');
      name = name.replace(';', '_');
      name = name.replace(':', '_');
      name = name.replace(',', '_');
      return name;
   }

}
