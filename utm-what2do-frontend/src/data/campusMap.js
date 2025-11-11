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
        time: '11月7日 · 15:00',
        club: 'SAGE UTM'
      },
      {
        id: '2',
        title: 'AI Ethics Forum',
        time: '11月9日 · 10:00',
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
        time: '7月24日 · 13:00',
        club: 'Mapping Lab'
      },
      {
        id: '10',
        title: 'Cloud Native Crash Course',
        time: '7月28日 · 15:30',
        club: 'DevOps Circle'
      },
      {
        id: '11',
        title: 'Sustainability Talk',
        time: '8月3日 · 11:00',
        club: 'Green UTM'
      },
      {
        id: '12',
        title: 'Exam Prep Marathon',
        time: '8月5日 · 09:00',
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
      { id: '4', title: 'Sunset Concert', time: '7月25日 · 18:30', club: 'Music Society' },
      { id: '5', title: 'Board Game Night', time: '7月26日 · 19:00', club: 'Uni D&D Club' },
      { id: '14', title: 'Poetry Slam', time: '7月27日 · 17:00', club: 'Arts Collective' }
    ]
  },
  {
    id: 'dv-field',
    name: 'South Field',
    coords: [43.54612, -79.66462],
    tag: 'outdoor',
    events: [
      { id: '6', title: 'Lakeside Music Fest', time: '8月1日 · 19:00', club: 'Music Society' }
    ]
  },
  {
    id: 'ccit',
    name: 'Innovation Complex',
    coords: [43.54698, -79.6626],
    tag: 'tech',
    events: [
      { id: '7', title: 'Hack Sprint', time: '8月12日 · 09:00', club: 'Dev Club' },
      { id: '8', title: 'XR Lab Demo', time: '8月15日 · 14:00', club: 'XR Collective' },
      { id: '9', title: 'Product Jam', time: '8月20日 · 12:00', club: 'Entrepreneurship Hub' },
      { id: '13', title: 'AI Studio Showcase', time: '8月22日 · 16:00', club: 'AI Studio' }
    ]
  }
];

export const filterTags = [
  { id: 'all', label: '全部' },
  { id: 'academic', label: '学术' },
  { id: 'tech', label: '科技' },
  { id: 'social', label: '社交' },
  { id: 'outdoor', label: '户外' }
];
