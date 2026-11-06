package com.englishapp.service.ai.record;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RedactorTest {

    @Test
    void should_替换手机号_当_原话含手机号() {
        assertEquals("妈妈电话＊＊＊哦", Redactor.redact("妈妈电话13812345678哦"));
    }

    @Test
    void should_替换身份证含末位X_当_原话含身份证() {
        assertEquals("号码＊＊＊", Redactor.redact("号码11010519491231002X"));
    }

    @Test
    void should_替换邮箱和全角长数字_当_原话含邮箱() {
        assertEquals("发到＊＊＊或＊＊＊", Redactor.redact("发到a.b@qq.com或１２３４５６７"));
    }

    @Test
    void should_替换带空格的号码_当_分段说出() {
        assertEquals("打＊＊＊", Redactor.redact("打138 1234 5678"));
    }

    @Test
    void should_保留短数字_当_只是数数() {
        assertEquals("我有3只小猫和12条鱼", Redactor.redact("我有3只小猫和12条鱼"));
    }
}
