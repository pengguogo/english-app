-- ============================================================
-- V22: 统一 V21 新增课程的 image key
-- 将每个课程(READING/QUIZ)的所有 items 的 image 统一为主图key
-- 主图key = 该课程第一个非空 image 字段
-- 共 180 条 UPDATE 语句
-- ============================================================

-- Lesson 264: 腕龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是腕龙","content":"腕龙是生活在很久以前的巨大恐龙。它最特别的地方是有一条长长的脖子，可以够到高高的树顶，像一架高高的云梯。","image":"dinosaur/brachiosaurus"},{"title":"腕龙的身高","content":"腕龙身高可以达到13米，相当于四层楼那么高。它的前腿比后腿长，所以身体前面高后面低，看起来像一座小山。","image":"dinosaur/brachiosaurus"},{"title":"腕龙的食物","content":"腕龙是食草恐龙，最爱吃树叶。它每天要吃掉很多很多叶子，才能填饱大大的肚子，就像一台不停吃树叶的机器。","image":"dinosaur/brachiosaurus"}]}' WHERE id = 264;

-- Lesson 265: 剑龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是剑龙","content":"剑龙背上长着一排大大的骨板，像背着一把把小扇子。它的大小和大象差不多，是温顺的食草恐龙。","image":"dinosaur/stegosaurus"},{"title":"剑龙的骨板","content":"剑龙背上的骨板可以保护自己，还能调节体温。冷的时候晒太阳变暖，热的时候迎风散热，像一块天然的温度调节板。","image":"dinosaur/stegosaurus"},{"title":"剑龙的尾巴","content":"剑龙的尾巴末端有四根尖尖的刺。遇到危险时，它会用力甩动尾巴，用尖刺赶跑想吃它的肉食恐龙。","image":"dinosaur/stegosaurus"}]}' WHERE id = 265;

-- Lesson 266: 三角龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是三角龙","content":"三角龙的名字意思是长着三只角的恐龙。它头上有两根长角，鼻子上还有一根短角，看起来非常威武。","image":"dinosaur/triceratops"},{"title":"三角龙的头盾","content":"三角龙的脖子后面有一块大大的骨头盾牌，可以保护脖子不被咬到，就像戴着一顶大大的防护帽子。","image":"dinosaur/triceratops"},{"title":"三角龙的防御","content":"三角龙虽然吃草，但很勇敢。遇到霸王龙时，它会低下头用尖角顶过去，保护自己和同伴不被伤害。","image":"dinosaur/triceratops"}]}' WHERE id = 266;

-- Lesson 267: 甲龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是甲龙","content":"甲龙全身覆盖着厚厚的骨甲，就像穿了一身硬邦邦的铠甲。它走起路来慢吞吞的，但敌人很难伤害到它。","image":"dinosaur/ankylosaurus"},{"title":"甲龙的铠甲","content":"甲龙的背上、脖子上、甚至眼皮上都有骨板。这些铠甲硬邦邦的，连霸王龙锋利的牙齿都咬不动它。","image":"dinosaur/ankylosaurus"},{"title":"甲龙的尾巴","content":"甲龙的尾巴末端有一个大大的骨锤。遇到敌人时，它用力一甩，骨锤能把敌人的骨头打断，非常厉害。","image":"dinosaur/ankylosaurus"}]}' WHERE id = 267;

-- Lesson 268: 副栉龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是副栉龙","content":"副栉龙头上长着一个长长的空心冠子，像戴了一顶小帽子。它是食草恐龙，平时用两条腿走路。","image":"dinosaur/parasaurolophus"},{"title":"副栉龙的冠","content":"副栉龙头上的冠是空心的，连通着鼻子。它通过冠子发出声音，可以和远处的同伴说话，像一个小喇叭。","image":"dinosaur/parasaurolophus"},{"title":"副栉龙的生活","content":"副栉龙喜欢和同伴住在一起，成群结队地生活。大家一起找食物，一起防范危险，非常团结友爱。","image":"dinosaur/parasaurolophus"}]}' WHERE id = 268;

-- Lesson 270: 霸王龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是霸王龙","content":"霸王龙是地球上最厉害的肉食恐龙之一。它体型巨大，张开大嘴能一口吞下一大块肉，非常威风。","image":"dinosaur/t-rex"},{"title":"霸王龙的牙齿","content":"霸王龙的牙齿又长又尖，像一把把匕首。它的牙齿还有锯齿边缘，能轻松撕开猎物，非常锋利。","image":"dinosaur/t-rex"},{"title":"霸王龙的小手","content":"霸王龙的前肢很短小，比小朋友的手臂还短。但它的嘴巴和后腿很强大，所以小手并不影响它捕猎。","image":"dinosaur/t-rex"}]}' WHERE id = 270;

-- Lesson 271: 迅猛龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是迅猛龙","content":"迅猛龙是一种小巧的肉食恐龙。它只有火鸡那么大，但跑得飞快，是聪明又灵活的小猎手。","image":"dinosaur/velociraptor"},{"title":"迅猛龙的爪子","content":"迅猛龙的脚上有镰刀一样的大爪子，又弯又尖。它用爪子抓住猎物，像攀爬钩一样牢牢不放。","image":"dinosaur/velociraptor"},{"title":"迅猛龙的智慧","content":"迅猛龙很聪明，喜欢成群结队地捕猎。它们会一起合作，围住比它们大的猎物，配合得非常默契。","image":"dinosaur/velociraptor"}]}' WHERE id = 271;

-- Lesson 272: 棘龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是棘龙","content":"棘龙背上有高高的骨柱撑起的大帆，像扬起的一张大帆。它是最大的肉食恐龙，比霸王龙还要长。","image":"dinosaur/spinosaurus"},{"title":"棘龙的帆","content":"棘龙背上的帆可以调节体温。冷的时候侧面晒太阳取暖，热的时候迎风散热，像一块天然的空调。","image":"dinosaur/spinosaurus"},{"title":"棘龙的食物","content":"棘龙虽然很大，但最爱吃鱼。它的嘴巴长长的，牙齿尖尖的，特别适合抓滑溜溜的鱼。","image":"dinosaur/spinosaurus"}]}' WHERE id = 272;

-- Lesson 273: 异特龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是异特龙","content":"异特龙是生活在霸王龙之前的凶猛猎手。它的名字意思是不同的蜥蜴，因为它的脊椎骨长得很特别。","image":"dinosaur/allosaurus"},{"title":"异特龙的捕猎","content":"异特龙力气很大，可以捕食比它还大的食草恐龙。它用锋利的牙齿和爪子撕咬，是当时最可怕的猎手。","image":"dinosaur/allosaurus"},{"title":"异特龙的特征","content":"异特龙头上有一对小小的角。它的眼睛上方有突起的骨头，看起来像戴了一副特别的小眼镜。","image":"dinosaur/allosaurus"}]}' WHERE id = 273;

-- Lesson 274: 食肉牛龙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是食肉牛龙","content":"食肉牛龙头上长着两只小角，像一头牛。它的皮肤上还有小凸起，像一颗颗小豆子，非常特别。","image":"dinosaur/carnotaurus"},{"title":"食肉牛龙的角","content":"食肉牛龙头上的小角可能用来和同伴争斗。打架时它们用头顶来顶去，像两只小牛在比力气。","image":"dinosaur/carnotaurus"},{"title":"食肉牛龙的速度","content":"食肉牛龙的后腿特别强壮，跑得非常快。它是恐龙里的短跑健将，能追上跑得很快的猎物。","image":"dinosaur/carnotaurus"}]}' WHERE id = 274;

-- Lesson 276: 小丑鱼 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是小丑鱼","content":"小丑鱼穿着橙白相间的条纹衣服，像个小丑一样，所以叫小丑鱼。它住在温暖的浅海里，非常可爱。","image":"ocean/clownfish"},{"title":"小丑鱼和海葵","content":"小丑鱼住在海葵的触手里。海葵有毒刺保护小丑鱼，小丑鱼引来小鱼给海葵吃，它们是互相帮助的好朋友。","image":"ocean/clownfish"},{"title":"小丑鱼的生活","content":"小丑鱼一生都住在同一株海葵里。鱼爸爸会认真照顾鱼卵，等小鱼孵化后就把它们带大，是个好爸爸。","image":"ocean/clownfish"}]}' WHERE id = 276;

-- Lesson 277: 海马 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是海马","content":"海马的脑袋长得像小马，身体弯弯的像棋子。它游泳时直直地立着，慢慢悠悠地在水里飘来飘去。","image":"ocean/seahorse"},{"title":"海马的特殊","content":"海马最特别的是海马爸爸负责生宝宝。妈妈把卵产在爸爸的肚袋里，由爸爸孵化并生下小海马。","image":"ocean/seahorse"},{"title":"海马的尾巴","content":"海马的尾巴能卷住海藻，把自己固定住。这样即使水流很急，它也不会被冲走，非常聪明。","image":"ocean/seahorse"}]}' WHERE id = 277;

-- Lesson 278: 海龟 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是海龟","content":"海龟是大海里的旅行家。它有硬硬的壳和像船桨一样的四肢，能在海里游得很远很远。","image":"ocean/sea-turtle"},{"title":"海龟的壳","content":"海龟的壳是它的家，也是它的盾牌。遇到危险时，它把头和四肢缩进壳里，敌人就咬不到了。","image":"ocean/sea-turtle"},{"title":"海龟的旅行","content":"海龟会在大海里游几千公里。到了生蛋的时候，它会回到自己出生的沙滩，挖坑把蛋埋起来。","image":"ocean/sea-turtle"}]}' WHERE id = 278;

-- Lesson 279: 海星 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是海星","content":"海星长得像一颗五角星，身上硬硬的。它住在海底，慢吞吞地在石头上爬来爬去，不慌不忙。","image":"ocean/starfish"},{"title":"海星的再生","content":"海星有神奇的本领，如果一只胳膊断了，它能长出新的来。有的海星还能从断臂长成一只新的海星。","image":"ocean/starfish"},{"title":"海星的嘴巴","content":"海星的嘴巴长在身体下面。它吃东西时把胃翻出来包住食物，比如贝壳，慢慢消化掉。","image":"ocean/starfish"}]}' WHERE id = 279;

-- Lesson 280: 珊瑚礁 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是珊瑚礁","content":"珊瑚礁是大海里五颜六色的家。它像一座座海底城堡，很多小鱼小虾都住在里面，热闹极了。","image":"ocean/coral-reef"},{"title":"珊瑚虫","content":"珊瑚其实是一种小动物，叫珊瑚虫。无数珊瑚虫住在一起，分泌出硬硬的骨头，慢慢堆成珊瑚礁。","image":"ocean/coral-reef"},{"title":"珊瑚礁的家","content":"珊瑚礁是海洋里最热闹的地方。小丑鱼、蝴蝶鱼、海马都住在里面，就像海底的一座大城市。","image":"ocean/coral-reef"}]}' WHERE id = 280;

-- Lesson 282: 灯笼鱼 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是灯笼鱼","content":"灯笼鱼生活在黑漆漆的深海里。它身上有小小的发光器，像挂着一盏小灯笼，一闪一闪的真好看。","image":"ocean/lanternfish"},{"title":"灯笼鱼的发光","content":"灯笼鱼靠肚子里的特殊器官发光。它的光能吸引小鱼过来，还能和同伴互相打招呼，很有用。","image":"ocean/lanternfish"},{"title":"灯笼鱼的生活","content":"灯笼鱼白天躲在深海，晚上游到浅一些的地方找食物。它是深海里数量最多的小鱼之一。","image":"ocean/lanternfish"}]}' WHERE id = 282;

-- Lesson 283: 巨型乌贼 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是巨型乌贼","content":"巨型乌贼是大海里最神秘的动物之一。它的身体可以长到十几米长，眼睛有盘子那么大。","image":"ocean/giant-squid"},{"title":"巨型乌贼的触手","content":"巨型乌贼有十条触手，其中两条特别长。触手上长满吸盘，能紧紧抓住猎物，力气很大。","image":"ocean/giant-squid"},{"title":"巨型乌贼的天敌","content":"巨型乌贼最大的敌人是抹香鲸。它们会在深海里打架，抹香鲸身上常常有乌贼吸盘的痕迹。","image":"ocean/giant-squid"}]}' WHERE id = 283;

-- Lesson 284: 深海鳗 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是深海鳗","content":"深海鳗的身体又细又长，像一条蛇。它的嘴巴大大的，牙齿尖尖的，看起来有些吓人。","image":"ocean/deep-sea-eel"},{"title":"深海鳗的嘴","content":"深海鳗的嘴巴能张得很大，肚子也能撑得很大。这样它可以一口吞下比自己还大的猎物。","image":"ocean/deep-sea-eel"},{"title":"深海鳗的饵","content":"有些深海鳗尾巴上有一根长长的发光丝。它把丝伸到前面，用光引来小鱼，然后一口吃掉。","image":"ocean/deep-sea-eel"}]}' WHERE id = 284;

-- Lesson 285: 琵琶鱼 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是琵琶鱼","content":"琵琶鱼长得很奇怪，扁扁的身体，大大的头。它头上有一根长长的杆子，杆子顶上会发光。","image":"ocean/anglerfish"},{"title":"琵琶鱼的灯","content":"琵琶鱼头上的灯是发光的诱饵。它在黑暗的深海里点亮灯，小鱼以为那是食物，游过来就被吃掉。","image":"ocean/anglerfish"},{"title":"琵琶鱼的捕猎","content":"琵琶鱼不喜欢追着跑，它喜欢趴在海底等。等小鱼靠近时，它猛地张开大嘴，把小鱼吸进去。","image":"ocean/anglerfish"}]}' WHERE id = 285;

-- Lesson 286: 海底热泉 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是海底热泉","content":"海底热泉是深海海底喷出热水的喷泉。海水从地壳裂缝渗进去，被岩浆加热后又喷出来。","image":"ocean/hydrothermal-vent"},{"title":"热泉的温度","content":"海底热泉喷出的水很热，有的有几十度甚至几百度。但深海的水很冷，所以周围又热又冷很奇妙。","image":"ocean/hydrothermal-vent"},{"title":"热泉的生物","content":"海底热泉附近住着很多奇怪的动物，比如巨大的管虫。它们不吃东西，靠热泉里的营养生活。","image":"ocean/hydrothermal-vent"}]}' WHERE id = 286;

