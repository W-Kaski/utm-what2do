y<template>
  <section class="map-page">
    <header class="hero">
      <div class="search-shell">
        <div class="search-bar">
          <span class="icon">🔍</span>
          <input
              v-model="searchTerm"
              type="search"
              placeholder="Search events around campus"
              @keyup.enter="handleSearch"
          />
          <button type="button" @click="handleSearch">搜索</button>
        </div>
        <div class="chip-row">
          <button
              v-for="filter in filters"
              :key="filter.id"
              type="button"
              :class="['chip', { active: activeFilter === filter.id }]"
              @click="setFilter(filter.id)"
          >
            {{ filter.label }}
          </button>
          <button type="button" class="chip outline" @click="locateUser">实时定位</button>
        </div>
      </div>
      <p class="hero-hint">
        数据由后台提供：建筑聚合、活动清单、社团信息以及地理坐标映射。当前示例为静态预览。
      </p>
    </header>

    <div class="map-layout">
      <div ref="mapRef" class="map-view"></div>
      <aside class="side-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">建筑热点</p>
            <h2>校园活动气泡</h2>
            <span>{{ filteredBuildings.length }} 个分布</span>
          </div>
        </div>
        <div class="building-list">
          <ul>
            <li
                v-for="building in visibleBuildings"
                :key="building.id"
                :class="{ active: building.id === selectedBuilding?.id }"
                @click="focusBuilding(building)"
            >
              <div class="badge">{{ building.events.length }}</div>
              <div class="building-meta">
                <strong>{{ building.name }}</strong>
                <span>{{ building.events.length }} 场活动 · {{ tagLabel(building.tag) }}</span>
              </div>
            </li>
          </ul>
        </div>

        <div class="event-list">
          <header>
            <p class="eyebrow">
              Events @
              <span v-if="selectedBuilding">{{ selectedBuilding.name }}</span>
              <span v-else>Campus</span>
            </p>
            <button type="button" @click="openBuildingEvents">查看全部</button>
          </header>
          <div class="event-cards">
            <article
                v-for="evt in visibleEvents"
                :key="evt.id"
                @click="openEventDetail(evt.id)"
            >
              <h3>{{ evt.title }}</h3>
              <p>{{ evt.time }}</p>
              <span>{{ evt.club }}</span>
            </article>
            <div
                v-for="slot in placeholderSlots"
                :key="`placeholder-${slot}`"
                class="event-placeholder"
            ></div>
          </div>
        </div>
      </aside>
    </div>
  </section>
</template>

<script setup>
import {computed, onBeforeUnmount, onMounted, ref, watch} from 'vue';
import {useRouter} from 'vue-router';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

import {campusBuildings, filterTags} from '@/data/campusMap';

import iconRetinaUrl from 'leaflet/dist/images/marker-icon-2x.png';
import iconUrl from 'leaflet/dist/images/marker-icon.png';
import shadowUrl from 'leaflet/dist/images/marker-shadow.png';

L.Icon.Default.mergeOptions({
  iconRetinaUrl,
  iconUrl,
  shadowUrl
});

const mapRef = ref(null);
const mapInstance = ref(null);
const router = useRouter();

const searchTerm = ref('');
const activeFilter = ref('all');
const filters = filterTags;

const selectedBuilding = ref(null);
const markers = ref([]);
const userMarker = ref(null);

const focusQuery = ref(null);

const filteredBuildings = computed(() => {
  const term = searchTerm.value.trim().toLowerCase();
  return campusBuildings.filter((building) => {
    const matchesTag = activeFilter.value === 'all' ? true : building.tag === activeFilter.value;
    const matchesSearch =
        !term ||
        building.name.toLowerCase().includes(term) ||
        building.events.some((evt) => evt.title.toLowerCase().includes(term));
    return matchesTag && matchesSearch;
  });
});

const maxVisibleEvents = 3;
const visibleBuildings = computed(() => filteredBuildings.value.slice(0, 5));
const visibleEvents = computed(() =>
    selectedBuilding.value ? selectedBuilding.value.events.slice(0, maxVisibleEvents) : []
);
const placeholderSlots = computed(() =>
    Math.max(0, maxVisibleEvents - visibleEvents.value.length)
);

const tagLabel = (tagId) => {
  return filters.find((filter) => filter.id === tagId)?.label || '活动';
};

const handleSearch = () => {
  if (!searchTerm.value) return;
  const firstMatch = filteredBuildings.value[0];
  if (firstMatch) {
    focusBuilding(firstMatch);
  }
};

const setFilter = (id) => {
  activeFilter.value = id;
};

const createBubbleIcon = (count, isActive) =>
    L.divIcon({
      className: 'bubble-icon',
      html: `<div class="bubble ${isActive ? 'bubble--active' : ''}">${count}</div>`,
      iconSize: [46, 46],
      iconAnchor: [23, 46]
    });

const renderMarkers = () => {
  if (!mapInstance.value) return;
  markers.value.forEach((marker) => marker.remove());
  markers.value = filteredBuildings.value.map((building) => {
    const icon = createBubbleIcon(building.events.length, building.id === selectedBuilding.value?.id);
    const marker = L.marker(building.coords, {icon});
    marker.on('click', () => focusBuilding(building));
    marker.addTo(mapInstance.value);
    return marker;
  });
};

const focusBuilding = (building) => {
  selectedBuilding.value = building;
  if (mapInstance.value) {
    mapInstance.value.flyTo(building.coords, 17, {duration: 0.8});
  }
  renderMarkers();
};

const openBuildingEvents = () => {
  router.push({name: 'events', query: selectedBuilding.value ? {search: selectedBuilding.value.name} : {}});
};

