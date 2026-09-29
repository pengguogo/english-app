-- 水果乐园趣味化试点：缩短首关、补齐场景图，并将复习课调整为看图挑战。
UPDATE lesson
SET name = '水果寻宝·听音寻果',
    content = '{"type":"WORD","items":[{"word":"apple","phonetic":"/ˈæpəl/","translation":"苹果","image":"fruit/apple"},{"word":"banana","phonetic":"/bəˈnænə/","translation":"香蕉","image":"fruit/banana"},{"word":"orange","phonetic":"/ˈɔːrɪndʒ/","translation":"橙子","image":"fruit/orange"},{"word":"grape","phonetic":"/ɡreɪp/","translation":"葡萄","image":"fruit/grape"},{"word":"peach","phonetic":"/piːtʃ/","translation":"桃子","image":"fruit/peach"}]}'
WHERE id = 12 AND type = 'WORD';

UPDATE lesson
SET name = '水果寻宝·店员对话',
    content = '{"type":"DIALOGUE","scene":"水果店","tip":"帮 Mimi 用英语买水果","items":[{"speaker":"Mimi","text":"Hello! I want an apple, please.","translation":"你好！我想要一个苹果。","scene":"水果店","image":"fruit/fruit-shop","audio":""},{"speaker":"Seller","text":"Here you are. Do you like bananas?","translation":"给你。你喜欢香蕉吗？","scene":"水果店","image":"fruit/fruit-shop","audio":""},{"speaker":"Mimi","text":"Yes, I do. Thank you!","translation":"是的，我喜欢。谢谢！","scene":"水果店","image":"fruit/fruit-shop","audio":""}]}'
WHERE id = 114 AND type = 'DIALOGUE';

UPDATE lesson
SET name = '水果寻宝·看图挑战',
    content = '{"type":"QUIZ","items":[{"activity":"IMAGE_WORD","question":"图片里的水果用英语怎么说？","audioText":"Which fruit is this?","audioLanguage":"en","image":"fruit/apple","options":["apple","banana","grape"],"answer":0},{"activity":"IMAGE_WORD","question":"图片里的水果用英语怎么说？","audioText":"Which fruit is this?","audioLanguage":"en","image":"fruit/banana","options":["orange","banana","peach"],"answer":1},{"activity":"IMAGE_WORD","question":"图片里的水果用英语怎么说？","audioText":"Which fruit is this?","audioLanguage":"en","image":"fruit/grape","options":["grape","apple","pear"],"answer":0},{"activity":"IMAGE_WORD","question":"图片里的水果用英语怎么说？","audioText":"Which fruit is this?","audioLanguage":"en","image":"fruit/orange","options":["banana","peach","orange"],"answer":2}]}'
WHERE id = 131 AND type = 'QUIZ';
