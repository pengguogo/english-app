package com.englishapp.service.ai.guide;

import java.util.List;

/**
 * 索引里的一节可学课程。文本字段已归一化为小写，route / image 在建索引时生成并校验。
 *
 * @param orderKey 展示顺序：学科、主题、单元、课程的 sortOrder 拼成的稳定排序键
 */
record CourseDoc(
        int lessonId,
        String title,
        int unitId,
        int themeId,
        String themeName,
        boolean quiz,
        String route,
        String image,
        List<Integer> orderKey,
        String titleText,
        String themeText,
        String lessonText,
        String unitText,
        String itemText) {
}
