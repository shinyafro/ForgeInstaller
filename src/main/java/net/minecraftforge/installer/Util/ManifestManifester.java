package net.minecraftforge.installer.Util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ManifestManifester {
    public static InputStream getManifest(URL url) throws Throwable{
        Path tempDirWithPrefix;
        try{
            tempDirWithPrefix = Files.createTempDirectory("forge");
            InputStream in = url.openStream();
            Files.copy(in, tempDirWithPrefix, StandardCopyOption.REPLACE_EXISTING);
            ZipFile z = new ZipFile(tempDirWithPrefix.toString());
            Enumeration<? extends ZipEntry> entries = z.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("install_profile.json")) {
                    InputStream stream = z.getInputStream(entry);
                    return stream;
                }
            }
        }catch (IOException e){
            throw e;
        }
        throw new FileNotFoundException();
    }
}
