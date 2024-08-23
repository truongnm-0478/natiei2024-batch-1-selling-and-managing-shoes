package group1.intern.bean;

import group1.intern.util.util.PaginationUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountFilter {
    private int page = 1;
    private String order = "asc";
    // role = "customer" | "manager"
    private String role = "customer";
    private String sortBy = "role";
    private String query = "";

    public String buildQueryString() {
        StringBuilder queryString = new StringBuilder();
        queryString.append("?page=").append(page)
            .append("&order=").append(order)
            .append("&role=").append(role)
            .append("&sortBy=").append(sortBy)
            .append("&query=").append(query);
        return queryString.toString();
    }

    public PaginationUtil createPaginationUtil(int totalElements, int pageSize, int displayPageNum) {
        return new PaginationUtil(
            totalElements,
            pageSize,
            page,
            displayPageNum,
            buildQueryString()
        );
    }

}
