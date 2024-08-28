package group1.intern.service.impl;

import group1.intern.bean.ExcelReader;
import group1.intern.bean.RevenueInfo;
import group1.intern.service.ExcelService;
import group1.intern.service.OrdersService;
import group1.intern.util.excel.ExportExcel;
import group1.intern.util.util.CommonUtils;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    public OrdersService ordersService;

    public <T extends ExcelReader<T>> List<T> readerExcelFile(MultipartFile file, Class<T> clazz) throws IOException, Exception {
        List<T> result = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            result.add((T) clazz.getDeclaredConstructor().newInstance().fromRow(row));
        }


        workbook.close();
        return result;
    }

    @Override
    public ExportExcel exportFileExcelStatistic(YearMonth yearMonth) {
        RevenueInfo revenueInfo = ordersService.getMonthlyRevenue(yearMonth);
                Map<String, RevenueInfo> dailyRevenue = ordersService.getDailyRevenueDetails(yearMonth);
                Map<String, RevenueInfo> monthlyRevenue = ordersService.getMonthlyRevenueDetails(yearMonth.getYear());
                // Tạo file Excel mới

                List<String> sheetNames = Arrays.asList("Số liệu thống kê", "Biểu đồ thống kê");
                ExportExcel exportExcel = new ExportExcel(sheetNames, 0, 0);

                XSSFCellStyle styleHeaderRed = exportExcel.createBoldRedStyle();

                exportExcel.addMergedCell(0, "Tổng quát thông tin trong tháng", 0, 0, 0, 2, styleHeaderRed);
                exportExcel.createHeaderRow(0,
                                Arrays.asList("Số lượng đơn hàng", "Doanh thu hiện tại", "Doanh thu dự tính"), 1,
                                0);
                exportExcel.addRow(0,
                                Arrays.asList(revenueInfo.getOrderCount() + "",
                                                CommonUtils.formatToVND(
                                                                (int) Math.floor(revenueInfo.getCurrentRevenue())),
                                                CommonUtils.formatToVND(
                                                                (int) Math.floor(revenueInfo.getPredictedRevenue()))),
                                2, 0);

                AtomicInteger index = new AtomicInteger(4);
                int firstIndex = index.getAndIncrement();
                exportExcel.addMergedCell(0, "Bảng doanh thu trong tháng", firstIndex, 0, firstIndex, 2,
                                styleHeaderRed);
                exportExcel.createHeaderRow(0, Arrays.asList("Ngày", "Số lượng đơn hàng", "Doanh thu"),
                                index.getAndIncrement(),
                                0);
                for (Map.Entry<String, RevenueInfo> entry : dailyRevenue.entrySet()) {
                        String key = entry.getKey();
                        RevenueInfo value = entry.getValue();
                        exportExcel.addRow(0,
                                        Arrays.asList(key, value.getOrderCount() + "",
                                                        CommonUtils.formatToVND(
                                                                        (int) Math.floor(value.getCurrentRevenue()))),
                                        index.getAndIncrement(), 0);
                }
                int sheetIndex = 1;
                int startRow = 0;
                int endRow = dailyRevenue.size() -1 ;
                int startCol = 0;
                int endCol = 1;
                exportExcel.createDailyRevenueColumnChart(sheetIndex, startRow, endRow, startCol, endCol, dailyRevenue);

                index.getAndIncrement();
                index.getAndIncrement();

                firstIndex = index.getAndIncrement();
                exportExcel.addMergedCell(0, "Bảng doanh thu trong năm", firstIndex, 0, firstIndex, 2, styleHeaderRed);
                exportExcel.createHeaderRow(0, Arrays.asList("Ngày", "Số lượng đơn hàng", "Doanh thu"),
                                index.getAndIncrement(),
                                0);
                for (Map.Entry<String, RevenueInfo> entry : monthlyRevenue.entrySet()) {
                        String key = entry.getKey();
                        RevenueInfo value = entry.getValue();
                        exportExcel.addRow(0,
                                        Arrays.asList(key, value.getOrderCount() + "",
                                                        CommonUtils.formatToVND(
                                                                        (int) Math.floor(value.getCurrentRevenue()))),
                                        index.getAndIncrement(), 0);
                }

                sheetIndex = 1;
                startRow = dailyRevenue.size() + 2;
                endRow = startRow + monthlyRevenue.size() - 1;
                startCol = 0;
                endCol = 1;
                exportExcel.createMonthlyRevenuePieChart(sheetIndex, startRow, endRow, startCol, endCol, monthlyRevenue);

                return exportExcel;
    }
}
