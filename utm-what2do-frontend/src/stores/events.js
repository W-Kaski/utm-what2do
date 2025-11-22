import { defineStore } from 'pinia';
import { eventsService } from '@/api/events';

const mockEvents = [
  {
    id: '1',
    title: 'Weaving Connections: Extractive Colonialism in Africa',
    description: 'The second "Weaving Connections" session centers on environmental justice and resource extraction.',
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
    title: 'Lakeside Music Night',
    description: 'Enjoy live music, great food, and the summer breeze beside the lake.',
    longDescription:
      'Join us on South Field for an immersive evening of music featuring acoustic bands and DJ sets. Picnic seating and art installations are available, so bring friends and soak up the summer vibe.',
    date: '2024-07-25',
    startTime: '18:30',
    endTime: '21:00',
    club: 'UTM Music Society',
    clubId: 'anime-club',
    tags: ['Music', 'Social', 'Outdoor'],
    location: 'South Field',
    locationId: 'dv-field',
    coverImage:
      'https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=1200&q=80',
    registration: ['Free entry - please reserve your spot in advance.', 'Feel free to bring picnic blankets and reusable cups.'],
    organizer: {
      name: 'UTM Music Society',
      description: 'Student-led music community curating open mics, band collaborations, and music workshops.',
      avatar:
        'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80',
      clubId: 'anime-club',
      contact: '@utmmusic'
    }
  }
];

export const useEventStore = defineStore('events', {
  state: () => ({
    events: [],
    selectedEvent: null,
    loading: false,
    error: null,
    pagination: {
      page: 1,
      pageSize: 10,
      total: 0
    }
  }),
  getters: {
    getEventById: (state) => (id) => state.events.find((event) => event.id === id)
  },
  actions: {
    async fetchEvents(params = {}) {
      this.loading = true;
      this.error = null;
      try {
        const response = await eventsService.getEvents(params);
        if (response?.data) {
          this.events = response.data.records || response.data;
          if (response.data.total) {
            this.pagination.total = response.data.total;
          }
        }
      } catch (err) {
        console.error('Failed to fetch events:', err);
        this.error = err.message || 'Failed to fetch events';
      } finally {
        this.loading = false;
      }
    },

    async fetchEventById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await eventsService.getEventById(id);
        if (response?.data) {
          this.selectedEvent = response.data;
          return response.data;
        }
      } catch (err) {
        console.error('Failed to fetch event:', err);
        this.error = err.message || 'Failed to fetch event';
      } finally {
        this.loading = false;
      }
      return this.selectedEvent;
    },

    async createEvent(eventData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await eventsService.createEvent(eventData);
        if (response?.data) {
          this.events.unshift(response.data);
          return response.data;
        }
      } catch (err) {
        console.error('Failed to create event:', err);
        this.error = err.message || 'Failed to create event';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async updateEvent(id, eventData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await eventsService.updateEvent(id, eventData);
        if (response?.data) {
          const index = this.events.findIndex(e => e.id === id);
          if (index !== -1) {
            this.events[index] = response.data;
          }
          return response.data;
        }
      } catch (err) {
        console.error('Failed to update event:', err);
        this.error = err.message || 'Failed to update event';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async deleteEvent(id) {
      this.loading = true;
      this.error = null;
      try {
        await eventsService.deleteEvent(id);
        this.events = this.events.filter(e => e.id !== id);
      } catch (err) {
        console.error('Failed to delete event:', err);
        this.error = err.message || 'Failed to delete event';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async likeEvent(id) {
      try {
        await eventsService.likeEvent(id);
        const event = this.events.find(e => e.id === id);
        if (event) {
          event.likesCount = (event.likesCount || 0) + 1;
          event.liked = true;
        }
      } catch (err) {
        console.error('Failed to like event:', err);
        throw err;
      }
    },

    selectEvent(id) {
      this.selectedEvent = this.events.find((event) => event.id === id) || null;
    },

    clearError() {
      this.error = null;
    }
  }
});