-- Lesson 288: 水星 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是水星","content":"水星是离太阳最近的行星。它虽然叫水星，但上面根本没有水，是一颗光秃秃的石头星球。","image":"space/mercury"},{"title":"水星的温度","content":"水星白天被太阳晒得特别热，晚上又冷得要命。因为它没有大气层保温，温度变化非常大。","image":"space/mercury"},{"title":"水星的样子","content":"水星表面坑坑洼洼的，和月球很像。这些坑是陨石撞击留下的，有大有小，到处都是。","image":"space/mercury"}]}' WHERE id = 288;

-- Lesson 289: 金星 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是金星","content":"金星是夜空中最亮的星星，傍晚和早晨都能看到。它被厚厚的云层包着，闪闪发亮特别好看。","image":"space/venus"},{"title":"金星的大气","content":"金星的大气层特别厚，里面全是二氧化碳。这层大气像厚厚的被子，把热量都捂在里面。","image":"space/venus"},{"title":"金星的温度","content":"金星是太阳系里最热的行星，比水星还热。它的表面温度有400多度，连铅都会融化掉。","image":"space/venus"}]}' WHERE id = 289;

-- Lesson 290: 地球 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是地球","content":"地球是我们生活的星球，也是目前唯一发现有生命的行星。它从太空看是蓝蓝的，所以叫蓝色星球。","image":"space/earth"},{"title":"地球的水","content":"地球表面大部分都是海洋，所以看起来是蓝色的。水是生命不可缺少的东西，有了水才有动植物。","image":"space/earth"},{"title":"地球的大气","content":"地球有一层大气层，里面有我们呼吸的氧气。大气层还能挡住太阳的有害光线，保护我们安全。","image":"space/earth"}]}' WHERE id = 290;

-- Lesson 291: 火星 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是火星","content":"火星是红色的星球。它的土壤里有铁锈，所以看起来红红的，像生锈了一样。","image":"space/mars"},{"title":"火星的表面","content":"火星上有高高的山，也有深深的谷。它还有太阳系最高的山，叫奥林匹斯山，比珠穆朗玛峰高很多。","image":"space/mars"},{"title":"火星的探索","content":"人类已经发射了很多探测器去火星。火星车上装着各种仪器，帮我们了解火星的秘密。","image":"space/mars"}]}' WHERE id = 291;

-- Lesson 292: 木星 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是木星","content":"木星是太阳系里最大的行星。它大到能装下一千多个地球，但是它是一个气体星球，没有硬硬的地面。","image":"space/jupiter"},{"title":"木星的红斑","content":"木星表面有一个红色的大斑点，其实是一场超级大风暴。这场风暴刮了几百年还没停，非常厉害。","image":"space/jupiter"},{"title":"木星的卫星","content":"木星有很多卫星，至少有七八十颗。其中四颗特别大，是伽利略用望远镜最先发现的。","image":"space/jupiter"}]}' WHERE id = 292;

-- Lesson 294: 火箭 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是火箭","content":"火箭是能把人或卫星送上太空的工具。它喷出长长的火焰，推着自己飞向高高的天空。","image":"space/rocket"},{"title":"火箭的推力","content":"火箭靠向下喷气获得向上的推力。它带着很多燃料，燃料燃烧产生高温气体，猛烈地向下喷出。","image":"space/rocket"},{"title":"火箭的分级","content":"大火箭通常分成几级。第一级燃料用完就扔掉，第二级接着烧，这样越飞越轻越飞越快。","image":"space/rocket"}]}' WHERE id = 294;

-- Lesson 295: 卫星 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是卫星","content":"卫星是绕着地球转的机器。它飞得很快，不会掉下来，能在天上工作很多很多年。","image":"space/satellite"},{"title":"卫星的作用","content":"卫星能帮我们做很多事。通信卫星转播电视节目，气象卫星预报天气，导航卫星帮我们认路。","image":"space/satellite"},{"title":"人造卫星","content":"人类发射了很多人造卫星，它们绕着地球飞。有的飞得低，有的飞得高，组成一个太空大家庭。","image":"space/satellite"}]}' WHERE id = 295;

-- Lesson 296: 空间站 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是空间站","content":"空间站是宇航员在太空里的家。它像一座大房子，宇航员在里面生活和工作很长时间。","image":"space/space-station"},{"title":"空间站的生活","content":"在空间站里，东西会飘在空中，因为那里没有重力。宇航员睡觉要绑在睡袋里，不然会飘走。","image":"space/space-station"},{"title":"空间站的实验","content":"宇航员在空间站里做各种科学实验。他们研究植物怎么在太空生长，还观察地球的变化。","image":"space/space-station"}]}' WHERE id = 296;

-- Lesson 297: 月球车 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是月球车","content":"月球车是在月球上跑的车。它不需要人开，自己能动，帮我们把月球的秘密带回来。","image":"space/lunar-rover"},{"title":"月球车的作用","content":"月球车装着照相机和仪器，在月球上拍照、挖土、测石头。它把看到的东西传回地球。","image":"space/lunar-rover"},{"title":"中国的月球车","content":"中国发射过玉兔号月球车。它在月球上走了很远，拍了很多照片，还带回了月球土壤。","image":"space/lunar-rover"}]}' WHERE id = 297;

-- Lesson 298: 天文望远镜 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是天文望远镜","content":"天文望远镜是看星星的工具。它能把远处的东西放大，让我们看到肉眼看不见的星星。","image":"space/telescope"},{"title":"望远镜的原理","content":"望远镜用大大的镜片收集光线。镜片越大，收集的光越多，能看到的东西就越远越清楚。","image":"space/telescope"},{"title":"著名望远镜","content":"哈勃望远镜是在太空里的望远镜。它在外面拍照不受大气干扰，拍出了很多美丽的星云照片。","image":"space/telescope"}]}' WHERE id = 298;

-- Lesson 300: 蜜蜂 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是蜜蜂","content":"蜜蜂是勤劳的小昆虫。它飞到花朵上采花蜜，带回蜂巢做成甜甜的蜂蜜，非常辛苦。","image":"insect/bee"},{"title":"蜜蜂的舞蹈","content":"蜜蜂会跳舞告诉同伴花在哪里。如果跳圆圈舞，花就在附近；如果跳八字舞，花就比较远。","image":"insect/bee"},{"title":"蜜蜂的家","content":"蜜蜂住在蜂巢里。蜂巢是一个个六角形的小房间，有的装蜂蜜，有的住宝宝，整整齐齐的。","image":"insect/bee"}]}' WHERE id = 300;

-- Lesson 301: 蝴蝶 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是蝴蝶","content":"蝴蝶是穿着花衣裳的昆虫。它的翅膀上有美丽的花纹，飞起来像一朵会飞的花。","image":"insect/butterfly"},{"title":"蝴蝶的一生","content":"蝴蝶小时候是毛毛虫，只吃叶子。它结成蛹睡一觉，醒来就变成了美丽的蝴蝶，这叫变态。","image":"insect/butterfly"},{"title":"蝴蝶的食物","content":"蝴蝶用长长的嘴吸花蜜。它飞到花丛中，把嘴伸进花朵里吸蜜，同时帮花朵传粉。","image":"insect/butterfly"}]}' WHERE id = 301;

-- Lesson 302: 瓢虫 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是瓢虫","content":"瓢虫是圆圆的小甲虫，背上有点点。有的是红底黑点，有的是黄底黑点，都很可爱。","image":"insect/ladybug"},{"title":"瓢虫的益处","content":"瓢虫是农民的好帮手。它最爱吃蚜虫，一只瓢虫一天能吃掉好多只蚜虫，保护庄稼。","image":"insect/ladybug"},{"title":"瓢虫的防御","content":"瓢虫遇到危险会装死，从叶子上滚下去。它还会从腿里流出难闻的黄色液体，让鸟儿不想吃它。","image":"insect/ladybug"}]}' WHERE id = 302;

-- Lesson 303: 蚂蚁 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是蚂蚁","content":"蚂蚁是小小的大力士。它虽然很小，但能举起比自己重几十倍的东西，力气可大了。","image":"insect/ant"},{"title":"蚂蚁的家","content":"蚂蚁住在地下的蚁巢里。蚁巢有很多房间和通道，有的住蚂蚁，有的放食物，像一座地下城市。","image":"insect/ant"},{"title":"蚂蚁的团结","content":"蚂蚁很团结，做事都一起。它们排着队搬运食物，遇到大的食物就一起抬，谁也不偷懒。","image":"insect/ant"}]}' WHERE id = 303;

-- Lesson 304: 蜻蜓 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是蜻蜓","content":"蜻蜓是飞得很快的昆虫。它有四片透明的翅膀，能像直升机一样停在空中，飞起来很灵活。","image":"insect/dragonfly"},{"title":"蜻蜓的眼睛","content":"蜻蜓有两只大大的复眼。每只复眼由很多小眼组成，能看到四面八方，抓虫子特别准。","image":"insect/dragonfly"},{"title":"蜻蜓的幼虫","content":"蜻蜓宝宝叫水虿，生活在水里。它在水里长大，然后爬出水面，脱掉皮变成会飞的蜻蜓。","image":"insect/dragonfly"}]}' WHERE id = 304;

-- Lesson 306: 萤火虫 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是萤火虫","content":"萤火虫是夏夜里的小灯笼。它的尾巴会一闪一闪发光，在草丛里飞来飞去，特别好看。","image":"insect/firefly"},{"title":"萤火虫的光","content":"萤火虫发光是为了找朋友。雄萤火虫飞起来发光，雌萤火虫在草里回应，它们靠光说话。","image":"insect/firefly"},{"title":"萤火虫的生活","content":"萤火虫喜欢住在干净潮湿的地方。它的宝宝也会发光，住在水边或草丛里慢慢长大。","image":"insect/firefly"}]}' WHERE id = 306;

-- Lesson 307: 蟋蟀 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是蟋蟀","content":"蟋蟀是会唱歌的昆虫。夏天的晚上，它在草丛里唧唧唧地叫，像在开一场好听的音乐会。","image":"insect/cricket"},{"title":"蟋蟀的歌声","content":"蟋蟀靠摩擦翅膀发出声音。两片翅膀互相摩擦，就像拉小提琴一样，能发出响亮的歌声。","image":"insect/cricket"},{"title":"蟋蟀的家","content":"蟋蟀喜欢住在草丛或石头缝里。有的蟋蟀还会挖洞，在地下做一个小窝，白天躲在里面。","image":"insect/cricket"}]}' WHERE id = 307;

-- Lesson 308: 飞蛾 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是飞蛾","content":"飞蛾是蝴蝶的亲戚。它喜欢在夜里活动，白天躲在树叶背面睡觉，晚上才出来找花蜜。","image":"insect/moth"},{"title":"飞蛾和蝴蝶","content":"飞蛾和蝴蝶很像，但有区别。蝴蝶触角细细的，飞蛾触角像羽毛；蝴蝶白天飞，飞蛾夜里飞。","image":"insect/moth"},{"title":"飞蛾的趋光","content":"飞蛾喜欢光，夜里会围着灯飞。科学家说这是因为飞蛾靠月亮认路，灯光让它迷路了。","image":"insect/moth"}]}' WHERE id = 308;

-- Lesson 309: 独角仙 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是独角仙","content":"独角仙是威武的甲虫。雄独角仙头上长着一只大角，像独角兽一样，所以叫独角仙。","image":"insect/rhinoceros-beetle"},{"title":"独角仙的角","content":"独角仙的角是打架的武器。两只雄独角仙为了争食物会顶角，谁把谁顶翻谁就赢了。","image":"insect/rhinoceros-beetle"},{"title":"独角仙的生活","content":"独角仙喜欢住在树林里，最爱吸树汁。它白天躲在树洞里，晚上出来找东西吃。","image":"insect/rhinoceros-beetle"}]}' WHERE id = 309;

-- Lesson 310: 螳螂 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是螳螂","content":"螳螂是绿色的捕虫高手。它前肢弯弯的，像两把镰刀，举在胸前，随时准备抓虫子。","image":"insect/mantis"},{"title":"螳螂的捕猎","content":"螳螂很有耐心，会一动不动地等。等虫子靠近时，它闪电一样伸出前肢，紧紧夹住猎物。","image":"insect/mantis"},{"title":"螳螂的保护色","content":"螳螂的身体是绿色的，躲在草里很难被发现。有的螳螂像枯叶，有的像花朵，伪装本领很高。","image":"insect/mantis"}]}' WHERE id = 310;

-- Lesson 312: 云朵 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是云朵","content":"云朵是飘在天上的水汽。地上的水变成看不见的水蒸气飞上天，遇冷就变成小水滴聚成云。","image":"weather/cloud"},{"title":"云朵的形状","content":"云朵有各种各样的形状。有的像棉花糖一团一团，有的像羽毛细细长长，有的像山一样高。","image":"weather/cloud"},{"title":"云朵的种类","content":"云朵有高有低。高的云在很高的天上，薄的像纱；低的云离地面近，灰灰的常常带来雨水。","image":"weather/cloud"}]}' WHERE id = 312;

-- Lesson 313: 小雨 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是雨","content":"雨是从云里掉下来的水滴。云里的小水滴越聚越多，太重了托不住，就掉下来变成雨。","image":"weather/rain"},{"title":"雨的大小","content":"雨有毛毛雨也有大雨。毛毛雨细细的几乎看不见，大雨哗哗地下，地上会积水。","image":"weather/rain"},{"title":"雨的好处","content":"雨能滋润大地，让花草树木喝饱水。雨水还会流进河流，最后汇入大海，让水一直循环。","image":"weather/rain"}]}' WHERE id = 313;

