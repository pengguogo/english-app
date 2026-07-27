-- ============================================================
-- V23: 汪汪队扩展角色
-- 新增 1 个单元、3 节单词课，共 20 个角色词条。
-- 图片键统一指向 /images/paw-patrol/{key}.jpg。
-- ============================================================

INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(99, 3, '更多救援伙伴', 4, 1);

-- Lesson 480: 雪山、丛林与城市伙伴（7 个）
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(480, 99, '新伙伴集合', 'WORD', '{
  "type":"WORD",
  "items":[
    {"word":"Everest","phonetic":"/ˈevərɪst/","translation":"珠珠（雪地救援犬）","emoji":"🏔️","image":"paw-patrol/everest"},
    {"word":"Tracker","phonetic":"/ˈtrækər/","translation":"小克（丛林救援犬）","emoji":"🌴","image":"paw-patrol/tracker"},
    {"word":"Liberty","phonetic":"/ˈlɪbərti/","translation":"莉伯蒂（城市救援犬）","emoji":"🏙️","image":"paw-patrol/liberty"},
    {"word":"Rex","phonetic":"/reks/","translation":"雷克斯（恐龙救援犬）","emoji":"🦖","image":"paw-patrol/rex"},
    {"word":"Wild Cat","phonetic":"/waɪld kæt/","translation":"野猫（摩托救援队员）","emoji":"🏍️","image":"paw-patrol/wild-cat"},
    {"word":"Al","phonetic":"/æl/","translation":"阿尔（大卡车救援犬）","emoji":"🚛","image":"paw-patrol/al"},
    {"word":"Coral","phonetic":"/ˈkɔːrəl/","translation":"珊瑚（海洋救援犬）","emoji":"🪸","image":"paw-patrol/coral"}
  ]
}', 1, 3);

-- Lesson 481: 双胞胎、猫咪小队与海盗伙伴（7 个）
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(481, 99, '特别行动队', 'WORD', '{
  "type":"WORD",
  "items":[
    {"word":"Arrby","phonetic":"/ˈɑːrbi/","translation":"阿比（海盗狗狗）","emoji":"🏴‍☠️","image":"paw-patrol/arrby"},
    {"word":"Tuck","phonetic":"/tʌk/","translation":"塔克（威力双胞胎）","emoji":"⚡","image":"paw-patrol/tuck"},
    {"word":"Ella","phonetic":"/ˈelə/","translation":"艾拉（威力双胞胎）","emoji":"✨","image":"paw-patrol/ella"},
    {"word":"Shade","phonetic":"/ʃeɪd/","translation":"莎德（猫咪小队）","emoji":"🐈","image":"paw-patrol/shade"},
    {"word":"Rory","phonetic":"/ˈrɔːri/","translation":"罗莉（猫咪小队）","emoji":"🐈‍⬛","image":"paw-patrol/rory"},
    {"word":"Leo","phonetic":"/ˈliːoʊ/","translation":"利奥（猫咪小队）","emoji":"🦁","image":"paw-patrol/leo"},
    {"word":"Sweetie","phonetic":"/ˈswiːti/","translation":"甜甜（皇家小狗）","emoji":"👑","image":"paw-patrol/sweetie"}
  ]
}', 2, 3);

-- Lesson 482: 救援轮、冒险湾居民与反派（6 个）
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(482, 99, '冒险湾朋友', 'WORD', '{
  "type":"WORD",
  "items":[
    {"word":"Roxi","phonetic":"/ˈrɒksi/","translation":"罗茜（救援轮队员）","emoji":"🛞","image":"paw-patrol/roxi"},
    {"word":"Boomer","phonetic":"/ˈbuːmər/","translation":"布默（赛车手）","emoji":"🏁","image":"paw-patrol/boomer"},
    {"word":"Mayor Goodway","phonetic":"/ˈmeɪər ˈɡʊdweɪ/","translation":"古威市长","emoji":"🏛️","image":"paw-patrol/mayor-goodway"},
    {"word":"Chickaletta","phonetic":"/ˌtʃɪkəˈletə/","translation":"咕咕鸡","emoji":"🐔","image":"paw-patrol/chickaletta"},
    {"word":"Cap''n Turbot","phonetic":"/ˈkæptɪn ˈtɜːrboʊ/","translation":"海象队长（海洋专家）","emoji":"⚓","image":"paw-patrol/captain-turbot"},
    {"word":"Mayor Humdinger","phonetic":"/ˈmeɪər ˈhʌmdɪŋər/","translation":"韩丁纳市长","emoji":"🎩","image":"paw-patrol/mayor-humdinger"}
  ]
}', 3, 3);
