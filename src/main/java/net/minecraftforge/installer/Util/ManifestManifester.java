package net.minecraftforge.installer.Util;

import net.minecraftforge.installer.ForgeClasses.VersionInfo;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ManifestManifester {
    public static InputStream getManifest(URL url) throws IOException {
        Path tempDir;

        tempDir = Files.createTempDirectory("forge");
        InputStream in = url.openStream();
        Files.copy(in, tempDir, StandardCopyOption.REPLACE_EXISTING);
        VersionInfo.setInstallerDir(tempDir);
        ZipFile z = new ZipFile(tempDir.toString());
        Enumeration<? extends ZipEntry> entries = z.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().equals("install_profile.json")) {
                InputStream stream = z.getInputStream(entry);
                return stream;
            }
        }

        throw new FileNotFoundException();
    }
}
