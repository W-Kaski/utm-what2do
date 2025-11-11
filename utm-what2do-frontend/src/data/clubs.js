const cover =
  'https://images.unsplash.com/photo-1523580846011-d3a5bc25702b?auto=format&fit=crop&w=1600&q=80';

export const clubCategories = [
  { id: 'academic', label: 'Academic', description: '科研、读书会、学术讨论' },
  { id: 'culture', label: 'Culture', description: '多元文化 & 语言交流' },
  { id: 'arts', label: 'Arts', description: '音乐、戏剧、视觉艺术' },
  { id: 'sports', label: 'Sports', description: '体育竞赛与健康生活' },
  { id: 'community', label: 'Community service', description: '志愿、公益项目' },
  { id: 'interests', label: 'Interests', description: '兴趣小组与创客空间' },
  { id: 'career', label: 'Career', description: '职业发展与职场技能' }
];

export const clubs = [
  {
    id: 'f1-paddock',
    name: 'F1 Paddock Society',
    tagline: 'Speed, strategy, STEM.',
    category: 'academic',
    logo: 'https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&w=200&q=80',
    coverImage: cover,
    blurb: 'From aerodynamics to race analytics, we decode every lap with engineers and fans alike.',
    tags: ['工程', '数据', '社区'],
    stats: { posts: 84, members: 62, events: 18 },
    posts: [
      {
        id: 'post-1',
        title: '风洞实验室开放日招募志愿者',
        preview: '协助我们下周在创新实验室的风洞测试...',
        time: '3 小时前'
      },
      {
        id: 'post-2',
        title: '蒙特利尔大奖赛复盘分享会',
        preview: '拆解车队策略，讨论安全车窗口与轮胎管理。',
        time: '昨天'
      }
    ],
    events: [
      {
        id: 'event-1',
        title: 'F1 数据建模工作坊',
        date: '2024-07-30',
        location: 'Innovation Complex 210'
      }
    ],
    members: [
      { id: 'member-1', name: 'Jasper', role: 'President' },
      { id: 'member-2', name: 'Lynn', role: 'Data Lead' },
      { id: 'member-3', name: 'Farrah', role: 'Logistics' }
    ],
    newestEvent: {
      title: '夏季模拟赛车联赛',
      detail: '报名截止至 8/5，冠军可获得多伦多赛车场体验券。',
      date: '2024-08-08'
    }
  },
  {
    id: 'anime-club',
    name: 'Anime Creators Circle',
    tagline: 'Illustrate, animate, celebrate fandom.',
    category: 'arts',
    logo: 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=200&q=80',
    coverImage:
      'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1600&q=80',
    blurb: '翻转传统社团，结合原画创作、配音与短动画制作的沉浸式体验。',
    tags: ['插画', '配音', '动画'],
    stats: { posts: 121, members: 210, events: 36 },
    posts: [
      {
        id: 'post-3',
        title: '夏季主题角色征集',
        preview: '欢迎投稿原创角色设定，优胜者将发布限量周边。',
        time: '5 小时前'
      },
      {
        id: 'post-4',
        title: '配音体验营招募导师',
        preview: '寻找愿意分享配音技巧的同学，一起完成短剧配音。',
        time: '昨天'
      }
    ],
    events: [
      {
        id: 'event-2',
        title: '角色设计黑客松',
        date: '2024-08-12',
        location: 'CCIT Studio'
      },
      {
        id: 'event-3',
        title: '同人展快闪',
        date: '2024-08-22',
        location: 'Student Centre'
      }
    ],
    members: [
      { id: 'member-4', name: 'Mika', role: '创意总监' },
      { id: 'member-5', name: 'Aaron', role: '活动统筹' },
      { id: 'member-6', name: 'Suri', role: '社群运营' }
    ],
    newestEvent: {
      title: 'Dungeons & Dragons Cos Night',
      detail: '跨社团联动，鼓励原创剧情表演与摄影。',
      date: '2024-09-02'
    }
  },
  {
    id: 'uni-mapping',
    name: 'Uni Mapping Lab',
    tagline: 'Mapping smarter communities.',
    category: 'community',
    logo: 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=200&q=80',
    coverImage:
      'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1600&q=80',
    blurb: '通过 GIS、无人机数据与开放数据平台，帮助校园与城市更易读。',
    tags: ['GIS', '环境', '开放数据'],
    stats: { posts: 65, members: 74, events: 22 },
    posts: [
      {
        id: 'post-5',
        title: 'Mississauga 快速公交可视化项目启动',
        preview: '公开招募数据分析与可视化志愿者，一起完成成果展。',
        time: '今天'
      }
    ],
    events: [
      {
        id: 'event-4',
        title: 'OpenStreetMap 校园编辑夜',
        date: '2024-08-05',
        location: 'Library Collaboratory'
      }
    ],
    members: [
      { id: 'member-7', name: 'Harper', role: 'Project Manager' },
      { id: 'member-8', name: 'Zoey', role: 'GIS Analyst' }
    ],
    newestEvent: {
      title: 'UTM 无人机航拍体验',
      detail: '联合工程学院提供飞行安全培训与实操。',
      date: '2024-08-18'
    }
  },
  {
    id: 'uni-dnd',
    name: 'Uni D&D Club',
    tagline: 'Heroes assemble!',
    category: 'interests',
    logo: 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=200&q=80',
    coverImage:
      'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1600&q=80',
    blurb: '桌面角色扮演与故事创作社区，欢迎新手和资深 Dungeon Master。',
    tags: ['桌游', '创意写作', '社交'],
    stats: { posts: 121, members: 53, events: 36 },
    posts: [
      {
        id: 'post-6',
        title: '本周冒险招募新玩家',
        preview: '席位有限，请在周三前确认角色设定。',
        time: '1 小时前'
      }
    ],
    events: [
      {
        id: 'event-5',
        title: '地下城大师训练营',
        date: '2024-07-28',
        location: 'Student Centre 2F'
      }
    ],
    members: [
      { id: 'member-9', name: 'Eli', role: 'Dungeon Master' },
      { id: 'member-10', name: 'Rae', role: 'Community Host' }
    ],
    newestEvent: {
      title: 'Newest Event: Heroes Assemble',
      detail: '欢迎加入我们的周度聚会，提供零食与新手教程。',
      date: '2024-07-25'
    }
  }
];

export const findClubById = (id) => clubs.find((club) => club.id === id);
