package group1.intern.repository.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BaseRepository<T> {
    List<T> fetchAllDataWithoutPagination(List<WhereElements> whereElements, Sort sort, String... relationships);

    Page<T> fetchAllDataWithPagination(List<WhereElements> whereElements, Pageable pageable, String... relationships);

    List<T> fetchAllDataWithFirstQuery(List<WhereElements> whereElements, String baseQuery, Sort sort, Pageable pageable);
}
