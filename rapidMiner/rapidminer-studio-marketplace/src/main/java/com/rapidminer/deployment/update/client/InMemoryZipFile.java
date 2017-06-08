package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InMemoryZipFile {
    private Map<String, byte[]> contents = new HashMap();

    public InMemoryZipFile(byte[] zipBuffer) throws IOException {
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(zipBuffer));

        while(true) {
            ZipEntry entry;
            do {
                if((entry = zin.getNextEntry()) == null) {
                    zin.close();
                    return;
                }
            } while(entry.isDirectory());

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] buf = new byte[10240];

            int length;
            while((length = zin.read(buf)) != -1) {
                buffer.write(buf, 0, length);
            }

            zin.closeEntry();
            this.contents.put(entry.getName(), buffer.toByteArray());
        }
    }

    public Set<String> entryNames() {
        return this.contents.keySet();
    }

    public byte[] getContents(String name) {
        return (byte[])this.contents.get(name);
    }

    public boolean containsEntry(String name) {
        return this.contents.containsKey(name);
    }
}
