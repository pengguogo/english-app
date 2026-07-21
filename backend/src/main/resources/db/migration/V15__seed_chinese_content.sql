-- ============================================================
-- V15: 语文模块内容播种
-- 4个主题(9-12) × 2单元(20-27) × 3课时(62-85) = 24课时
-- 课时类型: WORD 12个(拼音6+汉字6) + SENTENCE 6个(古诗) + READING 6个(寓言)
-- 所有 theme 的 subject_id=2(语文)
-- ============================================================

-- Section 1: 新增主题（subject_id=2 语文）
INSERT INTO theme (id, name, sort_order, is_locked, subject_id) VALUES
(9, '拼音启蒙', 1, 0, 2),
(10, '汉字识读', 2, 1, 2),
(11, '古诗朗诵', 3, 1, 2),
(12, '寓言故事', 4, 1, 2);

-- Section 2: 新增单元
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(20, 9, '单韵母', 1, 0),
(21, 9, '声母', 2, 1),
(22, 10, '自然汉字', 1, 0),
(23, 10, '生活汉字', 2, 1),
(24, 11, '五言绝句', 1, 0),
(25, 11, '经典古诗', 2, 1),
(26, 12, '经典寓言', 1, 0),
(27, 12, '启蒙故事', 2, 1);

-- ============================================================
-- Section 3: 主题9 拼音启蒙（WORD类型，无图片，用emoji）
-- ============================================================

-- Lesson 62: a o e (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(62, 20, '单韵母 a o e', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"ā","phonetic":"ā","translation":"啊","emoji":"🗣️"},'
|| '{"word":"á","phonetic":"á","translation":"啊(二声)","emoji":"❓"},'
|| '{"word":"ǎ","phonetic":"ǎ","translation":"啊(三声)","emoji":"😮"},'
|| '{"word":"à","phonetic":"à","translation":"啊(四声)","emoji":"❗"},'
|| '{"word":"ō","phonetic":"ō","translation":"喔","emoji":"🐓"},'
|| '{"word":"ē","phonetic":"ē","translation":"鹅","emoji":"🦢"}'
|| ']}', 1, 3);

-- Lesson 63: i u ü (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(63, 20, '单韵母 i u ü', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"ī","phonetic":"ī","translation":"衣","emoji":"👕"},'
|| '{"word":"ū","phonetic":"ū","translation":"乌","emoji":"🐦‍⬛"},'
|| '{"word":"ǖ","phonetic":"ǖ","translation":"鱼","emoji":"🐟"},'
|| '{"word":"í","phonetic":"í","translation":"仪","emoji":"🙇"},'
|| '{"word":"ú","phonetic":"ú","translation":"无","emoji":"🚫"},'
|| '{"word":"ǘ","phonetic":"ǘ","translation":"鱼(二声)","emoji":"🐠"}'
|| ']}', 2, 3);

-- Lesson 64: 复韵母 (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(64, 20, '复韵母', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"ai","phonetic":"āi","translation":"爱","emoji":"❤️"},'
|| '{"word":"ei","phonetic":"ēi","translation":"欸","emoji":"🗣️"},'
|| '{"word":"ui","phonetic":"uī","translation":"威","emoji":"💪"},'
|| '{"word":"ao","phonetic":"āo","translation":"奥","emoji":"🏆"},'
|| '{"word":"ou","phonetic":"ōu","translation":"欧","emoji":"🌍"},'
|| '{"word":"iu","phonetic":"iū","translation":"优","emoji":"⭐"}'
|| ']}', 3, 3);

-- Lesson 65: b p m f (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(65, 21, '声母 b p m f', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"bā","phonetic":"bā","translation":"八","emoji":"8️⃣"},'
|| '{"word":"pā","phonetic":"pā","translation":"趴","emoji":"🤸"},'
|| '{"word":"mā","phonetic":"mā","translation":"妈","emoji":"👩"},'
|| '{"word":"fā","phonetic":"fā","translation":"发","emoji":"📤"},'
|| '{"word":"bó","phonetic":"bó","translation":"伯","emoji":"👨"},'
|| '{"word":"pō","phonetic":"pō","translation":"坡","emoji":"⛰️"}'
|| ']}', 1, 3);

-- Lesson 66: d t n l (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(66, 21, '声母 d t n l', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"dā","phonetic":"dā","translation":"搭","emoji":"🏗️"},'
|| '{"word":"tā","phonetic":"tā","translation":"他","emoji":"🧑"},'
|| '{"word":"ná","phonetic":"ná","translation":"拿","emoji":"✋"},'
|| '{"word":"lā","phonetic":"lā","translation":"拉","emoji":"🤝"},'
|| '{"word":"dú","phonetic":"dú","translation":"读","emoji":"📖"},'
|| '{"word":"tú","phonetic":"tú","translation":"图","emoji":"🖼️"}'
|| ']}', 2, 3);

