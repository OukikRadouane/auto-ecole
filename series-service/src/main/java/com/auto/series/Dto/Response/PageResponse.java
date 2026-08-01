package com.auto.series.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@Builder
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <E, D> PageResponse<D> from(Page<E> entityPage, Function<E, D> mapper) {
        return PageResponse.<D>builder()
                .content(entityPage.getContent().stream().map(mapper).toList())
                .page(entityPage.getNumber())
                .size(entityPage.getSize())
                .totalElements(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .last(entityPage.isLast())
                .build();
    }
}
