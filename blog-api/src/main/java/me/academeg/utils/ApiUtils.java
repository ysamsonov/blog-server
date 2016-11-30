package me.academeg.utils;

import me.academeg.Constants;
import me.academeg.common.ApiResult;
import me.academeg.common.ArbitraryResult;
import me.academeg.common.CollectionResult;
import me.academeg.common.ResultFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * ApiUtils
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
final public class ApiUtils {

    public static Sort parseSorts(final String orderByExpr) {
        if (StringUtils.isEmpty(orderByExpr)) {
            return null;
        }

        Sort result = null;

        final List<String> segments = Arrays.stream(orderByExpr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        for (final String sorts : segments) {
            final String[] sortParts = sorts.split(":");
            for (int i = 0; i < sortParts.length; i++) {
                sortParts[i] = sortParts[i].trim();
            }

            if (sortParts.length < 1) {
                throw new IllegalArgumentException();
            }

            final Sort sort = new Sort(Sort.Direction.fromString(sortParts.length == 2 ? sortParts[1] : "asc"), sortParts[0]);
            if (result == null) {
                result = sort;
            } else {
                result = result.and(sort);
            }
        }
        return result;
    }

    public static Pageable createPageRequest(final Integer limit, final Integer pageNum, final String orderBy) {
        final int pageSize = ofNullable(limit).orElse(Constants.DEFAULT_DATA_PAGE_SIZE);
        final int page = ofNullable(pageNum).orElse(0);
        final Sort sort = parseSorts(orderBy);
        return new PageRequest(page, pageSize, sort);
    }

    public static <T> ApiResult singleResult(final T result) {
        return ResultFactory.build().ok(new ArbitraryResult<>(result));
    }

    public static <T> ApiResult listResult(final Collection<T> result, final long total) {
        return ResultFactory.build().ok(new CollectionResult<>(result, total));
    }

    public static <T> ApiResult listResult(final Page<T> page) {
        return ResultFactory.build().ok(new CollectionResult<>(page.getContent(), page.getTotalElements()));
    }

    public static <T> ApiResult listResult(final Collection<T> resultCollection) {
        return listResult(resultCollection, (long) resultCollection.size());
    }
}
