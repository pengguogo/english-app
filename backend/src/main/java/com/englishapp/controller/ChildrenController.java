package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ChildRequestDTO;
import com.englishapp.dto.ChildResponseDTO;
import com.englishapp.dto.MeResponseDTO;
import com.englishapp.dto.SelectChildRequestDTO;
import com.englishapp.security.AuthPrincipal;
import com.englishapp.security.SessionCurrentChild;
import com.englishapp.service.AuthService;
import com.englishapp.service.ChildrenService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/** 本家庭孩子档案：列表、新增、切换当前孩子 */
@RestController
@RequestMapping("/api/v1/children")
public class ChildrenController {
    private final ChildrenService children;
    private final AuthService auth;
    private final SessionCurrentChild current;

    public ChildrenController(ChildrenService children, AuthService auth, SessionCurrentChild current) {
        this.children = children;
        this.auth = auth;
        this.current = current;
    }

    @GetMapping
    public Result<List<ChildResponseDTO>> list() {
        return Result.success(children.list(current.requireFamilyId()));
    }

    @PostMapping
    public Result<ChildResponseDTO> create(@Valid @RequestBody ChildRequestDTO request) {
        return Result.success(children.create(current.requireFamilyId(), request));
    }

    /** 先校验孩子属于本家庭（否则 404），再写入 Session */
    @PutMapping("/current")
    public Result<MeResponseDTO> select(@Valid @RequestBody SelectChildRequestDTO request) {
        AuthPrincipal p = current.principal();
        ChildResponseDTO child = children.requireOwned(p.familyId(), request.childId());
        current.selectChild(child.id());
        return Result.success(auth.me(p, child.id()));
    }
}