-- Lesson 314: 大风 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是风","content":"风是空气在流动。太阳晒得不均匀，有的地方热有的地方冷，空气一流动就变成了风。","image":"weather/wind"},{"title":"风的大小","content":"风有微风也有狂风。微风轻轻吹，树叶摇一摇；狂风呼呼响，能把大树吹倒，力量很大。","image":"weather/wind"},{"title":"风的作用","content":"风能帮我们做很多事。风能吹动风车发电，能把花粉吹到远处，还能把云吹走让天放晴。","image":"weather/wind"}]}' WHERE id = 314;

-- Lesson 315: 雪花 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是雪花","content":"雪花是天上掉下来的小冰晶。天很冷的时候，云里的水直接变成冰，变成小雪花飘飘下来。","image":"weather/snowflake"},{"title":"雪花的形状","content":"雪花都是六角形的，但每一片都不一样。它们有的像星星，有的像花朵，非常精致漂亮。","image":"weather/snowflake"},{"title":"雪的作用","content":"雪盖在地上像一床棉被，能保护麦苗过冬。雪融化后变成水，还能让土壤湿润，庄稼长得好。","image":"weather/snowflake"}]}' WHERE id = 315;

-- Lesson 316: 雷电 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是雷电","content":"雷电是雷雨天出现的闪电和雷声。天空突然亮一下是闪电，轰隆隆的声音是雷声。","image":"weather/lightning"},{"title":"闪电的形成","content":"闪电是云里的电放出来。云里的小水滴摩擦产生电，电太多了就打出来，变成一道亮光。","image":"weather/lightning"},{"title":"雷电的安全","content":"打雷闪电时不要在大树下躲雨，也不要在水里游泳。最好待在屋里，等雷电过去再出去玩。","image":"weather/lightning"}]}' WHERE id = 316;

-- Lesson 318: 春天 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是春天","content":"春天是一年的第一个季节。天气慢慢变暖，冰雪融化，小草发芽，万物都苏醒了。","image":"weather/spring"},{"title":"春天的变化","content":"春天树木长出新叶，桃花梨花开了，小鸟叽叽喳喳地唱歌，青蛙也醒了，到处生机勃勃。","image":"weather/spring"},{"title":"春天的活动","content":"春天最适合去外面玩。小朋友可以放风筝、踏青、看花开，还能种下小树苗等它长大。","image":"weather/spring"}]}' WHERE id = 318;

-- Lesson 319: 夏天 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是夏天","content":"夏天是一年里最热的季节。太阳火辣辣地晒，知了在树上叫，大家爱吃冰激凌解暑。","image":"weather/summer"},{"title":"夏天的特点","content":"夏天白天很长，晚上很晚才天黑。有时候会下雷阵雨，哗啦啦下一阵，雨后空气很凉快。","image":"weather/summer"},{"title":"夏天的活动","content":"夏天可以去游泳、吃西瓜、捉知了。晚上还能看到萤火虫，在草丛里一闪一闪的。","image":"weather/summer"}]}' WHERE id = 319;

-- Lesson 320: 秋天 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是秋天","content":"秋天是丰收的季节。天气慢慢变凉，树叶变黄变红，果园里的果子都熟了。","image":"weather/autumn"},{"title":"秋天的变化","content":"秋天树叶落下来，像下了一场金色的雨。大雁排成人字形飞往南方，准备过冬。","image":"weather/autumn"},{"title":"秋天的活动","content":"秋天可以去摘苹果、看红叶、放风筝。农民伯伯忙着收粮食，田野里一片忙碌的景象。","image":"weather/autumn"}]}' WHERE id = 320;

-- Lesson 321: 冬天 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是冬天","content":"冬天是一年里最冷的季节。天气很冷，很多地方会下雪，小动物都躲起来冬眠了。","image":"weather/winter"},{"title":"冬天的变化","content":"冬天树叶掉光了，河水结冰了。小熊、青蛙、刺猬都在睡觉，等春天来了才醒来。","image":"weather/winter"},{"title":"冬天的活动","content":"冬天可以堆雪人、打雪仗、滑雪。过年的时候还能贴春联、放鞭炮，一家人团团圆圆。","image":"weather/winter"}]}' WHERE id = 321;

-- Lesson 322: 节气 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是节气","content":"节气是古人对天气变化的总结。他们观察太阳和天气，把一年分成24段，每段一个节气。","image":"weather/solar-terms"},{"title":"二十四节气","content":"一年有24个节气。春天有立春、雨水、惊蛰等，每个节气大约隔15天，名字都有意义。","image":"weather/solar-terms"},{"title":"节气的用途","content":"节气能指导农民种地。比如清明前后种瓜点豆，霜降要收庄稼，古人靠节气安排一年的农活。","image":"weather/solar-terms"}]}' WHERE id = 322;

-- Lesson 324: 鼓 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是鼓","content":"鼓是最古老的乐器之一。用鼓棒敲鼓面就会发出咚咚的声音，又响又有力，特别热闹。","image":"music/drum"},{"title":"鼓的构造","content":"鼓有圆圆的鼓面和鼓身。鼓面是用皮做的，现在也有塑料的；鼓身是木头或金属做的，把鼓面撑紧。","image":"music/drum"},{"title":"鼓的用途","content":"鼓的用处很多。过节时敲锣打鼓很热闹，军队里用鼓指挥行进，乐队里鼓手负责打节拍。","image":"music/drum"}]}' WHERE id = 324;

-- Lesson 325: 铃鼓 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是铃鼓","content":"铃鼓是一种带铃铛的小鼓。它的一面有皮，鼓身边上有一圈小铜铃，摇一摇就会叮当响。","image":"music/tambourine"},{"title":"铃鼓的声音","content":"铃鼓能发出两种声音。用手敲鼓面是咚咚声，摇晃或摩擦鼓边是沙沙的铃声，特别活泼。","image":"music/tambourine"},{"title":"铃鼓的使用","content":"铃鼓常用来给舞蹈伴奏。跳舞的人一手拿铃鼓，一边跳一边敲，声音和动作配在一起很好看。","image":"music/tambourine"}]}' WHERE id = 325;

-- Lesson 326: 木琴 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是木琴","content":"木琴是用木条做的乐器。它有一排长短不同的木条，排成梯形，用鼓棒一敲就叮叮咚咚响。","image":"music/xylophone"},{"title":"木琴的声音","content":"木琴的声音清脆悦耳，像水滴落在石头上。木条越长声音越低，越短声音越高，能敲出好听的曲子。","image":"music/xylophone"},{"title":"木琴的演奏","content":"演奏木琴时用两根小鼓棒。两只手各拿一根，在木条上敲来敲去，就能弹出欢快的乐曲。","image":"music/xylophone"}]}' WHERE id = 326;

-- Lesson 327: 三角铁 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是三角铁","content":"三角铁是一根弯成三角形的金属条。它小小的，拿在手里用金属棒一敲，叮的一声很清脆。","image":"music/triangle"},{"title":"三角铁的声音","content":"三角铁的声音叮叮当当，能响很久。它像一颗小铃铛，声音穿透力很强，在乐队里特别明显。","image":"music/triangle"},{"title":"三角铁的演奏","content":"演奏三角铁时用一根小金属棒敲。可以敲里面、外面或角上，声音有点不一样，但都很清脆。","image":"music/triangle"}]}' WHERE id = 327;

-- Lesson 328: 沙锤 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是沙锤","content":"沙锤是装着沙子或豆子的小乐器。它通常是葫芦或木头做的，摇一摇就发出沙沙的声音。","image":"music/maraca"},{"title":"沙锤的声音","content":"沙锤的声音沙沙的，软软的。它不像鼓那么响，但能给音乐增加节奏，让曲子更活泼。","image":"music/maraca"},{"title":"沙锤的用途","content":"沙锤是节奏乐器。演奏时两只手各拿一个，跟着节拍摇动，能给歌曲打好拍子。","image":"music/maraca"}]}' WHERE id = 328;

-- Lesson 330: 钢琴 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是钢琴","content":"钢琴是乐器之王。它有黑白相间的琴键，用手指一按就叮叮咚咚响，能弹出很多好听的曲子。","image":"music/piano"},{"title":"钢琴的发声","content":"钢琴里面有很多根弦。按下琴键，里面的小锤子就敲弦，弦振动发出声音，键越高声音越高。","image":"music/piano"},{"title":"钢琴的用途","content":"钢琴能独奏也能伴奏。学钢琴能锻炼手指灵活，还能让人更聪明，很多小朋友都在学。","image":"music/piano"}]}' WHERE id = 330;

-- Lesson 331: 小提琴 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是小提琴","content":"小提琴是用弓拉的弦乐器。它有四根弦，用弓在弦上拉，就能发出优美动听的声音。","image":"music/violin"},{"title":"小提琴的构造","content":"小提琴有琴身、琴颈和琴头。琴身是木头的，像葫芦形；琴颈上有四根弦，调一调能变高变低。","image":"music/violin"},{"title":"小提琴的声音","content":"小提琴的声音悠扬婉转，像人在唱歌。它在乐队里很重要，常常拉主旋律，是弦乐的灵魂。","image":"music/violin"}]}' WHERE id = 331;

-- Lesson 332: 吉他 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是吉他","content":"吉他是用手指拨弦的乐器。它有六根弦，抱着弹，叮叮咚咚，常用来边弹边唱。","image":"music/guitar"},{"title":"吉他的种类","content":"吉他有很多种。木吉他是空心的，声音温柔；电吉他插上电，声音很酷，在乐队里特别帅。","image":"music/guitar"},{"title":"吉他的用途","content":"吉他很受欢迎，弹唱很方便。背着吉他走到哪里都能弹，是很多歌手最喜欢用的乐器。","image":"music/guitar"}]}' WHERE id = 332;

-- Lesson 333: 竖琴 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是竖琴","content":"竖琴是立着弹的弦乐器。它有很多根弦，斜斜地排在架子上，像一把大大的弓。","image":"music/harp"},{"title":"竖琴的弦","content":"竖琴有47根弦，排成一个三角形。弦有不同颜色，方便找到要弹的那根，用手指拨一拨就能响。","image":"music/harp"},{"title":"竖琴的声音","content":"竖琴的声音像流水，叮叮咚咚很柔和。弹竖琴时两只手在弦上拨来拨去，像仙女在弹琴。","image":"music/harp"}]}' WHERE id = 333;

-- Lesson 334: 大提琴 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是大提琴","content":"大提琴是放大的小提琴。它比小提琴大很多，坐在椅子上夹在腿间弹，声音低沉浑厚。","image":"music/cello"},{"title":"大提琴的声音","content":"大提琴的声音低低的，像大人在说话。它不像小提琴那么高，但很温暖，常常给乐曲打底。","image":"music/cello"},{"title":"大提琴的演奏","content":"大提琴也是用弓拉的。它比较大，演奏时要坐着，把琴撑在地上，用弓在弦上拉来拉去。","image":"music/cello"}]}' WHERE id = 334;

-- Lesson 336: 水彩 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是水彩","content":"水彩是一种用水调和的颜料。它颜色透明，画在纸上像水一样会流动，能画出淡淡的美丽色彩。","image":"art/watercolor"},{"title":"水彩的稀释","content":"画水彩时要加水稀释颜料。水加得多颜色就淡，水加得少颜色就深，可以调出很多层次。","image":"art/watercolor"},{"title":"水彩混色","content":"水彩可以混色。黄色加蓝色会变成绿色，红色加黄色会变成橙色，像变魔术一样有趣。","image":"art/watercolor"}]}' WHERE id = 336;

-- Lesson 337: 蜡笔 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是蜡笔","content":"蜡笔是用蜡做成的画笔。它摸起来滑滑的，画出来的颜色很鲜艳，小朋友最喜欢用它画画。","image":"art/crayon"},{"title":"蜡笔的特点","content":"蜡笔画出来的线条粗粗的，很有力量。它不怕水，所以可以叠着画很多层颜色。","image":"art/crayon"},{"title":"蜡笔的用法","content":"用蜡笔画画时要用力一点。可以画线条也可以涂大片颜色，还能用手指把颜色擦开。","image":"art/crayon"}]}' WHERE id = 337;

-- Lesson 338: 彩纸 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是彩纸","content":"彩纸是有很多颜色的纸。红的黄的蓝的绿的，五颜六色非常漂亮，可以用来做各种手工作品。","image":"art/color-paper"},{"title":"彩纸的用途","content":"彩纸可以折成小动物，可以剪成花朵，还可以搓成小圆球贴在画上，玩法多种多样。","image":"art/color-paper"},{"title":"彩纸手工","content":"用彩纸做手工要小心使用剪刀。可以把不同颜色的纸拼在一起，做出美丽的图画和装饰。","image":"art/color-paper"}]}' WHERE id = 338;

-- Lesson 339: 黏土 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是黏土","content":"黏土是一种软软的泥，可以捏成各种形状。它有红黄蓝绿很多颜色，是做手工的好材料。","image":"art/clay"},{"title":"黏土的玩法","content":"捏黏土时用手掌揉圆球，用手指搓长条。可以做小动物、小食物，想做什么就做什么。","image":"art/clay"},{"title":"黏土的保存","content":"黏土做完要包好保存，不然会变干变硬。做好的作品放一会儿会慢慢变硬，就能一直留着了。","image":"art/clay"}]}' WHERE id = 339;

-- Lesson 340: 画布 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是画布","content":"画布是画家专门用来画画的布。它绷在木框上，表面有点粗糙，画上去颜色很结实。","image":"art/canvas"},{"title":"画布的特点","content":"画布比纸更结实，可以画很大幅的画。它不容易破，画好的画可以挂很久很久。","image":"art/canvas"},{"title":"画布的用法","content":"在画布上画画一般用颜料。画错了可以等干了再盖一层颜色重新画，很方便。","image":"art/canvas"}]}' WHERE id = 340;

-- Lesson 342: 涂色 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是涂色","content":"涂色是把颜色填进图案里。要顺着同一个方向涂，颜色才会均匀好看，不会一块深一块浅。","image":"art/coloring"},{"title":"涂色的方法","content":"涂色时先涂边边再涂中间。用力要均匀，不要超出黑线，这样画出来的画才漂亮。","image":"art/coloring"},{"title":"颜色搭配","content":"涂色可以选喜欢的颜色。相邻的地方用不同颜色，画出来的画会更鲜艳更好看。","image":"art/coloring"}]}' WHERE id = 342;

