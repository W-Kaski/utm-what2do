export const mockPosts = [
  {
    id: 'post-1',
    author: {
      id: 'user-1',
      name: 'UTM Dev Club',
      avatar: 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=200&q=80',
      type: 'club',
      clubId: 'f1-paddock'
    },
    createdAt: '2024-07-20T08:30:00Z',
    content:
      'Hack Sprint 报名最后 48 小时！今年主题聚焦“AI + Campus”，提供整夜导师陪跑与 XR 体验区。欢迎所有专业的同学参加，记得自带电脑。',
    media: {
      type: 'images',
      items: [
        'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&q=80'
      ]
    },
    likes: 128,
    comments: 42,
    reposts: 9,
    liked: false,
    isFollowing: false,
    tags: ['热度', '黑客松'],
    commentsThread: [
      {
        id: 'c-1',
        author: {
          id: 'user-2',
          name: 'Zoey',
          avatar: 'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80'
        },
        content: '导师名单会公布吗？想提前了解主题方向～',
        createdAt: '2024-07-20T10:20:00Z',
        likes: 4,
        replies: [
          {
            id: 'c-1-r1',
            author: {
              id: 'user-1',
              name: 'UTM Dev Club',
              avatar: 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=200&q=80'
            },
            content: '会的！今晚在 IG 发布第一批导师，敬请关注。',
            createdAt: '2024-07-20T11:00:00Z',
            likes: 6
          }
        ]
      }
    ]
  },
  {
    id: 'post-2',
    author: {
      id: 'user-3',
      name: 'SAGE UTM',
      avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=200&q=80',
      type: 'club',
      clubId: 'uni-mapping'
    },
    createdAt: '2024-07-19T15:10:00Z',
    content:
      'Weaving Connections 第二场活动将邀请可持续农业与社会公正领域的嘉宾。我们将准备互动式圆桌讨论，期待你的参与！',
    media: {
      type: 'video',
      items: ['https://storage.googleapis.com/coverr-main/mp4/Mt_Baker.mp4'],
      thumbnail: 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=800&q=80'
    },
    likes: 203,
    comments: 58,
    reposts: 21,
    liked: true,
    isFollowing: true,
    tags: ['官方', '学校辅导'],
    commentsThread: [
      {
        id: 'c-2',
        author: {
          id: 'user-4',
          name: 'Noah',
          avatar: 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=200&q=80'
        },
        content: '请问报名后能获得讲座资料吗？',
        createdAt: '2024-07-19T16:00:00Z',
        likes: 3,
        replies: []
      }
    ]
  }
];
