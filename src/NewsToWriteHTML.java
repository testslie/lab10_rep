import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class NewsToWriteHTML {

    private static final String URL = "http://fat.urfu.ru/";
    private static final String FILE_NAME = "src/html/news.txt";
    private static final int MAX_RETRIES = 3;

    public static void main(String[] args) {
        int attempts = 0;
        boolean success = false;

        while (attempts < MAX_RETRIES && !success) {
            try {
                Document doc = Jsoup.connect(URL).get();
                Elements newsItems = doc.select("body > div.main_page > div.content_section.floating_element > div:nth-child(4) > p:nth-child(1)");

                if (newsItems.isEmpty()) {
                    System.out.println("Элементы не найдены.");
                    return;
                }

                try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
                    for (Element newsItem : newsItems) {
                        Element hrefTitle = newsItem.selectFirst("p:nth-child(1)");
                        Element hrefElement = newsItem.selectFirst("p:nth-child(1) > a");

                        if (hrefElement != null && hrefTitle != null) {
                            String title = hrefTitle.text();
                            String link = hrefElement.attr("href");

                            System.out.println("Тема : " + title);
                            System.out.println("Ссылка : " + link);

                            writer.println("Тема : " + title);
                            writer.println("Ссылка : " + link);
                            writer.println();
                        } else {
                            System.out.println("Не удалось найти информацию.\n");
                        }
                    }
                    success = true;

                } catch (IOException e) {
                    System.out.println("Ошибка записи в файл: " + e.getMessage());
                }

            } catch (IOException e) {
                attempts++;
                System.out.println("Ошибка подключения к сайту. Попытка " + attempts + " из " + MAX_RETRIES);

                if (attempts == MAX_RETRIES) {
                    System.out.println("Не удалось подключиться к сайту после " + MAX_RETRIES + " попыток.");
                }
            }
        }
    }
}
