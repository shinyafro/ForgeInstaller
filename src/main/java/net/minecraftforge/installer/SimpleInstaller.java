package net.minecraftforge.installer;
import java.io.File;
import java.io.IOException;

import com.google.common.base.Predicate;


public class SimpleInstaller
{
    private static OptionalListEntry[] optionals;
    public static void main(String[] args) throws IOException
    {
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
        File dir = new File("E:\\mc clients\\vanilla");
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
        public void setEnabled(boolean v){ this.enabled = v; }
    }

}
