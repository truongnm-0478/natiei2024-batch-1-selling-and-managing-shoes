package group1.intern.util.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFPieChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.*;

import group1.intern.bean.RevenueInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportExcel {

    private XSSFWorkbook workbook;
    private List<XSSFSheet> sheets;
    private int currentRow;
    private int currentColumn;

    public ExportExcel(List<String> sheetNames) {
        workbook = new XSSFWorkbook();
        sheets = new ArrayList<>();
        for (var sheetName : sheetNames) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            sheets.add(sheet);
        }
        currentRow = 0;
        currentColumn = 0;
    }

    public ExportExcel(List<String> sheetNames, int currentRow, int currentColumn) {
        workbook = new XSSFWorkbook();
        sheets = new ArrayList<>();
        for (var sheetName : sheetNames) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            sheets.add(sheet);
        }
        this.currentRow = currentRow;
        this.currentColumn = currentColumn;
    }

    public Cell addCell(int sheetIndex, String value, int startRow, int startCol, XSSFCellStyle style) {
        Cell cell = null;

        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            Row row = sheet.getRow(startRow);
            if (row == null) {
                row = sheet.createRow(startRow);
            }

            cell = row.createCell(startCol);
            cell.setCellValue(value);
            cell.setCellStyle(style);
        }

        return cell;
    }

    public Cell createHeaderRow(int sheetIndex, List<String> headers) {
        Cell cell = null;

        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            int indexRow = currentRow++;
            Row row = sheet.getRow(indexRow);
            if (row == null) {
                row = sheet.createRow(indexRow);
            }
            XSSFCellStyle style = createBoldBlueStyle();

            for (int i = currentColumn; i < headers.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(style);
            }
        }

        return cell;
    }

    public Cell createHeaderRow(int sheetIndex, List<String> headers, int startRow, int startCol) {
        Cell cell = null;

        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            Row row = sheet.getRow(startRow);
            if (row == null) {
                row = sheet.createRow(startRow);
            }
            XSSFCellStyle style = createBoldBlueStyle();

            for (int i = startCol; i < headers.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(style);
            }
        }

        return cell;
    }

    public Cell createHeaderRowByStartRow(int sheetIndex, List<String> headers, int startRow) {
        Cell cell = null;
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            Row row = sheet.createRow(startRow);
            XSSFCellStyle style = createBoldBlueStyle();

            for (int i = currentColumn; i < headers.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(style);
            }
        }
        return cell;
    }

    public Cell createHeaderRowByStartCol(int sheetIndex, List<String> headers, int startCol) {
        Cell cell = null;
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            int indexRow = currentRow++;
            Row row = sheet.getRow(indexRow);
            if (row == null) {
                row = sheet.createRow(indexRow);
            }
            XSSFCellStyle style = createBoldBlueStyle();

            for (int i = startCol; i < headers.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(style);
            }
        }
        return cell;
    }

    public Cell createHeaderCol(int sheetIndex, List<String> headers, int startRow, int startCol) {
        Cell cell = null;
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            XSSFCellStyle style = createBoldBlueStyle();

            for (int i = 0; i < headers.size(); i++) {
                Row row = sheet.getRow(startRow + i);
                if (row == null) {
                    row = sheet.createRow(startRow + i);
                }
                cell = row.createCell(startCol);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(style);
            }
        }
        return cell;
    }

    public Cell addRow(int sheetIndex, List<String> data) {
        Cell cell = null;
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            int indexRow = currentRow++;
            Row row = sheet.getRow(indexRow);
            if (row == null) {
                row = sheet.createRow(indexRow);
            }
            XSSFCellStyle style = createBoldStyle();

            for (int i = currentColumn; i < data.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(data.get(i));
                cell.setCellStyle(style);
            }
        }
        return cell;
    }

    public Cell addRowByStartCol(int sheetIndex, List<String> data, int startCol) {
        Cell cell = null;
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            int indexRow = currentRow++;
            Row row = sheet.getRow(indexRow);
            if (row == null) {
                row = sheet.createRow(indexRow);
            }
            XSSFCellStyle style = createBoldStyle();

            for (int i = startCol; i < data.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(data.get(i));
                cell.setCellStyle(style);
            }
        }
        return cell;
    }

    public Cell addRowByStartRow(int sheetIndex, List<String> data, int startRow) {
        Cell cell = null;
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            Row row = sheet.getRow(startRow);
            if (row == null) {
                row = sheet.createRow(startRow);
            }
            XSSFCellStyle style = createBoldStyle();

            for (int i = currentColumn++; i < data.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(data.get(i));
                cell.setCellStyle(style);
            }
        }
        return cell;
    }

    public Cell addRow(int sheetIndex, List<String> data, int startRow, int startCol) {
        Cell cell = null;
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            Row row = sheet.getRow(startRow);
            if (row == null) {
                row = sheet.createRow(startRow);
            }
            XSSFCellStyle style = createBoldStyle();

            for (int i = startCol; i < data.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(data.get(i));
                cell.setCellStyle(style);
            }
        }
        return cell;
    }

    public Cell addCol(int sheetIndex, List<String> data, int startRow, int startCol) {
        Cell cell = null;
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            XSSFCellStyle style = createBoldStyle();

            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.getRow(startRow + i);
                if (row == null) {
                    row = sheet.createRow(startRow + i);
                }
                cell = row.createCell(startCol);
                cell.setCellValue(data.get(i));
                cell.setCellStyle(style);
            }
        }
        return cell;
    }

    public XSSFCellStyle createBoldBlueStyle() {
        XSSFCellStyle style = createBoldStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(new XSSFColor(Color.BLUE, null));
        style.setFont(font);

        return style;
    }

    public XSSFCellStyle createBoldRedStyle() {
        XSSFCellStyle style = createBoldStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(new XSSFColor(Color.RED, null));
        style.setFont(font);

        return style;
    }

    public XSSFCellStyle createBoldStyle() {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    public void mergeCells(int sheetIndex, int startRow, int startCol, int endRow, int endCol) {
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
    
            CellRangeAddress cellRangeAddress = new CellRangeAddress(
                startRow,  
                endRow,    
                startCol,  
                endCol     
            );
    
            // Gộp ô
            sheet.addMergedRegion(cellRangeAddress);
        }
    }    

    public void addMergedCell(int sheetIndex, String value, int startRow, int startCol, int endRow, int endCol, XSSFCellStyle style) {
        // Gộp các ô
        mergeCells(sheetIndex, startRow, startCol, endRow, endCol);
    
        // Thêm nội dung vào ô gộp
        if (!sheets.isEmpty() && sheets.size() > sheetIndex) {
            XSSFSheet sheet = sheets.get(sheetIndex);
            Row row = sheet.getRow(startRow);
            if (row == null) {
                row = sheet.createRow(startRow);
            }
            Cell cell = row.createCell(startCol);
            cell.setCellValue(value);
            cell.setCellStyle(style);  // hoặc sử dụng bất kỳ style nào bạn muốn
        }
    }
    
    public void export(HttpServletResponse response, String fileName) throws IOException {
        for(int i = 0; i < sheets.size() ; i++){
            XSSFSheet  sheet = sheets.get(i);
            for (int colIndex = 0; colIndex < sheet.getRow(0).getLastCellNum(); colIndex++) {
                sheet.autoSizeColumn(colIndex);
            }
        }
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void createDailyRevenueColumnChart(int sheetIndex, int startRow, int endRow, int startCol, int endCol, Map<String, RevenueInfo> dailyRevenue) {
        
        XSSFSheet sheet = sheets.get(sheetIndex);
        
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, endCol + 1, startRow, endCol + 11, endRow + 10);

        // Create the chart object
        XSSFChart chart = ((XSSFDrawing) drawing).createChart(anchor);
        chart.setTitleText("Doanh thu theo tháng");
        chart.setTitleOverlay(false);

        // Populate sheet with dailyRevenue data starting at startRow, startCol
        int rowIdx = startRow;
        for (Map.Entry<String, RevenueInfo> entry : dailyRevenue.entrySet()) {
            Row row = sheet.createRow(rowIdx++);
            Cell cell1 = row.createCell(startCol);
            cell1.setCellValue(entry.getKey()); // Day

            Cell cell2 = row.createCell(startCol + 1);
            cell2.setCellValue(entry.getValue().getCurrentRevenue()); // Revenue
        }

        // Define the data for the chart
        XDDFDataSource<String> days = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(startRow, endRow, startCol, startCol)); // Days in specified column range
        XDDFNumericalDataSource<Double> revenues = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(startRow, endRow, startCol + 1, startCol + 1)); // Revenues in specified column range

        // Create a data series for the chart
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.LEFT);
        bottomAxis.setTitle("Ngày");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.BOTTOM);
        leftAxis.setTitle("Doanh thu");
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        XDDFChartData data = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFChartData.Series series = data.addSeries(days, revenues);
        series.setTitle("Doanh thu theo tháng", null);

        chart.plot(data);
    }

    public void createMonthlyRevenuePieChart(int sheetIndex, int startRow, int endRow, int startCol, int endCol, Map<String, RevenueInfo> monthlyRevenue) {
        XSSFSheet sheet = sheets.get(sheetIndex);
        
        // Xác định vị trí của biểu đồ tròn trên trang tính
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, endCol + 2, startRow, endCol + 12, endRow + 10);
    
        // Tạo đối tượng biểu đồ
        XSSFChart chart = ((XSSFDrawing) drawing).createChart(anchor);
        chart.setTitleText("Doanh thu theo năm");
        chart.setTitleOverlay(false);
    
        // Thêm chú thích cho biểu đồ
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT); // Đặt vị trí của chú thích
    
        // Điền dữ liệu vào sheet với monthlyRevenue bắt đầu từ startRow, startCol
        int rowIdx = startRow;
        for (Map.Entry<String, RevenueInfo> entry : monthlyRevenue.entrySet()) {
            Row row = sheet.createRow(rowIdx++);
            Cell cell1 = row.createCell(startCol);
            cell1.setCellValue(entry.getKey()); // Tháng
    
            Cell cell2 = row.createCell(startCol + 1);
            cell2.setCellValue(entry.getValue().getCurrentRevenue()); // Doanh thu
        }
    
        // Xác định dữ liệu cho biểu đồ tròn
        XDDFDataSource<String> months = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(startRow, endRow, startCol, startCol)); // Tháng trong phạm vi cột được chỉ định
        XDDFNumericalDataSource<Double> revenues = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(startRow, endRow, startCol + 1, startCol + 1)); // Doanh thu trong phạm vi cột được chỉ định
    
        // Tạo chuỗi dữ liệu cho biểu đồ tròn
        XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
        XDDFChartData.Series series = data.addSeries(months, revenues);
        series.setTitle("Doanh thu theo năm", null);
    
        chart.plot(data);
    }
    
}
