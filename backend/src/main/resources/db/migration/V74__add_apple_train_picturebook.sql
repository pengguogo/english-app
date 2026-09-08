-- V74：增加适合 6 岁孩子的火车主题听读绘本《苹果坐上小火车》。
-- 使用 6 页看图故事、3 道图片选择题和 3 个跟读词组。

INSERT INTO unit (theme_id, name, sort_order, is_locked)
SELECT id, '苹果坐上小火车', 5, 0 FROM theme WHERE name = '绘本小火车';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '看图听故事', 'READING', '{"type":"READING","picturebook":true,"bookTitle":"苹果坐上小火车","items":[
{"title":"苹果准备好啦","content":"Red apples are ready.","translation":"红红的苹果准备去海边城市。远处的小火车也准备出发啦。","image":"story/railway-logistics-01","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"今天，我们陪苹果坐一次小火车。"},
{"title":"住进保鲜箱","content":"Apples go in the box.","translation":"苹果整齐地住进橙色保鲜箱，一路都能凉凉的。","image":"story/railway-logistics-02","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"请找一找箱子上的白色雪花。"},
{"title":"吊到火车上","content":"Up goes the orange box!","translation":"大吊车稳稳地举起橙色箱子，把它轻轻放到火车上。","image":"story/railway-logistics-04","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"吊车工作时，大家都站在安全线外。"},
{"title":"绿灯亮啦","content":"Green light. Go, train!","translation":"绿色信号灯亮了。蓝色火车拉着苹果慢慢出发。","image":"story/railway-logistics-06","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"请用手指找一找绿色信号灯。"},
{"title":"穿过山谷","content":"The train crosses the bridge.","translation":"小火车穿过山谷和大桥，橙色箱子一直跟在后面。","image":"story/railway-logistics-07","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"学一学小火车：咔嚓、咔嚓。"},
{"title":"苹果到站啦","content":"The apples are here!","translation":"苹果安全到达商店。大家很快就能吃到新鲜苹果啦。","image":"story/railway-logistics-10","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"叮咚！苹果小火车顺利到站。"}
]}', 1, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='苹果坐上小火车';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '听一听找图片', 'QUIZ', '{"type":"QUIZ","picturebook":true,"bookTitle":"苹果坐上小火车","items":[
{"question":"哪一幅图里，苹果正在装进橙色保鲜箱？","audioText":"Find the apples.","image":"story/railway-logistics-02","options":[{"text":"苹果装进橙色箱子","image":"story/railway-logistics-02","showText":false},{"text":"火车穿过大桥","image":"story/railway-logistics-07","showText":false}],"answer":0,"hint":"找一找许多红红的苹果。","audioLanguage":"en","voiceProfile":"challenge-curious"},
{"question":"绿色信号亮起后，哪一幅图里的火车正在出发？","audioText":"Go, train!","image":"story/railway-logistics-06","options":[{"text":"吊车举起橙色箱子","image":"story/railway-logistics-04","showText":false},{"text":"蓝色火车开始出发","image":"story/railway-logistics-06","showText":false}],"answer":1,"hint":"找一找绿色信号灯和蓝色火车。","audioLanguage":"en","voiceProfile":"challenge-curious"},
{"question":"故事最后，苹果到达哪里？","audioText":"The apples are here!","image":"story/railway-logistics-10","options":[{"text":"苹果到达商店","image":"story/railway-logistics-10","showText":false},{"text":"苹果刚刚开始装车","image":"story/railway-logistics-01","showText":false}],"answer":0,"hint":"故事最后，工作人员正在检查送到的苹果。","audioLanguage":"en","voiceProfile":"challenge-curious"}
]}', 2, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='苹果坐上小火车';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '跟着说一说', 'READING', '{"type":"READING","picturebook":true,"bookTitle":"苹果坐上小火车","items":[
{"title":"红苹果","content":"red apples","translation":"红苹果。指一指画面，跟着声音说。","image":"story/railway-logistics-01","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"},
{"title":"小火车","content":"the train","translation":"小火车。听一听，再慢慢说一遍。","image":"story/railway-logistics-06","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"},
{"title":"出发","content":"Go, train!","translation":"小火车，出发！请你来当一次小司机。","image":"story/railway-logistics-07","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"}
]}', 3, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='苹果坐上小火车';