-- Lesson 67: g k h (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(67, 21, '声母 g k h', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"gē","phonetic":"gē","translation":"哥","emoji":"👦"},'
|| '{"word":"kē","phonetic":"kē","translation":"科","emoji":"🔬"},'
|| '{"word":"hē","phonetic":"hē","translation":"喝","emoji":"🥤"},'
|| '{"word":"gū","phonetic":"gū","translation":"姑","emoji":"👧"},'
|| '{"word":"kū","phonetic":"kū","translation":"哭","emoji":"😢"},'
|| '{"word":"hú","phonetic":"hú","translation":"湖","emoji":"🏞️"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 4: 主题10 汉字识读（WORD类型，带图片）
-- ============================================================

-- Lesson 68: 山水日月 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(68, 22, '自然汉字：山水日月', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"山","phonetic":"shān","translation":"山","emoji":"⛰️","image":"chinese/hanzi-shan"},'
|| '{"word":"水","phonetic":"shuǐ","translation":"水","emoji":"💧","image":"chinese/hanzi-shui"},'
|| '{"word":"日","phonetic":"rì","translation":"太阳","emoji":"☀️","image":"chinese/hanzi-ri"},'
|| '{"word":"月","phonetic":"yuè","translation":"月亮","emoji":"🌙","image":"chinese/hanzi-yue"}'
|| ']}', 1, 3);

-- Lesson 69: 云雨风雪 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(69, 22, '自然汉字：云雨风雪', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"云","phonetic":"yún","translation":"云朵","emoji":"☁️","image":"chinese/hanzi-yun"},'
|| '{"word":"雨","phonetic":"yǔ","translation":"下雨","emoji":"🌧️","image":"chinese/hanzi-yu"},'
|| '{"word":"风","phonetic":"fēng","translation":"刮风","emoji":"💨","image":"chinese/hanzi-feng"},'
|| '{"word":"雪","phonetic":"xuě","translation":"下雪","emoji":"❄️","image":"chinese/hanzi-xue"}'
|| ']}', 2, 3);

-- Lesson 70: 花草树木 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(70, 22, '自然汉字：花草树木', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"花","phonetic":"huā","translation":"花朵","emoji":"🌸","image":"chinese/hanzi-hua"},'
|| '{"word":"草","phonetic":"cǎo","translation":"小草","emoji":"🌿","image":"chinese/hanzi-cao"},'
|| '{"word":"树","phonetic":"shù","translation":"大树","emoji":"🌳","image":"chinese/hanzi-shu"},'
|| '{"word":"木","phonetic":"mù","translation":"木头","emoji":"🪵","image":"chinese/hanzi-mu"}'
|| ']}', 3, 3);

-- Lesson 71: 上下左右 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(71, 23, '方位汉字：上下左右', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"上","phonetic":"shàng","translation":"上面","emoji":"⬆️","image":"chinese/hanzi-shang"},'
|| '{"word":"下","phonetic":"xià","translation":"下面","emoji":"⬇️","image":"chinese/hanzi-xia"},'
|| '{"word":"左","phonetic":"zuǒ","translation":"左边","emoji":"⬅️","image":"chinese/hanzi-zuo"},'
|| '{"word":"右","phonetic":"yòu","translation":"右边","emoji":"➡️","image":"chinese/hanzi-you"}'
|| ']}', 1, 3);

-- Lesson 72: 大小多少 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(72, 23, '比较汉字：大小多少', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"大","phonetic":"dà","translation":"大小的大","emoji":"🐘","image":"chinese/hanzi-da"},'
|| '{"word":"小","phonetic":"xiǎo","translation":"大小的小","emoji":"🐜","image":"chinese/hanzi-xiao"},'
|| '{"word":"多","phonetic":"duō","translation":"多少的多","emoji":"🌈","image":"chinese/hanzi-duo"},'
|| '{"word":"少","phonetic":"shǎo","translation":"多少的少","emoji":"🌱","image":"chinese/hanzi-shao"}'
|| ']}', 2, 3);

-- Lesson 73: 家庭汉字 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(73, 23, '家庭汉字：爸妈哥弟', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"爸","phonetic":"bà","translation":"爸爸","emoji":"👨","image":"chinese/hanzi-ba"},'
|| '{"word":"妈","phonetic":"mā","translation":"妈妈","emoji":"👩","image":"chinese/hanzi-ma"},'
|| '{"word":"哥","phonetic":"gē","translation":"哥哥","emoji":"👦","image":"chinese/hanzi-ge"},'
|| '{"word":"弟","phonetic":"dì","translation":"弟弟","emoji":"👶","image":"chinese/hanzi-di"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 5: 主题11 古诗朗诵（SENTENCE类型，无图片）
-- ============================================================

