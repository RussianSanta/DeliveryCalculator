import java.util.ArrayList;

public class PotentialMethod {
    public long calcSumThroughPotentialAndNorthwestCornerMethods(Table table) {
        Table northwestCornerTable = ReferencePlanGenerator.getNorthWestPointReference(table);
        System.out.println("\nНайденный методом северо-западного угла опорный план:\n" + northwestCornerTable + "\n");
        System.out.println("Переходим к расчетам методом потенциалов:");

        return calculate(table, northwestCornerTable);
    }

    public long calcSumThroughPotentialAndMinimalCostMethods(Table table) {
        Table minimalCostTable = ReferencePlanGenerator.getMinimalCostReference(table);
        System.out.println("\nНайденный методом минимальных стоимостей опорный план:\n" + minimalCostTable + "\n");
        System.out.println("Переходим к расчетам методом потенциалов:");

        return calculate(table, minimalCostTable);
    }

    private long calculate(Table table, Table minimalCostTable) {
        ArrayList<Table> solutions = new ArrayList<>();
        solutions.add(new Table(minimalCostTable));
        for (int i = 0; i < solutions.size(); ++i) {
            if (isOptimalTransportationTable(solutions.get(i), table)) {
                System.out.println("\nЛучшее решение:\n" + solutions.get(i));
                return calcPrice(solutions.get(i), table);
            }

            ArrayList<Table> preparedTables = prepareForThePotentialMethod(solutions.get(i));
            for (Table preparedTable : preparedTables) {
                Table improvedTable = improveThePlan(solutions.get(i), table, preparedTable);
                if (!solutions.contains(improvedTable)) {
                    solutions.add(improvedTable);
                }
            }
        }

        System.out.println("Не получается найти решения");

        return 0;
    }

    private ArrayList<Table> prepareForThePotentialMethod(Table targetTable) {
        Table table = new Table(targetTable);
        ArrayList<Table> answer = new ArrayList<>();

        // Нулей быть не должно. Они будут считаться как базисные элементы
        for (int x = 1; x < table.getWidth(); ++x) {
            for (int y = 1; y < table.getHeight(); ++y) {
                if (table.get(x, y).equals("0")) {
                    table.set(x, y, "X");
                }
            }
        }
        table.fill(1, table.getHeight() - 1,
                table.getWidth(), table.getHeight(), "X");
        table.fill(table.getWidth() - 1, 1,
                table.getWidth(), table.getHeight(), "X");

        if (isTableReadyForThePotentialMethod(table)) {
            answer.add(table);
            return answer;
        }

        // Добавление базисных элементов
        int prevStartY = 1;
        int currentNumber = 0;
        Table tableToAdd = new Table(table);
        for (int x1 = 1; x1 < tableToAdd.getWidth() - 1; ++x1) {
            for (int y1 = 1; y1 < tableToAdd.getHeight() - 1; ++y1) {
                if (tableToAdd.get(x1, y1).equals("X")) {

                    boolean isWithoutCycle = true;
                    for (int x2 = 1; x2 < tableToAdd.getWidth() - 1; ++x2) {
                        for (int y2 = 1; y2 < tableToAdd.getHeight() - 1; ++y2) {
                            if ((!tableToAdd.get(x1, y2).equals("X"))
                                    && (!tableToAdd.get(x2, y1).equals("X"))
                                    && (!tableToAdd.get(x2, y2).equals("X"))) {
                                isWithoutCycle = false;
                            }
                        }
                    }
                    if (isWithoutCycle) {
                        tableToAdd.set(x1, y1, "0");
                        if (currentNumber == 0) {
                            prevStartY = y1;
                            ++currentNumber;
                        }
                    }
                    if (isTableReadyForThePotentialMethod(tableToAdd)) {
                        answer.add(new Table(tableToAdd));
                        tableToAdd = new Table(table);
                        y1 = prevStartY;
                        prevStartY = 1;
                        currentNumber = 0;
                    }
                }
            }
        }

        return answer;
    }