-- Lesson 343: 剪贴 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是剪贴","content":"剪贴是用剪刀把纸剪成形状，再贴到另一张纸上。可以做出花朵、小动物等美丽图案。","image":"art/cut-paste"},{"title":"剪刀的使用","content":"用剪刀要小心。手指放进剪刀的圆圈里，一张一合地剪，剪刀尖不要对着人，注意安全。","image":"art/cut-paste"},{"title":"剪贴的组合","content":"剪贴可以把不同颜色不同形状的纸拼在一起。先想好要做什么，再剪再贴，作品会更漂亮。","image":"art/cut-paste"}]}' WHERE id = 343;

-- Lesson 344: 折纸 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是折纸","content":"折纸是把方形的纸折成各种形状。不用剪刀和胶水，只用手折，就能变出很多有趣的东西。","image":"art/origami"},{"title":"折纸的基本折法","content":"折纸最常用的是对折。把纸对齐边边再压平，折痕要折得清楚，作品才会整齐好看。","image":"art/origami"},{"title":"折纸作品","content":"折纸可以折小船、青蛙、飞机、花朵。跟着步骤一步步折，一张纸就能变成很多好玩的东西。","image":"art/origami"}]}' WHERE id = 344;

-- Lesson 345: 盖章 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是盖章","content":"盖章是把印章蘸上颜料，再按在纸上。印章上有图案，按下去就会印出好看的形状。","image":"art/stamp"},{"title":"盖章的方法","content":"盖章时印章要拿正，按下去用力均匀，再轻轻拿起来，图案才会清楚完整。","image":"art/stamp"},{"title":"盖章创作","content":"可以用不同图案的印章组合。盖成一排排或者一圈圈，就能做出美丽的花纹和装饰。","image":"art/stamp"}]}' WHERE id = 345;

-- Lesson 346: 拼贴 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是拼贴","content":"拼贴是把各种材料拼在一起贴成画。可以用纸、布、树叶等，做出有立体感的作品。","image":"art/collage"},{"title":"拼贴的材料","content":"拼贴材料很多样。彩纸、毛线、纽扣、干树叶都可以用，不同材料有不同的质感。","image":"art/collage"},{"title":"拼贴的方法","content":"拼贴时先摆放好再贴。想好构图，大的材料放下面，小的材料放上面，一层层贴上去。","image":"art/collage"}]}' WHERE id = 346;

-- Lesson 348: 足球 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是足球","content":"足球是用脚踢的球。它是圆圆的，黑白相间，小朋友们在一起踢足球非常开心。","image":"sports/football"},{"title":"足球的玩法","content":"踢足球只能用脚，不能用手。要把球踢进对方的球门里，进球多的一队就赢了。","image":"sports/football"},{"title":"足球比赛","content":"足球比赛有两支队伍。每队很多人一起配合，传球跑动，团队协作才能赢得比赛。","image":"sports/football"}]}' WHERE id = 348;

-- Lesson 349: 篮球 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是篮球","content":"篮球是橙色的球，表面有小颗粒。它有弹性，拍在地上会弹起来，很好玩。","image":"sports/basketball"},{"title":"篮球的玩法","content":"打篮球要用手拍球运球。把球投进高高的篮筐里就能得分，投进一个很难的球会得到三分。","image":"sports/basketball"},{"title":"篮球比赛","content":"篮球比赛两支队伍对抗。队员要传球、运球、投篮，还要防守对方，互相配合才能赢。","image":"sports/basketball"}]}' WHERE id = 349;

-- Lesson 350: 排球 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是排球","content":"排球是比较轻的球。它的表面柔软，颜色鲜艳，打起来不会手痛，适合小朋友玩。","image":"sports/volleyball"},{"title":"排球的玩法","content":"打排球不能让球落地。要用手把球打给队友，再打到对方那边，来回打来打去很有趣。","image":"sports/volleyball"},{"title":"排球比赛","content":"排球比赛中间有一张高高的网。两边各六个人，把球打过网，对方接不到就得分了。","image":"sports/volleyball"}]}' WHERE id = 350;

-- Lesson 351: 网球 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是网球","content":"网球是黄绿色的小球，毛茸茸的很有弹性。打网球要用专门的球拍，球拍有网线。","image":"sports/tennis"},{"title":"网球的玩法","content":"打网球用球拍把球打过中间的网。要瞄准对方接不到的地方打，让对方接不到球。","image":"sports/tennis"},{"title":"网球场地","content":"网球场有草地和硬地两种。中间有网隔开，球员在两边跑来跑去打球，要跑得很快。","image":"sports/tennis"}]}' WHERE id = 351;

-- Lesson 352: 乒乓球 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是乒乓球","content":"乒乓球是小小的白色或黄色球，很轻。它弹跳很快，是我国的国球，很多人喜欢。","image":"sports/table-tennis"},{"title":"乒乓球的玩法","content":"打乒乓球用小球拍在桌子上打。球要在自己这边弹一下，打过网，再在对方那边弹一下。","image":"sports/table-tennis"},{"title":"乒乓球特点","content":"乒乓球在桌上打，不用大场地。球小速度快，要眼疾手快才能接住，很锻炼反应能力。","image":"sports/table-tennis"}]}' WHERE id = 352;

-- Lesson 354: 跑步 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是跑步","content":"跑步是跑着前进的运动。它最简单也最锻炼身体，只要有一双运动鞋就能跑。","image":"sports/running"},{"title":"跑步的方法","content":"跑步时手臂要摆动，脚步要轻快。眼睛看前面，呼吸要均匀，一步一步向前跑。","image":"sports/running"},{"title":"跑步比赛","content":"跑步比赛看谁先到终点。有短跑和长跑，短跑要爆发力，长跑要有耐力坚持到底。","image":"sports/running"}]}' WHERE id = 354;

-- Lesson 355: 跳远 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是跳远","content":"跳远是跳得最远的比赛。先跑起来，再用力一跳，跳进沙坑里，看谁跳得最远。","image":"sports/long-jump"},{"title":"跳远的方法","content":"跳远要先助跑加快速度。到起跳线用力蹬地起跳，在空中伸腿，最后落入沙坑。","image":"sports/long-jump"},{"title":"跳远的测量","content":"跳远成绩是从起跳线量到落点。起跳不能超过线，落地要稳，跳得越远成绩越好。","image":"sports/long-jump"}]}' WHERE id = 355;

-- Lesson 356: 接力赛 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是接力赛","content":"接力赛是几个人一起跑的比赛。每人跑一段，把接力棒传给下一个人，团队合作完成。","image":"sports/relay"},{"title":"接力棒","content":"接力棒是一根小棒子。跑的人拿着它，交给下一个人时不能掉，掉棒了就麻烦了。","image":"sports/relay"},{"title":"接力赛配合","content":"接力赛最重要的是传棒。要提前准备好，跑得快接得稳，大家齐心协力才能赢。","image":"sports/relay"}]}' WHERE id = 356;

-- Lesson 357: 平衡木 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是平衡木","content":"平衡木是一条长长的窄木板。运动员要在上面做各种动作，不能掉下来，很考验平衡。","image":"sports/balance-beam"},{"title":"平衡木的玩法","content":"走平衡木要张开手臂保持平衡。一步一步慢慢走，眼睛看前面，身体不能晃。","image":"sports/balance-beam"},{"title":"平衡木运动","content":"平衡木是体操项目之一。运动员在上面走、跳、转体，动作要优美又稳当，很难很厉害。","image":"sports/balance-beam"}]}' WHERE id = 357;

-- Lesson 358: 跳绳 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是跳绳","content":"跳绳是拿着绳子跳来跳去的运动。一根绳子就能玩，是很好的锻炼身体的方法。","image":"sports/jump-rope"},{"title":"跳绳的方法","content":"跳绳时双手摇绳，绳子到脚前跳起来。要手腕用力摇绳，脚尖轻轻跳，节奏要一致。","image":"sports/jump-rope"},{"title":"跳绳的好处","content":"跳绳能让人长高变灵活。可以一个人跳，也可以几个人一起跳，越跳越熟练越快。","image":"sports/jump-rope"}]}' WHERE id = 358;

-- Lesson 360: 医生 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是医生","content":"医生是给病人治病的人。他们穿白大褂，会用听诊器听心跳，帮小朋友恢复健康。","image":"career/doctor"},{"title":"医生的工作","content":"医生会问哪里不舒服，然后检查身体。他们会开药或者打针，让病人快点好起来。","image":"career/doctor"},{"title":"看医生","content":"生病了要去看医生。医生很亲切，要勇敢配合检查，听完医生的话才能快点好。","image":"career/doctor"}]}' WHERE id = 360;

-- Lesson 361: 护士 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是护士","content":"护士是照顾病人的人。她们戴白色帽子，会量体温、打针、发药，细心照顾每个病人。","image":"career/nurse"},{"title":"护士的工作","content":"护士每天查房看病人。她们按时给病人吃药，观察病情变化，还会安慰病人不要害怕。","image":"career/nurse"},{"title":"护士和医生","content":"护士和医生一起工作。医生看病开药，护士照顾病人，他们合作让病人恢复健康。","image":"career/nurse"}]}' WHERE id = 361;

-- Lesson 362: 消防员 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是消防员","content":"消防员是救火救人的人。他们穿厚厚的防火服，开着红色消防车，勇敢地去救火。","image":"career/firefighter"},{"title":"消防员的工作","content":"消防员不只灭火。他们还救人、救小动物，发生灾难时他们都会去帮忙，很勇敢。","image":"career/firefighter"},{"title":"消防安全","content":"小朋友不能玩火。遇到火灾要打119，用湿毛巾捂住嘴鼻弯腰跑，不能坐电梯。","image":"career/firefighter"}]}' WHERE id = 362;

-- Lesson 363: 警察 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是警察","content":"警察是保护大家安全的人。他们穿蓝色制服，会指挥交通、抓坏人，让大家生活安全。","image":"career/police"},{"title":"警察的工作","content":"警察在街上巡逻。他们帮助走丢的小朋友找家人，指挥车辆通行，处理各种问题。","image":"career/police"},{"title":"遇到危险","content":"遇到危险要打110找警察。警察叔叔阿姨会保护我们，记住他们的电话很重要。","image":"career/police"}]}' WHERE id = 363;

-- Lesson 364: 救护车 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是救护车","content":"救护车是送病人的车。它是白色的，车身有红十字，还会发出呜呜的警报声。","image":"career/ambulance"},{"title":"救护车的特点","content":"救护车里面像小医院。有担架、氧气和各种急救设备，可以在路上给病人急救。","image":"career/ambulance"},{"title":"急救电话","content":"有人病得很急要打120。救护车会很快赶到，把病人送到医院及时治疗，救命要快。","image":"career/ambulance"}]}' WHERE id = 364;

-- Lesson 366: 厨师 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是厨师","content":"厨师是做菜的人。他们穿白色制服戴高帽子，在厨房里做出好吃的饭菜给大家吃。","image":"career/chef"},{"title":"厨师的工作","content":"厨师会切菜、炒菜、煮汤。他们知道什么食材搭配好吃，做出色香味俱全的美食。","image":"career/chef"},{"title":"厨师的手艺","content":"厨师要练很久才能做好菜。他们刀工很快，炒菜动作熟练，还会做漂亮的摆盘。","image":"career/chef"}]}' WHERE id = 366;

-- Lesson 367: 建筑师 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是建筑师","content":"建筑师是设计房子的人。他们先画出图纸，告诉工人房子要建成什么样子。","image":"career/architect"},{"title":"建筑师的工作","content":"建筑师要考虑很多事。房子要结实、好看、好用，还要能遮风挡雨，住着舒服。","image":"career/architect"},{"title":"建筑设计","content":"建筑师用尺子和笔画图。他们设计各种各样的房子，有高有矮有方有圆，每栋都不同。","image":"career/architect"}]}' WHERE id = 367;

-- Lesson 368: 邮递员 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是邮递员","content":"邮递员是送信和包裹的人。他们穿绿色制服骑绿色摩托车，把信件送到千家万户。","image":"career/postman"},{"title":"邮递员的工作","content":"邮递员每天整理信件。按照地址一封封送到每家每户，不管刮风下雨都要工作。","image":"career/postman"},{"title":"信件的旅行","content":"写好的信要贴邮票放进邮筒。邮递员取出信，分类送到各地，信就开始旅行了。","image":"career/postman"}]}' WHERE id = 368;

-- Lesson 369: 园丁 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是园丁","content":"园丁是照顾花草树木的人。他们浇水、施肥、修剪，让花园和公园变得美丽。","image":"career/gardener"},{"title":"园丁的工作","content":"园丁每天给植物浇水。他们拔杂草、捉害虫、修剪枝叶，让花草长得健康漂亮。","image":"career/gardener"},{"title":"园丁的工具","content":"园丁有很多工具。水壶浇水，剪刀修剪，铲子松土，每样工具都有用处。","image":"career/gardener"}]}' WHERE id = 369;

-- Lesson 370: 飞行员 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是飞行员","content":"飞行员是开飞机的人。他们穿制服戴帽子，坐在飞机驾驶舱里，带旅客飞上天空。","image":"career/pilot"},{"title":"飞行员的工作","content":"飞行员要检查飞机。起飞前看仪表盘，飞行中保持方向高度，安全把旅客送到目的地。","image":"career/pilot"},{"title":"飞行员的要求","content":"当飞行员要身体好。眼睛要看得清，反应要快，还要学很多知识，责任很大。","image":"career/pilot"}]}' WHERE id = 370;

-- Lesson 372: 种子 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是种子","content":"种子是植物的小宝宝。它小小的，里面藏着新生命，种到土里就能长出新的植物。","image":"plant/seed"},{"title":"种子的样子","content":"种子有各种形状。有的圆圆像豆子，有的扁扁像瓜子，大小颜色都不一样。","image":"plant/seed"},{"title":"种子的旅行","content":"种子会到处旅行。风会吹走蒲公英种子，小鸟会带走种子，种子到哪就在哪发芽。","image":"plant/seed"}]}' WHERE id = 372;

