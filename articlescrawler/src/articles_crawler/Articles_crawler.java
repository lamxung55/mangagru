/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package articles_crawler;

import com.google.gson.Gson;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Mina Mimi
 */
public class Articles_crawler {

    final static Logger logger = Logger.getLogger(Articles_crawler.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // TODO code application logic here
//        FileWriter fileWriter = new FileWriter(new File("C:/running_projects/articles_crawler/out.txt"));
//        fileWriter.write(output);
//        fileWriter.flush();
//        fileWriter.close();
        logger.info("Starting...");
        Articles_crawler articles_crawler = new Articles_crawler();
        articles_crawler.doCrawler();

    }

    private void doCrawler() {
        ExecutorService executors = Executors.newFixedThreadPool(100);
        int max = 20000;
        for (int i = 0; i < max; i++) {
            executors.submit(new Worker(i));
        }

    }

    private class Worker implements Runnable {

        private int pageId;

        public Worker(int pageId) {
            this.pageId = pageId;
        }

        @Override
        public void run() {
            try {
                logger.info("====================PAGE:" + pageId);
                String output = Jsoup.connect("https://vnexpress.net/ajax/listcategory/category_id/1001014/page/" + pageId).execute().charset("UTF-8").body();
                if (output == null || "".equals(output)) {
                    return;
                }
                Gson g = new Gson();
                DataResponse s = g.fromJson(output, DataResponse.class);
                if ("2".equals(s.getError())) {
                    logger.info("=================END");
                    return;
                }
                //logger.info("GetHTML:" + s.getHtml());
                //List lstArtSumary = new ArrayList<>();
                Document doc = Jsoup.parse(s.getHtml());
                Elements lstTitles = doc.getElementsByClass("item-news");
                for (Element element : lstTitles) {
//                    logger.info("Title:" + element.select("h3 > a").text());
//                    logger.info("Link:" + element.select("h3 > a").attr("href"));
//                    logger.info("Desc:" + element.select("div:eq(1) > p:eq(1) > a").text());                    
                    String title = element.select("h3 > a").text();
                    String desc = element.select("div:eq(1) > p:eq(1) > a").text();
                    String link = element.select("h3 > a").attr("href");
                    if (!"".equals(title) && !"".equals(desc) && !"".equals(link)) {
                        String temp = element.select("h3 > a").text() + "|" + element.select("div:eq(1) > p:eq(1) > a").text()
                                + "|" + link;
                        if (link != null && !"".equals(link)) {
                            try {
                                Document docContent = Jsoup.connect(link).get();
                                String date = docContent.getElementsByClass("date").text();
                                //logger.info("Date:" + date);
                                //artSumary.setContent(content);
                                temp = temp + "|" + date;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        logger.info(temp);
                    }
//                    ArtSumary artSumary = new ArtSumary();
//                    artSumary.setTitle(element.select("h3 > a").text());
//                    artSumary.setLink(link);
//                    if (link != null && !"".equals(link)) {
//                        try {
//                            String content = Jsoup.connect(link).get().toString();
//                            logger.info("Content:" + content);
//                            artSumary.setContent(content);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    lstArtSumary.add(artSumary);

                }
            } catch (Exception ex) {
                logger.error(ex);

            }
        }

    }

}
