package net.minecraftforge.installer.ForgeClasses;

public interface IMonitor {
    void setMaximum(int max);
    void setNote(String note);
    void setProgress(int progress);
    void close();
}
