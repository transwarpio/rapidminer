package com.rapidminer.tools;

import com.rapidminer.Process;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.io.process.XMLTools;
import com.rapidminer.operator.*;
import com.rapidminer.tools.documentation.OperatorDocBundle;
import com.rapidminer.tools.documentation.OperatorDocumentation;
import io.transwarp.midas.operator.UserDefinedOp;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;

public class UserDefinedOperatorService {
    private static String prefix = "remote";
    private static String icon = "table.png";
    private static String UserDefinedOperatorXML = "MidasUserDefinedOperatorsCore.xml";
    private static String UserDefinedOperatorDocXML = "MidasUserDefinedOperatorsCoreDoc";
    private static String UserDefinedOperatorDocXMLFile = UserDefinedOperatorDocXML + "_zh.xml";
    private static String ioErrorKey = "remote.user_defined.io.error";
    private static String extensionKey = "remote.extension";
    private static OperatorDocBundle doc = new OperatorDocBundle();
    private static Map<String, OperatorDescription> udops = new HashMap<>();

    private static String getPrefix() {
        return groupJoin(prefix, I18N.getGUILabel(extensionKey));
    }

    public static void init() {
        try {
            File userDefinedOperatorXMLFile = new File(FileSystemService.getUserDefinedOperatorHome(),
                    UserDefinedOperatorXML);
            if (userDefinedOperatorXMLFile.exists()) {
                registerOperators(userDefinedOperatorXMLFile.toURI().toURL());
            }
        } catch (IOException e) {
            LogService.getRoot().log(Level.WARNING, e.getMessage());
        }
    }

    private static List<String> getOperatorKeys(Document document) {
        return getOperatorKeys(document.getDocumentElement());
    }