-- Lesson 74: 静夜思 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(74, 24, '静夜思', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"床前明月光","translation":"明亮的月光照在床前","emoji":"🌙"},'
|| '{"sentence":"疑是地上霜","translation":"好像是地上结了一层白霜","emoji":"❄️"},'
|| '{"sentence":"举头望明月","translation":"抬起头望着天上的明月","emoji":"🌕"},'
|| '{"sentence":"低头思故乡","translation":"低下头思念远方的家乡","emoji":"🏡"}'
|| ']}', 1, 3);

-- Lesson 75: 春晓 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(75, 24, '春晓', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"春眠不觉晓","translation":"春天的夜晚睡得香甜不知不觉就到了天亮","emoji":"🌅"},'
|| '{"sentence":"处处闻啼鸟","translation":"到处都能听到鸟儿清脆的叫声","emoji":"🐦"},'
|| '{"sentence":"夜来风雨声","translation":"回想昨夜的风声和雨声","emoji":"🌧️"},'
|| '{"sentence":"花落知多少","translation":"不知有多少花瓣被风雨打落","emoji":"🌸"}'
|| ']}', 2, 3);

-- Lesson 76: 咏鹅 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(76, 24, '咏鹅', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"鹅鹅鹅","translation":"大白鹅啊大白鹅","emoji":"🦢"},'
|| '{"sentence":"曲项向天歌","translation":"弯曲着脖子对着天空唱歌","emoji":"🎵"},'
|| '{"sentence":"白毛浮绿水","translation":"白色的羽毛漂浮在碧绿的水面上","emoji":"💧"},'
|| '{"sentence":"红掌拨清波","translation":"红色的脚掌拨动着清澈的水波","emoji":"🦶"}'
|| ']}', 3, 3);

-- Lesson 77: 悯农 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(77, 25, '悯农', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"锄禾日当午","translation":"农民在正午烈日下给禾苗除草","emoji":"☀️"},'
|| '{"sentence":"汗滴禾下土","translation":"汗水滴落在禾苗下的泥土里","emoji":"💧"},'
|| '{"sentence":"谁知盘中餐","translation":"谁知道盘中的饭菜","emoji":"🍚"},'
|| '{"sentence":"粒粒皆辛苦","translation":"每一粒都是农民辛苦劳动得来的","emoji":"🌾"}'
|| ']}', 1, 3);

-- Lesson 78: 登鹳雀楼 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(78, 25, '登鹳雀楼', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"白日依山尽","translation":"白色的太阳依傍着山峦渐渐落下","emoji":"🌇"},'
|| '{"sentence":"黄河入海流","translation":"黄河水奔腾着流入大海","emoji":"🌊"},'
|| '{"sentence":"欲穷千里目","translation":"想要看到更远的千里风光","emoji":"🔭"},'
|| '{"sentence":"更上一层楼","translation":"就要再登上更高的一层楼","emoji":"🏛️"}'
|| ']}', 2, 3);

-- Lesson 79: 望庐山瀑布 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(79, 25, '望庐山瀑布', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"日照香炉生紫烟","translation":"阳光照射在香炉峰上生出紫色的烟雾","emoji":"🏔️"},'
|| '{"sentence":"遥看瀑布挂前川","translation":"远远望去瀑布像白练挂在山前","emoji":"💦"},'
|| '{"sentence":"飞流直下三千尺","translation":"飞腾的水流直冲而下有三千尺长","emoji":"💧"},'
|| '{"sentence":"疑是银河落九天","translation":"让人怀疑是银河从九天之上落下来","emoji":"✨"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 6: 主题12 寓言故事（READING类型，带图片）
-- ============================================================

-- Lesson 80: 龟兔赛跑 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(80, 26, '龟兔赛跑', 'READING',
'{"type":"READING","items":['
|| '{"title":"比赛开始","content":"有一天，兔子嘲笑乌龟跑得慢。乌龟说：我们来比赛吧！兔子哈哈大笑，答应了。动物们都来观看。","image":"story/tortoise-hare-1"},'
|| '{"title":"兔子睡觉","content":"比赛开始了，兔子跑得飞快，很快就把乌龟远远甩在后面。兔子想：乌龟那么慢，我先睡一觉吧。于是它靠在大树下睡着了。","image":"story/tortoise-hare-2"},'
|| '{"title":"乌龟赢了","content":"乌龟虽然慢，但它一步一步不停地爬。等兔子醒来时，乌龟已经快到终点了！兔子拼命追，但已经太晚了。乌龟赢了比赛。","image":"story/tortoise-hare-3"}'
|| ']}', 1, 3);

