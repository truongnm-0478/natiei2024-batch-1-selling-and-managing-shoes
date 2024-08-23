package group1.intern.bean;

import group1.intern.service.ExcelService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Row;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductExcel implements ExcelReader<ProductExcel>{
    private String name;
    private String category;
    private String material;
    private Integer originPrice;
    private String gender;
    private String color;
    private String style;
    private String url;
    private String quantity;

    @Override
    public ProductExcel fromRow(Row row) {
        return ProductExcel.builder()
            .name(ExcelService.getCellValueAsString(row.getCell(0)))
            .category(ExcelService.getCellValueAsString(row.getCell(1)))
            .material(ExcelService.getCellValueAsString(row.getCell(2)))
            .originPrice((int) ExcelService.getCellValueAsNumeric(row.getCell(3)))
            .gender(ExcelService.getCellValueAsString(row.getCell(4)))
            .color(ExcelService.getCellValueAsString(row.getCell(5)))
            .style(ExcelService.getCellValueAsString(row.getCell(6)))
            .url(ExcelService.getCellValueAsString(row.getCell(7)))
            .quantity(ExcelService.getCellValueAsString(row.getCell(8)))
            .build();
    }
}
