package envorimentMerger.service;

import envorimentMerger.model.MergeResult;
import envorimentMerger.model.Variable;

import java.util.ArrayList;
import java.util.List;

public class ParserService {

    // 1. Парсим JSON и получаем список переменных
    public List<Variable> parseVariables(String json) {

        List<Variable> result = new ArrayList<>();

        // 2. Находим блок values
        int start = json.indexOf("\"values\":");
        if (start == -1) return result;

        int arrayStart = json.indexOf("[", start);
        int arrayEnd = json.indexOf("]", arrayStart);

        if (arrayStart == -1 || arrayEnd == -1) return result;

        String arrayContent = json.substring(arrayStart + 1, arrayEnd);

        // 3. Делим на объекты
        String[] objects = arrayContent.split("\\{");

        for (String obj : objects) {

            if (!obj.contains("key")) continue;

            // 4. Достаём key и value
            String key = extract(obj, "key");
            String value = extract(obj, "value");

            if (key != null && value != null) {
                result.add(new Variable(key, value));
            }
        }

        return result;
    }

    // 5. Универсальный метод извлечения поля
    private String extract(String text, String field) {

        int start = text.indexOf("\"" + field + "\"");
        if (start == -1) return null;

        int colon = text.indexOf(":", start);
        int firstQuote = text.indexOf("\"", colon + 1);
        int secondQuote = text.indexOf("\"", firstQuote + 1);

        if (firstQuote == -1 || secondQuote == -1) return null;

        return text.substring(firstQuote + 1, secondQuote);
    }

    // 6. Объединяем переменные из нескольких файлов и отслеживаем изменения
    public MergeResult mergeVariablesWithTracking(
            java.util.List<java.nio.file.Path> files,
            FileService fileService
    ) throws Exception {

        MergeResult result = new MergeResult();

        // итоговая Map
        java.util.Map<String, String> map = result.getFinalData();

        // 1. идём по всем файлам
        for (java.nio.file.Path file : files) {

            // 2. читаем файл
            String json = fileService.readFile(file);

            // 3. парсим переменные
            java.util.List<Variable> vars = parseVariables(json);

            // 4. идём по переменным
            for (Variable v : vars) {

                String key = v.getKey();
                String newValue = v.getValue();

                // 5. если ключа ещё нет → NEW
                if (!map.containsKey(key)) {

                    map.put(key, newValue);
                    result.getAdded().put(key, newValue);

                } else {

                    String oldValue = map.get(key);

                    // 6. если значение изменилось → UPDATED
                    if (!oldValue.equals(newValue)) {

                        map.put(key, newValue);
                        result.getUpdated().put(key, newValue);
                    }
                }
            }
        }

        return result;
    }

    // 7. Собираем JSON из Map
    public String buildJson(String envName, java.util.Map<String, String> data) {

        StringBuilder json = new StringBuilder();

        json.append("{\n");
        json.append("  \"name\": \"").append(envName).append("\",\n");
        json.append("  \"values\": [\n");

        int i = 0;
        int size = data.size();

        for (String key : data.keySet()) {

            json.append("    {\n");
            json.append("      \"key\": \"").append(key).append("\",\n");
            json.append("      \"value\": \"").append(data.get(key)).append("\",\n");
            json.append("      \"enabled\": true\n");
            json.append("    }");

            if (i < size - 1) {
                json.append(",");
            }

            json.append("\n");
            i++;
        }

        json.append("  ]\n");
        json.append("}");

        return json.toString();
    }
}