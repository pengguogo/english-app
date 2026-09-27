// 飞行棋游戏 - 基于图片棋盘
const boardContainer = document.querySelector('.board-container');
const BOARD_SIZE = 600;

// ========== 游戏配置 ==========
let gameSettings = {
    language: 'zh',  // 语言：zh=中文, en=英文
    pieceStyle: 'original'  // 棋子样式：original=原始颜色, animals=动物头像
};

// 颜色定义（原始样式）
const COLORS_ORIGINAL = {
    red: { main: '#e74c3c', light: '#fadbd8', dark: '#c0392b', emoji: '🔴', name_zh: '红色', name_en: 'Red' },
    yellow: { main: '#f39c12', light: '#fdebd0', dark: '#d68910', emoji: '🟡', name_zh: '黄色', name_en: 'Yellow' },
    blue: { main: '#3498db', light: '#d6eaf8', dark: '#2980b9', emoji: '🔵', name_zh: '蓝色', name_en: 'Blue' },
    green: { main: '#27ae60', light: '#d5f4e6', dark: '#229954', emoji: '🟢', name_zh: '绿色', name_en: 'Green' }
};

// 动物头像样式
const COLORS_ANIMALS = {
    red: { main: '#e74c3c', light: '#fadbd8', dark: '#c0392b', emoji: '🦁', name_zh: '红色狮子', name_en: 'Red Lion' },
    blue: { main: '#3498db', light: '#d6eaf8', dark: '#2980b9', emoji: '🐯', name_zh: '蓝色老虎', name_en: 'Blue Tiger' },
    yellow: { main: '#f39c12', light: '#fdebd0', dark: '#d68910', emoji: '🐼', name_zh: '黄色熊猫', name_en: 'Yellow Panda' },
    green: { main: '#27ae60', light: '#d5f4e6', dark: '#229954', emoji: '🐘', name_zh: '绿色大象', name_en: 'Green Elephant' }
};

// 根据设置获取颜色配置
function getColors() {
    return gameSettings.pieceStyle === 'animals' ? COLORS_ANIMALS : COLORS_ORIGINAL;
}

// 语言翻译
const TRANSLATIONS = {
    zh: {
        gameTitle: '🎲 飞行棋 🎲',
        selectPlayers: '请选择玩家人数（2-4人）',
        settings: '⚙️ 游戏设置',
        language: '语言',
        pieceStyle: '棋子样式',
        styleOriginal: '原始颜色',
        styleAnimals: '动物头像',
        startGame: '开始游戏',
        yellowPlayer: '🟡 黄色玩家',
        bluePlayer: '🔵 蓝色玩家',
        redPlayer: '🔴 红色玩家',
        greenPlayer: '🟢 绿色玩家',
        rollDice: '掷骰子',
        selectPlayer: '选择玩家开始游戏！',
        planeTakeoff: '飞机起飞到起点！',
        rollSixAgain: '掷出6点！再掷一次',
        turnToPlayer: '轮到',
        player: '玩家',
       到达终点: '到达终点！🎉',
        exceedFinish: '超过终点，弹回',
        steps: '步！↩️',
        triggerShortcut: '触发飞行通道！✈️',
        colorJump: '停在',
        colorTile: '色格子上，飞跃！✈️',
       击落: '击落了',
        selectPiece: '请选择要移动的棋子',
        playerWins: '玩家获胜！🎊',
        twoPlayer: '双人对战',
        threePlayer: '三人游戏',
        fourPlayer: '四人游戏',
        allPlayers: '全部玩家'
    },
    en: {
        gameTitle: '🎲 Ludo Game 🎲',
        selectPlayers: 'Select number of players (2-4)',
        settings: '⚙️ Game Settings',
        language: 'Language',
        pieceStyle: 'Piece Style',
        styleOriginal: 'Original Colors',
        styleAnimals: 'Animal Avatars',
        startGame: 'Start Game',
        yellowPlayer: '🟡 Yellow Player',
        bluePlayer: '🔵 Blue Player',
        redPlayer: '🔴 Red Player',
        greenPlayer: '🟢 Green Player',
        rollDice: 'Roll Dice',
        selectPlayer: 'Select players to start!',
        planeTakeoff: 'Plane takes off!',
        rollSixAgain: 'Rolled 6! Roll again',
        turnToPlayer: 'Turn',
        player: 'Player',
       到达终点: 'Reached the finish! 🎉',
        exceedFinish: 'Exceeded finish, bounce back',
        steps: ' steps! ↩️',
        triggerShortcut: 'Triggered shortcut! ✈️',
        colorJump: 'Landed on',
        colorTile: 'tile, jump! ✈️',
       击落: 'captured',
        selectPiece: 'Select a piece to move',
        playerWins: 'player wins! 🎊',
        twoPlayer: '2 Players',
        threePlayer: '3 Players',
        fourPlayer: '4 Players',
        allPlayers: 'All Players'
    }
};

// 获取翻译文本
function t(key) {
    return TRANSLATIONS[gameSettings.language][key] || key;
}

// 动态获取 COLORS 对象
const COLORS = new Proxy({}, {
    get: function(target, prop) {
        const colors = getColors();
        if (prop in colors) {
            const color = colors[prop];
            // 添加 name 属性，根据语言返回对应名称
            return {
                ...color,
                name: gameSettings.language === 'en' ? color.name_en : color.name_zh
            };
        }
        return undefined;
    }
});

// 所有玩家颜色列表（用于布局和初始化）
const ALL_COLORS = ['red', 'yellow', 'blue', 'green'];

// 游戏中实际参与的玩家（在游戏开始时设置）
let activePlayers = [];

// 玩家顺序 - 按照顺时针：红->绿->黄->蓝
const PLAYERS = ['red', 'green', 'yellow', 'blue'];

// ========== 新的硬编码坐标系统 ==========

