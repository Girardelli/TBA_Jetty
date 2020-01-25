package be.tba.util.common;

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
