-- V82：强化现有六课、24 字认读，不修改历史课程进度。
CREATE TABLE character_progress (
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 user_id INTEGER NOT NULL DEFAULT 1,
 word TEXT NOT NULL,
 independent_days INTEGER NOT NULL DEFAULT 0,
 last_independent_date TEXT,
 wrong_count INTEGER NOT NULL DEFAULT 0,
 assisted_count INTEGER NOT NULL DEFAULT 0,
 last_outcome TEXT NOT NULL,
 due_date TEXT NOT NULL,
 UNIQUE(user_id, word)
);
CREATE INDEX idx_character_progress_user_due ON character_progress(user_id, due_date);
CREATE TABLE character_attempt (
 event_id TEXT PRIMARY KEY,
 user_id INTEGER NOT NULL,
 word TEXT NOT NULL,
 outcome TEXT NOT NULL,
 attempt_date TEXT NOT NULL
);

UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"山","phonetic":"shān","translation":"山","image":"hanzi-pilot/hanzi-shan","recognition":true,"exampleWord":"小山","exampleSentence":"远处有一座小山。"},{"word":"水","phonetic":"shuǐ","translation":"水","image":"hanzi-pilot/hanzi-shui","recognition":true,"exampleWord":"河水","exampleSentence":"河水流过小山。"},{"word":"日","phonetic":"rì","translation":"太阳","image":"hanzi-pilot/hanzi-ri","recognition":true,"exampleWord":"日出","exampleSentence":"日出时，天亮了。"},{"word":"月","phonetic":"yuè","translation":"月亮","image":"hanzi-pilot/hanzi-yue","recognition":true,"exampleWord":"月亮","exampleSentence":"月亮出来了。"}]}' WHERE id = 68;

UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"云","phonetic":"yún","translation":"云朵","image":"hanzi-pilot/hanzi-yun","recognition":true,"exampleWord":"白云","exampleSentence":"天上有白云。"},{"word":"雨","phonetic":"yǔ","translation":"下雨","image":"hanzi-pilot/hanzi-yu","recognition":true,"exampleWord":"下雨","exampleSentence":"外面下雨了。"},{"word":"风","phonetic":"fēng","translation":"刮风","image":"hanzi-pilot/hanzi-feng","recognition":true,"exampleWord":"大风","exampleSentence":"大风吹动小树。"},{"word":"雪","phonetic":"xuě","translation":"下雪","image":"hanzi-pilot/hanzi-xue","recognition":true,"exampleWord":"白雪","exampleSentence":"地上有白雪。"}]}' WHERE id = 69;

UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"花","phonetic":"huā","translation":"花朵","image":"hanzi-pilot/hanzi-hua","recognition":true,"exampleWord":"红花","exampleSentence":"红花开了。"},{"word":"草","phonetic":"cǎo","translation":"小草","image":"hanzi-pilot/hanzi-cao","recognition":true,"exampleWord":"小草","exampleSentence":"小草长高了。"},{"word":"树","phonetic":"shù","translation":"大树","image":"hanzi-pilot/hanzi-shu","recognition":true,"exampleWord":"大树","exampleSentence":"大树下很凉快。"},{"word":"木","phonetic":"mù","translation":"木头","image":"hanzi-pilot/hanzi-mu","recognition":true,"exampleWord":"木头","exampleSentence":"这是一块木头。"}]}' WHERE id = 70;

UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"上","phonetic":"shàng","translation":"上面","image":"hanzi-pilot/hanzi-shang","recognition":true,"exampleWord":"上面","exampleSentence":"小鸟在树上。"},{"word":"下","phonetic":"xià","translation":"下面","image":"hanzi-pilot/hanzi-xia","recognition":true,"exampleWord":"下面","exampleSentence":"小猫在树下。"},{"word":"左","phonetic":"zuǒ","translation":"左边","image":"hanzi-pilot/hanzi-zuo","recognition":true,"exampleWord":"左边","exampleSentence":"左边有一朵花。"},{"word":"右","phonetic":"yòu","translation":"右边","image":"hanzi-pilot/hanzi-you","recognition":true,"exampleWord":"右边","exampleSentence":"右边有一棵树。"}]}' WHERE id = 71;

UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"大","phonetic":"dà","translation":"大小的大","image":"hanzi-pilot/hanzi-da","recognition":true,"exampleWord":"大山","exampleSentence":"前面是一座大山。"},{"word":"小","phonetic":"xiǎo","translation":"大小的小","image":"hanzi-pilot/hanzi-xiao","recognition":true,"exampleWord":"小花","exampleSentence":"小花开了。"},{"word":"多","phonetic":"duō","translation":"多少的多","image":"hanzi-pilot/hanzi-duo","recognition":true,"exampleWord":"很多","exampleSentence":"树上有很多花。"},{"word":"少","phonetic":"shǎo","translation":"多少的少","image":"hanzi-pilot/hanzi-shao","recognition":true,"exampleWord":"很少","exampleSentence":"这边的花很少。"}]}' WHERE id = 72;

UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"爸","phonetic":"bà","translation":"爸爸","image":"hanzi-pilot/hanzi-ba","recognition":true,"exampleWord":"爸爸","exampleSentence":"爸爸回家了。"},{"word":"妈","phonetic":"mā","translation":"妈妈","image":"hanzi-pilot/hanzi-ma","recognition":true,"exampleWord":"妈妈","exampleSentence":"妈妈笑了。"},{"word":"哥","phonetic":"gē","translation":"哥哥","image":"hanzi-pilot/hanzi-ge","recognition":true,"exampleWord":"哥哥","exampleSentence":"哥哥在看书。"},{"word":"弟","phonetic":"dì","translation":"弟弟","image":"hanzi-pilot/hanzi-di","recognition":true,"exampleWord":"弟弟","exampleSentence":"弟弟在玩球。"}]}' WHERE id = 73;
