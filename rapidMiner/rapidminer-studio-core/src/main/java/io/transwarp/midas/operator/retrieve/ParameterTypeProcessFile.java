package io.transwarp.midas.operator.retrieve;

import com.rapidminer.Process;
import com.rapidminer.operator.ExecutionUnit;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.tools.UserDefinedOperatorService;
import com.rapidminer.tools.XMLException;
import io.transwarp.midas.MidasJsonExporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParameterTypeProcessFile extends ParameterTypeFile {
    public ParameterTypeProcessFile(String key, String description, String extension, boolean optional) {
        super(key, description, extension, optional);
    }

    @Override
    public String getValueString(String value) {
        if (value == null) {
            return null;
        } else {
            File file = new File(value);
            if (!file.exists()) {
                return null;
            } else {

                FileReader reader = null;
                try {
                    Process process = new Process();
                    reader = new FileReader(file);
                    process.readProcess(reader);
                    return new MidasJsonExporter().export(process.getRootOperator());
                } catch (XMLException | IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        }
    }
}