-- Lesson 373: 土壤 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是土壤","content":"土壤是植物生长的家。它是棕色的泥土，里面有营养，种子种在里面就能长大。","image":"plant/soil"},{"title":"土壤的作用","content":"土壤给植物提供营养。它能存住水分，植物的根扎在土里，吸收水和养分慢慢生长。","image":"plant/soil"},{"title":"土壤里的小生命","content":"土壤里有很多小东西。有小虫子、落叶、小石子，它们一起让土壤变得肥沃。","image":"plant/soil"}]}' WHERE id = 373;

-- Lesson 374: 阳光 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"阳光的作用","content":"阳光是植物的粮食。植物的叶子会吸收阳光，把它变成营养，让自己长得又高又壮。","image":"plant/sunlight"},{"title":"植物向光","content":"植物喜欢阳光。它们会朝着有阳光的方向长，阳台上的花会向窗外伸，就是想晒太阳。","image":"plant/sunlight"},{"title":"阳光与颜色","content":"阳光让植物变绿。叶子里的叶绿素吸收阳光，叶子才会绿油油的，没有阳光就会发黄。","image":"plant/sunlight"}]}' WHERE id = 374;

-- Lesson 375: 水分 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"水对植物","content":"植物要喝水才能活。根从土里吸水，水顺着茎送到每片叶子，植物才会精神饱满。","image":"plant/water"},{"title":"浇水的方法","content":"给植物浇水要适量。水太多根会烂，水太少会干枯，早上浇水最好，太阳不大不烫。","image":"plant/water"},{"title":"水的作用","content":"水帮植物运送营养。根吸的水带着土里的养分，送到各处，植物才能开花结果。","image":"plant/water"}]}' WHERE id = 375;

-- Lesson 376: 发芽 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是发芽","content":"发芽是种子长出小芽。种子喝饱水晒到太阳，壳裂开，小芽钻出来开始新生命。","image":"plant/sprout"},{"title":"发芽的过程","content":"种子先吸水变大。然后壳裂开，先长出小根往下扎，再长出小芽往上冒，一天天长高。","image":"plant/sprout"},{"title":"照顾小芽","content":"小芽很娇嫩。要轻轻浇水，让它晒太阳但不能太晒，保护好它就能长成大植物。","image":"plant/sprout"}]}' WHERE id = 376;

-- Lesson 378: 向日葵 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是向日葵","content":"向日葵是跟着太阳转的花。它有金黄色的花瓣和高高的茎，脸总是朝着太阳。","image":"plant/sunflower"},{"title":"向日葵的特点","content":"向日葵长得比人还高。它的大花盘里有很多瓜子，成熟后可以吃，又香又好吃。","image":"plant/sunflower"},{"title":"向日葵生长","content":"向日葵喜欢阳光。春天播种，夏天开花，它跟着太阳从东转到西，是个爱晒太阳的植物。","image":"plant/sunflower"}]}' WHERE id = 378;

-- Lesson 379: 苹果树 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是苹果树","content":"苹果树是长苹果的树。它有粗壮的树干和绿绿的叶子，春天开花秋天结果。","image":"plant/apple-tree"},{"title":"苹果的生长","content":"苹果先开白色小花。花谢了结出小苹果，慢慢长大变红，熟了就可以摘下来吃了。","image":"plant/apple-tree"},{"title":"苹果的营养","content":"苹果又甜又有营养。它有很多维生素，常吃苹果身体好，每天一个苹果医生远离我。","image":"plant/apple-tree"}]}' WHERE id = 379;

-- Lesson 380: 番茄 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是番茄","content":"番茄也叫西红柿。它圆圆的红红的，可以做菜也可以生吃，酸酸甜甜很好吃。","image":"plant/tomato"},{"title":"番茄的生长","content":"番茄长在藤蔓上。先开黄色小花，然后结出绿色小番茄，慢慢变红就成熟了。","image":"plant/tomato"},{"title":"番茄的吃法","content":"番茄吃法很多。可以炒鸡蛋、煮汤、做番茄酱，生吃也很好吃，营养丰富小朋友喜欢。","image":"plant/tomato"}]}' WHERE id = 380;

-- Lesson 381: 胡萝卜 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是胡萝卜","content":"胡萝卜是橙色的根菜。它长在土里，叶子露在外面，拔出来才能看到橙色萝卜。","image":"plant/carrot"},{"title":"胡萝卜的特点","content":"胡萝卜脆脆的甜甜的。它有很多胡萝卜素，吃了对眼睛好，能让眼睛更亮。","image":"plant/carrot"},{"title":"胡萝卜生长","content":"胡萝卜喜欢凉快天气。把种子撒在土里，长出叶子，根慢慢膨大变成胡萝卜。","image":"plant/carrot"}]}' WHERE id = 381;

-- Lesson 382: 南瓜 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是南瓜","content":"南瓜是橙色的大蔬菜。它圆圆的或长长的，皮硬硬的，里面有很多种子和橙色果肉。","image":"plant/pumpkin"},{"title":"南瓜的特点","content":"南瓜很大很重。它长得慢，要很久才能成熟，但能放很久不坏，冬天也能吃。","image":"plant/pumpkin"},{"title":"南瓜的吃法","content":"南瓜可以煮汤、蒸着吃、做南瓜饼。它甜甜的软软的，营养很多，小朋友都爱吃。","image":"plant/pumpkin"}]}' WHERE id = 382;

-- Lesson 384: 小狗 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是小狗","content":"小狗是人类的好朋友。它有四条腿和毛茸茸的尾巴，会汪汪叫，最喜欢和主人玩。","image":"pet/puppy"},{"title":"小狗的特点","content":"小狗鼻子很灵敏。它能闻出很远很远的味道，还能记住主人的气味，特别聪明。","image":"pet/puppy"},{"title":"小狗的生活","content":"小狗喜欢跑跳玩球。它摇尾巴表示开心，看到主人回家会扑过来，是很忠诚的朋友。","image":"pet/puppy"}]}' WHERE id = 384;

-- Lesson 385: 小猫 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是小猫","content":"小猫是可爱的小动物。它有尖尖的耳朵和长长的尾巴，会喵喵叫，动作很灵活。","image":"pet/kitten"},{"title":"小猫的特点","content":"小猫眼睛很灵敏。晚上它看得很清楚，脚上有肉垫走路没声音，是抓老鼠的小能手。","image":"pet/kitten"},{"title":"小猫的生活","content":"小猫爱干净爱睡觉。它会用舌头舔毛洗澡，一天要睡很久，还喜欢追毛线球玩。","image":"pet/kitten"}]}' WHERE id = 385;

-- Lesson 386: 金鱼 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是金鱼","content":"金鱼是养在水里的宠物。它有漂亮的尾巴和鳞片，在水里游来游去非常好看。","image":"pet/goldfish"},{"title":"金鱼的特点","content":"金鱼生活在水里。它用鳃呼吸，用鳍游泳，不眨眼睛因为眼睛没有眼皮，很有趣。","image":"pet/goldfish"},{"title":"养金鱼","content":"养金鱼要准备鱼缸。水要干净不能太多，不能喂太多食物，金鱼才能健康活很久。","image":"pet/goldfish"}]}' WHERE id = 386;

-- Lesson 387: 仓鼠 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是仓鼠","content":"仓鼠是小巧可爱的宠物。它圆滚滚的，有两颗大板牙和腮帮子，活泼又好玩。","image":"pet/hamster"},{"title":"仓鼠的特点","content":"仓鼠会把食物塞进腮帮。它两边脸颊能装很多食物，像小仓库一样，所以叫仓鼠。","image":"pet/hamster"},{"title":"养仓鼠","content":"仓鼠住笼子里。要放木屑和跑轮让它运动，它白天睡觉晚上活动，是夜行小动物。","image":"pet/hamster"}]}' WHERE id = 387;

-- Lesson 388: 兔子 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是兔子","content":"兔子是长耳朵的小动物。它有长长的耳朵和短短的尾巴，蹦蹦跳跳很可爱。","image":"pet/rabbit"},{"title":"兔子的特点","content":"兔子耳朵很长很灵敏。它能听到很远的声音，耳朵还能散热，跑起来一蹦一蹦很快。","image":"pet/rabbit"},{"title":"兔子的生活","content":"兔子爱吃萝卜和青菜。它胆子小喜欢安静，要住干净的地方，毛茸茸的摸着很舒服。","image":"pet/rabbit"}]}' WHERE id = 388;

-- Lesson 390: 喂食 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"宠物要吃饭","content":"宠物和小朋友一样要吃饭。不同宠物吃不同食物，小狗吃狗粮，小猫吃猫粮，不能乱喂。","image":"pet/feeding"},{"title":"喂食的方法","content":"喂食要定时定量。每天固定时间喂，不能喂太多也不能太少，让宠物吃饱又不撑。","image":"pet/feeding"},{"title":"不能吃的","content":"有些食物宠物不能吃。巧克力、葡萄对狗不好，要喂宠物专门的食物才安全健康。","image":"pet/feeding"}]}' WHERE id = 390;

-- Lesson 391: 清洁 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"宠物要洗澡","content":"宠物要保持干净。定期给宠物洗澡，用温水和小动物专用的沐浴露，洗完要吹干。","image":"pet/cleaning"},{"title":"清洁的环境","content":"宠物的家要打扫。狗窝要清理，鱼缸要换水，仓鼠笼要换木屑，干净才不生病。","image":"pet/cleaning"},{"title":"清洁的好处","content":"经常清洁宠物才健康。干净的小动物毛色亮没有味道，住得舒服人也喜欢抱它。","image":"pet/cleaning"}]}' WHERE id = 391;

-- Lesson 392: 散步 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"小狗要散步","content":"小狗每天要出去散步。在外面跑跑跳跳晒太阳，既锻炼身体又能认识新朋友。","image":"pet/walking"},{"title":"散步要牵绳","content":"带小狗散步要牵绳子。这样小狗不会跑丢也不会吓到别人，是对大家负责的做法。","image":"pet/walking"},{"title":"散步的好处","content":"散步让小狗开心健康。在外面闻闻各种味道，看看新鲜事物，小狗会很快乐。","image":"pet/walking"}]}' WHERE id = 392;

-- Lesson 393: 体检 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"宠物要体检","content":"宠物也要看医生。定期带宠物去兽医那里检查身体，打疫苗，预防生病。","image":"pet/checkup"},{"title":"看兽医","content":"兽医是给动物看病的医生。他们会检查宠物的眼睛耳朵牙齿，看看宠物健不健康。","image":"pet/checkup"},{"title":"打疫苗","content":"宠物要打疫苗。疫苗能让宠物不生大病，像小朋友打预防针一样，保护它们健健康康。","image":"pet/checkup"}]}' WHERE id = 393;

-- Lesson 394: 玩耍 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"和宠物玩","content":"和宠物玩耍很有趣。可以扔球让小狗捡，用逗猫棒逗小猫，一起玩大家都很开心。","image":"pet/playing"},{"title":"玩耍的好处","content":"玩耍让宠物不无聊。它们需要运动和陪伴，多和宠物玩，它们会更健康更亲人。","image":"pet/playing"},{"title":"安全玩耍","content":"和宠物玩要温柔。不能拉尾巴不能打它，要轻轻抚摸，宠物开心了才会喜欢和你玩。","image":"pet/playing"}]}' WHERE id = 394;

-- Lesson 396: 大桥 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是大桥","content":"大桥是跨过江河的建筑物。它把两边连起来，让人和车可以从河这边走到河那边。","image":"building/bridge"},{"title":"大桥的结构","content":"大桥很结实。它有粗壮的桥墩撑在水里，桥面平平的让车走，还有栏杆保护安全。","image":"building/bridge"},{"title":"大桥的作用","content":"大桥让出行更方便。没有桥要绕很远，有了桥一下子就过河了，车和人都能快速通过。","image":"building/bridge"}]}' WHERE id = 396;

-- Lesson 397: 灯塔 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是灯塔","content":"灯塔是海边的高塔。它塔顶有灯，晚上亮起来，给海上的船指方向，让船安全回来。","image":"building/lighthouse"},{"title":"灯塔的作用","content":"灯塔在黑暗中发光。船在海上看到灯塔就知道陆地在哪里，不会迷路也不会撞到礁石。","image":"building/lighthouse"},{"title":"灯塔的特点","content":"灯塔建得很高。一般建在海边或小岛上，用石头建成很结实，能抵抗大风大浪。","image":"building/lighthouse"}]}' WHERE id = 397;

-- Lesson 398: 钟楼 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是钟楼","content":"钟楼是挂大钟的高楼。它会在整点敲钟，钟声很大传得很远，告诉大家现在几点了。","image":"building/clock-tower"},{"title":"钟楼的作用","content":"钟楼在没有表的时候很重要。人们听到钟声就知道时间，知道该做什么，很有用。","image":"building/clock-tower"},{"title":"钟楼的样子","content":"钟楼有高高的塔身。顶上有大大的钟面，有的钟楼四面都有钟，远远就能看到时间。","image":"building/clock-tower"}]}' WHERE id = 398;

-- Lesson 399: 高塔 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是高塔","content":"高塔是很高的塔形建筑。它一层层往上建，站得高望得远，可以看很美的风景。","image":"building/tower"},{"title":"高塔的特点","content":"高塔建得很高很尖。它底座大上面小，这样才稳当，风吹不倒，能站很久很久。","image":"building/tower"},{"title":"登塔观景","content":"很多高塔可以登。爬到塔顶往下看，城市变小了，房子像积木，看得很远很壮观。","image":"building/tower"}]}' WHERE id = 399;

-- Lesson 400: 摩天楼 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是摩天楼","content":"摩天楼是很高很高的大楼。它有一百多层那么高，像碰到天空一样，所以叫摩天楼。","image":"building/skyscraper"},{"title":"摩天楼的特点","content":"摩天楼有电梯。因为太高走不动，要坐电梯上下，很快就能到很高的楼层。","image":"building/skyscraper"},{"title":"摩天楼的作用","content":"摩天楼能住很多人。里面有家、有商店、有办公室，一栋楼就像一个小城市。","image":"building/skyscraper"}]}' WHERE id = 400;

