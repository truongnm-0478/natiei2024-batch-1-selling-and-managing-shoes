package group1.intern.util.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtil {

    private final int totalItems;
    private final int itemsPerPage;
    private final int currentPage;
    private final int midRange;
    private final String buildQueryString;

    public PaginationUtil(int totalItems, int itemsPerPage, int currentPage, int midRange, String buildQueryString) {
        this.totalItems = totalItems;
        this.itemsPerPage = itemsPerPage;
        this.currentPage = currentPage;
        this.midRange = midRange;
        this.buildQueryString = buildQueryString;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    public List<Integer> getPageNumbers() {
        List<Integer> pageNumbers = new ArrayList<>();
        int totalPages = getTotalPages();

        if (totalPages <= midRange) {
            // Show all pages
            for (int i = 1; i <= totalPages; i++) {
                pageNumbers.add(i);
            }
        } else {
            int startPage, endPage;

            if (currentPage <= midRange / 2) {
                // Display initial pages
                startPage = 1;
                endPage = midRange;
            } else if (currentPage > totalPages - midRange / 2) {
                // Display ending pages
                startPage = totalPages - midRange + 1;
                endPage = totalPages;
            } else {
                // Display middle pages
                startPage = currentPage - midRange / 2;
                endPage = currentPage + midRange / 2;

                if (startPage < 1) {
                    startPage = 1;
                    endPage = midRange;
                }

                if (endPage > totalPages) {
                    endPage = totalPages;
                    startPage = totalPages - midRange + 1;
                }
            }

            for (int i = startPage; i <= endPage; i++) {
                pageNumbers.add(i);
            }
        }

        return pageNumbers;
    }

    public String getPageUrl(int pageNumber) {
        if(buildQueryString.contains("?")){
            return buildQueryString + "&page=" + pageNumber;
        }
        return buildQueryString + "?page=" + pageNumber;
    }

    public String getPrevPageUrl() {
        return currentPage > 1 ? getPageUrl(currentPage - 1) : null;
    }

    public String getNextPageUrl() {
        return currentPage < getTotalPages() ? getPageUrl(currentPage + 1) : null;
    }

    public String getFirstPageUrl() {
        return getPageUrl(1);
    }

    public String getLastPageUrl() {
        return getPageUrl(getTotalPages());
    }
}
