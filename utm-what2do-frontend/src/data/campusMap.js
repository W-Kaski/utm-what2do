export const campusBuildings = [
  {
    id: 'dv',
    name: 'Deerfield Hall',
    coords: [43.54867, -79.66374],
    tag: 'academic',
    events: [
      {
        id: '1',
        title: 'Weaving Connections',
        time: 'Nov 7 · 15:00',
        club: 'SAGE UTM'
      },
      {
        id: '2',
        title: 'AI Ethics Forum',
        time: 'Nov 9 · 10:00',
        club: 'Dev Club'
      }
    ]
  },
  {
    id: 'ib',
    name: 'Instructional Centre',
    coords: [43.54805, -79.6621],
    tag: 'academic',
    events: [
      {
        id: '3',
        title: 'Data Viz Workshop',
        time: 'Jul 24 · 13:00',
        club: 'Mapping Lab'
      },
      {
        id: '10',
        title: 'Cloud Native Crash Course',
        time: 'Jul 28 · 15:30',
        club: 'DevOps Circle'
      },
      {
        id: '11',
        title: 'Sustainability Talk',
        time: 'Aug 3 · 11:00',
        club: 'Green UTM'
      },
      {
        id: '12',
        title: 'Exam Prep Marathon',
        time: 'Aug 5 · 09:00',
        club: 'Scholars Hub'
      }
    ]
  },
  {
    id: 'kn',
    name: 'Kaneff Centre',
    coords: [43.5467, -79.6633],
    tag: 'social',
    events: [
      { id: '4', title: 'Sunset Concert', time: 'Jul 25 · 18:30', club: 'Music Society' },
      { id: '5', title: 'Board Game Night', time: 'Jul 26 · 19:00', club: 'Uni D&D Club' },
      { id: '14', title: 'Poetry Slam', time: 'Jul 27 · 17:00', club: 'Arts Collective' }
    ]
  },
  {
    id: 'dv-field',
    name: 'South Field',
    coords: [43.54612, -79.66462],
    tag: 'outdoor',
    events: [
      { id: '6', title: 'Lakeside Music Fest', time: 'Aug 1 · 19:00', club: 'Music Society' }
    ]
  },
  {
    id: 'ccit',
    name: 'Innovation Complex',
    coords: [43.54698, -79.6626],
    tag: 'tech',
    events: [
      { id: '7', title: 'Hack Sprint', time: 'Aug 12 · 09:00', club: 'Dev Club' },
      { id: '8', title: 'XR Lab Demo', time: 'Aug 15 · 14:00', club: 'XR Collective' },
      { id: '9', title: 'Product Jam', time: 'Aug 20 · 12:00', club: 'Entrepreneurship Hub' },
      { id: '13', title: 'AI Studio Showcase', time: 'Aug 22 · 16:00', club: 'AI Studio' }
    ]
  }
];

export const filterTags = [
  { id: 'all', label: 'All' },
  { id: 'academic', label: 'Academic' },
  { id: 'tech', label: 'Tech' },
  { id: 'social', label: 'Social' },
  { id: 'outdoor', label: 'Outdoor' }
];