-- Lesson 402: 木屋 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是木屋","content":"木屋是用木头搭的房子。它用一根根木头拼起来，住进去有淡淡的木头香味，很温馨。","image":"building/wooden-house"},{"title":"木屋的特点","content":"木屋保暖又凉快。木头能让屋里冬天不冷夏天不热，住着很舒服，还很好看。","image":"building/wooden-house"},{"title":"木屋的生活","content":"木屋一般建在山里或乡下。周围有树有花，空气清新，住在里面像住在大自然里。","image":"building/wooden-house"}]}' WHERE id = 402;

-- Lesson 403: 公寓 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是公寓","content":"公寓是很多人一起住的大楼。一栋楼里有很多家，每家有自己的房间，像叠在一起的盒子。","image":"building/apartment"},{"title":"公寓的特点","content":"公寓住很省地方。一栋楼住很多家，不用占很多地，城市里很多人都住公寓。","image":"building/apartment"},{"title":"公寓的生活","content":"住公寓有邻居。大家住在一栋楼里要互相尊重，不能太吵，要一起维护公共环境。","image":"building/apartment"}]}' WHERE id = 403;

-- Lesson 404: 图书馆 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是图书馆","content":"图书馆是放很多书的建筑。里面有各种各样的书，大家可以免费借来看，很安静。","image":"building/library"},{"title":"图书馆的作用","content":"图书馆让大家看书学习。那里很安静，书很多，可以坐在那慢慢看，也可以借回家。","image":"building/library"},{"title":"图书馆的规则","content":"在图书馆要安静。不能大声说话不能跑，看完书要放回原处，要爱护每一本书。","image":"building/library"}]}' WHERE id = 404;

-- Lesson 405: 博物馆 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是博物馆","content":"博物馆是展览宝贝的地方。里面有古老的文物、动物标本、艺术品，能看到很多稀奇东西。","image":"building/museum"},{"title":"博物馆的作用","content":"博物馆让人长知识。在那里看到古代的东西和有趣的事物，了解历史和大自然，很有意义。","image":"building/museum"},{"title":"参观博物馆","content":"参观博物馆要慢慢看。不能乱摸展品，跟着讲解员听故事，能看到学到很多新知识。","image":"building/museum"}]}' WHERE id = 405;

-- Lesson 406: 体育馆 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是体育馆","content":"体育馆是运动的地方。它有很大的室内场地，可以打篮球、游泳、看比赛，很热闹。","image":"building/stadium"},{"title":"体育馆的特点","content":"体育馆空间很大。没有柱子挡住，能坐很多观众，里面有很多运动设施可以玩。","image":"building/stadium"},{"title":"体育馆的活动","content":"体育馆举办各种活动。有比赛可以看，也可以去那里锻炼身体，是运动爱好者的乐园。","image":"building/stadium"}]}' WHERE id = 406;

-- Lesson 408: 春节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是春节","content":"春节是中国最重要的传统节日，在农历正月初一。它标志着新年的开始，家家户户都欢聚一堂，辞旧迎新。","image":"festival/spring-festival"},{"title":"春节的习俗","content":"春节时人们会贴春联、挂红灯笼、放鞭炮。全家人围坐在一起吃年夜饭，还会给小朋友发压岁钱，非常热闹。","image":"festival/spring-festival"},{"title":"春节的美食","content":"年夜饭桌上有很多好吃的，饺子像金元宝，鱼代表年年有余，年糕代表步步高升，每道菜都有好寓意。","image":"festival/spring-festival"}]}' WHERE id = 408;

-- Lesson 409: 元宵节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是元宵节","content":"元宵节在农历正月十五，是春节之后的第一个节日。这一天月亮圆圆的，人们会赏月、猜灯谜，非常热闹。","image":"festival/lantern-festival"},{"title":"吃元宵","content":"元宵节要吃元宵或汤圆。它是用糯米粉做的圆圆团子，里面有甜甜的芝麻或花生馅，象征着团团圆圆。","image":"festival/lantern-festival"},{"title":"赏花灯","content":"元宵节晚上到处挂着五颜六色的花灯，有兔子灯、龙灯、莲花灯。小朋友们提着小灯笼走街串巷，开心极了。","image":"festival/lantern-festival"}]}' WHERE id = 409;

-- Lesson 410: 清明节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是清明节","content":"清明节在每年的四月五日前后，是祭祖扫墓的日子。人们会去祖先的墓前献花，表达对亲人的思念。","image":"festival/qingming-festival"},{"title":"踏青郊游","content":"清明时节春暖花开，万物复苏。人们会走出家门去郊外踏青，欣赏美丽的春天景色，感受大自然的美好。","image":"festival/qingming-festival"},{"title":"清明的食物","content":"清明节有吃青团的习惯。青团是用艾草汁和糯米粉做的，绿绿的软软的，里面包着豆沙，又香又甜。","image":"festival/qingming-festival"}]}' WHERE id = 410;

-- Lesson 411: 端午节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是端午节","content":"端午节在农历五月初五，是为了纪念爱国诗人屈原。人们在这一天赛龙舟、吃粽子，流传了很多年。","image":"festival/dragon-boat-festival"},{"title":"龙舟比赛","content":"端午节最热闹的活动就是赛龙舟。一条长长的龙舟上坐着很多人，大家一起用力划桨，鼓声咚咚，水花四溅。","image":"festival/dragon-boat-festival"},{"title":"粽子的故事","content":"端午节要吃粽子。粽子是用竹叶包着糯米做的，有三角形也有长条形。里面可以放红枣或鲜肉，特别香。","image":"festival/dragon-boat-festival"}]}' WHERE id = 411;

-- Lesson 412: 中秋节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是中秋节","content":"中秋节在农历八月十五，这一天的月亮又大又圆。它代表着家人团聚，是仅次于春节的重要传统节日。","image":"festival/mid-autumn-festival"},{"title":"赏月习俗","content":"中秋节晚上一家人会坐在一起赏月。大人们喝茶聊天，小朋友们提着灯笼玩耍，月光下的夜晚温馨又美好。","image":"festival/mid-autumn-festival"},{"title":"月饼的故事","content":"中秋节要吃月饼。月饼圆圆的像天上的月亮，里面有各种馅料。全家人分着吃一个月饼，象征团团圆圆。","image":"festival/mid-autumn-festival"}]}' WHERE id = 412;

-- Lesson 414: 圣诞节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是圣诞节","content":"圣诞节是十二月二十五日，是西方最重要的节日。这一天人们会装饰圣诞树，互相送礼物，到处充满欢乐。","image":"festival/christmas"},{"title":"圣诞老人","content":"圣诞节有一位神秘的圣诞老人。他穿着红衣服，留着白胡子，坐着驯鹿拉的雪橇，从烟囱进来给小朋友送礼物。","image":"festival/christmas"},{"title":"圣诞树","content":"圣诞节家家户户都会准备一棵圣诞树。树上挂满彩灯、彩球和小星星，顶上放一颗大星星，闪闪发光特别漂亮。","image":"festival/christmas"}]}' WHERE id = 414;

-- Lesson 415: 万圣节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是万圣节","content":"万圣节在十月三十一日晚上。小朋友们会穿上各种有趣的服装，戴上面具，扮成小幽灵或小巫师去讨糖。","image":"festival/halloween"},{"title":"不给糖就捣蛋","content":"万圣节最有趣的活动是不给糖就捣蛋。小朋友们提着南瓜灯挨家挨户敲门，要是不给糖，他们就会搞小恶作剧。","image":"festival/halloween"},{"title":"南瓜灯","content":"万圣节的标志是南瓜灯。人们把南瓜掏空，刻出眼睛和嘴巴，里面放上蜡烛，亮起来像一个笑脸，又有趣又可爱。","image":"festival/halloween"}]}' WHERE id = 415;

-- Lesson 416: 复活节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是复活节","content":"复活节在春天，是西方的一个重要节日。它象征着新生和希望，小鸡和小兔子是复活节的可爱象征。","image":"festival/easter"},{"title":"复活节彩蛋","content":"复活节最有趣的活动是找彩蛋。大人把鸡蛋煮熟后涂上漂亮的颜色，藏在花园里让小朋友去找，特别好玩。","image":"festival/easter"},{"title":"复活节兔子","content":"复活节有一只可爱的兔子。传说复活节兔子会带来彩蛋和糖果，小朋友们最喜欢这只毛茸茸的小使者了。","image":"festival/easter"}]}' WHERE id = 416;

-- Lesson 417: 感恩节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是感恩节","content":"感恩节是北美的节日，在十一月。这一天家人会聚在一起，感谢这一年的收获和生活中的美好事物。","image":"festival/thanksgiving"},{"title":"感恩节大餐","content":"感恩节大餐非常丰盛，主菜是烤火鸡。桌上还有土豆泥、南瓜派和玉米面包，全家人围坐在一起吃得开心满足。","image":"festival/thanksgiving"},{"title":"学会感恩","content":"感恩节提醒我们要学会说谢谢。感谢爸爸妈妈的照顾，感谢老师的教导，感谢朋友的陪伴，感恩让心里暖暖的。","image":"festival/thanksgiving"}]}' WHERE id = 417;

-- Lesson 418: 儿童节 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是儿童节","content":"六月一日是国际儿童节，是全世界小朋友的节日。这一天学校会举办各种活动，小朋友们收到礼物开心极了。","image":"festival/childrens-day"},{"title":"儿童节活动","content":"儿童节学校会组织联欢会，小朋友们唱歌跳舞表演节目。有的去公园玩，有的去看电影，有的收到新玩具。","image":"festival/childrens-day"},{"title":"儿童的权利","content":"儿童节也提醒大家要爱护小朋友。每个孩子都有受教育、被保护和快乐成长的权利，全世界都在努力让小朋友更幸福。","image":"festival/childrens-day"}]}' WHERE id = 418;

-- Lesson 420: 太阳能 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是太阳能","content":"太阳能是太阳发出的能量。太阳每天发出光和热，我们可以用太阳能板把这些能量变成电，点亮家里的灯。","image":"energy/solar-power"},{"title":"太阳能板","content":"太阳能板像一块块蓝色的板子，放在屋顶上。它能吸收阳光变成电，既不污染空气，也不用花钱买燃料，非常环保。","image":"energy/solar-power"},{"title":"太阳能的用处","content":"太阳能可以烧热水，可以发电，还能让太阳能玩具车跑起来。很多地方的路灯也用太阳能，白天充电晚上亮。","image":"energy/solar-power"}]}' WHERE id = 420;

-- Lesson 421: 风能 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是风能","content":"风能就是风的能量。风吹过的时候有力量，可以推动风车转动，风车转动就能发出电来，是一种干净的能源。","image":"energy/wind-power"},{"title":"风力发电机","content":"风力发电机像一个超级大的风车。它有三片长长的叶片，立在空旷的地方，风一吹叶片就转，把风变成电。","image":"energy/wind-power"},{"title":"风能的好处","content":"风能不用烧煤也不冒烟，非常干净。海边的风特别大，所以很多风力发电机建在海边，能发出很多电。","image":"energy/wind-power"}]}' WHERE id = 421;

-- Lesson 422: 水能 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是水能","content":"水能是流动的水的力量。河水从高处流下来时很有劲儿，可以推动水轮机转动，从而发出电来。","image":"energy/hydro-power"},{"title":"水力发电站","content":"水力发电站建在大江大河上。它有一个高高的大坝把水拦住，水从高处冲下来推动机器，就能发出很多电。","image":"energy/hydro-power"},{"title":"水能的优点","content":"水能是可再生的能源，水一直流就一直有电。而且不会冒黑烟污染空气，是很环保的发电方式。","image":"energy/hydro-power"}]}' WHERE id = 422;

-- Lesson 423: 地热 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是地热","content":"地热是地球内部的热量。地球肚子里很热很热，有的地方热水会从地下喷出来，这就是地热在冒泡。","image":"energy/geothermal"},{"title":"温泉的秘密","content":"地下热水流到地面就成了温泉。泡温泉暖暖的很舒服，因为地球内部的热量把水加热了，这就是地热的力量。","image":"energy/geothermal"},{"title":"地热发电","content":"地热还可以用来发电。人们把地下冒出来的蒸汽引到机器里，推动机器转动就发出电了，又环保又稳定。","image":"energy/geothermal"}]}' WHERE id = 423;

-- Lesson 424: 电池车 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是电池车","content":"电池车是用电跑的汽车。它不用加汽油，只要给电池充电就能跑，不会冒尾气，是一种很环保的车。","image":"energy/electric-car"},{"title":"电池车的充电","content":"电池车像手机一样需要充电。充电桩就是电池车的加油站，插上电充一会儿，车子就能跑很远。","image":"energy/electric-car"},{"title":"电池车的好处","content":"电池车不排废气，空气更干净。它开起来很安静，没有突突突的声音，而且用电比用汽油便宜多了。","image":"energy/electric-car"}]}' WHERE id = 424;

-- Lesson 426: 吊车 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是吊车","content":"吊车是能举起很重东西的机器。它有一条长长的手臂，能把钢筋水泥高高吊起，是建筑工地上的大力士。","image":"energy/crane"},{"title":"吊车的手臂","content":"吊车的臂像长长的积木，可以伸长缩短。臂的头上挂着钩子和钢绳，能稳稳地钩住重物把它吊到高处。","image":"energy/crane"},{"title":"吊车的用处","content":"吊车在建筑工地上大显身手。它可以把钢材吊到楼顶，把集装箱搬到船上，人力搬不动的东西它都能搬。","image":"energy/crane"}]}' WHERE id = 426;

-- Lesson 427: 挖掘机 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是挖掘机","content":"挖掘机是会挖土的机器。它有一只长长的手臂和一个大铲斗，能在地上挖出深深的坑，是工地上的挖土能手。","image":"energy/excavator"},{"title":"挖掘机的铲斗","content":"挖掘机的前面有一个大大的铲斗，像手掌一样。铲斗挖一下就能挖起很多土，比人用铁锹挖快多了。","image":"energy/excavator"},{"title":"挖掘机的履带","content":"挖掘机下面是履带而不是轮子。履带像两条长长的带子，让挖掘机在泥地上也不会陷进去，走得很稳。","image":"energy/excavator"}]}' WHERE id = 427;

