/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mangacrawler;

import static com.gargoylesoftware.htmlunit.BrowserVersion.CHROME;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.mina.bo.Chapter;
import com.mina.bo.Files;
import com.mina.bo.Links;
import com.mina.bo.Manga;
import com.mina.util.HibernateUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Mina Mimi
 */
public class MangaCrawler {

    final static Logger logger = Logger.getLogger(MangaCrawler.class);

    private static String ROOT_DIR = "";
    private static int TPS = 100;

    static {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        logger.info("Starting...");
        //Load config
        try (InputStream input = MangaCrawler.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);
            ROOT_DIR = prop.getProperty("ROOT_DIR");
            TPS = Integer.parseInt(prop.getProperty("TPS"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        MangaCrawler articles_crawler = new MangaCrawler();
        articles_crawler.doUpdateChapterContent();
//        articles_crawler.DownloadImage("https://s51.mkklcdnv6tempv2.com/mangakakalot/y2/yume_maboroshi_no_gotoku/chapter_58_single_combat/19.jpg", "C:\\running_projects\\manga\\out\\test333.jpg");

    }

    ExecutorService executors = Executors.newFixedThreadPool(10);
    ExecutorService executorsDownload = Executors.newFixedThreadPool(10);

    public static Object _lock = new Object();
    public static int TOTAL_IMAGES = 0;
    public static int DOWNLOADED_IMAGES = 0;

    public static long TOTAL_MANGA = 0;
    public static int DOWNLOADED = 0;
    public static int DOWNLOADING = 0;

    private void doCrawler() {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.openSession();

            //Tinh tong so manga
            Query query = session.createQuery("select count(*) from Links where link not like '%chapter%' and Status is null");
            Long count = (Long) query.uniqueResult();
            TOTAL_MANGA = count;
            logger.info("=================TOTAL MANGA:" + count + ".Total paging:" + (count / TPS + 1));
            int pageCount = (int) (count / TPS + 1);
            for (int k = 0; k < pageCount; k++) {
                tx = session.beginTransaction();
                query = session.createQuery("from Links where link not like '%chapter%' and Status is null order by id");
                List<Links> lstLinkManga = query.setFirstResult(k * TPS).setMaxResults(k * TPS + TPS).list();
                for (int i = 0; i < lstLinkManga.size(); i++) {
                    executors.submit(new Worker(lstLinkManga.get(i)));
                    lstLinkManga.get(i).setStatus(1);
                    session.saveOrUpdate(lstLinkManga.get(i));
                    session.flush();
                    logger.info("========================Downloading:" + (++DOWNLOADING) + "/" + TOTAL_MANGA + ".PAGE:" + k);
                }
                tx.commit();
            }

        } catch (Exception ex) {
            logger.error(ex);

        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    private void doUpdateChapterContent() {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.openSession();

            //Tinh tong so manga
            Query query = session.createQuery("select count(*) from Chapter where Status is null");
            Long count = (Long) query.uniqueResult();

            logger.info("=================TOTAL CHAPTER:" + count + ".Total paging:" + (count / TPS + 1));
            int pageCount = (int) (count / TPS + 1);
            for (int k = 0; k < pageCount; k++) {
                try {

                    tx = session.beginTransaction();
                    query = session.createQuery("from Chapter where Status is null");
                    List<Chapter> lstLinkManga = query.setMaxResults(TPS).list();
                    for (int i = 0; i < lstLinkManga.size(); i++) {
                        try {
                            executors.submit(new WorkerUpdateContentOfChapter(lstLinkManga.get(i)));
                            lstLinkManga.get(i).setStatus(1);
                            session.saveOrUpdate(lstLinkManga.get(i));
                            session.flush();
                            logger.info("==========Downloading:" + (++DOWNLOADING) + "/" + count + ".Downloaded:" + DOWNLOADED);
                        } catch (Exception e) {
                            logger.error(e);
                        }
                    }
                    tx.commit();
                    while (true) {
                        if (DOWNLOADING - DOWNLOADED >= 2 * TPS) {
                            logger.info("DOWNLOADING:" + DOWNLOADING + "-DOWNLOADED:" + DOWNLOADED);
                            logger.info("WAITTTTTTTTTTTTTTTTTTTTTTTTTTTTTT>>>>>>>>>>>>>>>>>>");
                            Thread.sleep(1000);
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }

        } catch (Exception ex) {
            logger.error(ex);

        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    private class Worker implements Runnable {

        private Links linkManga;

        public Worker(Links linkManga) {
            this.linkManga = linkManga;
        }

        @Override
        public void run() {
            try {
                logger.info("Start get Manga:" + linkManga.getLink());
                String url = linkManga.getLink();
                //Document doc = Jsoup.connect(linkManga).referrer("https://mangakakalot.com/").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
                Document doc = Jsoup.connect(linkManga.getLink()).followRedirects(true).execute().parse();
                if (doc.body().html().contains("REDIRECT") && doc.html().contains("window.location.assign")) {
                    url = doc.body().html().split(":")[1] + ":" + doc.body().html().split(":")[2];
                    doc = Jsoup.connect(url).followRedirects(true).execute().parse();
                }
                if (url.contains("mangakakalot.com")) {
                    parseFromMangaKakalot(doc);
                } else if (url.contains("https://readmanganato.com/")) {
                    parseFromReadManga(doc);
                }

                synchronized (_lock) {
                    ++DOWNLOADED;
                }
                logger.info("Finish get Manga:" + linkManga.getLink() + ".DOWNLOADED:" + DOWNLOADED + "/" + TOTAL_MANGA);
            } catch (Exception ex) {
                logger.error("Error for Manga link:" + linkManga.getLink());
                logger.error(ex);
            }
        }

//        public void run222() {
//            Session session = null;
//            Transaction localTransaction = null;
//            try {
//                logger.info("Start get Manga:" + linkManga);
//                Document doc = Jsoup.connect("https://mangakakalot.com/chapter/eg_maker/chapter_0").get();
//
//                Elements chapterReader = doc.getElementsByClass("container-chapter-reader");
//                Elements images = chapterReader.get(0).getElementsByTag("img");
//                for (Element image : images) {
//                    String imageUrl = image.attributes().get("src");
//                    String imageName = image.attributes().get("title");
//                    //downloadImage(imageUrl, imageName);
//                    executorsDownload.submit(new DownloadImageWorder(imageUrl, ROOT_DIR + File.separator + imageName + ".jpg"));
//                }
//                logger.info("Finish get Manga:" + linkManga);
//                int x = 0;
//            } catch (Exception ex) {
//                logger.error(ex);
//
//            } finally {
//                if (session != null) {
//                    session.close();
//                }
//            }
//        }
        private void parseFromMangaKakalot(Document doc) {
            Session session = null;
            Transaction tx = null;
            try {
                session = HibernateUtil.openSession();
                tx = session.beginTransaction();
                Manga manga = new Manga();

                String content = doc.getElementById("noidungm").text();
                manga.setContent(content);
                manga.setImage(doc.selectFirst("div.manga-info-pic>img").attributes().get("src"));
                Element elements = doc.getElementsByClass("manga-info-text").get(0);
                manga.setName(elements.selectFirst("li>h1").text());
                manga.setAuthor(elements.selectFirst("li>a").text());

                try {
                    manga.setNameother(elements.getElementsByClass("story-alternative").get(0).text());
                } catch (Exception e) {
                }

                for (Element el : doc.select("ul.manga-info-text>li")) {
                    if (el.text().contains("Status :")) {
                        manga.setStatus(el.text().split(":")[1].trim());
                    } else if (el.text().contains("Last updated :")) {
                        manga.setLastUpdate(el.text().split(":")[1].trim());
                    } else if (el.text().contains("View :")) {
                        manga.setViews(Long.parseLong(el.text().split(":")[1].trim().replace(",", "")));
                    } else if (el.text().contains("Genres :")) {
                        manga.setGenres(el.text().split(":")[1].trim());
                    } else if (el.text().contains("rate :")) {
                        String html = el.html();
                        Pattern pattern = Pattern.compile("\"ratingValue\":(.*?),");
                        Matcher matcher = pattern.matcher(html);
                        if (matcher.find()) {
                            float score = Float.parseFloat(matcher.group(1).replace("\"", "").trim());
                            manga.setScore(score);
                        }
                        pattern = Pattern.compile("\"ratingCount\":(.*?),");
                        matcher = pattern.matcher(html);
                        if (matcher.find()) {
                            System.out.println(matcher.group(1));
                            int totalRate = Integer.parseInt(matcher.group(1).replace("\"", "").trim());
                            manga.setTotalRate(totalRate);
                        }
                    }
                }
                manga.setSource(linkManga.getLink());
                session.save(manga);
                logger.info("Manga:" + manga.getName() + " saved");

                //Read chapter info
                //div.chapter-list > div:nth-child(1) > span:nth-child(1)
                int max = doc.select("div.chapter-list>div.row").size();
                for (Element el : doc.select("div.chapter-list>div.row")) {
                    logger.info("Start get Chapter:" + el.getElementsByTag("a").text());
                    Chapter chapter = new Chapter();
                    chapter.setName(el.getElementsByTag("a").text());
                    chapter.setViews(Long.parseLong(el.getElementsByTag("span").get(1).text().replace(",", "")));
                    chapter.setLastUpdate(el.getElementsByTag("span").get(2).text());
                    chapter.setMangaId(manga.getId());
                    chapter.setSource(el.getElementsByTag("a").get(0).attributes().get("href"));
                    chapter.setOrder(max--);
                    session.save(chapter);
                    logger.info("Chapter:" + el.getElementsByTag("a").text() + " saved");

                    //Download image-files
                    Document docImage = Jsoup.connect(chapter.getSource()).get();

                    Elements chapterReader = docImage.getElementsByClass("container-chapter-reader");
                    Elements images = chapterReader.get(0).getElementsByTag("img");
                    for (Element image : images) {
                        String imageUrl = image.attributes().get("src");
                        String fileName = getFileNameFromUrlPath(imageUrl);
                        Files file = new Files();
                        file.setChapterId(chapter.getId());
                        file.setSrcUrl(imageUrl);
                        session.save(file);

//                        String folder = ROOT_DIR + File.separator + toSlug(manga.getName()) + File.separator + toSlug(chapter.getName());
//                        if (!new File(folder).exists()) {
//                            new File(folder).mkdirs();
//                        }
                        //executorsDownload.submit(new DownloadImageWorder(imageUrl, folder + File.separator + fileName));
//                        synchronized (_lock) {
//                            logger.info("Total image to download:" + (++TOTAL_IMAGES));
//                        }
                    }
                }
                linkManga.setStatus(3);

            } catch (Exception ex) {
                logger.error("Error for Manga link:" + linkManga.getLink());
                logger.error(ex);
                linkManga.setStatus(2);
            } finally {
                session.saveOrUpdate(linkManga);
                tx.commit();
                if (session != null) {
                    session.close();
                }
            }
        }

        private void parseFromReadManga(Document doc) {
            Session session = null;
            Transaction tx = null;
            try {
                session = HibernateUtil.openSession();
                tx = session.beginTransaction();
                Manga manga = new Manga();
                manga.setContent(doc.getElementById("panel-story-info-description").text());
                manga.setName(doc.selectFirst("div.story-info-right>h1").text());

                manga.setImage(doc.getElementsByClass("img-loading").get(0).attributes().get("src"));

                manga.setNameother(doc.getElementsByClass("table-value").get(0).text());
                manga.setAuthor(doc.getElementsByClass("table-value").get(1).text());
                manga.setStatus(doc.getElementsByClass("table-value").get(2).text());
                manga.setGenres(doc.getElementsByClass("table-value").get(3).text());

                manga.setLastUpdate(doc.getElementsByClass("stre-value").get(0).text());
                manga.setViews(Long.parseLong(doc.getElementsByClass("stre-value").get(1).text().replace(",", "")));

                //manga.setAuthor(elements.selectFirst("li>a").text());                
                String html = doc.getElementById("rate_row_cmd").html();
                Pattern pattern = Pattern.compile("\"v:average\">(.*?)<\\/em>");
                Matcher matcher = pattern.matcher(html);
                if (matcher.find()) {
                    float score = Float.parseFloat(matcher.group(1).replace("\"", "").trim());
                    manga.setScore(score);
                }
                pattern = Pattern.compile("\"v:votes\">(.*?)<\\/em>");
                matcher = pattern.matcher(html);
                if (matcher.find()) {
                    System.out.println(matcher.group(1));
                    int totalRate = Integer.parseInt(matcher.group(1).replace("\"", "").trim());
                    manga.setTotalRate(totalRate);
                }
                manga.setSource(linkManga.getLink());
                session.save(manga);
                logger.info("Manga:" + manga.getName() + " saved");

                //Read chapter info
                //div.chapter-list > div:nth-child(1) > span:nth-child(1)
                int max = doc.select("ul.row-content-chapter>li").size();
                for (Element el : doc.select("ul.row-content-chapter>li")) {
                    logger.info("Start get Chapter:" + el.getElementsByTag("a").text());
                    Chapter chapter = new Chapter();
                    chapter.setName(el.getElementsByTag("a").text());
                    chapter.setViews(Long.parseLong(el.getElementsByTag("span").get(0).text().replace(",", "")));
                    chapter.setLastUpdate(el.getElementsByTag("span").get(1).text());
                    chapter.setMangaId(manga.getId());
                    chapter.setSource(el.getElementsByTag("a").get(0).attributes().get("href"));
                    chapter.setOrder(max--);
                    session.save(chapter);
                    logger.info("Chapter:" + el.getElementsByTag("a").text() + " saved");

                    //Download image-files
                    Document docImage = Jsoup.connect(chapter.getSource()).get();

                    Elements chapterReader = docImage.getElementsByClass("container-chapter-reader");
                    Elements images = chapterReader.get(0).getElementsByTag("img");
                    for (Element image : images) {
                        String imageUrl = image.attributes().get("src");
                        String fileName = getFileNameFromUrlPath(imageUrl);

                        Files file = new Files();
                        file.setChapterId(chapter.getId());
                        file.setSrcUrl(imageUrl);
                        session.save(file);

//                        String folder = ROOT_DIR + File.separator + toSlug(manga.getName()) + File.separator + toSlug(chapter.getName());
//                        if (!new File(folder).exists()) {
//                            new File(folder).mkdirs();
//                        }
                        //executorsDownload.submit(new DownloadImageWorder(imageUrl, folder + File.separator + fileName));
//                        synchronized (_lock) {
//                            logger.info("Total image to download:" + (++TOTAL_IMAGES));
//                        }
                    }
                }
                linkManga.setStatus(3);

            } catch (Exception ex) {
                logger.error("Error for Manga link:" + linkManga.getLink());
                logger.error(ex);
                linkManga.setStatus(2);
            } finally {
                session.saveOrUpdate(linkManga);
                if (session != null) {
                    session.close();
                }
                tx.commit();
            }
        }
    }

    private class DownloadImageWorder implements Runnable {

        private String imageUrl;
        private String path;

        public DownloadImageWorder(String imageUrl, String path) {
            this.imageUrl = imageUrl;
            this.path = path;
        }

        @Override
        public void run() {
            int retry = 0;
            while (!DownloadImage(imageUrl, path) && retry < 3) {
                retry++;
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }

        }
    }

    public boolean DownloadImage(String imageUrl, String path) {
        logger.info("Start downloading image:" + imageUrl);
        // This will get input data from the server
        InputStream inputStream = null;

        // This will read the data from the server;
        OutputStream outputStream = null;

        try {
            // This will open a socket from client to server
            URL url = new URL(imageUrl);

            // This user agent is for if the server wants real humans to visit
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

            // This socket type will allow to set user_agent
            URLConnection con = url.openConnection();

            // Setting the user agent
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Referer", "https://mangakakalot.com/");

            // Requesting input data from server
            inputStream = con.getInputStream();

            // Open local file writer
            outputStream = new FileOutputStream(path);

            // Limiting byte written to file per loop
            byte[] buffer = new byte[2048];

            // Increments file size
            int length;

            // Looping until server finishes
            while ((length = inputStream.read(buffer)) != -1) {
                // Writing data
                outputStream.write(buffer, 0, length);
            }
            logger.info("Download success image:" + imageUrl);
            synchronized (_lock) {
                DOWNLOADED_IMAGES++;
            }
            logger.info("Downloaded image: " + DOWNLOADED_IMAGES + "/" + TOTAL_IMAGES);

            return true;
        } catch (Exception ex) {
            logger.error("Cannot download file:" + imageUrl);
            logger.error(ex);
            return false;
        } finally {
            try {
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
            }
            try {
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
            }
            logger.info("Finish downloading image:" + imageUrl);
        }

    }

    private static WebClient createWebClient() throws MalformedURLException {
        WebClient webClient = new WebClient(CHROME);
        WebClientOptions options = webClient.getOptions();
        options.setCssEnabled(false);
        options.setJavaScriptEnabled(true);
        options.setRedirectEnabled(true);
        // IMPORTANT: Without the country/language selection cookie the redirection does not work!
        webClient.addCookie("storepath=us/en", new URL("http://www.zara.com/"), null);
        return webClient;
    }

    private static String getFileNameFromUrlPath(String url) {
        int pos = url.lastIndexOf("/");
        return url.substring(pos + 1, url.length());
    }

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    private class WorkerUpdateContentOfChapter implements Runnable {

        private Chapter chapter;

        public WorkerUpdateContentOfChapter(Chapter chapter) {
            this.chapter = chapter;
        }

        @Override
        public void run() {
            try {
                logger.info("Start get Chapter:" + chapter.getSource());
                Document doc = Jsoup.connect(chapter.getSource()).followRedirects(true).execute().parse();
                getContent(doc);
                logger.info("Finish get Chapter:" + chapter.getSource());
            } catch (Exception ex) {
                logger.error("Error for Manga link:" + chapter.getSource());
                logger.error(ex);
            }
        }

        private void getContent(Document doc) {
            Session session = null;
            Transaction tx = null;
            try {
                session = HibernateUtil.openSession();
                tx = session.beginTransaction();

                Elements elements = doc.select("div.container-chapter-reader>img");
                StringBuilder strBuilder = new StringBuilder();
                for (Element el : elements) {
                    strBuilder.append(el.toString());
                }
                logger.info("Content Lenght:" + strBuilder.toString().length());
                chapter.setContent(strBuilder.toString());
                chapter.setStatus(2);
                logger.info("Chapter:" + chapter.getName() + " saved");

            } catch (Exception ex) {
                logger.error("Error for Chapter link:" + chapter.getSource());
                logger.error(ex);
                chapter.setStatus(3);
            } finally {
                session.saveOrUpdate(chapter);
                tx.commit();
                if (session != null) {
                    session.close();
                }
                synchronized (_lock) {
                    DOWNLOADED++;
                }
            }
        }
    }

}