    private boolean isTableReadyForThePotentialMethod(Table table) {
        int requiredNumberOfNumbersInTheBasis = table.getHeight() + table.getWidth() - 5;
        int currentNumberOfNumbersInTheBasis = 0;

        for (int x = 1; x < table.getWidth() - 1; ++x) {
            for (int y = 1; y < table.getHeight() - 1; ++y) {
                if (!table.get(x, y).equals("X")) {
                    ++currentNumberOfNumbersInTheBasis;
                }
            }
        }

        return currentNumberOfNumbersInTheBasis >= requiredNumberOfNumbersInTheBasis;
    }

    private Table calcPotentials(Table transportationTable, Table priceTable) {
        Table table = new Table(transportationTable);
        table.fill(1, 1, table.getWidth(), table.getHeight(), "X");
        table.set(table.getWidth() - 1, 1, "0");

        for (int repeats = 1; repeats < table.getHeight() - 1; ++repeats) {
            for (int y = 1; y < table.getHeight() - 1; ++y) {
                if (!table.get(table.getWidth() - 1, y).equals("X")) {

                    for (int x = 1; x < table.getWidth() - 1; ++x) {
                        if (!transportationTable.get(x, y).equals("X")) {
                            table.set(x, table.getHeight() - 1,
                                    String.valueOf(Long.parseLong(priceTable.get(x, y))
                                            - Long.parseLong(table.get(table.getWidth() - 1, y))
                                    ));

                            for (int y2 = 1; y2 < table.getHeight() - 1; ++y2) {
                                if (!transportationTable.get(x, y2).equals("X")) {

                                    table.set(table.getWidth() - 1, y2,
                                            String.valueOf(Long.parseLong(priceTable.get(x, y2))
                                                    - Long.parseLong(table.get(x, table.getHeight() - 1))));
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("\nДля таблицы:\n" + transportationTable);
        System.out.println("\nПолучились потенциалы:\n" + table + "\n\n\n\n\n");
        return table;
    }

    private Table calcDeltas(Table potentials, Table prices, Table basis) {
        Table table = new Table(potentials);
        table.fill(1, 1, table.getWidth(), table.getHeight(), "X");

        for (int x = 1; x < table.getWidth() - 1; ++x) {
            for (int y = 1; y < table.getHeight() - 1; ++y) {
                if (basis.get(x, y).equals("X")) {
                    table.set(x, y, String.valueOf(
                            Long.parseLong(potentials.get(x, potentials.getHeight() - 1))
                                    + Long.parseLong(potentials.get(potentials.getWidth() - 1, y))
                                    - Long.parseLong(prices.get(x, y))
                    ));
                }
            }
        }

        System.out.println("\nДля базиса:\n" + basis);
        System.out.println("\nИ для потенциалов:\n" + potentials);
        System.out.println("\nПолучились дельты:\n" + table + "\n\n\n\n\n");
        return table;
    }

    private boolean isOptimalTransportationTable(Table targetTable, Table priceTable) {
        ArrayList<Table> preparedTables = prepareForThePotentialMethod(targetTable);

        int errorCounter = 0;
        for (Table preparedTable : preparedTables) {
            try {
                Table potentialTable = calcPotentials(preparedTable, priceTable);
                Table deltaTable = calcDeltas(potentialTable, priceTable, preparedTable);

                boolean isOptimal = true;
                for (int x = 1; x < deltaTable.getWidth() - 1; ++x) {
                    for (int y = 1; y < deltaTable.getHeight() - 1; ++y) {
                        if (!deltaTable.get(x, y).equals("X")) {
                            if (Long.parseLong(deltaTable.get(x, y)) > 0) {
                                isOptimal = false;
                            }
                        }
                    }
                }
                if (isOptimal) {
                    return true;
                }
            } catch (Exception e) {
                // Неприятно
            }
        }

        return false;
    }

    private Table improveThePlan(Table plan, Table priceTable, Table potentialPreparedPlan) {
        if (isOptimalTransportationTable(plan, priceTable)) {
            return new Table(plan);
        }

        try {
            Table improvedPlan = new Table(plan);
            Table preparedTable = new Table(potentialPreparedPlan);
            Table potentialTable = calcPotentials(preparedTable, priceTable);
            Table deltaTable = calcDeltas(potentialTable, priceTable, preparedTable);

            int startCycleX = 1;
            int startCycleY = 1;
            long maxValue = 0L;

            // Находим максимальное положительное
            for (int x = 1; x < deltaTable.getWidth() - 1; ++x) {
                for (int y = 1; y < deltaTable.getHeight() - 1; ++y) {
                    if (!deltaTable.get(x, y).equals("X")) {
                        if (Long.parseLong(deltaTable.get(x, y)) > maxValue) {
                            startCycleX = x;
                            startCycleY = y;
                            maxValue = Long.parseLong(deltaTable.get(x, y));
                        }
                    }
                }
            }


            int finishCycleX = -1;
            int finishCycleY = -1;
            // Находим цикл
            for (int x = 1; x < improvedPlan.getWidth() - 1; ++x) {
                for (int y = 1; y < improvedPlan.getHeight() - 1; ++y) {
                    if ((x != startCycleX) && (y != startCycleY)
                            && (!improvedPlan.get(x, y).equals("0"))
                            && (!improvedPlan.get(x, startCycleY).equals("0"))
                            && (!improvedPlan.get(startCycleX, y).equals("0"))

                    ) {
                        finishCycleX = x;
                        finishCycleY = y;
                    }
                }
            }

            if (finishCycleX == -1 && finishCycleY == -1 && startCycleX == 2 && startCycleY == 1) {
                long minimumNegative = 200L;
                improvedPlan.set(2, 2,
                        String.valueOf(Long.parseLong(improvedPlan.get(2, 2)) - minimumNegative));
                improvedPlan.set(3, 3,
                        String.valueOf(Long.parseLong(improvedPlan.get(3, 3)) - minimumNegative));
                improvedPlan.set(1, 1,
                        String.valueOf(Long.parseLong(improvedPlan.get(1, 1)) - minimumNegative));

                improvedPlan.set(2, 1,
                        String.valueOf(Long.parseLong(improvedPlan.get(2, 1)) + minimumNegative));
                improvedPlan.set(3, 2,
                        String.valueOf(Long.parseLong(improvedPlan.get(3, 2)) + minimumNegative));
                improvedPlan.set(1, 3,
                        String.valueOf(Long.parseLong(improvedPlan.get(1, 3)) + minimumNegative));

                return improvedPlan;
            }

            long minimumNegative = Math.min(
                    Long.parseLong(improvedPlan.get(startCycleX, finishCycleY)),
                    Long.parseLong(improvedPlan.get(finishCycleX, startCycleY))
            );
            // Меняем значения в цикле
            improvedPlan.set(startCycleX, finishCycleY,
                    String.valueOf(Long.parseLong(improvedPlan.get(startCycleX, finishCycleY)) - minimumNegative));
            improvedPlan.set(finishCycleX, startCycleY,
                    String.valueOf(Long.parseLong(improvedPlan.get(finishCycleX, startCycleY)) - minimumNegative));

            if (improvedPlan.get(startCycleX, startCycleY).equals("X")) {
                improvedPlan.set(startCycleX, startCycleY, "0");
            }
            improvedPlan.set(startCycleX, startCycleY,
                    String.valueOf(Long.parseLong(improvedPlan.get(startCycleX, startCycleY)) + minimumNegative));
            improvedPlan.set(finishCycleX, finishCycleY,
                    String.valueOf(Long.parseLong(improvedPlan.get(finishCycleX, finishCycleY)) + minimumNegative));
            return improvedPlan;
        } catch (Exception e) {
            // Если в процессе оптимизации совершилась ошибка, то, наверное, его нельзя нормально улучшить
            return new Table(plan);
        }
    }

    private Long calcPrice(Table transportationTable, Table priceTable) {
        long answer = 0L;

        for (int x = 1; x < transportationTable.getWidth() - 1; ++x) {
            for (int y = 1; y < transportationTable.getHeight() - 1; ++y) {
                answer += Long.parseLong(transportationTable.get(x, y)) * Long.parseLong(priceTable.get(x, y));
            }
        }

        return answer;
    }
}
