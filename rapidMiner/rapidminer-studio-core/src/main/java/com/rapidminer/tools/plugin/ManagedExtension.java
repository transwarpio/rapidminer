/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.tools.plugin;

import com.rapidminer.RapidMiner;
import com.rapidminer.io.process.XMLTools;
import com.rapidminer.tools.*;
import com.rapidminer.tools.update.internal.UpdateManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarFile;
import java.util.logging.Level;


/**
 *
 * @author Simon Fischer
 *
 */
public class ManagedExtension {

	/** Maps {@link ManagedExtension#getPackageId()} to the ManagedExtension itself. */
	private static final Map<String, ManagedExtension> MANAGED_EXTENSIONS = new HashMap<>();

	private final SortedSet<String> installedVersions = new TreeSet<>(new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			return normalizeVersion(o1).compareTo(normalizeVersion(o2));
		}
	});
	private final String packageID;
	private final String name;
	private String selectedVersion;
	private boolean active;
	private boolean installedInHomeDir;
	private final String license;

	private ManagedExtension(Element element, boolean homeDir) {
		this.installedInHomeDir = homeDir;
		this.packageID = XMLTools.getTagContents(element, "id");
		this.name = XMLTools.getTagContents(element, "name");
		this.license = XMLTools.getTagContents(element, "license");
		this.active = Boolean.parseBoolean(XMLTools.getTagContents(element, "active"));
		this.selectedVersion = XMLTools.getTagContents(element, "selected-version");
		NodeList versions = element.getElementsByTagName("installed-version");
		for (int i = 0; i < versions.getLength(); i++) {
			installedVersions.add(((Element) versions.item(i)).getTextContent());
		}
	}

	private ManagedExtension(String id, String name, String license) {
		super();
		this.packageID = id;
		this.name = name;
		this.license = license;
		this.selectedVersion = null;
		// installedVersions.add(version);
		this.setActive(true);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public String getName() {
		return name;
	}

	private File findFile() {
		return findFile(selectedVersion);
	}

	private File findFile(String version) {
		for (File dir : getManagedExtensionsDirectories()) {
			File file = new File(dir, packageID + "-" + version + ".jar");
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

	/**
	 * This method returns the jar file of the extension or throws an {@link FileNotFoundException}
	 * exception.
	 */
	public JarFile findArchive() throws IOException {
		File findFile = findFile();
		if (findFile != null) {
			return new JarFile(findFile);
		}
		throw new FileNotFoundException("Could not access file of installed extension.");
	}

	public JarFile findArchive(String version) throws IOException {
		File findFile = findFile(version);
		if (findFile == null) {
			throw new IOException("Failed to find extension jar file (extension " + getName() + ", version " + version
					+ ").");
		} else {
			try {
				return new JarFile(findFile);
			} catch (IOException e) {
				throw new IOException("Failed to open jar file " + findFile + ": " + e, e);
			}
		}
	}

	public String getSelectedVersion() {
		return selectedVersion;
	}

	private static File[] getManagedExtensionsDirectories() {
		File local = getUserExtensionsDir();
		try {
			File global = getGlobalExtensionsDir();
			return new File[] { global, local };
		} catch (IOException e) {
			LogService.getRoot()
			.log(Level.WARNING,
					"com.rapid_i.deployment.update.client.ManagedExtension.no_properties_set",
					new Object[] { RapidMiner.PROPERTY_RAPIDMINER_INIT_PLUGINS,
					PlatformUtilities.PROPERTY_RAPIDMINER_HOME });

			return new File[] { local };
		}
	}

	private static File getGlobalExtensionsDir() throws IOException {
		return new File(Plugin.getPluginLocation(), "managed");
	}

	public static File getUserExtensionsDir() {
		return FileSystemService.getUserConfigFile("managed");
	}

	private Element toXML(Document doc) {
		Element result = doc.createElement("extension");
		XMLTools.setTagContents(result, "id", packageID);
		XMLTools.setTagContents(result, "name", name);
		XMLTools.setTagContents(result, "active", "" + active);
		XMLTools.setTagContents(result, "license", license);
		XMLTools.setTagContents(result, "selected-version", getSelectedVersion());
		for (String v : installedVersions) {
			Element elem = doc.createElement("installed-version");
			result.appendChild(elem);
			elem.appendChild(doc.createTextNode(v));
		}
		return result;
	}

	private static Document toXML(boolean inHomeDir) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = doc.createElement("extensions");
		doc.appendChild(root);
		for (ManagedExtension ext : MANAGED_EXTENSIONS.values()) {
			if (ext.installedInHomeDir == inHomeDir) {
				root.appendChild(ext.toXML(doc));
			}
		}
		return doc;
	}

	public static void saveConfiguration() {
		try {
			File localDir = getUserExtensionsDir();
			if (!localDir.exists()) {
				localDir.mkdirs();
			}
			XMLTools.stream(toXML(true), new File(localDir, "extensions.xml"), Charset.forName("UTF-8"));
		} catch (Exception e) {
			// LogService.getRoot().log(Level.WARNING, "Cannot save local user extensions: "+e, e);
			LogService.getRoot().log(
					Level.WARNING,
					I18N.getMessage(LogService.getRoot().getResourceBundle(),
							"com.rapid_i.deployment.update.client.ManagedExtension.saving_local_user_extensions_error", e),
					e);

		}
		try {
			File globalDir = getGlobalExtensionsDir();
			if (!globalDir.exists()) {
				globalDir.mkdirs();
			}
			XMLTools.stream(toXML(false), new File(globalDir, "extensions.xml"), Charset.forName("UTF-8"));
		} catch (Exception e) {
			// LogService.getRoot().log(Level.WARNING, "Cannot save global extensions: "+e, e);
			LogService.getRoot().log(
					Level.WARNING,
					I18N.getMessage(LogService.getRoot().getResourceBundle(),
							"com.rapid_i.deployment.update.client.ManagedExtension.saving_global_extensions_error", e), e);
		}
		// LogService.getRoot().config("Saved extension state.");
		LogService.getRoot()
				.log(Level.CONFIG, "com.rapid_i.deployment.update.client.ManagedExtension.saved_extension_state");
	}

	/** Reads configuration files. */
	private static void readConfiguration() {
		MANAGED_EXTENSIONS.clear();
		try {
			File file = new File(getUserExtensionsDir(), "extensions.xml");
			if (file.exists()) {
				parse(XMLTools.parse(file), true);
			}
		} catch (Exception e) {
			// LogService.getRoot().log(Level.WARNING, "Failed to read local extension state: "+e,
			// e);
			LogService
					.getRoot()
					.log(Level.WARNING,
							I18N.getMessage(
									LogService.getRoot().getResourceBundle(),
									"com.rapid_i.deployment.update.client.ManagedExtension.reading_local_extensions_state_error",
									e), e);
		}
		try {
			File file = new File(getGlobalExtensionsDir(), "extensions.xml");
			if (file.exists()) {
				parse(XMLTools.parse(file), false);
			}
		} catch (Exception e) {
			// LogService.getRoot().log(Level.WARNING, "Failed to read global extension state: "+e,
			// e);
			LogService
					.getRoot()
					.log(Level.WARNING,
							I18N.getMessage(
									LogService.getRoot().getResourceBundle(),
									"com.rapid_i.deployment.update.client.ManagedExtension.reading_global_extensions_state_error",
									e), e);
		}
		// LogService.getRoot().config("Read extension state.");
		LogService.getRoot().log(Level.CONFIG, "com.rapid_i.deployment.update.client.ManagedExtension.read_extansion_state");
	}

	private static void parse(Document parse, boolean inHomeDir) {
		NodeList extensions = parse.getDocumentElement().getElementsByTagName("extension");
		for (int i = 0; i < extensions.getLength(); i++) {
			register(new ManagedExtension((Element) extensions.item(i), inHomeDir));
		}
	}

	private static void register(ManagedExtension ext) {
		MANAGED_EXTENSIONS.put(ext.packageID, ext);
	}

	public static List<File> getActivePluginJars() {
		List<File> result = new LinkedList<>();
		for (ManagedExtension ext : MANAGED_EXTENSIONS.values()) {
			if (ext.isActive()) {
				File file = ext.findFile();
				if (file != null) {
					result.add(file);
				}
			}
		}
		return result;
	}

	public static ManagedExtension get(String packageId) {
		return MANAGED_EXTENSIONS.get(packageId);
	}

	public static ManagedExtension getOrCreate(String packageId, String packageName, String license) {
		ManagedExtension ext = MANAGED_EXTENSIONS.get(packageId);
		if (ext == null) {
			ext = new ManagedExtension(packageId, packageName, license);
			ext.installedInHomeDir = isInstallToHome();
			MANAGED_EXTENSIONS.put(packageId, ext);
			saveConfiguration();
		}
		return ext;
	}

	public static ManagedExtension remove(String packageId) {
		return MANAGED_EXTENSIONS.remove(packageId);
	}

	public String getPackageId() {
		return packageID;
	}

	public void addAndSelectVersion(String version) {
		this.selectedVersion = version;
		installedVersions.add(version);
		saveConfiguration();
	}

	public File getDestinationFile(String version) throws IOException {
		if (installedInHomeDir) {
			return new File(getUserExtensionsDir(), packageID + "-" + version + ".jar");
		} else {
			makeGlobalManagedExtensionsDir();
			return new File(getGlobalExtensionsDir(), packageID + "-" + version + ".jar");
		}
	}

	private static void makeGlobalManagedExtensionsDir() throws IOException {
		File managedDir = getGlobalExtensionsDir();
		if (!managedDir.exists()) {
			if (!managedDir.mkdirs()) {
				throw new IOException("Cannot create directory " + managedDir
						+ ". Make sure you have administrator privileges or check property "
						+ UpdateManager.PARAMETER_INSTALL_TO_HOME + " in the preferences.");
			}
		}
	}

	private static boolean isInstallToHome() {
		return !"false".equals(ParameterService.getParameterValue(UpdateManager.PARAMETER_INSTALL_TO_HOME));
	}

	public static void init() {
		readConfiguration();
	}

	public static Collection<ManagedExtension> getAll() {
		return MANAGED_EXTENSIONS.values();
	}

	public Set<String> getInstalledVersions() {
		return installedVersions;
	}

	public void setSelectedVersion(String version) {
		this.selectedVersion = version;
	}

	public String getLatestInstalledVersionBefore(String version) {
		SortedSet<String> head = installedVersions.headSet(version);
		return head.isEmpty() ? null : head.last();
	}

	public String getLatestInstalledVersion() {
		return installedVersions.isEmpty() ? null : installedVersions.last();
	}

	/**
	 * Adds leading zeroes until the version is of the form "XX.XX.XXX".
	 */
	public static String normalizeVersion(String version) {
		if (version == null) {
			return null;
		}
		String[] split = version.split("\\.");
		if (split.length < 3) {
			String[] newSplit = new String[3];
			System.arraycopy(split, 0, newSplit, 0, split.length);
			for (int i = split.length; i < newSplit.length; i++) {
				newSplit[i] = "0";
			}
			split = newSplit;
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
			int lastDigit;
			for (lastDigit = split[i].length() - 1; lastDigit >= 0; lastDigit--) {
				if (Character.isDigit(split[i].charAt(lastDigit))) {
					break;
				}
			}
			String letters = split[i].substring(lastDigit + 1);
			String digits = split[i].substring(0, lastDigit + 1);
			int desiredLength = i == split.length - 1 ? 3 : 2;
			while (digits.length() < desiredLength) {
				digits = "0" + digits;
			}
			if (i != 0) {
				result.append('.');
			}
			result.append(digits).append(letters);
		}
		return result.toString();
	}

	private static Collection<ManagedExtension> getActiveByLicense(String license) {
		List<ManagedExtension> result = new LinkedList<>();
		for (ManagedExtension ext : MANAGED_EXTENSIONS.values()) {
			if (ext.isActive() && ext.license != null && ext.license.equals(license)) {
				result.add(ext);
			}
		}
		return result;
	}

	/** Returns true if uninstall was successful. */
	public boolean uninstallActiveVersion() {
		File file = findFile(selectedVersion);
		// we only mark as uninstalled if
		// (1) File does not exist, probably was removed manually
		// (2) We were able to remove it (requires administrator permissions if installed globally).
		if (file != null && file.exists()) {
			file.delete();
		}
		installedVersions.remove(selectedVersion);
		selectedVersion = null;
		active = false;
		if (installedVersions.isEmpty()) {
			MANAGED_EXTENSIONS.remove(this.getPackageId());
		}
		saveConfiguration();
		return true;
	}
}
