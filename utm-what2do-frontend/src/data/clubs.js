const cover =
  'https://images.unsplash.com/photo-1523580846011-d3a5bc25702b?auto=format&fit=crop&w=1600&q=80';

export const clubCategories = [
  { id: 'academic', label: 'Academic', description: 'Research circles, book clubs, academic discussions' },
  { id: 'culture', label: 'Culture', description: 'Multicultural programs & language exchanges' },
  { id: 'arts', label: 'Arts', description: 'Music, theatre, visual arts' },
  { id: 'sports', label: 'Sports', description: 'Athletics competitions and healthy living' },
  { id: 'community', label: 'Community service', description: 'Volunteer and impact initiatives' },
  { id: 'interests', label: 'Interests', description: 'Interest groups and makerspaces' },
  { id: 'career', label: 'Career', description: 'Career development and workplace skills' }
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
    tags: ['Engineering', 'Data', 'Community'],
    stats: { posts: 84, members: 62, events: 18 },
    posts: [
      {
        id: 'post-1',
        title: 'Wind tunnel open house volunteer call',
        preview: 'Help us with next week\'s wind tunnel tests in the innovation lab...',
        time: '3 hours ago'
      },
      {
        id: 'post-2',
        title: 'Montreal Grand Prix debrief session',
        preview: 'Breaking down race strategy, safety-car windows, and tire management.',
        time: 'Yesterday'
      }
    ],
    events: [
      {
        id: 'event-1',
        title: 'F1 data modeling workshop',
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
      title: 'Summer sim racing league',
      detail: 'Register by 8/5; the champion wins a Toronto Motorsports Park experience pass.',
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
    blurb: 'Reimagining club life with concept art workshops, voice acting, and short-form animation labs.',
    tags: ['Illustration', 'Voice acting', 'Animation'],
    stats: { posts: 121, members: 210, events: 36 },
    posts: [
      {
        id: 'post-3',
        title: 'Summer character call',
        preview: 'Send in original character concepts; winners receive limited merch drops.',
        time: '5 hours ago'
      },
      {
        id: 'post-4',
        title: 'Voice acting bootcamp mentor search',
        preview: 'Looking for peers to share vocal tips and finish a short-play dubbing session.',
        time: 'Yesterday'
      }
    ],
    events: [
      {
        id: 'event-2',
        title: 'Character design hackathon',
        date: '2024-08-12',
        location: 'CCIT Studio'
      },
      {
        id: 'event-3',
        title: 'Fan art pop-up',
        date: '2024-08-22',
        location: 'Student Centre'
      }
    ],
    members: [
      { id: 'member-4', name: 'Mika', role: 'Creative Director' },
      { id: 'member-5', name: 'Aaron', role: 'Events Lead' },
      { id: 'member-6', name: 'Suri', role: 'Community Manager' }
    ],
    newestEvent: {
      title: 'Dungeons & Dragons Cos Night',
      detail: 'Cross-club collab encouraging original storytelling performances and photography.',
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
    blurb: 'Using GIS, drone data, and open platforms to make campus and city spaces easier to read.',
    tags: ['GIS', 'Environment', 'Open data'],
    stats: { posts: 65, members: 74, events: 22 },
    posts: [
      {
        id: 'post-5',
        title: 'Mississauga rapid transit visualization launch',
        preview: 'Recruiting data analytics and visualization volunteers to help deliver the showcase.',
        time: 'Today'
      }
    ],
    events: [
      {
        id: 'event-4',
        title: 'OpenStreetMap campus edit night',
        date: '2024-08-05',
        location: 'Library Collaboratory'
      }
    ],
    members: [
      { id: 'member-7', name: 'Harper', role: 'Project Manager' },
      { id: 'member-8', name: 'Zoey', role: 'GIS Analyst' }
    ],
    newestEvent: {
      title: 'UTM drone aerial capture lab',
      detail: 'Partnering with Engineering to cover flight safety plus hands-on practice.',
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
    blurb: 'Tabletop role-playing and storytelling community that welcomes new players and veteran Dungeon Masters alike.',
    tags: ['Tabletop', 'Creative writing', 'Social'],
    stats: { posts: 121, members: 53, events: 36 },
    posts: [
      {
        id: 'post-6',
        title: 'Looking for players for this week\'s quest',
        preview: 'Limited seats, so lock in your character sheet by Wednesday.',
        time: '1 hour ago'
      }
    ],
    events: [
      {
        id: 'event-5',
        title: 'Dungeon Master bootcamp',
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
      detail: 'Join our weekly gathering for snacks plus a new-player walkthrough.',
      date: '2024-07-25'
    }
  }
];

export const findClubById = (id) => clubs.find((club) => club.id === id);
