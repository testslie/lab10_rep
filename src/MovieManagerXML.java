import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MovieManagerXML {

    private static final String FILE_PATH = "src/XML/movies.xml";
    private static Document doc;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            loadDocument();

            int command;
            do {
                System.out.println("Введите команду: 1. Показать все фильмы, 2. Добавить фильм, 3. Искать фильм по режиссёру, 4. Удалить фильм по названию, (0) Выход");
                command = scanner.nextInt();
                scanner.nextLine();

                switch (command) {
                    case 1:
                        printMovies();
                        break;
                    case 2:
                        addMovieInteractive(scanner);
                        break;
                    case 3:
                        searchMoviesByDirectorInteractive(scanner);
                        break;
                    case 4:
                        deleteMovieByTitleInteractive(scanner);
                        break;
                    case 0:
                        System.out.println("Выход из программы.");
                        break;
                    default:
                        System.out.println("Неизвестная команда. Попробуйте снова.");
                        break;
                }
            } while (command != 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDocument() {
        try {
            File inputFile = new File(FILE_PATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Файл загружен. Корневой элемент: " + doc.getDocumentElement().getNodeName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printMovies() {
        NodeList nodelist = doc.getElementsByTagName("movie");

        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                System.out.println("\nТекущий элемент: " + node.getNodeName());
                System.out.println("Название фильма: " + element.getElementsByTagName("title").item(0).getTextContent());
                System.out.println("Режиссёр: " + element.getElementsByTagName("director").item(0).getTextContent());
                System.out.println("Год выпуска: " + element.getElementsByTagName("year").item(0).getTextContent());
            }
        }
    }

    private static void addMovieInteractive(Scanner scanner) {
        System.out.print("Введите название фильма: ");
        String title = scanner.nextLine();
        System.out.print("Введите имя режиссёра: ");
        String director = scanner.nextLine();
        System.out.print("Введите год выпуска: ");
        String year = scanner.nextLine();

        Element newMovie = doc.createElement("movie");

        Element titleElement = doc.createElement("title");
        titleElement.appendChild(doc.createTextNode(title));
        newMovie.appendChild(titleElement);

        Element directorElement = doc.createElement("director");
        directorElement.appendChild(doc.createTextNode(director));
        newMovie.appendChild(directorElement);

        Element yearElement = doc.createElement("year");
        yearElement.appendChild(doc.createTextNode(year));
        newMovie.appendChild(yearElement);

        doc.getDocumentElement().appendChild(newMovie);
        saveDocument();
        System.out.println("Новый фильм добавлен и сохранён в файл.");
    }

    private static void searchMoviesByDirectorInteractive(Scanner scanner) {
        System.out.print("Введите имя режиссёра для поиска: ");
        String director = scanner.nextLine();

        NodeList nodeList = doc.getElementsByTagName("movie");
        List<Element> movies =
                getElementsList(nodeList).stream()
                        .filter(element -> element.getElementsByTagName("director").item(0).getTextContent().equalsIgnoreCase(director))
                        .collect(Collectors.toList());

        if (movies.isEmpty()) {
            System.out.println("Фильмы с таким режиссёром не найдены.");
        } else {
            System.out.println("Найденные фильмы:");
            for (Element movie : movies) {
                System.out.println("Название: " + movie.getElementsByTagName("title").item(0).getTextContent());
                System.out.println("Год выпуска: " + movie.getElementsByTagName("year").item(0).getTextContent());
            }
        }
    }

    private static void deleteMovieByTitleInteractive(Scanner scanner) {
        System.out.print("Введите название фильма для удаления: ");
        String title = scanner.nextLine();

        NodeList nodeList = doc.getElementsByTagName("movie");
        boolean deleted = false;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getElementsByTagName("title").item(0).getTextContent().equalsIgnoreCase(title)) {
                    element.getParentNode().removeChild(element);
                    deleted = true;
                    break;
                }
            }
        }

        if (deleted) {
            saveDocument();
            System.out.println("Фильм с названием \"" + title + "\" удалён.");
        } else {
            System.out.println("Фильм с таким названием не найден.");
        }
    }

    private static void saveDocument() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILE_PATH));
            transformer.transform(source, result);
            System.out.println("Изменения сохранены в файл.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Element> getElementsList(NodeList nodeList) {
        return java.util.stream.IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(node -> (Element) node)
                .collect(Collectors.toList());
    }
}
