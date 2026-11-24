package com.englishapp.service.ai.explore;

import com.englishapp.dto.AgentChatResponseDTO.Option;
import java.util.List;

/** 引导流程使用既有课程配图，下一步由作答和活动结果决定。 */
public enum TrainGuide {
    INTRO("火车出发啦", "车头带着车厢往前走。火车也能帮我们把苹果送到远方。", 6, null),
    QUESTION("谁带着苹果往前走？", "看看蓝色火车，你能找到在最前面领路的那一部分吗？", 6,
            new Question(new Option("a", "车厢"), new Option("b", "车头"), "b")),
    HINT("换个地方看一看", "火车开过大桥，最前面那节负责拉车，后面的车厢跟着它。哪一节是车头？", 7,
            new Question(new Option("a", "最前面那节"), new Option("b", "装苹果的车厢"), "a")),
    BOOK("苹果坐上小火车", "找到车头啦！现在陪苹果坐一站小火车，读完回来告诉我发现了什么。", 4, null),
    RECAP("到站后想一想", "欢迎回来！你还记得苹果坐火车时，装在什么里面吗？", 2,
            new Question(new Option("a", "保鲜箱"), new Option("b", "书包"), "a")),
    DONE("火车探险完成", "你找到了车头，还记住了苹果装在保鲜箱里。我们下次再一起出发！", 10, null);

    public final String title;
    public final String speech;
    public final String image;
    public final Question question;

    TrainGuide(String title, String speech, int picture, Question question) {
        this.title = title;
        this.speech = speech;
        this.image = "/images/story/railway-logistics-%02d.jpg".formatted(picture);
        this.question = question;
    }

    public record Question(Option first, Option second, String correct) {
        public List<Option> options() { return List.of(first, second); }
    }
}
