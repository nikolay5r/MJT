package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

public class CsvProcessor implements CsvProcessorAPI {
    private Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String row = null;
            while ((row = bufferedReader.readLine()) != null) {
                if (!row.contains(delimiter)) {
                    throw new CsvDataNotCorrectException("Csv doesn't contain the right delimiter.");
                }

                String[] data = row.split(delimiter);

                for (int i = 0; i < data.length; i++) {
                    if (data[i].isBlank()) {
                        throw new CsvDataNotCorrectException("Csv cannot contain empty values.");
                    }

                    data[i] = data[i].strip();
                }

                table.addData(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        MarkdownTablePrinter printer = new MarkdownTablePrinter();
        Collection<String> rows = printer.printTable(table, alignments);

        try {
            for (String row : rows) {
                writer.write(row);
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
