/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roborally.ai;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gross
 */
public class AI extends ClassLoader {

    private Class<?> creatorClass;
    private JarFile file;
    private String[] aiProfiles;

    public AI(String filename) throws IOException, AIClientCreatorNotFoundException {
        file = new JarFile(filename);
        Enumeration<JarEntry> entries = file.entries();
        //System.out.println(filename);
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            //System.out.println("Entry: " + entry);
            Class<?> classForEntry = loadEntry(entry);
            //System.out.println("Class: " + classForEntry);
            if (classForEntry == null) {
                continue;
            }
            try {
                classForEntry.getMethod("createAIClient", String.class, String.class, int.class, String.class, String.class, String.class);
                Method method = classForEntry.getMethod("getAIProfiles");
                aiProfiles = (String[]) method.invoke(null);
                creatorClass = classForEntry;
                break;
            } catch (ClassCastException ex) {
            } catch (IllegalAccessException ex) {
            } catch (IllegalArgumentException ex) {
            } catch (InvocationTargetException ex) {
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
            }
        }
        if (creatorClass == null) {
            throw new AIClientCreatorNotFoundException(filename);
        }
    }

    public void createAIClient(String profile, String ip, int port, String serverPassword, String clientName, String gameName) {
        Method method;
        try {
            method = creatorClass.getMethod("createAIClient", String.class, String.class, int.class, String.class, String.class, String.class);
            method.invoke(null, profile, ip, port, serverPassword, clientName, gameName);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(AI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected Class<?> getAIClientCreatorClass() {
        return creatorClass;
    }

    protected Class<?> loadEntry(JarEntry entry) {
        try {
            byte[] array = new byte[1024];
            InputStream in = this.file.getInputStream(entry);
            ByteArrayOutputStream out = new ByteArrayOutputStream(array.length);
            int length = in.read(array);
            while (length > 0) {
                out.write(array, 0, length);
                length = in.read(array);
            }
            return defineClass(entry.getName().replace("/", ".").replace(".class", ""), out.toByteArray(), 0, out.size());
        } catch (Error exception) {
            return null;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        JarEntry entry = this.file.getJarEntry(name.replace('.', '/') + ".class");
        if (entry == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            byte[] array = new byte[1024];
            InputStream in = this.file.getInputStream(entry);
            ByteArrayOutputStream out = new ByteArrayOutputStream(array.length);
            int length = in.read(array);
            while (length > 0) {
                out.write(array, 0, length);
                length = in.read(array);
            }
            return defineClass(name, out.toByteArray(), 0, out.size());
        } catch (IOException exception) {
            throw new ClassNotFoundException(name, exception);
        }
    }

    public String[] getAIProfiles() {
        return aiProfiles;
    }
}
