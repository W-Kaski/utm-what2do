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
      '48 hours left to register for Hack Sprint! This year\'s theme focuses on "AI + Campus" with overnight mentor support and an XR experience zone. Students from every major are welcome, just remember to bring your laptop.',
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
    tags: ['Trending', 'Hackathon'],
    commentsThread: [
      {
        id: 'c-1',
        author: {
          id: 'user-2',
          name: 'Zoey',
          avatar: 'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80'
        },
        content: 'Will the mentor list be posted? I want to get a sense of the themes ahead of time.',
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
            content: 'Yes! We are sharing the first mentor batch on IG tonight, so stay tuned.',
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
      'The second Weaving Connections event features guests working in sustainable agriculture and social justice. We are planning interactive roundtables and cannot wait to have you there!',
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
    tags: ['Official', 'Student support'],
    commentsThread: [
      {
        id: 'c-2',
        author: {
          id: 'user-4',
          name: 'Noah',
          avatar: 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=200&q=80'
        },
        content: 'Will we be able to access the presentation materials after registering?',
        createdAt: '2024-07-19T16:00:00Z',
        likes: 3,
        replies: []
      }
    ]
  }
];
