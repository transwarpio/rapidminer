package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.startup.NewsItem;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.license.License;
import com.rapidminer.tools.AbstractObservable;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Observer;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.nexus.NexusUtilities;
import com.rapidminer.tools.usagestats.UsageStatistics;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

enum NewsService {
    INSTANCE;

    private static final String NEWS_URL;
    private static final Object UPDATE_LOCK;
    private static final long DEPRECATION_TIME = 86400000L;
    private static final String PROGRESS_THREAD_ID = "load_news";
    private long lastUpdate;
    private boolean forceUpdate;
    private boolean firstQuery = true;
    private List<NewsItem> cachedNewsItems;
    private NewsService.NewsItemObservable newsItemObservable = null;
    private ProgressThread newsItemQuery = new ProgressThread("load_news", false) {
        public void run() {
            synchronized(NewsService.UPDATE_LOCK) {
                if(NewsService.this.isUpdateNecessary()) {
                    List queryResult = NewsService.this.queryNewsItems();
                    if(queryResult != null) {
                        NewsService.this.cachedNewsItems = queryResult;
                        NewsService.this.lastUpdate = System.currentTimeMillis();
                        NewsService.this.forceUpdate = false;
                        NewsService.this.newsItemObservable.fireUpdate();
                    }
                }

            }
        }
    };

    private NewsService() {
        this.newsItemQuery.setIndeterminate(true);
        this.newsItemQuery.addDependency(new String[]{"load_news"});
    }

    private List<NewsItem> queryNewsItems() {
        NewsItem[] result = null;
        boolean error = false;

        try {
            String e = NEWS_URL;
            if(this.firstQuery) {
                License activeLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
                boolean queryAdded = false;
                if(activeLicense != null && activeLicense.getLicenseID() != null) {
                    e = e + "?lid=" + activeLicense.getLicenseID();
                    queryAdded = true;
                }

                String clientId = UsageStatistics.getInstance().getUserKey();
                if(clientId != null && !clientId.trim().isEmpty() && "always".equals(ParameterService.getParameterValue("rapidminer.gui.transfer_usagestats"))) {
                    e = e + (queryAdded?"&":"?") + "cid=" + clientId;
                }
            }

            result = readNewsFromUrl(e);
        } catch (IOException var7) {
            error = true;
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.startup.NewsService.news_load_failed", var7.getMessage());
        }

        if(!error) {
            this.firstQuery = false;
        }

        return result != null?Arrays.asList(result):null;
    }

    private static NewsItem[] readNewsFromUrl(String urlString) throws IOException {
        String contentString;
        try {
            contentString = Tools.readTextFile((new URL(urlString)).openStream());
        } catch (RuntimeException var3) {
            throw new IOException(var3);
        }

        return contentString != null && !contentString.trim().isEmpty()?(NewsItem[])NexusUtilities.parseJacksonString(contentString, NewsItem[].class):new NewsItem[0];
    }

    public List<NewsItem> getNewsItems() {
        if(this.cachedNewsItems == null || this.isUpdateNecessary()) {
            this.newsItemQuery.start();
        }

        return (List)(this.cachedNewsItems != null?new LinkedList(this.cachedNewsItems):Collections.emptyList());
    }

    private boolean isUpdateNecessary() {
        if(this.lastUpdate > 0L && !this.forceUpdate) {
            long diff = System.currentTimeMillis() - this.lastUpdate;
            return diff > 86400000L;
        } else {
            return true;
        }
    }

    public void forceUpdate() {
        this.forceUpdate = true;
    }

    public void addNewsItemObserver(Observer<List<NewsItem>> observer) {
        this.newsItemObservable.addObserver(observer, false);
    }

    public void removeNewsItemObserver(Observer<List<NewsItem>> observer) {
        this.newsItemObservable.removeObserver(observer);
    }

    static {
        NEWS_URL = I18N.getMessage(I18N.getGUIBundle(), "gui.label.news.json.url", new Object[0]);
        UPDATE_LOCK = new Object();
    }

    private class NewsItemObservable extends AbstractObservable<List<NewsItem>> {
        private NewsItemObservable() {
        }

        public void fireUpdate() {
            this.fireUpdate(new ArrayList(NewsService.this.cachedNewsItems));
        }
    }
}
