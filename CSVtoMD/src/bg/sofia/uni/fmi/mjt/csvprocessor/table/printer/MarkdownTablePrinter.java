package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MarkdownTablePrinter implements TablePrinter {
    public MarkdownTablePrinter() {

    }

    private int getMaxLengthOfColumn(Collection<String> column) {
        int maxLength = 0;
        for (String str : column) {
            maxLength = Math.max(maxLength, str.length());
        }

        return maxLength;
    }

    private String getNoAlignment(int numberOfDashes) {
        return "-".repeat(numberOfDashes);
    }

    private String getLeftAlignment(int numberOfDashes) {
        return ":" + ("-".repeat(numberOfDashes));
    }

    private String getRightAlignment(int numberOfDashes) {
        return ("-".repeat(numberOfDashes)) + ":";
    }

    private String getCenterAlignment(int numberOfDashes) {
        return ":" + ("-".repeat(numberOfDashes)) + ":";
    }

    private String printAlignments(ColumnAlignment[] alignments, List<Integer> maxLengths) {
        StringBuilder printed = new StringBuilder("|");

        for (int i = 0; i < maxLengths.size(); i++) {
            printed.append(" ");
            if (i >= alignments.length) {
                printed.append(getNoAlignment(maxLengths.get(i)));
            } else {
                printed.append(
                    switch (alignments[i]) {
                        case LEFT -> getLeftAlignment(maxLengths.get(i) - 1);
                        case CENTER -> getCenterAlignment(maxLengths.get(i) - 2);
                        case RIGHT -> getRightAlignment(maxLengths.get(i) - 1);
                        case NOALIGNMENT -> getNoAlignment(maxLengths.get(i));
                    }
                );
            }
            printed.append(" |");
        }

        return printed.toString();
    }

    private String printRow(List<String> row, List<Integer> maxLengths) {
        StringBuilder printed = new StringBuilder("|");

        for (int i = 0; i < row.size(); i++) {
            printed.append(" ")
                    .append(row.get(i))
                    .append(" ".repeat(maxLengths.get(i) - row.get(i).length()))
                    .append(" |");
        }

        return printed.toString();
    }

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        ArrayList<String> columnNames = new ArrayList<>(table.getColumnNames());
        ArrayList<Integer> maxLengthsOfCols = new ArrayList<>();
        ArrayList<String> printedTable = new ArrayList<>();

        ArrayList<ArrayList<String>> tableValues = new ArrayList<>();
        for (int i = 0; i < table.getRowsCount(); i++) {
            tableValues.add(new ArrayList<>());
        }
        //get all maxLengths
        for (String colName : columnNames) {
            Collection<String> data = table.getColumnData(colName);
            maxLengthsOfCols.add(Math.max(getMaxLengthOfColumn(data), colName.length()));
        }
        // get all values
        for (String columnName: columnNames) {
            ArrayList<String> colVals = new ArrayList<>(table.getColumnData(columnName));
            for (int i = 0; i < colVals.size(); i++) {
                tableValues.get(i).add(colVals.get(i));
            }
        }

        printedTable.add(printRow(columnNames, maxLengthsOfCols));
        printedTable.add(printAlignments(alignments, maxLengthsOfCols));
        for (ArrayList<String> rowValues: tableValues) {
            printedTable.add(printRow(rowValues, maxLengthsOfCols));
        }

        return Collections.unmodifiableCollection(printedTable);
    }
}