// 所有位置的坐标（索引 0-95）
const POSITIONS = [
    { x: 56, y: 195 },   { x: 93, y: 180 },   { x: 126, y: 180 },  { x: 163, y: 196 },   // 0-3
    { x: 194, y: 164 },  { x: 179, y: 128 },  { x: 180, y: 94 },   { x: 194, y: 57 },    // 4-7
    { x: 231, y: 45 },   { x: 265, y: 44 },   { x: 299, y: 45 },   { x: 334, y: 45 },    // 8-11
    { x: 367, y: 45 },   { x: 405, y: 57 },   { x: 420, y: 94 },   { x: 420, y: 129 },   // 12-15
    { x: 404, y: 164 },  { x: 436, y: 196 },  { x: 472, y: 180 },  { x: 505, y: 180 },   // 16-19
    { x: 542, y: 197 },  { x: 555, y: 232 },  { x: 554, y: 265 },  { x: 555, y: 300 },   // 20-23
    { x: 555, y: 332 },  { x: 555, y: 367 },  { x: 542, y: 403 },  { x: 505, y: 418 },   // 24-27
    { x: 472, y: 418 },  { x: 435, y: 404 },  { x: 405, y: 434 },  { x: 420, y: 470 },   // 28-31
    { x: 421, y: 505 },  { x: 404, y: 541 },  { x: 367, y: 555 },  { x: 333, y: 556 },   // 32-35
    { x: 298, y: 556 },  { x: 265, y: 555 },  { x: 231, y: 554 },  { x: 193, y: 542 },   // 36-39
    { x: 178, y: 505 },  { x: 178, y: 471 },  { x: 194, y: 436 },  { x: 164, y: 404 },   // 40-43
    { x: 125, y: 419 },  { x: 93, y: 419 },   { x: 57, y: 403 },   { x: 43, y: 367 },    // 44-47
    { x: 43, y: 333 },   { x: 45, y: 299 },   { x: 44, y: 267 },   { x: 45, y: 232 },    // 48-51
    { x: 93, y: 299 },   { x: 129, y: 299 },  { x: 161, y: 300 },  { x: 195, y: 300 },   // 52-55
    { x: 229, y: 299 },  { x: 269, y: 300 },                                                                 // 56-57
    { x: 300, y: 94 },   { x: 298, y: 127 },  { x: 300, y: 162 },  { x: 299, y: 197 },   // 58-61
    { x: 300, y: 231 },  { x: 299, y: 271 },                                                                 // 62-63
    { x: 506, y: 301 },  { x: 470, y: 301 },  { x: 437, y: 301 },  { x: 403, y: 301 },   // 64-67
    { x: 369, y: 299 },  { x: 329, y: 301 },                                                                 // 68-69
    { x: 299, y: 505 },  { x: 299, y: 471 },  { x: 300, y: 438 },  { x: 298, y: 404 },   // 70-73
    { x: 300, y: 370 },  { x: 299, y: 331 },                                                                 // 74-75
    { x: 15, y: 170 },   { x: 429, y: 17 },   { x: 582, y: 430 },  { x: 168, y: 582 },   // 76-79 起飞点
    { x: 44, y: 45 },    { x: 100, y: 45 },   { x: 45, y: 98 },    { x: 98, y: 96 },    // 80-83 黄色基地
    { x: 500, y: 44 },   { x: 553, y: 45 },   { x: 500, y: 98 },   { x: 555, y: 99 },   // 84-87 蓝色基地
    { x: 501, y: 501 },  { x: 555, y: 503 },  { x: 501, y: 554 },  { x: 554, y: 554 },  // 88-91 红色基地
    { x: 45, y: 501 },   { x: 97, y: 501 },   { x: 44, y: 554 },   { x: 98, y: 554 }    // 92-95 绿色基地
];

// 起飞点
const START_POINTS = {
    yellow: 76,   // 黄色起飞点
    blue: 77,     // 蓝色起飞点
    red: 78,      // 红色起飞点
    green: 79     // 绿色起飞点
};

// 基地位置（每个颜色4个棋子）
const HOME_POSITIONS = {
    yellow: [80, 81, 82, 83],   // 黄色基地：1号-4号棋子
    blue: [84, 85, 86, 87],     // 蓝色基地：1号-4号棋子
    red: [88, 89, 90, 91],      // 红色基地：1号-4号棋子
    green: [92, 93, 94, 95]     // 绿色基地：1号-4号棋子
};

// 终点通道配置
const FINISH_CHANNELS = {
    yellow: { start: 52, end: 57, entry: 49, total: 55 },   // 黄色：52-57，从主路径49进入，总共55步
    blue: { start: 58, end: 63, entry: 10, total: 56 },     // 蓝色：58-63，从主路径10进入，总共56步
    red: { start: 64, end: 69, entry: 23, total: 56 },      // 红色：64-69，从主路径23进入，总共56步
    green: { start: 70, end: 75, entry: 36, total: 56 }     // 绿色：70-75，从主路径36进入，总共56步
};

// 各颜色起点位置（起飞点）- 方便其他函数使用
const START_POSITIONS = {
    yellow: { x: 15, y: 170 },
    blue: { x: 429, y: 17 },
    red: { x: 582, y: 430 },
    green: { x: 168, y: 582 }
};

// 飞行通道配置 - 基于主路径索引
// 每个颜色的棋子停在触发索引时，自动飞到目标索引
const SHORTCUTS = {
    yellow: { trigger: 17, destination: 29 },    // 黄色: 17 → 29
    blue: { trigger: 30, destination: 42 },      // 蓝色: 30 → 42
    red: { trigger: 43, destination: 3 },        // 红色: 43 → 3 (跨越循环)
    green: { trigger: 4, destination: 16 }       // 绿色: 4 → 16
};

