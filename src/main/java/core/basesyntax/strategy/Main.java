package core.basesyntax.strategy;

import core.basesyntax.converter.DataConverter;
import core.basesyntax.converter.DataConverterImpl;
import core.basesyntax.fileReader.FileReaderImpl;
import core.basesyntax.fileWriter.FileWriter;
import core.basesyntax.fileWriter.FileWriterImpl;
import core.basesyntax.model.FruitTransaction;
import core.basesyntax.report.ReportGenerator;
import core.basesyntax.report.ReportGeneratorImpl;
import core.basesyntax.service.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] arg) {
        // 1. Зчитування даних із вхідного файлу CSV
        FileReaderImpl fileReader = new FileReaderImpl();
        List<String> inputReport = fileReader.read("reportToRead.csv");

        // 2. Перетворення вхідних даних у список FruitTransactions
        DataConverter dataConverter = new DataConverterImpl();
        List<FruitTransaction> transactions = dataConverter.convertToTransaction(inputReport);

        // 3. Створюйте та відчувайте карту з усіма реалізаціями
        Map<FruitTransaction.Operation, OperationHandler> operationHandlers = new HashMap<>();

        operationHandlers.put(FruitTransaction.Operation.BALANCE, new BalanceOperation());
        operationHandlers.put(FruitTransaction.Operation.PURCHASE, new PurchaseOperation());
        operationHandlers.put(FruitTransaction.Operation.RETURN, new ReturnOperation());
        operationHandlers.put(FruitTransaction.Operation.SUPPLY, new SupplyOperation());
        OperationStrategy operationStrategy = new OperationStrategyImpl(operationHandlers);

        // 4. Обробляйте вхідні транзакції за допомогою відповідних реалізацій OperationHandler
        ShopService shopService = new ShopServiceImpl(operationStrategy);
        shopService.process(transactions);

        // 5. Створення звіту на основі поточного стану зберігання
        ReportGenerator reportGenerator = new ReportGeneratorImpl();
        String resultingReport = reportGenerator.getReport(((
                ShopServiceImpl) shopService).getInventory());

        // 6. Записати отриманий звіт у файл призначення
        FileWriter fileWriter = new FileWriterImpl();
        fileWriter.write(resultingReport, "finalReport.csv");
    }
}
