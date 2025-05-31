package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Путь к входному каталогу с логами
        String inputDir = "/home/oem/Рабочий стол/Java_test/src/main/java/org/example/input_logs";
        // Путь к выходному каталогу
        String outputDir = "/home/oem/Рабочий стол/Java_test/src/main/java/org/example/output_transactions";

        // Запуск процесса обработки логов
        LogProcessor.processLogs(inputDir, outputDir);
    }

}