// 主路径上各颜色对应的格子索引（用于颜色飞跃）
// 当棋子停在自己颜色的格子上时，飞到下一个同色格子
// 根据用户标注的颜色代码：
// 黄色：1/5/9/13-49, 蓝色：2/6/10-50, 红色：3/7/11-51, 绿色：0/4/8-48
const PATH_COLORS = {
    yellow: [1, 5, 9, 13, 17, 21, 25, 29, 33, 37, 41, 45, 49],
    blue: [2, 6, 10, 14, 18, 22, 26, 30, 34, 38, 42, 46, 50],
    red: [3, 7, 11, 15, 19, 23, 27, 31, 35, 39, 43, 47, 51],
    green: [0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48]
};

// 终点通道入口配置 - 各颜色在主路径上进入终点通道的索引
const FINISH_ENTRY_INDEX = {
    yellow: 49,   // 黄色在主路径索引49进入终点通道
    blue: 10,     // 蓝色在主路径索引10进入终点通道
    red: 23,      // 红色在主路径索引23进入终点通道
    green: 36     // 绿色在主路径索引36进入终点通道
};

// 各颜色在主路径中的起点索引（起飞后第一个停留后的下一步）
const PATH_START_INDEX = {
    yellow: 0,    // 黄色从主路径索引0开始: 0,1,2,3...
    blue: 13,     // 蓝色从主路径索引13开始: 13,14,15,16...
    red: 26,      // 红色从主路径索引26开始: 26,27,28...
    green: 39     // 绿色从主路径索引39开始: 39,40,41...
};

// 主路径 - 52个格子，沿着棋盘外圈顺时针（用户标注的精确坐标）
const MAIN_PATH = [
    { x: 91, y: 212 },   // 0
    { x: 122, y: 198 },  // 1
    { x: 151, y: 198 },  // 2
    { x: 183, y: 212 },  // 3
    { x: 209, y: 184 },  // 4
    { x: 195, y: 150 },  // 5
    { x: 196, y: 120 },  // 6
    { x: 209, y: 86 },   // 7
    { x: 241, y: 74 },   // 8
    { x: 270, y: 74 },   // 9
    { x: 300, y: 75 },   // 10
    { x: 330, y: 75 },   // 11
    { x: 358, y: 74 },   // 12
    { x: 390, y: 86 },   // 13
    { x: 404, y: 119 },  // 14
    { x: 404, y: 150 },  // 15
    { x: 390, y: 184 },  // 16
    { x: 416, y: 212 },  // 17
    { x: 448, y: 198 },  // 18
    { x: 477, y: 198 },  // 19
    { x: 508, y: 212 },  // 20
    { x: 519, y: 244 },  // 21
    { x: 518, y: 276 },  // 22
    { x: 520, y: 307 },  // 23
    { x: 519, y: 336 },  // 24
    { x: 519, y: 366 },  // 25
    { x: 508, y: 399 },  // 26
    { x: 477, y: 413 },  // 27
    { x: 447, y: 413 },  // 28
    { x: 416, y: 400 },  // 29
    { x: 391, y: 428 },  // 30
    { x: 405, y: 461 },  // 31
    { x: 404, y: 491 },  // 32
    { x: 390, y: 526 },  // 33
    { x: 358, y: 536 },  // 34
    { x: 328, y: 537 },  // 35
    { x: 299, y: 537 },  // 36
    { x: 270, y: 537 },  // 37
    { x: 239, y: 536 },  // 38
    { x: 210, y: 526 },  // 39
    { x: 195, y: 491 },  // 40
    { x: 196, y: 460 },  // 41
    { x: 210, y: 427 },  // 42
    { x: 182, y: 398 },  // 43
    { x: 151, y: 413 },  // 44
    { x: 122, y: 413 },  // 45
    { x: 90, y: 399 },   // 46
    { x: 81, y: 366 },   // 47
    { x: 79, y: 335 },   // 48
    { x: 80, y: 305 },   // 49
    { x: 80, y: 275 },   // 50
    { x: 82, y: 245 }    // 51
];

// 中心终点坐标（各颜色的最终目标）
const CENTER_FINISH = {
    yellow: { x: 274, y: 306 },  // 黄色中心终点
    blue: { x: 300, y: 278 },   // 蓝色中心终点
    red: { x: 325, y: 306 },   // 红色中心终点
    green: { x: 300, y: 333 }   // 绿色中心终点
};

// 终点通道 - 从主路径进入中心的6格路径
// 索引0在主路径上（入口），索引5是中心终点
const HOME_STRETCH = {
    yellow: [
        { x: 80, y: 306 },   // 入口（在主路径上）
        { x: 123, y: 305 },  // 第2格
        { x: 152, y: 305 },  // 第3格
        { x: 181, y: 305 },  // 第4格
        { x: 211, y: 305 },  // 第5格
        { x: 240, y: 305 }   // 第6格（接近中心）
    ],
    blue: [
        { x: 300, y: 75 },   // 入口（在主路径上）
        { x: 300, y: 118 },  // 第2格
        { x: 300, y: 150 },  // 第3格
        { x: 300, y: 181 },  // 第4格
        { x: 300, y: 211 },  // 第5格
        { x: 300, y: 242 }   // 第6格（接近中心）
    ],
    red: [
        { x: 520, y: 306 },  // 入口（在主路径上）
        { x: 477, y: 305 },  // 第2格
        { x: 448, y: 305 },  // 第3格
        { x: 418, y: 305 },  // 第4格
        { x: 389, y: 306 },  // 第5格
        { x: 359, y: 306 }   // 第6格（接近中心）
    ],
    green: [
        { x: 300, y: 536 },  // 入口（在主路径上）
        { x: 300, y: 492 },  // 第2格
        { x: 299, y: 461 },  // 第3格
        { x: 300, y: 430 },  // 第4格
        { x: 299, y: 399 },  // 第5格
        { x: 299, y: 369 }   // 第6格（接近中心）
    ]
};

