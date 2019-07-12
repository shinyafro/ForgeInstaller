package net.minecraftforge.installer;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.base.Predicate;
import net.minecraftforge.installer.ForgeClasses.ClientInstall;
import net.minecraftforge.installer.ForgeClasses.OptionalLibrary;
import net.minecraftforge.installer.ForgeClasses.VersionInfo;


public class SilentInstaller
{
    private static OptionalListEntry[] optionals;
    public static void main(String[] args) throws IOException
    {
        String dir = "C:\\Users\\Shiny\\AppData\\Roaming\\.minecraft";
        String forgeVersion = "1.12.2-14.23.5.2838";
        String forgeDownload = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/"+forgeVersion+"/forge-"+forgeVersion+"-installer.jar";
        URL forge = new URL(forgeDownload);
        new VersionInfo(forge);
        install(dir, forge);
    }

    public static void install(String directory, URL forge){


        if (System.getProperty("java.net.preferIPv4Stack") == null) //This is a dirty hack, but screw it, i'm hoping this as default will fix more things then it breaks.
        {
            System.setProperty("java.net.preferIPv4Stack", "true");
        }
        System.out.println("java.net.preferIPv4Stack=" + System.getProperty("java.net.preferIPv4Stack"));

        optionals = new OptionalListEntry[VersionInfo.getOptionals().size()];
        int x = 0;
        for (OptionalLibrary opt : VersionInfo.getOptionals())
            optionals[x++] = new OptionalListEntry(opt);


        Predicate<String> optPred = input -> {
            if (optionals == null)
                return true;

            for (OptionalListEntry ent : optionals)
            {
                if (ent.lib.getArtifact().equals(input))
                    return ent.isEnabled();
            }

            return false;
        };
        File dir = new File(directory);
        ClientInstall action = new ClientInstall();
        action.run(dir, optPred);
    }

    private static class OptionalListEntry
    {
        OptionalLibrary lib;
        private boolean enabled = false;

        OptionalListEntry(OptionalLibrary lib)
        {
            this.lib = lib;
            this.enabled = lib.getDefault();
        }

        public boolean isEnabled(){ return this.enabled; }
    }

}
