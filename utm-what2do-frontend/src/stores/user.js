import { defineStore } from 'pinia';

const defaultCover =
  'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1600&q=80';
const defaultAvatar =
  'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80';

export const useUserStore = defineStore('user', {
  state: () => ({
    name: 'UTM Explorer',
    bio: '让灵感随时发生 · 探索校园、收藏喜爱的活动',
    coverImage: defaultCover,
    avatar: defaultAvatar,
    favorites: []
  }),
  actions: {
    toggleFavorite(eventId) {
      if (!eventId) return;
      if (this.favorites.includes(eventId)) {
        this.favorites = this.favorites.filter((id) => id !== eventId);
      } else {
        this.favorites.unshift(eventId);
      }
    },
    isFavorited(eventId) {
      return this.favorites.includes(eventId);
    },
    setCoverImage(dataUrl) {
      if (dataUrl) {
        this.coverImage = dataUrl;
      }
    }
  }
});