// 游戏状态
let gameState = {
    activePlayers: [],      // 参与游戏的玩家颜色数组
    currentPlayerIndex: 0,  // 当前玩家在 activePlayers 中的索引
    diceValue: 0,
    diceRolled: false,
    pieces: {
        red: [
            { position: -1, finished: false },
            { position: -1, finished: false },
            { position: -1, finished: false },
            { position: -1, finished: false }
        ],
        yellow: [
            { position: -1, finished: false },
            { position: -1, finished: false },
            { position: -1, finished: false },
            { position: -1, finished: false }
        ],
        blue: [
            { position: -1, finished: false },
            { position: -1, finished: false },
            { position: -1, finished: false },
            { position: -1, finished: false }
        ],
        green: [
            { position: -1, finished: false },
            { position: -1, finished: false },
            { position: -1, finished: false },
            { position: -1, finished: false }
        ]
    },
    gameOver: false,
    pieceElements: {}
};

// 获取当前玩家颜色
function getCurrentPlayerColor() {
    return gameState.activePlayers[gameState.currentPlayerIndex];
}

// 检查玩家是否参与游戏
function isPlayerActive(color) {
    return gameState.activePlayers.includes(color);
}

// 获取棋子在主路径上的实际索引
function getMainPathIndex(color, position) {
    // 根据颜色确定主路径起点
    let mainPathStart;
    switch (color) {
        case 'yellow': mainPathStart = 0; break;
        case 'blue': mainPathStart = 13; break;
        case 'red': mainPathStart = 26; break;
        case 'green': mainPathStart = 39; break;
    }
    // position 1 = mainPathStart, position 2 = mainPathStart + 1, ...
    const actualIndex = (mainPathStart + position - 1) % 52;
    return actualIndex;
}

// 检查棋子是否在终点通道上
function isInFinishStretch(color, position) {
    // position > 52 表示进入或超过终点通道
    return position > 52;
}

// 获取棋子位置坐标
function getPieceCoordinates(color, pieceIndex) {
    const piece = gameState.pieces[color][pieceIndex];
    const finishChannel = FINISH_CHANNELS[color];

    // 在基地（未起飞）
    if (piece.position === -1) {
        const homeIndex = HOME_POSITIONS[color][pieceIndex];
        return POSITIONS[homeIndex];
    }

    // 已完成 - 飞回基地对应位置
    if (piece.finished) {
        const homeIndex = HOME_POSITIONS[color][pieceIndex];
        return POSITIONS[homeIndex];
    }

    // 在起飞点（刚起飞，position=0）
    if (piece.position === 0) {
        const startPoint = START_POINTS[color];
        return POSITIONS[startPoint];
    }

    // 计算当前在主路径上的索引
    let mainPathStart;
    switch (color) {
        case 'yellow': mainPathStart = 0; break;
        case 'blue': mainPathStart = 13; break;
        case 'red': mainPathStart = 26; break;
        case 'green': mainPathStart = 39; break;
    }

    const currentPathIndex = (mainPathStart + piece.position - 1) % 52;

    // 检查是否在终点通道上
    // 计算从起点到终点入口需要走多少步
    let stepsToEntry;
    const entryIndex = finishChannel.entry;

    if (entryIndex >= mainPathStart) {
        stepsToEntry = entryIndex - mainPathStart + 1;
    } else {
        stepsToEntry = (52 - mainPathStart) + entryIndex + 1;
    }

    // 如果走过的步数超过到达入口的步数，则在终点通道上
    if (piece.position > stepsToEntry) {
        const finishStepIndex = piece.position - stepsToEntry - 1; // 在终点通道中的第几步（0-5）
        const finishIndex = finishChannel.start + finishStepIndex;
        if (finishIndex <= finishChannel.end) {
            return POSITIONS[finishIndex];
        }
    }
    // 否则在主路径上（包括终点入口）

    // 在主路径上
    return POSITIONS[currentPathIndex];
}

// 创建棋子元素
function createPieceElement(color, index) {
    const piece = document.createElement('div');
    piece.className = `piece ${color}`;

    // 如果使用动物头像样式，添加 animals 类
    if (gameSettings.pieceStyle === 'animals') {
        piece.classList.add('animals');
        piece.textContent = COLORS[color].emoji;  // 显示动物头像
    } else {
        piece.textContent = index + 1;  // 显示数字
    }

    piece.id = `piece-${color}-${index}`;
    piece.onclick = () => handlePieceClick(color, index);

    const coords = getPieceCoordinates(color, index);
    // 不再使用-15偏移，直接使用用户标注的坐标作为棋子中心
    piece.style.left = coords.x + 'px';
    piece.style.top = coords.y + 'px';
    // 使用transform将棋子中心对齐到坐标点
    piece.style.transform = 'translate(-50%, -50%)';

    boardContainer.appendChild(piece);
    gameState.pieceElements[`${color}-${index}`] = piece;

    return piece;
}

// 移动棋子到新位置
function movePieceTo(color, index, animate = true) {
    const piece = gameState.pieces[color][index];
    let coords = getPieceCoordinates(color, index);
    const element = gameState.pieceElements[`${color}-${index}`];

    if (animate) {
        element.classList.add('moving');
    }

    // 检查同一位置是否有其他棋子，如果有则稍微偏移
    let offsetX = 0;
    let offsetY = 0;
    let overlapCount = 0;

    gameState.activePlayers.forEach(otherColor => {
        gameState.pieces[otherColor].forEach((otherPiece, otherIndex) => {
            if (otherPiece.position === -1 || otherPiece.finished) return;

            // 跳过自己
            if (otherColor === color && otherIndex === index) return;

            const otherCoords = getPieceCoordinates(otherColor, otherIndex);

            // 检查是否在同一位置
            if (otherCoords.x === coords.x && otherCoords.y === coords.y) {
                overlapCount++;
                // 每多3个棋子重叠（不太可能更多）
                if (overlapCount <= 3) {
                    // 根据重叠数量计算偏移
                    const offset = overlapCount * 6; // 6px偏移
                    const angle = (overlapCount - 1) * (Math.PI / 4); // 45度间隔

                    offsetX = Math.cos(angle) * offset;
                    offsetY = Math.sin(angle) * offset;
                }
            }
        });
    });

    // 应用坐标和偏移
    element.style.left = (coords.x + offsetX) + 'px';
    element.style.top = (coords.y + offsetY) + 'px';
    element.style.transform = 'translate(-50%, -50%)';

    if (piece.finished) {
        element.style.background = 'gold';
        element.textContent = '✓';
    }

    setTimeout(() => {
        element.classList.remove('moving');
    }, animate ? 500 : 0);
}

