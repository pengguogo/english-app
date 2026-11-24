package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.repository.GuardianConsentRepository;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.AuthService;
import com.englishapp.service.ai.explore.GuidedLearningService;
import com.englishapp.service.ai.explore.GuidedLearningService.Action;
import com.englishapp.service.ai.explore.GuidedLearningService.Run;
import com.englishapp.service.ai.explore.GuidedLearningService.Screen;
import com.englishapp.service.ai.explore.GuidedLearningService.Turn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/api/v1/agent/journey")
public class GuidedLearningController {
    private static final String KEY = GuidedLearningController.class.getName();
    private final CurrentChild current;
    private final GuardianConsentRepository consents;
    private final GuidedLearningService guide;

    public GuidedLearningController(CurrentChild current, GuardianConsentRepository consents, GuidedLearningService guide) {
        this.current = current;
        this.consents = consents;
        this.guide = guide;
    }

    @GetMapping
    public Result<Screen> state(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return Result.success(null);
        synchronized (WebUtils.getSessionMutex(session)) {
            return Result.success(guide.screen(activeRun(session, identity())));
        }
    }

    public record StepRequest(@NotNull Action action, UUID revision, @Size(max = 4) String option) { }

    @PostMapping
    public Result<Screen> act(@Valid @RequestBody StepRequest body, HttpServletRequest request) {
        HttpSession session = request.getSession();
        synchronized (WebUtils.getSessionMutex(session)) {
            Identity id = identity();
            Run next = guide.advance(activeRun(session, id), new Turn(body.action, body.revision, body.option),
                    id.childId, id.recordingFamily);
            if (next != null) session.setAttribute(KEY, next);
            return Result.success(guide.screen(next));
        }
    }

    static void confirmGuestReview(HttpSession session, int lessonId) {
        if (session == null) return;
        synchronized (WebUtils.getSessionMutex(session)) {
            Run run = (Run) session.getAttribute(KEY);
            Run reviewed = GuidedLearningService.confirmGuestReview(run, lessonId);
            if (reviewed != run) session.setAttribute(KEY, reviewed);
        }
    }

    private record Identity(Integer childId, Long recordingFamily) { }

    private Identity identity() {
        Integer child = current.currentChildId().orElse(null);
        Long family = child == null ? null : current.requireFamilyId();
        Long recordingFamily = family != null && consents.existsByFamilyIdAndConsentVersion(family, AuthService.CONSENT_VERSION)
                ? family : null;
        return new Identity(child, recordingFamily);
    }

    private Run activeRun(HttpSession session, Identity id) {
        Run run = (Run) session.getAttribute(KEY);
        if (run != null && (!Objects.equals(run.childId(), id.childId)
                || !Objects.equals(run.recordingFamily(), id.recordingFamily))) {
            session.removeAttribute(KEY);
            return null;
        }
        return run;
    }
}
