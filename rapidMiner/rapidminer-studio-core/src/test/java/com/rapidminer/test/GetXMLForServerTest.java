package com.rapidminer.test;

import io.transwarp.midas.operator.retrieve.ParameterTypeFileUpload;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class GetXMLForServerTest {

    String testLine = "select * from banana";
    String FileName = "/tmp/t.txt";

    @Before
    public void before() {
        try (PrintWriter pw = new PrintWriter(FileName)) {
            pw.print(testLine);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @After
    public void after() {
        File f = new File(FileName);
        f.delete();
    }

    @Test
    public void testGetXMLForServer() {
        ParameterTypeFileUpload param =
                new ParameterTypeFileUpload("queryFile", "sql query file", "sql", "");
        try {
            String queryFile = "queryFile";
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element element = param.getXMLForServer(queryFile, FileName, false, doc);
            assertEquals(element.getTagName(), "list");
            assertEquals(element.getAttribute("key"), queryFile);

            Element filePath = (Element)element.getChildNodes().item(0);
            assertEquals(filePath.getAttribute("key"), "filePath");
            assertEquals(filePath.getAttribute("value"), FileName);

            Element fileContents = (Element)element.getChildNodes().item(1);
            assertEquals(fileContents.getAttribute("key"), "fileContents");
            assertEquals(fileContents.getAttribute("value"), testLine);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }

    @Test
    public void testGetXMLForServerFileNotExist() {
        ParameterTypeFileUpload param =
                new ParameterTypeFileUpload("queryFile", "sql query file", "sql", "");
        try {
            String queryFile = "queryFile";
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element element = param.getXMLForServer(queryFile, "b.txt", false, doc);
            assertNull(element);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