-- Lesson 81: 拔苗助长 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(81, 26, '拔苗助长', 'READING',
'{"type":"READING","items":['
|| '{"title":"焦急的农夫","content":"有个农夫种了一片禾苗。他每天都去看，觉得禾苗长得太慢了。他焦急地想：怎样才能让禾苗长得快一些呢？","image":"story/pull-seedlings-1"},'
|| '{"title":"拔高禾苗","content":"农夫想出了一个好办法！他跑到田里，把每棵禾苗都往上拔了一点。拔了一整天，累得腰酸背痛，但心里很高兴。","image":"story/pull-seedlings-2"},'
|| '{"title":"禾苗枯了","content":"农夫回到家高兴地说：今天太累了，但禾苗都长高了！儿子跑去田里一看，禾苗全都枯死了。原来拔苗反而害了禾苗。","image":"story/pull-seedlings-3"}'
|| ']}', 2, 3);

-- Lesson 82: 井底之蛙 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(82, 26, '井底之蛙', 'READING',
'{"type":"READING","items":['
|| '{"title":"井里的青蛙","content":"有一只青蛙一直住在一口枯井里。它每天在井里跳来跳去，觉得这就是整个世界。它对自己的生活很满意。","image":"story/frog-well-1"},'
|| '{"title":"海龟来了","content":"一天，一只海龟路过井边。青蛙骄傲地说：我的家多好呀！海龟，你见过这么好的地方吗？海龟笑了笑。","image":"story/frog-well-2"},'
|| '{"title":"大海的故事","content":"海龟告诉青蛙：大海又大又深，比你的井大多了！青蛙听了非常惊讶，才知道自己看到的天空只是井口那么大的一小块。","image":"story/frog-well-3"}'
|| ']}', 3, 3);

-- Lesson 83: 小马过河 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(83, 27, '小马过河', 'READING',
'{"type":"READING","items":['
|| '{"title":"小马的任务","content":"马妈妈让小马把一袋麦子送到磨坊去。小马高兴地出发了。走着走着，一条小河挡住了去路。河水哗哗地流着。","image":"story/little-horse-1"},'
|| '{"title":"问别人","content":"小马问老牛：牛伯伯，水深吗？老牛说：很浅，刚到小腿。小马刚要过河，松鼠喊：别过！水很深，会淹死的！小马不知道该听谁的。","image":"story/little-horse-2"},'
|| '{"title":"自己试试","content":"小马回家问妈妈。妈妈说：光听别人说不行，要自己动脑筋。小马回到河边，小心地下了水。原来河水既不像老牛说的那么浅，也不像松鼠说的那么深。","image":"story/little-horse-3"}'
|| ']}', 1, 3);

-- Lesson 84: 狐假虎威 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(84, 27, '狐假虎威', 'READING',
'{"type":"READING","items":['
|| '{"title":"老虎抓狐狸","content":"一只老虎抓住了一只狐狸。狐狸眼珠一转，说：你不能吃我！我是天帝派来的百兽之王，你不信就跟在我后面走一趟。","image":"story/fox-tiger-1"},'
|| '{"title":"百兽逃跑","content":"老虎半信半疑，跟着狐狸往前走。森林里的小动物们看到狐狸大摇大摆地走来，身后还跟着老虎，吓得全都逃跑了。","image":"story/fox-tiger-2"},'
|| '{"title":"老虎上当","content":"老虎以为动物们真的怕狐狸，就放了它。其实动物们怕的是狐狸身后的老虎呀！狐狸借着老虎的威风吓跑了大家。","image":"story/fox-tiger-3"}'
|| ']}', 2, 3);

-- Lesson 85: 守株待兔 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(85, 27, '守株待兔', 'READING',
'{"type":"READING","items":['
|| '{"title":"意外的收获","content":"一个农夫在田里干活。突然，一只兔子慌慌张张地跑过来，撞在树桩上死了。农夫白捡了一只兔子，高兴极了。","image":"story/wait-rabbit-1"},'
|| '{"title":"放下农具","content":"农夫想：种田多辛苦呀，不如在这里等兔子撞树桩。于是他放下锄头，天天守在树桩旁边，等着兔子再来。","image":"story/wait-rabbit-2"},'
|| '{"title":"田地荒了","content":"日子一天天过去，农夫再也没有等到兔子。而他的田地因为没有人打理，长满了野草，庄稼全荒了。农夫后悔极了。","image":"story/wait-rabbit-3"}'
|| ']}', 3, 3);
