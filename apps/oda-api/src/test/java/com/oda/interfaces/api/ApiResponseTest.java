package com.oda.interfaces.api;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void 성공_응답_생성() {
        ApiResponse<String> response = ApiResponse.success("hello");
        assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS);
        assertThat(response.data()).isEqualTo("hello");
        assertThat(response.meta().errorCode()).isNull();
        assertThat(response.meta().message()).isNull();
    }

    @Test
    void 데이터_없는_성공_응답() {
        ApiResponse<?> response = ApiResponse.success();
        assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS);
        assertThat(response.data()).isNull();
        assertThat(response.meta().errorCode()).isNull();
    }

    @Test
    void 에러_응답_생성() {
        ApiResponse<Object> response = ApiResponse.fail("NOT_FOUND", "리소스를 찾을 수 없습니다");
        assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL);
        assertThat(response.data()).isNull();
        assertThat(response.meta().errorCode()).isEqualTo("NOT_FOUND");
        assertThat(response.meta().message()).isEqualTo("리소스를 찾을 수 없습니다");
    }
}
