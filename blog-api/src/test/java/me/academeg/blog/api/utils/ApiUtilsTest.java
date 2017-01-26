package me.academeg.blog.api.utils;

import me.academeg.blog.api.Constants;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.api.common.ApiResultWithData;
import me.academeg.blog.api.common.ArbitraryResult;
import me.academeg.blog.api.common.CollectionResult;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;


/**
 * ApiUtilsTest
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class ApiUtilsTest {

    @Test
    public void parseSorts_EmptyString_NoSorts() throws Exception {
        assertThat(ApiUtils.parseSorts("")).isNull();
    }

    @Test
    public void parseSorts() throws Exception {
        assertThat(ApiUtils.parseSorts("id"))
            .extracting(Sort.Order::getProperty, Sort.Order::getDirection)
            .containsExactly(tuple("id", Sort.Direction.ASC));

        assertThat(ApiUtils.parseSorts("id:DeSC, name, Column:DEsc"))
            .extracting(Sort.Order::getProperty, Sort.Order::getDirection)
            .containsExactly(
                tuple("id", Sort.Direction.DESC),
                tuple("name", Sort.Direction.ASC),
                tuple("Column", Sort.Direction.DESC)
            );
    }

    @Test
    public void createPageRequest_AllNulls() throws Exception {
        Pageable pageRequest = ApiUtils.createPageRequest(null, null, null);

        assertThat(pageRequest.getPageSize()).isEqualTo(Constants.DEFAULT_DATA_PAGE_SIZE);
        assertThat(pageRequest.getPageNumber()).isEqualTo(0);
        assertThat(pageRequest.getSort()).isNull();
    }

    @Test
    public void createPageRequest_Limit10_Page5() throws Exception {
        Pageable pageRequest = ApiUtils.createPageRequest(10, 5, null);

        assertThat(pageRequest.getPageSize()).isEqualTo(10);
        assertThat(pageRequest.getPageNumber()).isEqualTo(5);
        assertThat(pageRequest.getSort()).isNull();
    }

    @Test
    public void createPageRequest_Limit45_Page15_OrderBy() throws Exception {
        Pageable pageRequest = ApiUtils.createPageRequest(45, 15, "id, name, surname: DESc ");

        assertThat(pageRequest.getPageSize()).isEqualTo(45);
        assertThat(pageRequest.getPageNumber()).isEqualTo(15);
        assertThat(pageRequest.getSort())
            .extracting(Sort.Order::getProperty, Sort.Order::getDirection)
            .containsExactly(
                tuple("id", Sort.Direction.ASC),
                tuple("name", Sort.Direction.ASC),
                tuple("surname", Sort.Direction.DESC)
            );
    }

    @Test
    public void okResult() throws Exception {
        ApiResult result = ApiUtils.okResult();

        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getStatus()).isEqualTo(0);
    }

    @Test
    public void okSingleResult() throws Exception {
        ApiResultWithData<ArbitraryResult<String>> result = ApiUtils.singleResult("Simple string");

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).isEqualTo("Simple string");
    }

    @Test
    public void okListResultWithoutTotal() throws Exception {
        ArrayList<String> data = new ArrayList<>();
        data.add("Moscow");
        data.add("Saint. P.");
        data.add("Murmansk");

        ApiResultWithData<CollectionResult<String>> result = ApiUtils.listResult(data);

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).containsExactly(
            "Moscow",
            "Saint. P.",
            "Murmansk"
        );
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo((long) data.size());
    }

    @Test
    public void okListResultWithTotal() throws Exception {
        ArrayList<String> data = new ArrayList<>();
        data.add("Moscow");
        data.add("Saint. P.");
        data.add("Murmansk");

        ApiResultWithData<CollectionResult<String>> result = ApiUtils.listResult(data, 100);

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).containsExactly(
            "Moscow",
            "Saint. P.",
            "Murmansk"
        );
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(100L);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void okListResultWithPage() throws Exception {
        ArrayList<String> data = new ArrayList<>();
        data.add("Moscow");
        data.add("Saint. P.");
        data.add("Murmansk");

        Page<String> page = (Page<String>) Mockito.mock(Page.class);
        Mockito.when(page.getTotalElements()).thenReturn(100L);
        Mockito.when(page.getContent()).thenReturn(data);

        ApiResultWithData<CollectionResult<String>> result = ApiUtils.listResult(page);
        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).containsExactly(
            "Moscow",
            "Saint. P.",
            "Murmansk"
        );
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(100L);
    }
}
