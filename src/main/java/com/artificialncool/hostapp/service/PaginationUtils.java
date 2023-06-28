package com.artificialncool.hostapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

public class PaginationUtils {
    public static <T> Page<T> getPage(List<T> list, int pageNumber, int pageSize) {
        int totalItems = list.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        if (fromIndex >= totalItems || pageNumber > totalPages) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(pageNumber, pageSize), totalItems); // Empty page if fromIndex is out of bounds
        } else {
            List<T> pageList = list.subList(fromIndex, toIndex);
            return new PageImpl<>(pageList, PageRequest.of(pageNumber, pageSize), totalItems);

        }
    }
}