// 初始化所有棋子
function initializePieces() {
    // 只为参与游戏的玩家创建棋子
    gameState.activePlayers.forEach(color => {
        for (let i = 0; i < 4; i++) {
            createPieceElement(color, i);
        }
    });

    // 隐藏未参与玩家的面板
    ALL_COLORS.forEach(color => {
        if (!isPlayerActive(color)) {
            const panel = document.getElementById(`panel-${color}`);
            if (panel) panel.style.display = 'none';
        }
    });
}

// 掷骰子
function rollDice(color) {
    if (gameState.diceRolled || gameState.gameOver) return;

    // 验证是否是当前玩家的色子
    const currentPlayerColor = getCurrentPlayerColor();
    if (color !== currentPlayerColor) return;

    const dice = document.getElementById(`dice-${color}`);
    const rollBtn = document.getElementById(`rollBtn-${color}`);

    dice.classList.add('rolling');
    if (rollBtn) rollBtn.disabled = true;

    setTimeout(() => {
        gameState.diceValue = Math.floor(Math.random() * 6) + 1;
        const diceNumbers = ['⚀', '⚁', '⚂', '⚃', '⚄', '⚅'];
        dice.textContent = diceNumbers[gameState.diceValue - 1];
        dice.classList.remove('rolling');
        gameState.diceRolled = true;

        updateMessage(`${COLORS[currentPlayerColor].emoji}掷出了 ${gameState.diceValue} 点！`);

        setTimeout(() => checkMovablePieces(), 300);
    }, 500);
}

// 检查可移动的棋子
function checkMovablePieces() {
    const currentColor = getCurrentPlayerColor();
    const pieces = gameState.pieces[currentColor];
    const movable = [];

    document.querySelectorAll('.piece').forEach(p => p.classList.remove('selectable'));

    pieces.forEach((piece, index) => {
        if (piece.finished) return;

        if (piece.position === -1) {
            // 在基地，只有掷出6才能起飞
            if (gameState.diceValue === 6) {
                movable.push(index);
            }
        } else {
            // 在路径上，任何点数都可以移动（弹回规则）
            movable.push(index);
        }
    });

    if (movable.length === 0) {
        updateMessage(gameSettings.language === 'en' ? 'No movable pieces' : '没有可移动的棋子');
        setTimeout(nextPlayer, 1500);
    } else if (movable.length === 1) {
        setTimeout(() => movePiece(movable[0]), 500);
    } else {
        const msg = gameSettings.language === 'en'
            ? `Select a piece to move (${movable.length} available)`
            : `请选择棋子移动 (${movable.length}个可选)`;
        updateMessage(msg);
        movable.forEach(index => {
            const element = gameState.pieceElements[`${currentColor}-${index}`];
            if (element) element.classList.add('selectable');
        });
    }
}

// 处理棋子点击
function handlePieceClick(color, index) {
    if (!gameState.diceRolled) return;
    if (color !== getCurrentPlayerColor()) return;

    const element = gameState.pieceElements[`${color}-${index}`];
    if (!element || !element.classList.contains('selectable')) return;

    movePiece(index);
}

