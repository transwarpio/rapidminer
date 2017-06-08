package io.transwarp.midas.ui;


import com.rapidminer.io.process.XMLTools;
import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.XMLException;
import io.transwarp.midas.result.JobStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import scala.Int;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class RemoteResultOverviewItemExport {

    private static Element makeTestcase(Document document, JobStatus status, String name) {
        Element testcase = document.createElement("testcase");
        testcase.setAttribute("classname", "MidasTest");
        testcase.setAttribute("name", name);
        testcase.setAttribute("time", Long.toString((status.endTime() - status.startTime()) / 1000));
        if (status.isFailed()) {
            Element failure = document.createElement("failure");
            failure.setAttribute("message",
                    status.error().split(System.getProperty("line.separator"))[0]);
            failure.setAttribute("type", "failure");
            failure.setTextContent(status.error());
            testcase.appendChild(failure);
        }
        return testcase;
    }

    public static void export(Collection<RemoteResultOverviewItem> items) {
        try {
            File reportDir = new File(FileSystemService.getRapidMinerHome(), "report");
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            int fails = 0;
            for (RemoteResultOverviewItem item: items) {
                if (item.getStatus().isFailed()) {
                    fails++;
                }
            }


            String time = new SimpleDateFormat("yy.MM.dd-HH.mm.ss").format(new Date());
            File reportXml = new File(reportDir,
                    "test-" + time + ".xml");
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            document.setXmlVersion("1.0");
            Element testSuites = document.createElement("testsuites");
            Element testSuite = document.createElement("testsuite");
            testSuite.setAttribute("name", "MidasTestSuite");
            testSuite.setAttribute("tests", Integer.toString(items.size()));
            testSuite.setAttribute("failures", Integer.toString(fails));
            testSuite.setAttribute("timestamp", time);

            for (RemoteResultOverviewItem item: items) {
                testSuite.appendChild(makeTestcase(document, item.getStatus(), item.getProcessName()));
            }

            testSuites.appendChild(testSuite);
            document.appendChild(testSuites);
            XMLTools.stream(document, reportXml, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XMLException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
