import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MovieManagerJSON {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        Scanner scanner = new Scanner(System.in);

        try {
            Object obj = parser.parse(new FileReader("src/Json/movies.json"));
            JSONObject jsonObject = (JSONObject) obj;

            System.out.println("Корневой элемент: " + jsonObject.keySet().iterator().next());
            JSONArray jsonArray = (JSONArray) jsonObject.get("movies");

            for (Object o : jsonArray) {
                JSONObject movie = (JSONObject) o;
                System.out.println("\nТекущий элемент: movie");
                System.out.println("Название фильма - " + (String) movie.get("title"));
                System.out.println("Имя режиссёра - " + (String) movie.get("director"));
                System.out.println("Год выпуска - " + movie.get("year").toString());
            }

            searchMovies(scanner, jsonArray);

            addNewMovie(scanner, jsonArray);

            deleteMovieByTitle(scanner, jsonArray);

            try (FileWriter file = new FileWriter("src/Json/movies.json")) {
                file.write(jsonObject.toJSONString());
                file.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void searchMovies(Scanner scanner, JSONArray jsonArray) {
        System.out.print("\nВведите имя режиссёра: ");
        String director = scanner.nextLine();

        for (Object obj : jsonArray) {
            if (obj instanceof JSONObject) {
                JSONObject movie = (JSONObject) obj;
                if (director.equals(movie.get("director"))) {
                    System.out.println("\nНайденный фильм:");
                    System.out.println("Название фильма: " + movie.get("title"));
                    System.out.println("Режиссёр: " + movie.get("director"));
                    System.out.println("Год выпуска: " + movie.get("year"));
                }
            }
        }
    }

    private static void addNewMovie(Scanner scanner, JSONArray jsonArray) {
        System.out.println("\nДобавление нового фильма");
        System.out.print("Введите название фильма: ");
        String title = scanner.nextLine();
        System.out.print("Введите имя режиссёра: ");
        String director = scanner.nextLine();
        System.out.print("Введите год выпуска: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        JSONObject newMovie = new JSONObject();
        newMovie.put("title", title);
        newMovie.put("director", director);
        newMovie.put("year", year);

        jsonArray.add(newMovie);
        System.out.println("Фильм успешно добавлен.");
    }

    private static void deleteMovieByTitle(Scanner scanner, JSONArray jsonArray) {
        System.out.print("\nВведите название фильма для удаления: ");
        String title = scanner.nextLine();

        Iterator<?> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject movie = (JSONObject) iterator.next();
            if (title.equals((String) movie.get("title"))) {
                iterator.remove();
                System.out.println("Фильм успешно удалён.");
                return;
            }
        }
        System.out.println("Фильм с таким названием не найден.");
    }
}
