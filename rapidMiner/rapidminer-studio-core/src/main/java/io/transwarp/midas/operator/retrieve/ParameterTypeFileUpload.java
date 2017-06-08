package io.transwarp.midas.operator.retrieve;

import com.rapidminer.parameter.ParameterTypeFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParameterTypeFileUpload extends ParameterTypeFile {
    public ParameterTypeFileUpload(String key, String description, String extension, boolean optional) {
        super(key, description, extension, optional);
    }

    public ParameterTypeFileUpload(String key, String description, String extension, String defaultFileName) {
        super(key, description, extension, defaultFileName);
    }

    @Override
    public Element getXMLForServer(String key, String value, boolean hideDefault, Document doc) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        File file = new File(value);
        if (!file.exists()) {
            return null;
        }
        long fileSize = file.length();
        String fileContents = "";
        try (FileInputStream fi = new FileInputStream(file)) {
            if (fileSize > Integer.MAX_VALUE) {
                throw new IOException("file size is larger than " + Integer.MAX_VALUE);
            }
            byte[] buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset != buffer.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            fileContents = new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Element element = doc.createElement("list");
        element.setAttribute("key", key);

        Element elementFileName = doc.createElement("parameter");
        elementFileName.setAttribute("key", "filePath");
        elementFileName.setAttribute("value", file.getAbsolutePath());
        element.appendChild(elementFileName);

        Element elementFileContents = doc.createElement("parameter");
        elementFileContents.setAttribute("key", "fileContents");
        elementFileContents.setAttribute("value", fileContents);
        element.appendChild(elementFileContents);

        return element;
    }

    @Override
    public List<String[]> getValueAs2DList(String value) {
        List<String[]> list = new ArrayList<>();
        if (value == null || value.trim().isEmpty()) {
            return list;
        }

        File file = new File(value);
        if (!file.exists()) {
            return list;
        }
        long fileSize = file.length();
        String fileContents = "";
        try (FileInputStream fi = new FileInputStream(file)) {
            if (fileSize > Integer.MAX_VALUE) {
                throw new IOException("file size is larger than " + Integer.MAX_VALUE);
            }
            byte[] buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset != buffer.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            fileContents = new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        list.add(new String[]{"filePath", file.getAbsolutePath()});
        list.add(new String[]{"fileContents", fileContents});

        return list;
    }
}
