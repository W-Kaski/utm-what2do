import { defineStore } from 'pinia';
import apiClient from '@/api';

const mockEvents = [
  {
    id: '1',
    title: 'Weaving Connections: Extractive Colonialism in Africa',
    description: '第二场 “Weaving Connections” 系列，聚焦环境正义与资源开采的关系。',
    longDescription:
      "We're excited to announce the second part of our series, 'Weaving Connections' — this time discussing environmental injustice in Africa and how imperialism and resource extraction play roles in societal injustice. Register with the QR code or bio link and stay tuned for our amazing speakers!",
    date: '2024-11-07',
    startTime: '15:00',
    endTime: '18:00',
    club: 'SAGE UTM',
    clubId: 'uni-mapping',
    tags: ['Sign-up needed', 'In person', 'Cultural'],
    location: 'DV 3130',
    locationId: 'dv',
    coverImage:
      'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80',
    registration: [
      'Sign up by scanning the QR code on the poster',
      'Or use the link in bio @sage.utm (Instagram)'
    ],
    organizer: {
      name: 'SAGE UTM × Sahara African Students Club',
      description:
        'Hosted by SAGE UTM (UTM Student Association for Geography & Environment) and Sahara African Students Club.',
      avatar:
        'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=200&q=80',
      clubId: 'uni-mapping',
      contact: '@sage.utm'
    }
  },
  {
    id: '2',
    title: '湖畔音乐会',
    description: '在湖边享受现场音乐、美食与夏夜微风。',
    longDescription:
      '加入我们在南校区湖畔举办的沉浸式音乐之夜，集合原声乐队与 DJ 联合演出。活动提供野餐席位与艺术装置，欢迎携友前来，感受夏日氛围。',
    date: '2024-07-25',
    startTime: '18:30',
    endTime: '21:00',
    club: 'UTM Music Society',
    clubId: 'anime-club',
    tags: ['音乐', '社交', 'Outdoor'],
    location: 'South Field',
    locationId: 'dv-field',
    coverImage:
      'https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=1200&q=80',
    registration: ['免费入场，请提前预约席位', '欢迎携带野餐垫与可重复使用的杯具'],
    organizer: {
      name: 'UTM Music Society',
      description: '学生自发的音乐社群，策划开放麦、乐队联演与音乐工作坊。',
      avatar:
        'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80',
      clubId: 'anime-club',
      contact: '@utmmusic'
    }
  }
];

export const useEventStore = defineStore('events', {
  state: () => ({
    events: mockEvents,
    selectedEvent: null,
    loading: false,
    error: null
  }),
  actions: {
    async fetchEvents(params = {}) {
      this.loading = true;
      this.error = null;
      try {
        const { data } = await apiClient.get('/events', { params });
        if (data?.events) {
          this.events = data.events;
        }
      } catch (err) {
        this.error = err;
      } finally {
        this.loading = false;
      }
    },
    selectEvent(id) {
      this.selectedEvent = this.events.find((event) => event.id === id) || null;
    }
  }
});