    private static List<String> getOperatorKeys(Node element) {
        List<String> list = new ArrayList<>();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("group")) {
                list.addAll(getOperatorKeys(nodes.item(i)));
            } else if (node.getNodeName().equals("operator")) {
                list.add(node.getChildNodes().item(1).getTextContent());
            }
        }
        return list;
    }

    private static void registerOperators(URL url) throws IOException {
        URL[] urls = { FileSystemService.getUserDefinedOperatorHome().toURI().toURL() };
        try (InputStream inputStream = url.openStream();
             InputStream inputStreamAgain = url.openStream()) {
            OperatorService.registerOperators(OperatorService.OPERATORS_XML, inputStream,
                        new URLClassLoader(urls), null);
            List<String> keys = getOperatorKeys(XMLTools.parse(inputStreamAgain));
            for (String key: keys) {
                OperatorDescription description = OperatorService.getOperatorDescription(key);
                udops.put(getFullKey(description), description);
            }
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static Collection<OperatorDescription> getAllUserDefinedOperators() {
        return udops.values();
    }

    public static boolean isUserDefined(OperatorDescription description) {
        return udops.containsKey(getFullKey(description));
    }

    private static void saveOperatorDocBundle(File file) {
        try {
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            document.setXmlVersion("1.0");
            document.setXmlStandalone(false);
            Attr attr = document.createAttribute("encoding");
            attr.setValue("UTF-8");

            Element operatorHelp = document.createElement("operatorHelp");
            for (OperatorDescription deac: getAllUserDefinedOperators()) {
                addOperatorDoc(document, operatorHelp, deac.getName(), deac.getKey());
            }
            document.appendChild(operatorHelp);

            XMLTools.stream(document, file, Charset.forName("UTF-8"));
        } catch (ParserConfigurationException | XMLException e) {
            SwingTools.showSimpleErrorMessage(ioErrorKey, e);
        }
    }

    private static void saveOperators(File file) {
        try {
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            document.setXmlVersion("1.0");
            document.setXmlStandalone(false);
            Attr attr = document.createAttribute("encoding");
            attr.setValue("UTF-8");

            Element operators = document.createElement("operators");
            operators.setAttribute("name", "user_defined");
            operators.setAttribute("version", "5.0");
            operators.setAttribute("docbundle", UserDefinedOperatorDocXML);

            for (OperatorDescription deac: getAllUserDefinedOperators()) {
                addOperator(document, operators, deac);
            }
            document.appendChild(operators);
            XMLTools.stream(document, file, Charset.forName("UTF-8"));
        } catch (ParserConfigurationException | XMLException e) {
            SwingTools.showSimpleErrorMessage(ioErrorKey, e);
        }
    }

    private static Element getOrCreateGroup(Element root, List<String> groups) {
        if (!groups.isEmpty()) {
            String group = groups.get(0);
            NodeList list = root.getChildNodes();
            Element element = null;
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                if (e.getNodeName().equals("group") && e.getAttribute("key").equals(group)) {
                    element = e;
                }
            }

            if (element == null) {
                element = root.getOwnerDocument().createElement("group");
                element.setAttribute("key", group);
                root.appendChild(element);

            }
            return getOrCreateGroup(element, groups.size() == 1 ? Collections.<String>emptyList(): groups.subList(1, groups.size()));
        } else {
            return root;
        }
    }

    private static void addOperator(Document document, Element root, OperatorDescription deac) {
        Element operator = document.createElement("operator");
        Element key = document.createElement("key");
        key.setTextContent(deac.getKey());
        Element clazz = document.createElement("class");
        clazz.setTextContent(UserDefinedOp.class.getCanonicalName());
        Element iconEle = document.createElement("icon");
        iconEle.setTextContent(icon);

        operator.appendChild(key);
        operator.appendChild(clazz);
        operator.appendChild(iconEle);
        getOrCreateGroup(root, Arrays.asList(deac.getGroup().split("\\."))).appendChild(operator);
    }

    private static void addOperatorDoc(Document document, Element group, String name, String key) {
        Element operator = document.createElement("operator");

        Element keyEle = document.createElement("key");
        keyEle.setTextContent(key);
        Element help = document.createElement("help");
        help.setTextContent(name);
        Element synopsis = document.createElement("synopsis");
        synopsis.setTextContent(name);
        Element nameEle = document.createElement("name");
        nameEle.setTextContent(name);

        operator.appendChild(nameEle);
        operator.appendChild(help);
        operator.appendChild(keyEle);
        operator.appendChild(synopsis);
        group.appendChild(operator);
    }

    public static Operator getUserDefinedOperator(OperatorDescription description) {
        Reader reader = null;
        try {
            File processFile = new File(FileSystemService.getUserDefinedOperatorHome(), getFullKey(description) + ".rmp");
            Process process = new Process();
            reader = new FileReader(processFile);
            process.readProcess(reader);
            ExecutionUnit ex = process.getRootOperator().getSubprocess(0);
            Operator targetOp = ex.getOperators().get(0);
            targetOp.setEnclosingProcess(null);
            return targetOp;
        } catch (IOException | XMLException e) {
            SwingTools.showSimpleErrorMessage(ioErrorKey, e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    SwingTools.showSimpleErrorMessage(ioErrorKey, e);
                }
            }
        }
    }

    private static String getFullKey(OperatorDescription description) {
        return description.getGroup() + "." + description.getName();
    }

    private static String groupJoin(String base, String ext) {
        List<String> bs = Arrays.asList(base.split("\\."));
        List<String> es = Arrays.asList(ext.split("\\."));
        List<String> all = new ArrayList<>();
        all.addAll(bs);
        all.addAll(es);
        boolean flag = false;
        StringBuilder result=new StringBuilder();
        for (String string : all) {
            if (flag) {
                result.append(".");
            } else {
                flag=true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static void createUserDefinedOperator(String name, Operator op, String group) {
        if (OperatorService.getOperatorKeys().contains(name)) {
            throw new IllegalArgumentException("operator name " + name +" exists");
        }

        group = groupJoin(getPrefix(), group);

        Operator clonedSelectedOperator = (op.cloneOperator(op.getName(), false));
        clonedSelectedOperator.setEnclosingProcess(null);
        Process p = new Process();
        p.getRootOperator().getSubprocess(0).addOperator(clonedSelectedOperator);

        OperatorDocumentation opDoc = new OperatorDocumentation(name);
        opDoc.setSynopsis(name);
        opDoc.setDocumentation(name);

        OperatorDescription operatorDescription = new OperatorDescription(group, name,
                UserDefinedOp.class, null, icon, null, doc);
        try {
            OperatorService.registerOperator(operatorDescription, doc);
            udops.put(getFullKey(operatorDescription), operatorDescription);
            Writer writer = null;
            try {
                writer = new PrintWriter(
                        new File(FileSystemService.getUserDefinedOperatorHome(), getFullKey(operatorDescription) + ".rmp"));
                p.getRootOperator().writeXML(writer, false);

            } catch (IOException e1) {
                SwingTools.showSimpleErrorMessage(ioErrorKey, e1);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e1) {
                        SwingTools.showSimpleErrorMessage(ioErrorKey, e1);
                    }
                }
            }
            saveUserDefinedOperators();

        } catch (OperatorCreationException e) {
            SwingTools.showSimpleErrorMessage(ioErrorKey, e);
        }
    }

    public static void delUserDefinedOperators(OperatorDescription operatorDescription) {
        try {
            OperatorService.unregisterOperator(operatorDescription);
            new File(FileSystemService.getUserDefinedOperatorHome(),
                    getFullKey(operatorDescription) + ".rmp").delete();
            udops.remove(getFullKey(operatorDescription));
            UserDefinedOperatorService.saveUserDefinedOperators();
        } catch (IOException e) {
            SwingTools.showSimpleErrorMessage(ioErrorKey, e);
        }
    }

    private static void saveUserDefinedOperators() {
        try {
            saveOperatorDocBundle(new File(FileSystemService.getUserDefinedOperatorHome(),
                    UserDefinedOperatorDocXMLFile));
            saveOperators(new File(FileSystemService.getUserDefinedOperatorHome(),
                    UserDefinedOperatorXML));
        } catch (IOException e) {
            SwingTools.showSimpleErrorMessage(ioErrorKey, e);
        }
    }
}