-- Lesson 428: 洗衣机 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是洗衣机","content":"洗衣机是帮我们洗衣服的机器。只要把脏衣服放进去，加上水和洗衣液，它就会自动把衣服洗得干干净净。","image":"energy/washing-machine"},{"title":"洗衣机怎么工作","content":"洗衣机里面有一个大圆桶。机器开动后圆桶转来转去，水和衣服在里面翻滚，污渍就被洗掉了。","image":"energy/washing-machine"},{"title":"洗衣机的好处","content":"以前人们用手搓衣服很累很慢。有了洗衣机，只要按一下按钮就能洗衣服，省时又省力，真是个好帮手。","image":"energy/washing-machine"}]}' WHERE id = 428;

-- Lesson 429: 风扇 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是风扇","content":"风扇是能吹风的机器。夏天天气热的时候，打开风扇呼呼地吹出风来，让人觉得凉快舒服。","image":"energy/electric-fan"},{"title":"风扇的叶片","content":"风扇有几片倾斜的叶片。叶片转得很快时，就会把空气推过来形成风，风一吹到身上就觉得凉快了。","image":"energy/electric-fan"},{"title":"风扇的安全","content":"风扇转起来很快，小朋友不要把手伸进去。前面有网罩保护，但还是要小心，安全第一最重要。","image":"energy/electric-fan"}]}' WHERE id = 429;

-- Lesson 430: 电梯 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是电梯","content":"电梯是载人在楼层间上下的机器。高楼大厦里都有电梯，按一下按钮门打开，走进去就能到很高的楼层。","image":"energy/elevator"},{"title":"电梯怎么升降","content":"电梯有一根粗粗的钢缆拉着。电动机转动钢缆，把电梯厢拉上去或放下来，像坐小车厢一样稳稳的。","image":"energy/elevator"},{"title":"乘坐电梯的礼貌","content":"坐电梯要排队等候，先下后上。不要在电梯里蹦跳打闹，也不要乱按按钮，做个文明的小乘客。","image":"energy/elevator"}]}' WHERE id = 430;

-- Lesson 432: 手电筒 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是手电筒","content":"手电筒是一个能拿在手里的灯。停电或者走夜路时，打开手电筒就能照亮前面的路，非常方便。","image":"science/flashlight"},{"title":"手电筒的光","content":"手电筒前面有一个小灯泡和反光碗。打开开关，灯泡发光，反光碗把光聚在一起，照出去一条亮亮的光柱。","image":"science/flashlight"},{"title":"手电筒的电","content":"手电筒里要装电池才有电。电池的电让灯泡亮起来，电池没电了手电筒就不亮了，记得换新电池。","image":"science/flashlight"}]}' WHERE id = 432;

-- Lesson 433: 彩虹 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是彩虹","content":"彩虹是天空中美丽的七色拱桥。下完雨太阳出来时，天空中有时会出现彩虹，有红橙黄绿蓝靛紫七种颜色。","image":"science/rainbow"},{"title":"彩虹怎么形成","content":"雨后天空中有很多小水滴。阳光照进小水滴里会拐弯分散，变成七种颜色，就形成了漂亮的彩虹。","image":"science/rainbow"},{"title":"做个小彩虹","content":"喷水的时候背着太阳，有时也能看到小彩虹。因为水珠像小水滴一样能把阳光分开，这就是彩虹的秘密。","image":"science/rainbow"}]}' WHERE id = 433;

-- Lesson 434: 回声 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是回声","content":"回声是你说话后听到的重复声音。在空旷的山谷或大空房间里大喊一声，过一会儿会听到自己的声音回来。","image":"science/echo"},{"title":"回声怎么产生","content":"声音在空气中传播，碰到墙壁或山壁会弹回来。弹回来的声音传到耳朵里，就成了我们听到的回声。","image":"science/echo"},{"title":"哪里有回声","content":"在山谷里、空房间里、隧道中都能听到回声。如果地方太小声音弹回来太快，就和原来的声音混在一起听不出来了。","image":"science/echo"}]}' WHERE id = 434;

-- Lesson 435: 喇叭 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是喇叭","content":"喇叭是把声音放大的东西。学校广播、音响和汽车上都有喇叭，它能把小小的声音变成大大的声音传出来。","image":"science/loudspeaker"},{"title":"喇叭怎么发声","content":"喇叭里面有一个小零件叫振膜。电让振膜快速振动，推动空气振动，声音就变大了，传得很远很清楚。","image":"science/loudspeaker"},{"title":"喇叭的用处","content":"喇叭的用处可大了。学校用喇叭播通知，汽车用喇叭提醒路人，音乐会用喇叭放音乐，生活中处处都有喇叭。","image":"science/loudspeaker"}]}' WHERE id = 435;

-- Lesson 436: 音叉 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是音叉","content":"音叉是一种像叉子一样的工具。敲一下它会发出清脆的嗡嗡声，声音很纯净，能发出固定的音高。","image":"science/tuning-fork"},{"title":"音叉的振动","content":"敲音叉后它的两个叉股会快速振动。虽然看不太清楚，但用手轻轻碰一下就能感觉到它在抖，振动发出声音。","image":"science/tuning-fork"},{"title":"音叉的用处","content":"音叉发出的声音很准，音乐老师用它来定音。医生也会用音叉检查耳朵听不听得见，是个有用的科学小工具。","image":"science/tuning-fork"}]}' WHERE id = 436;

-- Lesson 438: 磁铁 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是磁铁","content":"磁铁是一块会吸铁的神奇石头。它能吸住铁钉、回形针等铁做的东西，有的磁铁力气大得惊人。","image":"science/magnet"},{"title":"磁铁的两极","content":"磁铁有两个极，一个叫北极一个叫南极。相同的极碰到一起会推开，不同的极碰到一起会吸住，很有趣。","image":"science/magnet"},{"title":"磁铁的用处","content":"磁铁用处很多。冰箱贴用磁铁吸在冰箱上，包的磁扣用磁铁关紧，指南针里也有磁铁帮我们认方向。","image":"science/magnet"}]}' WHERE id = 438;

-- Lesson 439: 指南针 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是指南针","content":"指南针是帮人认方向的工具。它的指针一头指南一头指北，无论走到哪里都能告诉你哪边是南方。","image":"science/compass"},{"title":"指南针的原理","content":"指南针的指针是一根小磁铁。地球本身也是个大方磁铁，小磁针被地球吸引，所以总是指着南北方向。","image":"science/compass"},{"title":"指南针的用处","content":"指南针是中国的四大发明之一。航海家在大海上靠它认方向，旅行的人也带着它，不会迷路。","image":"science/compass"}]}' WHERE id = 439;

-- Lesson 440: 浮力 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是浮力","content":"浮力是水把东西往上托的力。把东西放进水里，水会把它往上推，这就是为什么船能浮在水面上。","image":"science/buoyancy"},{"title":"什么能浮起来","content":"轻的东西和有空隙的东西容易浮起来，比如木头和空瓶子。重的实心东西容易沉下去，比如石头和铁块。","image":"science/buoyancy"},{"title":"浮力的用处","content":"浮力让大轮船能装很多东西在水上跑。游泳圈也是靠浮力把人托住，让小朋友在水里漂起来不会沉。","image":"science/buoyancy"}]}' WHERE id = 440;

-- Lesson 441: 水循环 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是水循环","content":"水循环是水在天上地下不停转圈圈。水从地上跑到天上，又从天上回到地上，一直循环不会停。","image":"science/water-cycle"},{"title":"水怎么到天上","content":"太阳晒热地面上的水，江河湖海的水变成看不见的水蒸气飞到天上，这个过程叫做蒸发。","image":"science/water-cycle"},{"title":"雨从哪里来","content":"天上的水蒸气越聚越多变成云。云里的水滴越来越大太重了，掉下来就变成雨，雨落回地上又开始新的循环。","image":"science/water-cycle"}]}' WHERE id = 441;

-- Lesson 442: 冰和蒸汽 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"水的三种样子","content":"水有三种样子。平常是液体能流动，很冷的时候变成固体的冰，很热的时候变成气体的水蒸气看不见。","image":"science/water-states"},{"title":"水变成冰","content":"水放在冰箱冷冻室里，温度很低就会结成冰。冰是硬硬的固体，有固定的形状，滑滑的凉凉的。","image":"science/water-states"},{"title":"水变成蒸汽","content":"把水烧开就会冒白气，那是水变成了水蒸气。水蒸气是气体看不见，飘到空中遇冷又变回小水滴。","image":"science/water-states"}]}' WHERE id = 442;

-- Lesson 444: 开心 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是开心","content":"开心就是心里美滋滋的感觉。过生日收到礼物、和朋友一起玩耍时，我们都会感到开心，忍不住笑出来。","image":"emotion/happy"},{"title":"开心的样子","content":"开心的时候嘴角会翘起来，眼睛弯弯的，脸上笑开了花。有时还会拍手跳起来，想把快乐分享给所有人。","image":"emotion/happy"},{"title":"让别人也开心","content":"自己开心的时候，可以分享给别人。给好朋友一个拥抱，对爸爸妈妈说声谢谢，快乐就会越传越多。","image":"emotion/happy"}]}' WHERE id = 444;

-- Lesson 445: 难过 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是难过","content":"难过是心里闷闷的不舒服的感觉。玩具坏了、和朋友吵架、想妈妈时，我们可能会感到难过，甚至会哭。","image":"emotion/sad"},{"title":"难过的样子","content":"难过的时候嘴角往下垂，眼睛红红的，眼泪会流出来。不想说话也不想玩，觉得浑身没力气。","image":"emotion/sad"},{"title":"难过时怎么办","content":"难过的时候可以抱抱爸爸妈妈，告诉他们为什么不开心。哭一哭也没关系，难过会慢慢过去，又会开心起来。","image":"emotion/sad"}]}' WHERE id = 445;

-- Lesson 446: 生气 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是生气","content":"生气是心里火辣辣的感觉。有人抢了玩具、事情做不好时，我们会觉得生气，脸涨得红红的。","image":"emotion/angry"},{"title":"生气的样子","content":"生气的时候眉头皱起来，嘴巴撅得高高的，有时会大声说话跺脚。身体绷得紧紧的，像一个小气球要爆炸。","image":"emotion/angry"},{"title":"生气时怎么办","content":"生气的时候先深呼吸，数一二三让自己冷静下来。不能打人摔东西，可以说出来为什么不高兴，找到解决的办法。","image":"emotion/angry"}]}' WHERE id = 446;

-- Lesson 447: 害怕 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是害怕","content":"害怕是心里咚咚跳的感觉。看到大狗、打雷闪电、一个人在黑屋子里时，我们会觉得害怕，想躲起来。","image":"emotion/scared"},{"title":"害怕的样子","content":"害怕的时候眼睛瞪得大大的，身体缩成一团，手心出冷汗。会躲到大人身后，抓住大人的手不放开。","image":"emotion/scared"},{"title":"害怕时怎么办","content":"害怕的时候可以找爸爸妈妈抱一抱。把害怕的事说出来，大人会保护你。勇敢不是不怕，是怕了还能去做。","image":"emotion/scared"}]}' WHERE id = 447;

-- Lesson 448: 惊讶 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是惊讶","content":"惊讶是没想到会有意外发生的感觉。突然收到礼物、看到魔术表演时，我们会惊讶地张大嘴巴。","image":"emotion/surprised"},{"title":"惊讶的样子","content":"惊讶的时候眼睛睁得圆圆的，嘴巴张成圆圆的，会发出哇的声音。有时还会用手捂住嘴巴，不敢相信。","image":"emotion/surprised"},{"title":"惊讶的好处","content":"惊讶让我们发现新鲜事物。看到没见过的东西觉得惊讶，就会想了解它，这样就能学到新知识了。","image":"emotion/surprised"}]}' WHERE id = 448;

-- Lesson 450: 深呼吸 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是深呼吸","content":"深呼吸就是慢慢地吸气再慢慢地呼气。生气或紧张的时候做深呼吸，能帮助自己平静下来。","image":"emotion/deep-breath"},{"title":"怎么深呼吸","content":"先用鼻子慢慢吸气，让肚子鼓起来，数一二三。再用嘴巴慢慢呼气，让肚子瘪下去，重复几次就好了。","image":"emotion/deep-breath"},{"title":"深呼吸的用处","content":"深呼吸能让我们冷静。考试紧张时、和小朋友吵架时、发脾气之前，做几个深呼吸心情就会好很多。","image":"emotion/deep-breath"}]}' WHERE id = 450;

-- Lesson 451: 倾诉 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是倾诉","content":"倾诉就是把心里的话说出来。不开心或者有心事的时候，找个人说一说，心里就会舒服很多。","image":"emotion/confide"},{"title":"找谁倾诉","content":"有心事可以告诉爸爸妈妈、老师或好朋友。他们会认真听你说，给你一个拥抱，帮你一起想办法解决。","image":"emotion/confide"},{"title":"倾诉的好处","content":"把不开心的事憋在心里会越来越难受。说出来就像打开了一个盖子，心里的气跑出去了，心情就变好了。","image":"emotion/confide"}]}' WHERE id = 451;

-- Lesson 452: 拥抱 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是拥抱","content":"拥抱是用双臂抱住别人。拥抱能传递爱和温暖，让人感到安心和幸福，是表达关心的好方法。","image":"emotion/hug"},{"title":"拥抱的力量","content":"一个拥抱很有力量。朋友难过时抱抱他，爸爸妈妈回家时抱抱他们，拥抱让人感觉被爱着，很幸福。","image":"emotion/hug"},{"title":"什么时候拥抱","content":"爸爸妈妈下班回家可以抱一抱，朋友过生日可以抱一抱，朋友难过时也可以抱一抱，拥抱让爱流动起来。","image":"emotion/hug"}]}' WHERE id = 452;

