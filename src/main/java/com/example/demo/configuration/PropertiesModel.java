package com.example.demo.configuration;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by as on 11.07.2018.
 */
public class PropertiesModel {
    public static String master;
    //public static String mainAppJar = DemoApplication.getParentDirectoryFromJar() + File.separator + "artifact" + ".jar";
    public static String mainAppJar;
    public static String databaseJar;
    public static String driver;

    public static String printAll() {
        return PropertiesModel.master + "\n" +
                PropertiesModel.mainAppJar + "\n" +
                PropertiesModel.databaseJar+"\n"+
                PropertiesModel.driver;
    }

    public static void setMaster(String master) {
        PropertiesModel.master = master;
    }

    public static void setMainAppJar(String mainAppJar) {
        PropertiesModel.mainAppJar = mainAppJar;
    }

    public static void setDatabaseJar(String databaseJar) {
        PropertiesModel.databaseJar = databaseJar;
    }

    public static void setDriver(String driver) { PropertiesModel.driver = driver; }

    public static void setValue(String name, String value, Class c) throws InvocationTargetException, IllegalAccessException {
        for (Method method : c.getClass().getMethods()) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (name.length() + 3))) {
                String expectedName = "get" + name;
                if (expectedName.toLowerCase().equals(method.getName().toLowerCase())) {
                    method.invoke(value);
                }
            }
        }
    }
}
