package group1.intern.service;

import group1.intern.bean.ExcelReader;
import group1.intern.util.excel.ExportExcel;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

public interface ExcelService {
    <T extends ExcelReader<T>> List<T> readerExcelFile(MultipartFile file, Class<T> clazz) throws IOException, Exception;

    static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    static double getCellValueAsNumeric(Cell cell) {
        if (cell == null) return 0;
        return cell.getNumericCellValue();
    }

    ExportExcel exportFileExcelStatistic(YearMonth yearMonth);
}
