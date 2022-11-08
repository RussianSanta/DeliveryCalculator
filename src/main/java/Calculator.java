import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Calculator {
    public static void main(String[] args) {
        Table table = fillData();

        System.out.println("Транспортная таблица:\n" + table);

        PotentialMethod mathMethods = new PotentialMethod();
        System.out.println("Методом северо-западного угла: " + mathMethods.calcSumThroughPotentialAndNorthwestCornerMethods(table) + " руб");

        System.out.println("Методом минимальных стоимостей: " + mathMethods.calcSumThroughPotentialAndMinimalCostMethods(table) + " руб");
    }

    private static Table fillData() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/calculator.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int countOfManufacturer = Integer.parseInt(properties.getProperty("countOfManufacturer"));
        int countOfConsumer = Integer.parseInt(properties.getProperty("countOfConsumer"));

        Table table = new Table(countOfConsumer + 2, countOfManufacturer + 2);


        table.set(0, 0, "Manufact/Cons");

        for (int i = 0; i < countOfConsumer; i++) {
            int j = i + 1;
            table.set(j, 0, properties.getProperty("name.c" + j));
        }

        table.set(countOfConsumer + 1, 0, "Production");

        for (int i = 0; i < countOfManufacturer; i++) {
            int j = i + 1;
            table.set(0, j, properties.getProperty("name.m" + j));
        }

        table.set(0, countOfManufacturer + 1, "Consumption");

        for (int i = 0; i < countOfConsumer; i++) {
            int a = i + 1;
            for (int j = 0; j < countOfManufacturer; j++) {
                int b = j + 1;
                table.set(a, b, properties.getProperty("transfer.m" + b + ".c" + a));
            }
        }

        for (int i = 0; i < countOfConsumer; i++) {
            int j = i + 1;
            table.set(j, countOfManufacturer + 1, properties.getProperty("count.c" + j));
        }

        for (int i = 0; i < countOfManufacturer; i++) {
            int j = i + 1;
            table.set(countOfConsumer + 1, j, properties.getProperty("count.m" + j));
        }

        table.set(countOfConsumer + 1, countOfManufacturer + 1, "||||||||||||||||||");

        return table;
    }
}
