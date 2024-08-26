package group1.intern.bean;

import org.apache.poi.ss.usermodel.Row;

public interface ExcelReader<T> {
    T fromRow(Row row);
}