// 移动棋子
function movePiece(pieceIndex) {
    const currentColor = getCurrentPlayerColor();
    const piece = gameState.pieces[currentColor][pieceIndex];

    document.querySelectorAll('.piece').forEach(p => p.classList.remove('selectable'));

    if (piece.position === -1) {
        piece.position = 0;
        movePieceTo(currentColor, pieceIndex);
        updateMessage(gameSettings.language === 'en' ? 'Plane takes off!' : '飞机起飞到起点！');
        updatePiecesStatus();

        setTimeout(() => {
            checkCapture(currentColor, pieceIndex);
            if (gameState.diceValue === 6 && !gameState.gameOver) {
                gameState.diceRolled = false;
                enableCurrentPlayerDice();
                updateMessage(gameSettings.language === 'en' ? 'Rolled 6! Roll again' : '掷出6点！再掷一次');
            } else if (!gameState.gameOver) {
                setTimeout(nextPlayer, 500);
            }
        }, 600);
        return;
    }

    const newPosition = piece.position + gameState.diceValue;
    const finishChannel = FINISH_CHANNELS[currentColor];

    // 计算到达终点入口需要的步数
    let mainPathStart;
    switch (currentColor) {
        case 'yellow': mainPathStart = 0; break;
        case 'blue': mainPathStart = 13; break;
        case 'red': mainPathStart = 26; break;
        case 'green': mainPathStart = 39; break;
    }

    let stepsToEntry;
    const entryIndex = finishChannel.entry;

    if (entryIndex >= mainPathStart) {
        stepsToEntry = entryIndex - mainPathStart + 1;
    } else {
        stepsToEntry = (52 - mainPathStart) + entryIndex + 1;
    }

    // 检查是否进入终点通道
    if (newPosition > stepsToEntry) {
        // 进入终点通道，计算在通道中的位置
        const finishStepIndex = newPosition - stepsToEntry - 1;
        const finishIndex = finishChannel.start + finishStepIndex;

        if (finishIndex === finishChannel.end) {
            // 刚好到达终点
            piece.position = newPosition;
            piece.finished = true;
            movePieceTo(currentColor, pieceIndex);
            updateMessage(gameSettings.language === 'en' ? 'Reached the finish! 🎉' : '到达终点！🎉');
            updatePiecesStatus();
            checkWin(currentColor);

            if (gameState.diceValue === 6 && !gameState.gameOver) {
                gameState.diceRolled = false;
                enableCurrentPlayerDice();
                updateMessage(gameSettings.language === 'en' ? 'Rolled 6! Roll again' : '掷出6点！再掷一次');
            } else if (!gameState.gameOver) {
                setTimeout(nextPlayer, 1000);
            }
            return;
        } else if (finishIndex > finishChannel.end) {
            // 超过终点，弹回规则
            const excess = finishIndex - finishChannel.end;
            const bounceBackIndex = finishChannel.end - excess;

            // 计算弹回后的position值
            const bounceBackStepIndex = bounceBackIndex - finishChannel.start;
            piece.position = stepsToEntry + 1 + bounceBackStepIndex;

            movePieceTo(currentColor, pieceIndex);
            updateMessage(gameSettings.language === 'en'
                ? `Exceeded finish, bounce back ${excess} steps! ↩️`
                : `超过终点，弹回${excess}步！↩️`);
            updatePiecesStatus();

            setTimeout(() => {
                checkCapture(currentColor, pieceIndex);

                if (gameState.diceValue === 6 && !gameState.gameOver) {
                    gameState.diceRolled = false;
                    enableCurrentPlayerDice();
                    updateMessage(gameSettings.language === 'en' ? 'Rolled 6! Roll again' : '掷出6点！再掷一次');
                } else if (!gameState.gameOver) {
                    setTimeout(nextPlayer, 500);
                }
            }, 600);
            return;
        }
    }

    piece.position = newPosition;

    movePieceTo(currentColor, pieceIndex);
    updatePiecesStatus();

    setTimeout(() => {
        // 检查是否触发飞行通道或颜色飞跃
        const shortcutResult = checkShortcut(currentColor, pieceIndex);

        if (shortcutResult.shouldFly) {
            // 触发飞行
            if (shortcutResult.flyType === 'shortcut') {
                updateMessage(`${COLORS[currentColor].emoji}${gameSettings.language === 'en' ? 'Triggered shortcut!' : '触发飞行通道！'}✈️`);
            } else if (shortcutResult.flyType === 'color') {
                updateMessage(`${COLORS[currentColor].emoji}停在${COLORS[currentColor].name}格子上，飞跃！✈️`);
            }

            setTimeout(() => {
                // 飞行到目的地
                piece.position = shortcutResult.newPosition;
                movePieceTo(currentColor, pieceIndex);
                updatePiecesStatus();

                // 如果是颜色飞跃，需要再次检查是否在飞行通道入口
                // 如果是飞行通道，直接结束
                if (shortcutResult.flyType === 'color') {
                    setTimeout(() => {
                        // 再次检查飞行通道
                        const secondCheck = checkShortcut(currentColor, pieceIndex);
                        if (secondCheck.shouldFly && secondCheck.flyType === 'shortcut') {
                            // 触发飞行通道
                            updateMessage(`${COLORS[currentColor].emoji}${gameSettings.language === 'en' ? 'Triggered shortcut!' : '触发飞行通道！'}✈️`);
                            setTimeout(() => {
                                piece.position = secondCheck.newPosition;
                                movePieceTo(currentColor, pieceIndex);
                                updatePiecesStatus();
                                setTimeout(() => {
                                    checkCapture(currentColor, pieceIndex);
                                    if (gameState.diceValue === 6 && !gameState.gameOver) {
                                        gameState.diceRolled = false;
                                        enableCurrentPlayerDice();
                                        updateMessage(gameSettings.language === 'en' ? 'Rolled 6! Roll again' : '掷出6点！再掷一次');
                                    } else if (!gameState.gameOver) {
                                        setTimeout(nextPlayer, 500);
                                    }
                                }, 600);
                            }, 500);
                        } else {
                            // 没有触发飞行通道，正常结束
                            setTimeout(() => {
                                checkCapture(currentColor, pieceIndex);
                                if (gameState.diceValue === 6 && !gameState.gameOver) {
                                    gameState.diceRolled = false;
                                    enableCurrentPlayerDice();
                                    updateMessage(gameSettings.language === 'en' ? 'Rolled 6! Roll again' : '掷出6点！再掷一次');
                                } else if (!gameState.gameOver) {
                                    setTimeout(nextPlayer, 500);
                                }
                            }, 600);
                        }
                    }, 500);
                } else {
                    // 飞行通道到达，直接结束
                    setTimeout(() => {
                        checkCapture(currentColor, pieceIndex);
                        if (gameState.diceValue === 6 && !gameState.gameOver) {
                            gameState.diceRolled = false;
                            enableCurrentPlayerDice();
                            updateMessage(gameSettings.language === 'en' ? 'Rolled 6! Roll again' : '掷出6点！再掷一次');
                        } else if (!gameState.gameOver) {
                            setTimeout(nextPlayer, 500);
                        }
                    }, 600);
                }
            }, 500); // 停顿500ms后飞行
        } else {
            // 正常移动，不飞行
            checkCapture(currentColor, pieceIndex);

            if (gameState.diceValue === 6 && !gameState.gameOver) {
                gameState.diceRolled = false;
                enableCurrentPlayerDice();
                updateMessage(gameSettings.language === 'en' ? 'Rolled 6! Roll again' : '掷出6点！再掷一次');
            } else if (!gameState.gameOver) {
                setTimeout(nextPlayer, 500);
            }
        }
    }, 600);
}

