package com.englishapp.security;

import java.util.Optional;

/**
 * 当前请求的身份：一律从服务端 Session 推导，不接受客户端传入的 userId。
 * 业务表的 user_id 即 child_id。
 */
public interface CurrentChild {

    /** 当前孩子 ID；未登录抛 401，已登录未选孩子抛 409（code 4091） */
    Integer requireChildId();

    /** 当前家庭 ID；未登录抛 401 */
    Long requireFamilyId();

    /** 家长二次验证 10 分钟内有效；未登录抛 401，未验证或过期抛 403（code 4031） */
    void requireParentVerified();

    /** 当前孩子 ID，不抛异常；游客或未选孩子时为 empty（公开浏览接口附带进度用） */
    Optional<Integer> currentChildId();
}
