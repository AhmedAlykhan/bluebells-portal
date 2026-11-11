package com.bluebells.assistant;

import com.bluebells.assistant.dto.AssistantFaqDto;
import com.bluebells.assistant.dto.AssistantQueryRequest;
import com.bluebells.assistant.dto.AssistantQueryResponse;
import com.bluebells.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assistant")
@Tag(name = "AI-Ready School Assistant", description = "Verified knowledge base and virtual school guide")
public class SchoolAssistantController {

    private final SchoolAssistantService assistantService;

    public SchoolAssistantController(SchoolAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/query")
    @Operation(summary = "Ask the verified virtual school assistant a question")
    public ResponseEntity<ApiResponse<AssistantQueryResponse>> queryAssistant(@Valid @RequestBody AssistantQueryRequest req) {
        AssistantQueryResponse response = assistantService.answerQuery(req);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/faq-topics")
    @Operation(summary = "Get official FAQ categories and suggested questions")
    public ResponseEntity<ApiResponse<List<AssistantFaqDto>>> getFaqTopics() {
        List<AssistantFaqDto> faqs = assistantService.getFaqCategories();
        return ResponseEntity.ok(ApiResponse.success(faqs));
    }
}
