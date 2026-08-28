-- V67：新增适合 5～6 岁儿童的“Feelings & Calm”英语主题。
-- 学习路径：识别情绪词 → 用 I feel... 表达 → 请求帮助并练习慢呼吸。

INSERT INTO theme (id, name, sort_order, is_locked, subject_id)
VALUES (51, 'Feelings & Calm', 18, 0, 1);

INSERT INTO unit (id, theme_id, name, sort_order, is_locked)
VALUES (152, 51, '说出感受，慢慢平静', 1, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(616, 152, '认识我的感受', 'WORD', '{
  "type":"WORD",
  "items":[
    {"word":"happy","phonetic":"ˈhæpi","translation":"开心的","image":"feelings-calm/happy"},
    {"word":"sad","phonetic":"sæd","translation":"难过的","image":"feelings-calm/sad"},
    {"word":"angry","phonetic":"ˈæŋɡri","translation":"生气的","image":"feelings-calm/angry"},
    {"word":"scared","phonetic":"skerd","translation":"害怕的","image":"feelings-calm/scared"}
  ]
}', 1, 3),
(617, 152, '我会说出感受', 'SENTENCE', '{
  "type":"SENTENCE",
  "items":[
    {"sentence":"I feel happy.","translation":"我感到开心。","image":"feelings-calm/happy"},
    {"sentence":"I feel sad.","translation":"我感到难过。","image":"feelings-calm/sad"},
    {"sentence":"I feel angry.","translation":"我感到生气。","image":"feelings-calm/angry"},
    {"sentence":"I feel scared.","translation":"我感到害怕。","image":"feelings-calm/scared"}
  ]
}', 2, 3),
(618, 152, '我会求助和平静', 'SENTENCE', '{
  "type":"SENTENCE",
  "items":[
    {"sentence":"I need help.","translation":"我需要帮助。","image":"feelings-calm/ask-for-help"},
    {"sentence":"Please stay with me.","translation":"请陪着我。","image":"feelings-calm/ask-for-help"},
    {"sentence":"I can breathe slowly.","translation":"我可以慢慢呼吸。","image":"feelings-calm/belly-breath"},
    {"sentence":"I feel calm now.","translation":"我现在平静下来了。","image":"feelings-calm/belly-breath"}
  ]
}', 3, 3);
