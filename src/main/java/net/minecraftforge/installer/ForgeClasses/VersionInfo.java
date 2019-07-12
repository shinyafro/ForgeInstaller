package net.minecraftforge.installer.ForgeClasses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;
import net.minecraftforge.installer.Util.ManifestManifester;

public class VersionInfo {
    public static VersionInfo instance;
    public final JsonRootNode versionData;
    private final List<OptionalLibrary> optionals = Lists.newArrayList();

    public static VersionInfo getInstance() {
        return instance;
    }

    public VersionInfo(URL url)
    {
        instance = this;
        InputStream installProfile = ManifestManifester.getManifest(url);
        JdomParser parser = new JdomParser();

        try
        {
            versionData = parser.parse(new InputStreamReader(installProfile, Charsets.UTF_8));

            if (versionData.isArrayNode("optionals"))
            {
                for (JsonNode opt : versionData.getArrayNode("optionals"))
                {
                    OptionalLibrary o = new OptionalLibrary(opt);
                    if (!o.isValid())
                    {
                        // Make this more prominent to the packer?
                        System.out.println("Optional Library is invalid, must specify a name, artifact and maven");
                        continue;
                    }
                    optionals.add(o);
                }
            }
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public static String getProfileName()
    {
        return instance.versionData.getStringValue("install","profileName");
    }

    public static String getVersionTarget()
    {
        return instance.versionData.getStringValue("install","target");
    }
    public static File getLibraryPath(File root)
    {
        String path = instance.versionData.getStringValue("install","path");
        String[] split = Iterables.toArray(Splitter.on(':').omitEmptyStrings().split(path), String.class);
        File dest = root;
        Iterable<String> subSplit = Splitter.on('.').omitEmptyStrings().split(split[0]);
        for (String part : subSplit)
        {
            dest = new File(dest, part);
        }
        dest = new File(new File(dest, split[1]), split[2]);
        String fileName = split[1]+"-"+split[2]+".jar";
        return new File(dest,fileName);
    }

    public static String getModListType()
    {
        return !instance.versionData.isStringValue("install", "modList") ? "" :
                instance.versionData.getStringValue("install", "modList");
    }

    public static boolean getStripMetaInf()
    {
        try
        {
            return instance.versionData.getBooleanValue("install", "stripMeta");
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static JsonNode getVersionInfo()
    {
        return instance.versionData.getNode("versionInfo");
    }

    public static File getMinecraftFile(File path)
    {
        return new File(new File(path, getMinecraftVersion()),getMinecraftVersion()+".jar");
    }
    public static String getContainedFile()
    {
        return instance.versionData.getStringValue("install","filePath");
    }
    public static void extractFile(File path) throws IOException
    {
        instance.doFileExtract(path);
    }

    private void doFileExtract(File path) throws IOException
    {
        if (Strings.isNullOrEmpty(getContainedFile())) return;
        System.out.println("Extracting: /" + getContainedFile());
        System.out.println("To:          " + path.getAbsolutePath());
        InputStream inputStream = getClass().getResourceAsStream("/"+getContainedFile());
        OutputSupplier<FileOutputStream> outputSupplier = Files.newOutputStreamSupplier(path);
        ByteStreams.copy(inputStream, outputSupplier);
    }

    public static String getMinecraftVersion()
    {
        return instance.versionData.getStringValue("install","minecraft");
    }

    public static String getMirrorListURL()
    {
        return instance.versionData.getStringValue("install","mirrorList");
    }

    public static boolean hasMirrors()
    {
        return instance.versionData.isStringValue("install","mirrorList");
    }

    public static boolean isInheritedJson()
    {
        return instance.versionData.isStringValue("versionInfo", "inheritsFrom") &&
                instance.versionData.isStringValue("versionInfo", "jar");
    }

    public static List<OptionalLibrary> getOptionals()
    {
        return instance.optionals;
    }

    public static List<LibraryInfo> getLibraries(String marker, Predicate<String> filter)
    {
        List<LibraryInfo> ret = Lists.newArrayList();

        for (JsonNode node : instance.versionData.getArrayNode("versionInfo", "libraries"))
            ret.add(new LibraryInfo(node, marker));

        for (OptionalLibrary opt : getOptionals())
        {
            LibraryInfo info = new LibraryInfo(opt, marker);
            info.setEnabled(filter.apply(opt.getArtifact()));
            ret.add(info);
        }

        return ret;
    }
}
