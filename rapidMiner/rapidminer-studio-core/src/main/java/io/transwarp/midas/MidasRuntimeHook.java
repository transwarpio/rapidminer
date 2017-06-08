package io.transwarp.midas;

import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.ObjectVisualizerService;
import com.rapidminer.tools.ParameterService;
import io.transwarp.midas.adaptor.AdaptorRegistry;
import io.transwarp.midas.client.ClientConfig;
import io.transwarp.midas.client.CustomOpManager;
import io.transwarp.midas.client.MidasClient;
import io.transwarp.midas.client.MidasClientFactory;

import java.io.File;

public class MidasRuntimeHook {
    public static void init() {
        AdaptorRegistry.setFrontEndFactory(new MidasFrontendFactory());
        AdaptorRegistry.setParameterService(new ParameterService());
        AdaptorRegistry.setVisualizer(new ObjectVisualizerService());

        File sessionFile = FileSystemService.getUserConfigFile("midas.session");
        File ownerFile = FileSystemService.getUserConfigFile("midas.owner");
        MidasClientFactory.addConf("owner", ownerFile.getAbsolutePath());
        MidasClientFactory.addConf("session", sessionFile.getAbsolutePath());

        CustomOpManager.load(FileSystemService.getUserConfigFile("custom_op.xml"));
    }

    public static void setMidasHome(String home) {
        ClientConfig.setMidasClientHome(home);
    }
}
