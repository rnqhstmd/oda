package com.oda.interfaces.api;

import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApiControllerAdviceTest {

    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/not-found")
        String notFound() { throw new CoreException(ErrorType.NOT_FOUND, "테스트 리소스를 찾을 수 없습니다"); }

        @GetMapping("/bad-request")
        String badRequest() { throw new IllegalArgumentException("잘못된 요청입니다"); }

        @GetMapping("/server-error")
        String serverError() { throw new RuntimeException("서버 내부 오류"); }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new TestController())
            .setControllerAdvice(new ApiControllerAdvice())
            .build();
    }

    @Test
    void NotFoundException_404_응답() throws Exception {
        mockMvc.perform(get("/test/not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.meta.result").value("FAIL"))
            .andExpect(jsonPath("$.meta.errorCode").value("NOT_FOUND"))
            .andExpect(jsonPath("$.meta.message").value("테스트 리소스를 찾을 수 없습니다"));
    }

    @Test
    void IllegalArgumentException_400_응답() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.meta.result").value("FAIL"))
            .andExpect(jsonPath("$.meta.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void RuntimeException_500_응답() throws Exception {
        mockMvc.perform(get("/test/server-error"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.meta.result").value("FAIL"))
            .andExpect(jsonPath("$.meta.errorCode").value("INTERNAL_ERROR"));
    }
}
