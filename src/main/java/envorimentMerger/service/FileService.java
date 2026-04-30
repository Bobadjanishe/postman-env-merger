package envorimentMerger.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileService {

    // 1. Получаем все файлы из папки
    public List<Path> getFiles(String folderPath) throws Exception {

        List<Path> result = new ArrayList<>();

        try (Stream<Path> files = Files.list(Path.of(folderPath))) {
            files.forEach(result::add);
        }

        return result;
    }

    // 2. Получаем только IFT файлы (без MERGED)
    public List<Path> getIftFiles(List<Path> files) {

        List<Path> result = new ArrayList<>();

        for (Path file : files) {
            String name = file.getFileName().toString();

            if (name.startsWith("IFT") && !name.contains("MERGED")) {
                result.add(file);
            }
        }

        return result;
    }

    // 3. Получаем только PreProd файлы (без MERGED)
    public List<Path> getPreProdFiles(List<Path> files) {

        List<Path> result = new ArrayList<>();

        for (Path file : files) {
            String name = file.getFileName().toString();

            if (name.startsWith("PreProd") && !name.contains("MERGED")) {
                result.add(file);
            }
        }

        return result;
    }

    // 4. Читаем файл как строку
    public String readFile(Path file) throws Exception {
        return Files.readString(file);
    }

    // 5. Сохраняем файл (с перезаписью)
    public void saveToFile(String content, String fileName) throws Exception {

        Path path = Path.of(
                System.getProperty("user.dir"),
                "src/main/resources/environments",
                fileName
        );

        System.out.println("\n==============================");

        // 1. если файл уже есть
        if (Files.exists(path)) {
            System.out.println("⚠️  ВНИМАНИЕ:");
            System.out.println("   Файл уже существует и будет перезаписан:");
            System.out.println("   ➜ " + fileName);
            System.out.println("------------------------------");
        } else {
            System.out.println("🆕 Создаётся новый файл:");
            System.out.println("   ➜ " + fileName);
            System.out.println("------------------------------");
        }

        // 2. запись файла
        Files.writeString(path, content);

        // 3. подтверждение
        System.out.println("✔ Готово:");
        System.out.println("   Файл успешно сохранён");
        System.out.println("   ➜ " + fileName);
        System.out.println("==============================\n");
    }
}