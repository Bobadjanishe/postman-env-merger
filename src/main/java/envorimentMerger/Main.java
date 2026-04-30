package envorimentMerger;

import envorimentMerger.model.MergeResult;
import envorimentMerger.service.FileService;
import envorimentMerger.service.ParserService;

import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        // 1. Путь к папке
        String folder = System.getProperty("user.dir") + "/src/main/resources/environments";

        // 2. Сервисы
        FileService fileService = new FileService();
        ParserService parserService = new ParserService();

        // 3. Получаем все файлы
        List<Path> allFiles = fileService.getFiles(folder);

        // 4. Делим файлы
        List<Path> iftFiles = fileService.getIftFiles(allFiles);
        List<Path> preProdFiles = fileService.getPreProdFiles(allFiles);

        // =========================
        // 5. Вывод файлов
        // =========================
        System.out.println("=== IFT FILES ===");
        iftFiles.forEach(f -> System.out.println(f.getFileName()));

        System.out.println("\n=== PREPROD FILES ===");
        preProdFiles.forEach(f -> System.out.println(f.getFileName()));


        // =========================
        // 6. MERGE IFT
        // =========================
        System.out.println("\n=== IFT: РЕЗУЛЬТАТ ОБЪЕДИНЕНИЯ ===");

        MergeResult iftResult =
                parserService.mergeVariablesWithTracking(iftFiles, fileService);

        // если нет изменений
        if (iftResult.getAdded().isEmpty() && iftResult.getUpdated().isEmpty()) {
            System.out.println("✔ Изменений нет");
        } else {

            System.out.println("\n\uD83D\uDFE2 Добавленные переменные:");
            iftResult.getAdded().forEach((k, v) -> System.out.println(k + " → " + v));

            System.out.println("\n🔄 Обновлённые переменные:");
            iftResult.getUpdated().forEach((k, v) -> System.out.println(k + " → " + v));
        }

        System.out.println("\n📦 Итоговый набор:");
        iftResult.getFinalData().forEach((k, v) -> System.out.println(k + " → " + v));

        // сохраняем
        String iftJson = parserService.buildJson("IFT", iftResult.getFinalData());
        fileService.saveToFile(iftJson, "IFT_MERGED.json");


        // =========================
        // 7. MERGE PREPROD
        // =========================
        System.out.println("\n=== PREPROD: РЕЗУЛЬТАТ ОБЪЕДИНЕНИЯ ===");

        MergeResult preProdResult =
                parserService.mergeVariablesWithTracking(preProdFiles, fileService);

        if (preProdResult.getAdded().isEmpty() && preProdResult.getUpdated().isEmpty()) {
            System.out.println("✔ Изменений нет");
        } else {

            System.out.println("\n\uD83D\uDFE2 Добавленные переменные:");
            preProdResult.getAdded().forEach((k, v) -> System.out.println(k + " → " + v));

            System.out.println("\n🔄 Обновлённые переменные:");
            preProdResult.getUpdated().forEach((k, v) -> System.out.println(k + " → " + v));
        }

        System.out.println("\n📦 Итоговый набор:");
        preProdResult.getFinalData().forEach((k, v) -> System.out.println(k + " → " + v));

        // сохраняем
        String preProdJson = parserService.buildJson("PreProd", preProdResult.getFinalData());
        fileService.saveToFile(preProdJson, "PreProd_MERGED.json");
    }
}