// 检查是否触发飞行通道或颜色飞跃
function checkShortcut(color, pieceIndex) {
    const piece = gameState.pieces[color][pieceIndex];

    // 只有在主路径上且未到达终点通道才能触发飞行
    if (piece.position === -1 || piece.finished || piece.position === 0) {
        return { shouldFly: false };
    }

    // 如果已经在终点通道，不能飞行
    if (isInFinishStretch(color, piece.position)) {
        return { shouldFly: false };
    }

    // 获取当前在主路径上的索引
    const currentIndex = getMainPathIndex(color, piece.position);

    // 【优先级1】首先检查是否触发特殊的飞行通道（SHORTCUTS）
    const shortcut = SHORTCUTS[color];
    if (currentIndex === shortcut.trigger) {
        // 计算飞行目的地的position值
        const startIndex = PATH_START_INDEX[color];
        let destPosition;

        if (shortcut.destination >= startIndex) {
            destPosition = shortcut.destination - startIndex + 1;
        } else {
            // 目的地在循环之前，需要加上52
            destPosition = (52 - startIndex) + shortcut.destination + 1;
        }

        return {
            shouldFly: true,
            newPosition: destPosition,
            flyType: 'shortcut'
        };
    }

    // 【优先级2】检查颜色飞跃 - 停在自己颜色的格子上时，飞到下一个同色格子
    // 但终点通道入口不触发颜色飞跃
    const finishEntry = FINISH_ENTRY_INDEX[color];
    if (currentIndex === finishEntry) {
        return { shouldFly: false };
    }

    const colorPositions = PATH_COLORS[color];
    if (colorPositions.includes(currentIndex)) {
        // 找到当前索引在颜色数组中的位置
        const currentPosInArray = colorPositions.indexOf(currentIndex);

        // 计算下一个同色格子的索引（循环）
        const nextIndexInArray = (currentPosInArray + 1) % colorPositions.length;
        const nextColorIndex = colorPositions[nextIndexInArray];

        // 计算飞行目的地的position值
        const startIndex = PATH_START_INDEX[color];
        let destPosition;

        if (nextColorIndex >= startIndex) {
            destPosition = nextColorIndex - startIndex + 1;
        } else {
            // 目的地在循环之前，需要加上52
            destPosition = (52 - startIndex) + nextColorIndex + 1;
        }

        // 确保不会飞到终点通道内（终点通道不能触发颜色飞跃）
        if (isInFinishStretch(color, destPosition)) {
            return { shouldFly: false };
        }

        return {
            shouldFly: true,
            newPosition: destPosition,
            flyType: 'color'
        };
    }

    return { shouldFly: false };
}

// 检查击落
function checkCapture(color, pieceIndex) {
    const piece = gameState.pieces[color][pieceIndex];
    if (piece.position === -1 || piece.finished) return;

    // 终点通道上的棋子不能被击落
    if (isInFinishStretch(color, piece.position)) return;

    const currentIndex = getMainPathIndex(color, piece.position);

    // 只检查参与游戏的玩家
    gameState.activePlayers.forEach(playerColor => {
        if (playerColor === color) return;

        gameState.pieces[playerColor].forEach((otherPiece, idx) => {
            if (otherPiece.position === -1 || otherPiece.finished) return;

            // 起飞点的棋子不能被击落
            if (otherPiece.position === 0) return;

            // 终点通道上的棋子不能被击落
            if (isInFinishStretch(playerColor, otherPiece.position)) return;

            const otherIndex = getMainPathIndex(playerColor, otherPiece.position);

            if (currentIndex === otherIndex) {
                otherPiece.position = -1;
                movePieceTo(playerColor, idx, false);
                updateMessage(`${COLORS[color].emoji}${gameSettings.language === 'en' ? 'captured' : '击落了'}${COLORS[playerColor].emoji}！`);
                updatePiecesStatus();
            }
        });
    });
}

// 检查获胜
function checkWin(color) {
    const allFinished = gameState.pieces[color].every(p => p.finished);
    if (allFinished) {
        gameState.gameOver = true;
        const winMsg = gameSettings.language === 'en'
            ? `🎊 ${COLORS[color].emoji}player wins! 🎊`
            : `🎊 ${COLORS[color].emoji}玩家获胜！🎊`;
        updateMessage(winMsg);

        // 禁用所有色子按钮
        gameState.activePlayers.forEach(playerColor => {
            const rollBtn = document.getElementById(`rollBtn-${playerColor}`);
            if (rollBtn) rollBtn.disabled = true;
        });

        document.querySelectorAll(`.piece.${color}`).forEach(p => {
            p.style.animation = 'pulse 0.5s infinite';
        });
    }
}

// 启用当前玩家的色子按钮
function enableCurrentPlayerDice() {
    const currentColor = getCurrentPlayerColor();
    const rollBtn = document.getElementById(`rollBtn-${currentColor}`);
    if (rollBtn) rollBtn.disabled = false;
}

// 下一个玩家
function nextPlayer() {
    if (gameState.gameOver) return;

    // 移动到下一个玩家（只在 activePlayers 中循环）
    gameState.currentPlayerIndex = (gameState.currentPlayerIndex + 1) % gameState.activePlayers.length;
    gameState.diceRolled = false;

    updateCurrentPlayer();
    updatePiecesStatus();

    const currentColor = getCurrentPlayerColor();
    const turnMsg = gameSettings.language === 'en'
        ? `Turn: ${COLORS[currentColor].emoji}`
        : `轮到${COLORS[currentColor].emoji}玩家`;
    updateMessage(turnMsg);

    // 启用当前玩家的色子按钮
    enableCurrentPlayerDice();
}

// 更新当前玩家显示
function updateCurrentPlayer() {
    const currentColor = getCurrentPlayerColor();

    // 移除所有面板的 active 状态
    ALL_COLORS.forEach(color => {
        const panel = document.getElementById(`panel-${color}`);
        if (panel) panel.classList.remove('active');
    });

    // 激活当前玩家的面板
    const currentPanel = document.getElementById(`panel-${currentColor}`);
    if (currentPanel) currentPanel.classList.add('active');
}

