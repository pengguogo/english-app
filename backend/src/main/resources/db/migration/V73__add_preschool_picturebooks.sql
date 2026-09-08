-- V73：为 6 岁、尚未识字的孩子增加三本看图听读绘本。
-- 每本均为 6 页短句故事、3 道图片选择题和 3 个跟读词组。

INSERT INTO unit (theme_id, name, sort_order, is_locked)
SELECT id, '小海龟回家', 2, 0 FROM theme WHERE name = '绘本小火车';
INSERT INTO unit (theme_id, name, sort_order, is_locked)
SELECT id, '大家的面包', 3, 0 FROM theme WHERE name = '绘本小火车';
INSERT INTO unit (theme_id, name, sort_order, is_locked)
SELECT id, '雨天森林邮局', 4, 0 FROM theme WHERE name = '绘本小火车';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '看图听故事', 'READING', '{"type":"READING","picturebook":true,"bookTitle":"小海龟回家","items":[
{"title":"去看大海","content":"Look at the sea!","translation":"看，大海蓝蓝的。点一点画面，听故事开始。","image":"story/ocean-station-01","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"今天，我们去大海里找一位小朋友。"},
{"title":"海龟妈妈","content":"Mama has three babies.","translation":"海龟妈妈有三个宝宝。一起数一数：一、二、三。","image":"story/ocean-station-03","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"请用手指一指海龟宝宝。"},
{"title":"少了一个","content":"One baby is missing.","translation":"咦，只看到两个宝宝，还有一个去了哪里？","image":"story/ocean-station-03","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"你觉得小海龟会在哪里？"},
{"title":"找到小海龟","content":"There it is! Help!","translation":"找到啦！小海龟被旧绳子缠住了。先告诉大人来帮忙。","image":"story/ocean-station-05","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"发现危险时，小朋友先告诉大人。"},
{"title":"安全了","content":"The turtle can swim.","translation":"绳子剪开了，小海龟又能游泳啦。","image":"story/ocean-station-06","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"学一学小海龟慢慢游。"},
{"title":"回家啦","content":"Three turtles are home.","translation":"三个宝宝都回到妈妈身边。大海也变得更干净了。","image":"story/ocean-station-07","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"小海龟回家啦！谢谢认真观察的你。"}
]}', 1, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='小海龟回家';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '听一听找图片', 'QUIZ', '{"type":"QUIZ","picturebook":true,"bookTitle":"小海龟回家","items":[
{"question":"哪一幅图里，海龟妈妈正在找宝宝？","audioText":"Find Mama Turtle.","image":"story/ocean-station-03","options":[{"text":"海龟妈妈和宝宝","image":"story/ocean-station-03","showText":false},{"text":"面包店","image":"story/bakery-order-01","showText":false}],"answer":0,"hint":"找一找蓝色大海里的海龟。","audioLanguage":"en","voiceProfile":"challenge-curious"},
{"question":"哪一幅图里，小海龟需要帮助？","audioText":"The turtle needs help.","image":"story/ocean-station-05","options":[{"text":"小海龟被绳子缠住","image":"story/ocean-station-05","showText":false},{"text":"小海龟安全游泳","image":"story/ocean-station-06","showText":false}],"answer":0,"hint":"看看哪只小海龟还不能游走。","audioLanguage":"en","voiceProfile":"challenge-curious"},
{"question":"故事最后是哪一幅图？","audioText":"The turtles are home.","image":"story/ocean-station-07","options":[{"text":"三个宝宝回到妈妈身边","image":"story/ocean-station-07","showText":false},{"text":"海面上的潜水器","image":"story/ocean-station-08","showText":false}],"answer":0,"hint":"最后，三个海龟宝宝都在一起。","audioLanguage":"en","voiceProfile":"challenge-curious"}
]}', 2, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='小海龟回家';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '跟着说一说', 'READING', '{"type":"READING","picturebook":true,"bookTitle":"小海龟回家","items":[
{"title":"大海","content":"the sea","translation":"大海。跟着声音说一遍。","image":"story/ocean-station-01","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"},
{"title":"小海龟","content":"a little turtle","translation":"一只小海龟。慢慢跟着说。","image":"story/ocean-station-05","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"},
{"title":"回家","content":"go home","translation":"回家。说完送小海龟回家。","image":"story/ocean-station-07","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"}
]}', 3, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='小海龟回家';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '看图听故事', 'READING', '{"type":"READING","picturebook":true,"bookTitle":"大家的面包","items":[
{"title":"新订单","content":"We need bread!","translation":"幼儿园需要好多面包。大家先看清订单。","image":"story/bakery-order-01","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"欢迎来到香香的面包店。"},
{"title":"揉一揉","content":"Mix, mix, mix.","translation":"揉一揉，揉一揉，软软的面团准备好了。","image":"story/bakery-order-02","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"小手在空中学一学揉面团。"},
{"title":"数一数","content":"One, two, three, four.","translation":"一、二、三、四。圆面包还差一个。","image":"story/bakery-order-03","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"请用手指点着圆面包数一数。"},
{"title":"认形状","content":"Circle, triangle, star.","translation":"圆形、三角形、星星。把形状放进对的篮子。","image":"story/bakery-order-04","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"你能找出有三个尖角的面包吗？"},
{"title":"一起帮忙","content":"We work together.","translation":"有人装篮，有人检查。大家一起做，很快就完成了。","image":"story/bakery-order-05","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"一起合作，面包一个也不会少。"},
{"title":"送到啦","content":"Bread for everyone!","translation":"香香的面包送到啦，每个小朋友都有一份。","image":"story/bakery-order-06","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"订单完成！谢谢小面包师。"}
]}', 1, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='大家的面包';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '听一听找图片', 'QUIZ', '{"type":"QUIZ","picturebook":true,"bookTitle":"大家的面包","items":[
{"question":"哪一幅图是在揉面团？","audioText":"Mix the dough.","image":"story/bakery-order-02","options":[{"text":"揉面团","image":"story/bakery-order-02","showText":false},{"text":"送面包","image":"story/bakery-order-06","showText":false}],"answer":0,"hint":"找找桌上软软的面团。","audioLanguage":"en","voiceProfile":"challenge-curious"},
{"question":"哪一幅图里能找到圆形、三角形和星星？","audioText":"Find the shapes.","image":"story/bakery-order-04","options":[{"text":"给面包认形状","image":"story/bakery-order-04","showText":false},{"text":"数圆面包","image":"story/bakery-order-03","showText":false}],"answer":0,"hint":"找一找三种不同形状的面包。","audioLanguage":"en","voiceProfile":"challenge-curious"},
{"question":"大家一起合作时是哪一幅图？","audioText":"We work together.","image":"story/bakery-order-05","options":[{"text":"大家一起装篮检查","image":"story/bakery-order-05","showText":false},{"text":"一个人看订单","image":"story/bakery-order-01","showText":false}],"answer":0,"hint":"看看哪幅图里每个人都在帮忙。","audioLanguage":"en","voiceProfile":"challenge-curious"}
]}', 2, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='大家的面包';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '跟着说一说', 'READING', '{"type":"READING","picturebook":true,"bookTitle":"大家的面包","items":[
{"title":"面包","content":"bread","translation":"面包。跟着声音说一遍。","image":"story/bakery-order-06","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"},
{"title":"数四个","content":"one, two, three, four","translation":"从一数到四。伸出手指一起数。","image":"story/bakery-order-03","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"},
{"title":"一起做","content":"work together","translation":"一起合作。邀请家人和你一起说。","image":"story/bakery-order-05","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"}
]}', 3, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='大家的面包';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '看图听故事', 'READING', '{"type":"READING","picturebook":true,"bookTitle":"雨天森林邮局","items":[
{"title":"下雨啦","content":"Rain, rain, rain.","translation":"森林下起大雨。邮局有三个包裹要送。","image":"story/forest-mail-01","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"穿好雨衣，我们去森林邮局。"},
{"title":"换条路","content":"The blue bridge is closed.","translation":"蓝桥不能走。大家选择有绿色标记的安全小路。","image":"story/forest-mail-02","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"请找一找图里的红叉和绿色标记。"},
{"title":"先送药箱","content":"Medicine comes first.","translation":"松鼠急需药箱，所以先去树洞。","image":"story/forest-mail-03","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"最需要的包裹，要先送到。"},
{"title":"树枝挡路","content":"Stop and ask for help.","translation":"树枝挡住小路。大家停在安全处，请护林员来帮忙。","image":"story/forest-mail-04","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"遇到危险时，要找哪位大人帮忙？"},
{"title":"看图找家","content":"Look at the signs.","translation":"看颜色和形状，就能找到小兔、松鼠和小鹿的家。","image":"story/forest-mail-05","audioLanguage":"en","voiceProfile":"story-narrator","prompt":"你能找到红色屋顶吗？"},
{"title":"全部送到","content":"All the boxes are here!","translation":"三个包裹都安全送到。太阳也出来啦。","image":"story/forest-mail-06","audioLanguage":"en","voiceProfile":"story-narrator","announcement":"叮咚！雨天邮递任务完成。"}
]}', 1, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='雨天森林邮局';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '听一听找图片', 'QUIZ', '{"type":"QUIZ","picturebook":true,"bookTitle":"雨天森林邮局","items":[
{"question":"哪一幅图告诉我们蓝桥不能走？","audioText":"The bridge is closed.","image":"story/forest-mail-02","options":[{"text":"桥边有红色叉号","image":"story/forest-mail-02","showText":false},{"text":"大家收到感谢卡","image":"story/forest-mail-06","showText":false}],"answer":0,"hint":"找一找表示不能通行的红叉。","audioLanguage":"en","voiceProfile":"challenge-curious"},
{"question":"哪一个包裹要先送？","audioText":"Medicine comes first.","image":"story/forest-mail-03","options":[{"text":"松鼠的药箱","image":"story/forest-mail-03","showText":false},{"text":"小鹿的图画书","image":"story/forest-mail-01","showText":false}],"answer":0,"hint":"生病时急需的是药箱。","audioLanguage":"en","voiceProfile":"challenge-curious"},
{"question":"树枝挡路时，大家怎么做？","audioText":"Ask for help.","image":"story/forest-mail-04","options":[{"text":"停下，请护林员帮忙","image":"story/forest-mail-04","showText":false},{"text":"继续冲过蓝桥","image":"story/forest-mail-02","showText":false}],"answer":0,"hint":"危险的事情交给准备好的大人。","audioLanguage":"en","voiceProfile":"challenge-curious"}
]}', 2, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='雨天森林邮局';

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
SELECT unit.id, '跟着说一说', 'READING', '{"type":"READING","picturebook":true,"bookTitle":"雨天森林邮局","items":[
{"title":"下雨","content":"rain","translation":"下雨。听一听，再跟着说。","image":"story/forest-mail-01","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"},
{"title":"停下来","content":"stop","translation":"停下来。遇到危险先停下。","image":"story/forest-mail-04","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"},
{"title":"请帮帮我","content":"help, please","translation":"请帮帮我。需要时勇敢请大人帮忙。","image":"story/forest-mail-04","audioLanguage":"en","voiceProfile":"story-narrator","trainAction":"speak"}
]}', 3, 3 FROM unit JOIN theme ON theme.id=unit.theme_id
WHERE theme.name='绘本小火车' AND unit.name='雨天森林邮局';
