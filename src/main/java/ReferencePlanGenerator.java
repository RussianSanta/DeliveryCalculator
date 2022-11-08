public class ReferencePlanGenerator {
    public static Table getMinimalCostReference(Table targetTable) {
        Table table = new Table(targetTable);
        Table answer = new Table(targetTable);
        answer.fill(1, 1, answer.getWidth(), answer.getHeight(), "0");

        Long product = 0L;
        long request = 0L;
        for (int i = 1; i < table.getHeight() - 1; ++i) {
            product += Long.parseLong(table.get(table.getWidth() - 1, i));
        }
        for (int i = 1; i < table.getWidth() - 1; ++i) {
            request += Long.parseLong(table.get(i, table.getHeight() - 1));
        }

        if (!product.equals(request)) {
            throw new IllegalArgumentException("Несбалансированная таблица, количество продукции не равно количеству потребления");
        }

        while (request > 0) {
            int minCostX = 1;
            int minCostY = 1;
            long minCost = Long.MAX_VALUE;
            for (int x = 1; x < table.getWidth() - 1; ++x) {
                for (int y = 1; y < table.getHeight() - 1; ++y) {
                    if (Long.parseLong(table.get(x, y)) < minCost) {
                        minCost = Long.parseLong(table.get(x, y));
                        minCostX = x;
                        minCostY = y;
                    }
                }
            }

            Long transported = Math.min(
                    Long.parseLong(table.get(minCostX, table.getHeight() - 1)),
                    Long.parseLong(table.get(table.getWidth() - 1, minCostY))
            );
            request -= transported;

            answer.set(minCostX, minCostY, String.valueOf(transported));
            answer.set(minCostX, answer.getHeight() - 1,
                    String.valueOf(transported + Long.parseLong(answer.get(minCostX, answer.getHeight() - 1))));
            answer.set(answer.getWidth() - 1, minCostY,
                    String.valueOf(transported + Long.parseLong(answer.get(answer.getWidth() - 1, minCostY))));
            answer.set(answer.getWidth() - 1, answer.getHeight() - 1, String.valueOf(
                    transported + Long.parseLong(answer.get(answer.getWidth() - 1, answer.getHeight() - 1))));

            table.set(minCostX, minCostY, String.valueOf(Long.MAX_VALUE));
            table.set(minCostX, table.getHeight() - 1,
                    String.valueOf(Long.parseLong(table.get(minCostX, table.getHeight() - 1)) - transported));
            table.set(table.getWidth() - 1, minCostY,
                    String.valueOf(Long.parseLong(table.get(table.getWidth() - 1, minCostY)) - transported));

            System.out.println("\nОставшиеся элементы:\n" + table);
            System.out.println("\nСледующий шаг в методе минимальных стоимостей:\n" + answer);
        }

        return answer;
    }

    public static Table getNorthWestPointReference(Table targetTable) {
        Table table = new Table(targetTable);
        Table answer = new Table(targetTable);
        answer.fill(1, 1, answer.getWidth(), answer.getHeight(), "0");

        Long product = 0L;
        long request = 0L;
        for (int i = 1; i < table.getHeight() - 1; ++i) {
            product += Long.parseLong(table.get(table.getWidth() - 1, i));
        }
        for (int i = 1; i < table.getWidth() - 1; ++i) {
            request += Long.parseLong(table.get(i, table.getHeight() - 1));
        }

        if (!product.equals(request)) {
            throw new IllegalArgumentException("Несбалансированная таблица, количество продукции не равно количеству потребления");
        }

        int x = 1;
        int y = 1;
        while (request != 0) {
            Long transported = Math.min(
                    Long.parseLong(table.get(x, table.getHeight() - 1)),
                    Long.parseLong(table.get(table.getWidth() - 1, y))
            );
            request -= transported;

            answer.set(x, y, String.valueOf(transported));
            answer.set(x, answer.getHeight() - 1,
                    String.valueOf(transported + Long.parseLong(answer.get(x, answer.getHeight() - 1))));
            answer.set(answer.getWidth() - 1, y,
                    String.valueOf(transported + Long.parseLong(answer.get(answer.getWidth() - 1, y))));
            answer.set(answer.getWidth() - 1, answer.getHeight() - 1, String.valueOf(
                    transported + Long.parseLong(answer.get(answer.getWidth() - 1, answer.getHeight() - 1))));

            table.set(x, table.getHeight() - 1,
                    String.valueOf(Long.parseLong(table.get(x, table.getHeight() - 1)) - transported));
            table.set(table.getWidth() - 1, y,
                    String.valueOf(Long.parseLong(table.get(table.getWidth() - 1, y)) - transported));

            if (table.get(x, table.getHeight() - 1).equals("0")) {
                ++x;
            }
            if (table.get(table.getWidth() - 1, y).equals("0")) {
                ++y;
            }

            System.out.println("\nСледующий шаг в методе северо-западного угла:\n" + answer);
        }

        return answer;
    }
}