const openEventDetail = (eventId) => {
  router.push({
    name: 'event-detail',
    params: { id: eventId },
    query: selectedBuilding.value?.id ? { from: 'map', focus: selectedBuilding.value.id } : { from: 'map' }
  });
};

const locateUser = () => {
  if (!navigator.geolocation || !mapInstance.value) return;
  navigator.geolocation.getCurrentPosition(
      (pos) => {
        const coords = [pos.coords.latitude, pos.coords.longitude];
        if (!userMarker.value) {
          userMarker.value = L.marker(coords, {
            icon: L.divIcon({
              className: 'user-dot',
              html: '<div class="user-marker"></div>',
              iconSize: [20, 20],
              iconAnchor: [10, 10]
            })
          }).addTo(mapInstance.value);
        } else {
          userMarker.value.setLatLng(coords);
        }
        mapInstance.value.flyTo(coords, 17);
      },
      () => {
        // fallback silently
      }
  );
};

onMounted(() => {
  const {focus} = router.currentRoute.value.query;
  focusQuery.value = focus;

  mapInstance.value = L.map(mapRef.value, {
    center: [43.5478, -79.6633],
    zoom: 16,
    zoomControl: false
  });

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap'
  }).addTo(mapInstance.value);

  renderMarkers();
  selectedBuilding.value = filteredBuildings.value[0] || null;

  if (focusQuery.value) {
    const target = campusBuildings.find((b) => b.id === focusQuery.value);
    if (target) {
      focusBuilding(target);
    }
  }
});

onBeforeUnmount(() => {
  if (mapInstance.value) {
    mapInstance.value.remove();
  }
});

watch([filteredBuildings], () => {
  renderMarkers();
});

watch(
    () => selectedBuilding.value,
    () => {
      renderMarkers();
    }
);
</script>

<style scoped>
.map-page {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.hero {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.search-shell {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.25rem;
  box-shadow: 0 18px 35px rgba(15, 23, 42, 0.08);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: 1px solid rgba(148, 163, 184, 0.5);
  border-radius: 999px;
  padding: 0.35rem 0.75rem;
}

.search-bar .icon {
  font-size: 1.1rem;
}

.search-bar input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 1rem;
}

.search-bar button {
  border: none;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  font-weight: 600;
  padding: 0.4rem 1.2rem;
}

.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.75rem;
}

.chip {
  border: none;
  border-radius: 999px;
  padding: 0.35rem 0.9rem;
  background: rgba(37, 99, 235, 0.15);
  color: #1d4ed8;
  font-weight: 600;
}

.chip.active {
  background: #1d4ed8;
  color: #fff;
}

.chip.outline {
  background: transparent;
  border: 1px solid rgba(37, 99, 235, 0.3);
  color: #1d4ed8;
}

.hero-hint {
  margin: 0;
  font-size: 0.9rem;
  color: #64748b;
}

.map-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(320px, 0.8fr);
  gap: 1.5rem;
}

.map-view {
  border-radius: 1.5rem;
  overflow: hidden;
  height: 690px;
  box-shadow: 0 35px 55px rgba(15, 23, 42, 0.12);
}

.side-panel {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.25rem;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1rem;
  height: 690px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 0.7rem;
  color: #94a3b8;
  margin: 0;
}

.panel-header h2 {
  margin: 0.25rem 0 0;
}

.building-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 0.25rem;
}

.building-list ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.building-list li {
  display: flex;
  gap: 0.85rem;
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 1rem;
  padding: 0.75rem;
  cursor: pointer;
  align-items: center;
  transition: border 0.2s ease, background 0.2s ease;
}

.building-list li.active {
  border-color: rgba(37, 99, 235, 0.4);
  background: rgba(37, 99, 235, 0.05);
}

.badge {
  width: 36px;
  height: 36px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: #1d4ed8;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.building-meta strong {
  display: block;
  color: #0f172a;
}

.building-meta span {
  color: #94a3b8;
  font-size: 0.85rem;
}

.event-list header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.event-list header button {
  border: none;
  background: transparent;
  color: #2563eb;
  font-weight: 600;
}

.event-cards {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-height: 315px;
  overflow: hidden;
}

.event-cards article {
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 1rem;
  padding: 0.85rem;
  cursor: pointer;
  transition: border 0.2s ease, transform 0.2s ease;
  min-height: 92px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.event-cards article:hover {
  border-color: rgba(37, 99, 235, 0.4);
  transform: translateY(-1px);
}

.event-placeholder {
  border: 1px solid transparent;
  border-radius: 1rem;
  min-height: 92px;
}

.event-cards h3 {
  margin: 0 0 0.2rem;
  font-size: 1rem;
}

.event-cards p {
  margin: 0;
  color: #475569;
}

.event-cards span {
  font-size: 0.85rem;
  color: #94a3b8;
}

:global(.bubble-icon .bubble) {
  background: #fff;
  border-radius: 999px;
  border: 3px solid #2563eb;
  color: #2563eb;
  font-weight: 700;
  width: 46px;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 25px rgba(37, 99, 235, 0.35);
}

:global(.bubble-icon .bubble--active) {
  background: #2563eb;
  color: #fff;
}

:global(.user-marker) {
  width: 16px;
  height: 16px;
  border-radius: 999px;
  background: #1d4ed8;
  border: 3px solid #fff;
  box-shadow: 0 0 0 6px rgba(59, 130, 246, 0.2);
}

@media (max-width: 900px) {
  .map-layout {
    grid-template-columns: 1fr;
  }

  .map-view {
    height: 420px;
  }
}
</style>
