package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ParentAgentSessionDetailDTO;
import com.englishapp.dto.ParentAgentSessionPageDTO;
import com.englishapp.dto.ParentAgentTopicDTO;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.ai.record.ParentAgentService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 家长查看与删除学习伙伴记录：需家长二次验证，只能看本家庭 */
@RestController
@RequestMapping("/api/v1/parent")
public class ParentAgentController {

    private final CurrentChild current;
    private final ParentAgentService service;

    public ParentAgentController(CurrentChild current, ParentAgentService service) {
        this.current = current;
        this.service = service;
    }

    @GetMapping("/agent-sessions")
    public Result<ParentAgentSessionPageDTO> list(@RequestParam(required = false) Integer childId,
                                                  @RequestParam(required = false) Boolean flagged,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return Result.success(service.list(verifiedFamily(), childId, flagged, page, size));
    }

    @GetMapping("/agent-sessions/{id}")
    public Result<ParentAgentSessionDetailDTO> detail(@PathVariable long id) {
        return Result.success(service.detail(verifiedFamily(), id));
    }

    @DeleteMapping("/agent-sessions/{id}")
    public Result<Void> delete(@PathVariable long id) {
        service.delete(verifiedFamily(), id);
        return Result.success(null);
    }

    @GetMapping("/learning-topics")
    public Result<List<ParentAgentTopicDTO>> topics(@RequestParam(required = false) Integer childId) {
        return Result.success(service.topics(verifiedFamily(), childId));
    }

    private long verifiedFamily() {
        current.requireParentVerified();
        return current.requireFamilyId();
    }
}