// 更新消息
function updateMessage(msg) {
    document.getElementById('gameMessage').textContent = msg;
}

// 更新棋子状态
function updatePiecesStatus() {
    // 只更新参与游戏的玩家
    gameState.activePlayers.forEach(color => {
        const container = document.getElementById(color + 'Pieces');
        if (!container) return;

        const pieces = gameState.pieces[color];
        let html = '';

        pieces.forEach((piece, index) => {
            let statusClass = 'home';
            let statusText = '●';

            if (piece.finished) {
                statusClass = 'finished';
                statusText = '✓';
            } else if (piece.position > -1) {
                statusClass = 'active';
                statusText = '◆';
            }

            html += `<span class="piece-dot ${statusClass}" style="background: ${COLORS[color].main};">${statusText}</span>`;
        });

        container.innerHTML = html;
    });
}

// 初始化游戏
function initGame() {
    updateLanguage();  // 更新界面语言
    initializePieces();
    updateCurrentPlayer();
    updatePiecesStatus();

    // 更新初始消息
    const initialMsg = gameSettings.language === 'en' ? 'Game started!' : '游戏开始！';
    updateMessage(initialMsg);
}

// 玩家选择逻辑
let selectedPlayerConfig = null;

// 初始化玩家选择界面
function initPlayerSelect() {
    const buttons = document.querySelectorAll('.player-option-btn');
    const startBtn = document.getElementById('startGameBtn');
    const languageSelect = document.getElementById('languageSelect');
    const pieceStyleSelect = document.getElementById('pieceStyleSelect');

    // 玩家选择按钮事件
    buttons.forEach(btn => {
        btn.addEventListener('click', () => {
            // 移除其他按钮的选中状态
            buttons.forEach(b => b.classList.remove('selected'));
            // 添加当前按钮的选中状态
            btn.classList.add('selected');

            // 保存选择的玩家配置
            const colors = btn.dataset.colors.split(',');
            selectedPlayerConfig = {
                count: parseInt(btn.dataset.players),
                colors: colors
            };

            // 启用开始按钮
            startBtn.disabled = false;
        });
    });

    // 语言切换事件
    languageSelect.addEventListener('change', (e) => {
        gameSettings.language = e.target.value;
        updateLanguage();
    });

    // 棋子样式切换事件
    pieceStyleSelect.addEventListener('change', (e) => {
        gameSettings.pieceStyle = e.target.value;
    });
}

// 更新界面语言
function updateLanguage() {
    // 更新标题
    const gameTitle = document.getElementById('gameTitle');
    if (gameTitle) gameTitle.textContent = t('gameTitle');

    // 更新提示文本
    const selectPlayersText = document.getElementById('selectPlayersText');
    if (selectPlayersText) selectPlayersText.textContent = t('selectPlayers');

    const settingsTitle = document.getElementById('settingsTitle');
    if (settingsTitle) settingsTitle.textContent = t('settings');

    const languageLabel = document.getElementById('languageLabel');
    if (languageLabel) languageLabel.textContent = t('language') + ':';

    const pieceStyleLabel = document.getElementById('pieceStyleLabel');
    if (pieceStyleLabel) pieceStyleLabel.textContent = t('pieceStyle') + ':';

    // 更新开始按钮
    const startBtn = document.getElementById('startGameBtn');
    if (startBtn) startBtn.textContent = t('startGame');

    // 更新玩家选择按钮
    const playerBtns = document.querySelectorAll('.player-option-btn');
    playerBtns.forEach(btn => {
        const playerCount = parseInt(btn.dataset.players);
        const colors = btn.dataset.colors;
        let mainText, subText;
        let emojis = '';

        // 提取现有的emoji
        const currentContent = btn.innerHTML;
        const emojiMatch = currentContent.match(/[🔴🟡🔵🟢]+/g);
        if (emojiMatch) {
            emojis = emojiMatch[0];
        }

        if (playerCount === 2) {
            mainText = t('twoPlayer');
            if (colors === 'red,yellow') {
                subText = gameSettings.language === 'en' ? 'Red vs Yellow' : '红色 vs 黄色';
            } else if (colors === 'blue,green') {
                subText = gameSettings.language === 'en' ? 'Blue vs Green' : '蓝色 vs 绿色';
            }
        } else if (playerCount === 3) {
            mainText = t('threePlayer');
            subText = gameSettings.language === 'en' ? 'Red, Yellow, Blue' : '红黄蓝';
        } else if (playerCount === 4) {
            mainText = t('fourPlayer');
            subText = t('allPlayers');
        }

        // 更新按钮文本
        btn.innerHTML = `${emojis} ${mainText}<br><small>${subText}</small>`;
    });

    // 更新玩家面板
    ALL_COLORS.forEach(color => {
        const panel = document.getElementById(`panel-${color}`);
        if (panel) {
            const h3 = panel.querySelector('h3');
            const btn = panel.querySelector('.roll-dice-btn');
            if (h3) h3.textContent = t(color + 'Player');
            if (btn) btn.textContent = t('rollDice');
        }
    });

    // 更新游戏消息
    const gameMessage = document.getElementById('gameMessage');
    if (gameMessage && gameMessage.textContent === '选择玩家开始游戏！') {
        gameMessage.textContent = t('selectPlayer');
    }
}

// 开始游戏
function startGame() {
    if (!selectedPlayerConfig) return;

    // 设置参与游戏的玩家
    gameState.activePlayers = selectedPlayerConfig.colors;

    // 隐藏玩家选择界面
    const overlay = document.getElementById('playerSelectOverlay');
    overlay.style.display = 'none';

    // 初始化游戏
    initGame();
}

// 启动
initPlayerSelect();
