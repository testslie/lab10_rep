import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NewsParser {
    public static void main(String[] args) {
        try {

            Document doc = Jsoup.connect("http://fat.urfu.ru/").get();
            Elements newsItems = doc.select("body > div.main_page > div.content_section.floating_element > div:nth-child(4) > p:nth-child(1)");

            if (newsItems.isEmpty()) {
                System.out.println("Элементы не найдены.");
                return;
            }

            for (Element newsItem : newsItems) {

                Element hrefTitle = newsItem.selectFirst("p:nth-child(1)");
                Element hrefElement = newsItem.selectFirst("p:nth-child(1) > a");

                if (hrefElement != null && hrefTitle != null) {
                    System.out.println("Тема : " + hrefTitle.text());
                    System.out.println("Ссылка : " + hrefElement.attr("href"));
                } else {
                    System.out.println("Не удалось найти информацию.\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}