-- Lesson 453: 微笑 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是微笑","content":"微笑是嘴角轻轻上扬。微笑不用说话就能让人感到友好和快乐，是最简单也最美好的表情。","image":"emotion/smile"},{"title":"微笑的力量","content":"微笑像阳光一样温暖。对别人微笑，别人也会对你微笑，紧张和害怕的时候对自己微笑，会变得勇敢一些。","image":"emotion/smile"},{"title":"什么时候微笑","content":"见到老师同学要微笑，收到礼物要微笑，拍照的时候要微笑，每天都多笑一笑，生活会更美好。","image":"emotion/smile"}]}' WHERE id = 453;

-- Lesson 454: 鼓励 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是鼓励","content":"鼓励是对别人说加油和相信你。朋友遇到困难时说一句你可以的，就是给他力量，让他更勇敢地面对。","image":"emotion/encourage"},{"title":"怎么鼓励别人","content":"鼓励可以说你真棒、加油、我相信你。也可以竖起大拇指、拍拍肩膀，用行动告诉别人你支持他。","image":"emotion/encourage"},{"title":"鼓励的好处","content":"被鼓励的人会更有信心。小朋友学走路摔倒了，听到加油就会爬起来再试，鼓励让我们都变得更勇敢。","image":"emotion/encourage"}]}' WHERE id = 454;

-- Lesson 456: 帐篷 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是帐篷","content":"帐篷是露营时住的小房子。它是用布和杆子搭起来的，可以折叠携带，到了营地撑开就能住进去。","image":"camping/tent"},{"title":"怎么搭帐篷","content":"搭帐篷先把杆子穿好立起来，再把帐篷布套上固定好。最后用钉子把绳子拉紧固定在地上，帐篷就搭好了。","image":"camping/tent"},{"title":"帐篷里的乐趣","content":"帐篷里铺上垫子和睡袋，就是温馨的小窝。晚上躺在帐篷里听虫鸣看星星，和好朋友说悄悄话，特别有趣。","image":"camping/tent"}]}' WHERE id = 456;

-- Lesson 457: 睡袋 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是睡袋","content":"睡袋是露营时睡觉用的袋子。它像一个大大的信封，钻进去拉上拉链，就能暖暖地睡一觉。","image":"camping/sleeping-bag"},{"title":"睡袋保暖","content":"睡袋里有厚厚的保暖材料。夜晚外面冷的时候，钻进睡袋就像盖了一床厚被子，身体的热气跑不出去，暖暖的。","image":"camping/sleeping-bag"},{"title":"用睡袋的讲究","content":"进睡袋前要把鞋子脱在外面，保持里面干净。拉链要拉好别留缝，不然冷风会钻进来，会着凉的。","image":"camping/sleeping-bag"}]}' WHERE id = 457;

-- Lesson 458: 手电 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"露营带手电","content":"露营一定要带手电筒。晚上天黑了没有灯，手电能照亮营地，去上厕所或者找东西都靠它。","image":"camping/flashlight"},{"title":"手电怎么用","content":"用手电对着要看的方向照，不要对着人的眼睛照，那样会刺眼。不用时关掉省电，记得多带几节备用电池。","image":"camping/flashlight"},{"title":"手电的妙用","content":"手电不只是照亮。遇到危险时可以晃动手电发出求救信号，夜晚走路时照亮脚下的路，是露营的好帮手。","image":"camping/flashlight"}]}' WHERE id = 458;

-- Lesson 459: 地图 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是地图","content":"地图是画着道路和地方的图。露营时带上地图，就能知道营地在哪、路怎么走，不会迷路。","image":"camping/map"},{"title":"怎么看地图","content":"看地图先找到自己在哪，再找要去的地方。地图上的线条是路，小符号代表山和河，跟着地图走就能到。","image":"camping/map"},{"title":"地图和指北针","content":"看地图时要配合指北针。先认清哪边是北，把地图摆正方向，这样看地图才不会走反方向。","image":"camping/map"}]}' WHERE id = 459;

-- Lesson 460: 水壶 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"露营带水壶","content":"露营时一定要带水壶。在野外走动会出汗口渴，随时喝水补充水分，身体才会舒服有精神。","image":"camping/water-bottle"},{"title":"水壶的选择","content":"露营的水壶要结实不漏水，容量大一些。最好有背带挂在身上，走起路来方便，随时能打开喝一口。","image":"camping/water-bottle"},{"title":"喝水的讲究","content":"露营时不要等渴了才喝，要经常喝一小口。出汗多会流失水分，多喝水才不会中暑头晕，保持健康。","image":"camping/water-bottle"}]}' WHERE id = 460;

-- Lesson 462: 生火 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"露营生火","content":"露营时可以在指定的地方生篝火。篝火能取暖、照明，还能烤食物，但一定要在大人看护下才能生火。","image":"camping/campfire"},{"title":"怎么安全生火","content":"生火要选空旷远离树木的地方。用石头围一个圈，火不能太大，旁边放好水或沙子，随时可以浇灭。","image":"camping/campfire"},{"title":"用火要小心","content":"火很有用但也很危险。不能往火里扔垃圾，不能在火边打闹，离开时一定要把火完全浇灭，不能留一点火星。","image":"camping/campfire"}]}' WHERE id = 462;

-- Lesson 463: 防雨 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"露营要防雨","content":"露营时可能会下雨，要做好防雨准备。带好雨衣和雨伞，帐篷要选防水的，这样下雨也不会淋湿。","image":"camping/rain-protection"},{"title":"帐篷防雨","content":"下雨前要检查帐篷有没有破洞，拉链要拉紧。在帐篷周围挖一条小沟，雨水会顺着沟流走，不会积在帐篷底下。","image":"camping/rain-protection"},{"title":"下雨怎么办","content":"下雨时不要在大树下躲雨，打雷闪电时更危险。穿好雨衣待在帐篷里，等雨停了再出去活动。","image":"camping/rain-protection"}]}' WHERE id = 463;

-- Lesson 464: 认路 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"露营要认路","content":"在野外不能乱跑，要记得走过的路。学会认路才不会迷路，能安全地回到营地。","image":"camping/find-way"},{"title":"怎么认路","content":"走路时注意记路上的标志，比如大树、石头、小溪。也可以在路上做记号，走一段做一个，回来时跟着记号走。","image":"camping/find-way"},{"title":"迷路了怎么办","content":"万一迷路了不要慌张乱跑。待在原地不要动，大声呼喊等大人来找，或者吹响随身带的求救哨。","image":"camping/find-way"}]}' WHERE id = 464;

-- Lesson 465: 防虫 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"野外有虫子","content":"森林里有很多虫子，有的会咬人。露营时要穿长袖长裤，喷上防虫液，这样虫子就不敢靠近了。","image":"camping/bug-protection"},{"title":"防虫小妙招","content":"帐篷的拉链要拉好，虫子就钻不进去。睡觉前检查帐篷里有没有虫子，吃完的食物要收好，不然会引来蚂蚁。","image":"camping/bug-protection"},{"title":"被虫咬了怎么办","content":"被虫子咬了不要用力抓，会越抓越痒。告诉大人帮你处理，涂上药膏，如果又红又肿要去看医生。","image":"camping/bug-protection"}]}' WHERE id = 465;

-- Lesson 466: 求助 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么时候要求助","content":"露营遇到危险或受伤时，要立刻找大人帮忙。不要自己乱动，大声喊救命让附近的人听到。","image":"camping/call-help"},{"title":"怎么求助","content":"求助可以大声呼喊，吹响求救哨，或者用手电晃动发出信号。如果有人经过，告诉他们发生了什么事。","image":"camping/call-help"},{"title":"牢记求助电话","content":"要记住急救电话一二零，报警电话一一零。说清楚你在哪里、发生了什么，大人会很快来帮助你。","image":"camping/call-help"}]}' WHERE id = 466;

-- Lesson 468: 高山 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是高山","content":"高山是又高又大的山。它高高耸立直入云霄，山顶上常常覆盖着白雪，看起来非常壮观。","image":"geography/mountain"},{"title":"高山的特点","content":"山越高空气越稀薄，温度也越低。所以有些高山的山顶常年有雪，而山脚下却很暖和，花草茂盛。","image":"geography/mountain"},{"title":"世界名山","content":"世界上最高的山是珠穆朗玛峰，在中国和尼泊尔的边界上。它有八千多米高，山顶终年积雪，被称为世界之巅。","image":"geography/mountain"}]}' WHERE id = 468;

-- Lesson 469: 河流 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是河流","content":"河流是陆地上流动的水。水从高处往低处流，汇成小溪再变成大河，一路流向湖泊或大海。","image":"geography/river"},{"title":"河流的样子","content":"河流弯弯曲曲地流过大地。河两岸长着花草树木，河里有鱼虾，很多小动物都住在河边，河流是它们的家。","image":"geography/river"},{"title":"中国的大河","content":"中国有两条大河，长江和黄河。黄河像一条黄色的巨龙，长江是中国最长的河，它们是中华文明的摇篮。","image":"geography/river"}]}' WHERE id = 469;

-- Lesson 470: 湖泊 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是湖泊","content":"湖泊是陆地上蓄水的大坑。水从四面八方流进来，聚在一起形成一大片水面，就像陆地上的大水盆。","image":"geography/lake"},{"title":"湖泊的样子","content":"湖泊的水一般比较平静，不像河流那样流得快。湖面上能倒映出蓝天白云，湖水清清的，很美丽。","image":"geography/lake"},{"title":"著名的湖","content":"中国有很多有名的湖。杭州西湖风景如画，青海湖是中国最大的咸水湖，每个湖都有自己的特色。","image":"geography/lake"}]}' WHERE id = 470;

-- Lesson 471: 瀑布 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是瀑布","content":"瀑布是水从高处跌落下来的景象。河流遇到陡崖，水哗哗地冲下来，像一块白色的布挂在山间，非常壮观。","image":"geography/waterfall"},{"title":"瀑布的声音","content":"瀑布的水从高处落下来，发出轰隆隆的巨响。站在旁边能感觉到水花飞溅，声音大得像打雷一样。","image":"geography/waterfall"},{"title":"著名瀑布","content":"世界上有名的瀑布之一是尼亚加拉大瀑布。中国也有黄果树瀑布，水流又大又急，吸引很多人去看。","image":"geography/waterfall"}]}' WHERE id = 471;

-- Lesson 472: 沙漠 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是沙漠","content":"沙漠是到处都是沙子的地方。那里很少下雨，天气又干又热，白天很热晚上很冷，水非常少。","image":"geography/desert"},{"title":"沙漠的样子","content":"沙漠里沙丘一个连一个，风吹过沙子会移动。沙漠里很少有植物，只有骆驼和仙人掌能在这里生活。","image":"geography/desert"},{"title":"沙漠里的骆驼","content":"骆驼是沙漠之舟。它的驼峰里存着脂肪，可以好几天不吃不喝，是人们在沙漠里最好的旅行伙伴。","image":"geography/desert"}]}' WHERE id = 472;

-- Lesson 474: 亚洲 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是亚洲","content":"亚洲是世界上最大的洲。中国就在亚洲，亚洲有很多人，也有很多国家，是一个热闹又广阔的地方。","image":"geography/asia"},{"title":"亚洲的特点","content":"亚洲有最高的山、最深的海、最多的人口。这里有热带雨林也有冰天雪地，风景各种各样非常丰富。","image":"geography/asia"},{"title":"亚洲的国家","content":"亚洲有很多国家。除了中国，还有日本、印度、韩国等。每个国家有自己的语言和美食，比如日本的寿司。","image":"geography/asia"}]}' WHERE id = 474;

-- Lesson 475: 欧洲 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是欧洲","content":"欧洲在亚洲的西边，是一个比较小的洲。虽然不大，但有很多国家，风景优美，有很多古老的城堡和教堂。","image":"geography/europe"},{"title":"欧洲的特点","content":"欧洲有很多美丽的国家。法国有埃菲尔铁塔，英国有大本钟，意大利有比萨斜塔，每个地方都很有特色。","image":"geography/europe"},{"title":"欧洲的生活","content":"欧洲人喜欢吃面包和奶酪。很多欧洲城市有古老的石头房子和石板路，走在街上就像走进了故事书。","image":"geography/europe"}]}' WHERE id = 475;

-- Lesson 476: 非洲 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是非洲","content":"非洲是一个很热的大洲。那里有大片草原和沙漠，有很多特别的动物，比如狮子、大象和长颈鹿。","image":"geography/africa"},{"title":"非洲的动物","content":"非洲的草原上住着很多野生动物。狮子在草丛里休息，大象用长鼻子喝水，长颈鹿伸长脖子吃树叶。","image":"geography/africa"},{"title":"非洲的特点","content":"非洲有世界上最大的沙漠撒哈拉。那里天气很热，阳光强烈。非洲人皮肤黑黑的，能保护自己不被晒伤。","image":"geography/africa"}]}' WHERE id = 476;

-- Lesson 477: 北京 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是北京","content":"北京是中国的首都，是全国的中心。它是一座古老又现代的城市，有很长的历史，也是现在的大都市。","image":"geography/beijing"},{"title":"北京的名胜","content":"北京有很多有名的地方。长城像一条巨龙盘在山上，故宫是皇帝住过的大房子，天安门广场又大又宽广。","image":"geography/beijing"},{"title":"北京的美食","content":"北京有很多好吃的。北京烤鸭皮脆肉香，炸酱面味道浓郁，糖葫芦酸酸甜甜，来北京一定要尝一尝。","image":"geography/beijing"}]}' WHERE id = 477;

-- Lesson 478: 纽约 (READING)
UPDATE lesson SET content = '{"type":"READING","items":[{"title":"什么是纽约","content":"纽约是美国最大的城市。那里有很高很高的楼，人多车多，是一个非常热闹的国际大都市。","image":"geography/new-york"},{"title":"纽约的名胜","content":"纽约有一座自由女神像，她举着火炬欢迎远方来的人。还有很多高楼大厦，晚上的灯光亮闪闪的非常漂亮。","image":"geography/new-york"},{"title":"纽约的特点","content":"纽约是世界上最繁华的城市之一。那里有百老汇的表演，有大大的中央公园，来自世界各地的人在这里生活。","image":"geography/new-york"}]}' WHERE id = 478;

