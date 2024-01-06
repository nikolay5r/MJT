package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BaseTable implements Table {
    private Map<String, Column> columns;
    private int rowsCount = 0;

    public BaseTable() {
        this.columns = new HashMap<>();
    }

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null) {
            throw new IllegalArgumentException("Data is null.");
        }

        if (columns.isEmpty()) {
            for (String colName: data) {
                columns.putIfAbsent(colName, new BaseColumn());
            }
        } else {
            if (data.length > columns.size()) {
                throw new CsvDataNotCorrectException("The data you want to put in is more than the table size.");
            }

            int index = 0;
            for (String colName: columns.keySet()) {
                columns.get(colName).addData(data[index++]);
            }
        }

        rowsCount++;
    }

    @Override
    public Collection<String> getColumnNames() {
        return columns.keySet();
    }

    @Override
    public Collection<String> getColumnData(String column) {
        if (column == null || column.isBlank()) {
            throw new IllegalArgumentException("Column name is null or empty.");
        }

        if (columns.containsKey(column)) {
            throw new IllegalArgumentException("No such column in base table.");
        }

        return columns.get(column).getData();
    }

    @Override
    public int getRowsCount() {
        return rowsCount;
    }
}
