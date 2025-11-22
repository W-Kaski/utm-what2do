# Frontend Codebase Audit Report
## UTM What2Do Frontend - Comprehensive Issue Analysis

Generated: 2025-11-22

---

## CRITICAL ISSUES

### 1. Response Data Extraction Mismatch (Response.data vs response.data.data)

**Issue**: Inconsistent response data structure handling across the codebase.

#### Location: `/home/user/utm-what2do/utm-what2do-frontend/src/api/auth.js` (Lines 5-15)
```javascript
async login(credentials) {
    const response = await apiClient.post('/users/login', credentials);
    if (response.data?.data?.token) {  // Line 11: Expects response.data.data.token
      localStorage.setItem('auth_token', response.data.data.token);
      localStorage.setItem('user_info', JSON.stringify(response.data.data.user));
    }
    return response.data;  // Line 15: Returns FULL response object
  }
```

**Problem**: 
- The API interceptor in `/home/user/utm-what2do/utm-what2do-frontend/src/api/index.js` (lines 28-37) returns the full response, but code assumes nested data structure
- When API returns successful response with code 200, the response object structure is: `{ code: 200, message, data: {...} }`
- However, auth.js returns `response.data` which is already the wrapped response object

#### Location: `/home/user/utm-what2do/utm-what2do-frontend/src/stores/user.js` (Lines 29, 67, 87)
```javascript
async login(credentials) {
    const response = await authService.login(credentials);
    if (response?.data?.user) {  // Line 29: Expects response.data.user
      this.setUser(response.data.user);
    }
    return response;
  }

async fetchCurrentUser() {
    const response = await authService.getCurrentUser();
    if (response?.data) {  // Line 67: Expects response.data
      this.setUser(response.data);
    }
  }

async updateProfile(profileData) {
    const response = await authService.updateProfile(profileData);
    if (response?.data) {  // Line 87: Expects response.data
      this.setUser(response.data);
    }
    return response;
  }
```

**Impact**: 
- User data extraction may fail or retrieve wrong data structure
- Login may succeed but user state not properly updated
- Profile updates may not reflect correct user data

**Fix**: Ensure consistent response structure handling. Either:
1. Have auth service unwrap the data: `return response.data.data || response.data`
2. Or have stores handle the wrapped structure: `this.setUser(response.data.data || response.data)`

---

### 2. Missing Loading State UI in Data-Fetching Pages

**Issue**: Stores have `loading` states, but pages don't display them to users.

#### Affected Pages:
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/EventsPage.vue` - No v-if loading
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/ClubsExplorePage.vue` - No v-if loading  
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/EventDetailPage.vue` - No loading indicator
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/PostDetailPage.vue` - No loading indicator
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/FeedPage.vue` - No loading indicator
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/HomePage.vue` - No loading states shown

**Problem**: 
- Users have no visual feedback while data is being fetched
- Stores track loading state but pages never reference it
- Example: FeedPage.vue calls `postStore.fetchPosts()` on mount (line 80) but never shows loading state
- EventsPage.vue calls `eventStore.fetchEvents()` (line 149) without displaying any loading indicator

**Example Code Gap**:
```javascript
// EventsPage.vue (Line 149)
onMounted(() => {
  syncFromQuery();
  eventStore.fetchEvents();  // No await, no loading state display
});
```

**Impact**: 
- Poor user experience with no feedback during network requests
- Users may think the app is frozen
- No visual indication of data loading status

**Fix**: 
```vue
<template>
  <div v-if="eventStore.loading" class="loading-spinner">
    Loading events...
  </div>
  <div v-else-if="eventStore.error" class="error-message">
    {{ eventStore.error }}
  </div>
  <div v-else>
    <!-- events content -->
  </div>
</template>
```

---

### 3. Error State Handling Issues

#### Location: Multiple stores and pages

**Problem 1**: Stores capture errors but pages don't display them

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/stores/posts.js` (Lines 34-36)
```javascript
async fetchPosts(params = {}) {
    this.loading = true;
    this.error = null;
    try {
      // ... fetch logic
    } catch (err) {
      console.error('Failed to fetch posts:', err);
      this.error = err.message || 'Failed to fetch posts';  // Error set but never shown
      this.posts = mockPosts;  // Falls back to mock data silently
    }
}
```

**Problem 2**: LoginPage accesses error incorrectly after store throws

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/pages/LoginPage.vue` (Line 136)
```javascript
} catch (err) {
    error.value = err.response?.data?.message || userStore.error || 'An error occurred';
    // err.response?.data?.message may not exist after interceptor transformation
}
```

**Impact**:
- API errors silently fail with fallback to mock data
- Error messages not shown to users
- Debugging becomes harder

**Fix**: 
1. Display error states in templates
2. Don't silently fallback to mock data in production
3. Standardize error structure in API interceptor

---

### 4. Optimistic Updates Without Error Recovery

**Issue**: State changes are made optimistically but not reverted on API failure.

#### Location: `/home/user/utm-what2do/utm-what2do-frontend/src/stores/posts.js` (Lines 63-83)
```javascript
async toggleLike(postId) {
    const post = this.posts.find((item) => item.id === postId);
    if (!post) return;

    try {
      if (post.liked) {
        await postsService.unlikePost(postId);
        post.liked = false;           // Optimistic update
        post.likes -= 1;              // Modified before API response
      } else {
        await postsService.likePost(postId);
        post.liked = true;
        post.likes += 1;
      }
    } catch (err) {
      console.error('Failed to toggle like:', err);
      // BUG: No revert of optimistic update
      post.liked = !post.liked;       // Line 80 just toggles again
      post.likes += post.liked ? 1 : -1;  // Incorrect recovery
    }
}
```

**Problem**: The error recovery (lines 80-81) doesn't correctly revert the original state change.

#### Location: `/home/user/utm-what2do/utm-what2do-frontend/src/stores/clubs.js` (Lines 67-93)
```javascript
async joinClub(clubId) {
    try {
      await clubsService.joinClub(clubId);
      const club = this.clubs.find(c => c.id === clubId);
      if (club) {
        club.membersCount = (club.membersCount || 0) + 1;  // Optimistic
        club.isJoined = true;
      }
    } catch (err) {
      console.error('Failed to join club:', err);
      // BUG: State not reverted on error
      throw err;
    }
}
```

**Impact**:
- UI state doesn't match server state after failures
- Offline support attempted but not properly implemented
- User sees inconsistent data

**Fix**: Store previous state before optimistic update, revert if error occurs:
```javascript
async toggleLike(postId) {
    const post = this.posts.find((item) => item.id === postId);
    if (!post) return;
    
    const previousLiked = post.liked;
    const previousCount = post.likes;

    try {
      post.liked = !post.liked;
      post.likes += post.liked ? 1 : -1;
      
      if (post.liked) {
        await postsService.likePost(postId);
      } else {
        await postsService.unlikePost(postId);
      }
    } catch (err) {
      // Revert on error
      post.liked = previousLiked;
      post.likes = previousCount;
      throw err;
    }
}
```

---

## HIGH PRIORITY ISSUES

### 5. Inconsistent Error Message Access

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/api/index.js` (Lines 28-37)
```javascript
apiClient.interceptors.response.use(
  (response) => {
    if (response.data && response.data.code !== undefined && response.data.code !== 200) {
      const error = new Error(response.data.message || 'Request failed');
      error.response = {
        data: response.data,
        status: response.status
      };
      return Promise.reject(error);
    }
    return response;
  },
```

**Problem**: 
- Error structure in interceptor doesn't match standard axios error structure
- Error.response.data is set to full response, not just error data
- Different error access patterns across pages

**Impact**:
- Inconsistent error handling in try-catch blocks
- err.response?.data?.message may not always work

**Fix**: Standardize error structure:
```javascript
const error = new Error(response.data.message || 'Request failed');
error.data = response.data;
error.statusCode = response.status;
error.code = response.data.code;
```

---

### 6. Missing Error Display Components in Pages

**Affected Pages**:
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/EventsPage.vue` - No error display
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/ClubsExplorePage.vue` - No error display
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/FeedPage.vue` - No error display

**Current Implementation** (EventsPage.vue lines 147-150):
```javascript
onMounted(() => {
  syncFromQuery();
  eventStore.fetchEvents();  // Errors silently handled
});
```

**Problem**: eventStore.error is captured but never displayed

**Fix**: Add error display in template:
```vue
<div v-if="eventStore.error" class="error-alert">
  <p>{{ eventStore.error }}</p>
  <button @click="eventStore.clearError">Dismiss</button>
</div>
```

---

### 7. Authentication Token Header Mismatch

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/api/index.js` (Line 17)
```javascript
if (token) {
  config.headers['satoken'] = token;  // Custom header name
}
```

**Problem**: 
- Header name 'satoken' must match backend expectation
- If backend expects 'Authorization' or 'X-Auth-Token', requests will fail with 401
- No validation that token is valid format

**Note**: This should be verified against backend implementation.

**Fix**: Use standard headers if possible:
```javascript
if (token) {
  config.headers['Authorization'] = `Bearer ${token}`;
}
```

---

### 8. Inconsistent Post/Comment Data Transformation

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/api/posts.js` (Lines 71-80)
```javascript
async getComments(postId) {
    const response = await apiClient.get(`/comments/post/${postId}`);
    if (response.data?.data) {
      if (Array.isArray(response.data.data)) {
        response.data.data = response.data.data.map(transformComment);  // Array
      } else if (response.data.data.records) {
        response.data.data.records = response.data.data.records.map(transformComment);  // Paginated
      }
    }
    return response.data;
}
```

**Problem**: 
- Code handles both array and paginated responses, but comment endpoints should return consistent format
- Transformation may not handle all response formats from backend

**Impact**: Comments may not display correctly depending on API response format

---

### 9. Missing Route Parameters Validation

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/pages/EventDetailPage.vue` (Line 134)
```javascript
const event = computed(() => eventStore.events.find((item) => item.id === route.params.id));
```

**Problem**: 
- If event not found in store, page shows "Event not found" but never tries to fetch it from API
- EventDetailPage should call `eventStore.fetchEventById(id)` on mount or if route.params.id changes
- Same issue in other detail pages

**Fix**: Add onMounted hook to fetch data if not in store:
```javascript
onMounted(() => {
  if (!event.value) {
    eventStore.fetchEventById(route.params.id);
  }
});
```

---

### 10. No Data Refetch on Route Parameter Change

**Location**: Multiple detail pages
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/EventDetailPage.vue`
- `/home/user/utm-what2do/utm-what2do-frontend/src/pages/ClubDetailPage.vue`

**Problem**: If user navigates between two detail pages (e.g., event 1 to event 2), pages don't fetch new data

**Example - EventDetailPage.vue**:
```javascript
const event = computed(() => eventStore.events.find((item) => item.id === route.params.id));
// No watch on route.params.id to refetch data
```

**Fix**: Add watcher:
```javascript
watch(() => route.params.id, (id) => {
  eventStore.fetchEventById(id);
});
```

---

### 11. Profile Update Error Handling Issue

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/pages/ProfilePage.vue` (Lines 124-138)
```javascript
const applyProfileChanges = async () => {
  try {
    await userStore.updateProfile({
      displayName: editableName.value,
      bio: editableBio.value
    });
    userStore.name = editableName.value;
    userStore.bio = editableBio.value;
  } catch (err) {
    // Still update locally for demo
    userStore.name = editableName.value;
    userStore.bio = editableBio.value;
  }
};
```

**Problem**: 
- Comment "Still update locally for demo" indicates code is not production-ready
- No error message shown to user
- Updates local state regardless of API success

**Impact**: User thinks profile was saved even if it failed

**Fix**: Only update on success, show error on failure:
```javascript
const applyProfileChanges = async () => {
  try {
    await userStore.updateProfile({
      displayName: editableName.value,
      bio: editableBio.value
    });
    showSuccessMessage('Profile updated successfully');
  } catch (err) {
    showErrorMessage(err.message || 'Failed to update profile');
    // Don't update local state on error
  }
};
```

---

## MEDIUM PRIORITY ISSUES

### 12. No Loading State for Initial Page Data

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/pages/HomePage.vue`
- Calls `eventStore.fetchEvents()` via component imports but doesn't show loading
- No visible indication while data is loading on first visit

**Impact**: Page may appear empty while loading

---

### 13. Fallback to Mock Data Masks Real Errors

**Location**: Multiple stores
- `/home/user/utm-what2do/utm-what2do-frontend/src/stores/events.js` (Line 94)
- `/home/user/utm-what2do/utm-what2do-frontend/src/stores/clubs.js` (Line 41)
- `/home/user/utm-what2do/utm-what2do-frontend/src/stores/buildings.js` (Line 34)

**Code Example**:
```javascript
async fetchEvents(params = {}) {
  try {
    const response = await eventsService.getEvents(params);
    // ...
  } catch (err) {
    console.error('Failed to fetch events:', err);
    this.error = err.message || 'Failed to fetch events';
    this.events = mockEvents;  // Silently falls back
  } finally {
    this.loading = false;
  }
}
```

**Problem**: 
- API failures are masked by mock data
- Users don't know they're seeing stale/demo data
- Makes debugging API issues harder
- In production, this could show incorrect information

**Impact**: Users see potentially outdated data without knowing

**Fix**: 
1. In development: show error message with option to retry
2. Don't automatically fall back to mock data in production
3. Add visual indicator that data is from cache/mock

---

### 14. Missing Cleanup in Event Listeners

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/pages/ProfilePage.vue` (Lines 114-122)
```javascript
const handleCoverChange = (event) => {
  const file = event.target.files?.[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = (e) => {
    userStore.setCoverImage(e.target?.result);
  };
  reader.readAsDataURL(file);
};
```

**Problem**: 
- FileReader onload handler not cleaned up if component unmounts
- Potential memory leak if many file reads are initiated

**Impact**: Minor memory issues, not critical

---

### 15. Unused or Incomplete Features

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/components/PostCard.vue` (Line 147)
```javascript
const handleRepost = () => {
  alert('Repost will connect to the compose window soon, stay tuned.');
};
```

**Problem**: Repost feature not implemented, shows alert placeholder

**Impact**: Feature appears to work but doesn't

---

### 16. No Refresh/Retry Mechanism

**Problem**: No way for users to retry failed API requests

**Affected Areas**:
- Failed event fetches
- Failed user profile loads
- Failed post creations

**Fix**: Add retry button in error states:
```vue
<div v-if="error" class="error-state">
  <p>{{ error }}</p>
  <button @click="retry">Try Again</button>
</div>
```

---

### 17. API Response Code Checking Issue

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/api/index.js` (Line 30)
```javascript
if (response.data && response.data.code !== undefined && response.data.code !== 200) {
  // Treat as error
}
```

**Problem**: 
- Only checks for code 200, but other codes might be valid (201 for created, 204 for no content)
- Code check is too strict

**Fix**: 
```javascript
if (response.data && response.data.code !== undefined && 
    (response.data.code < 200 || response.data.code >= 300)) {
  // Treat as error
}
```

---

### 18. PostDetailPage Missing Data Loading

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/pages/PostDetailPage.vue`
- Doesn't show loading state while fetching post
- Doesn't fetch if post not in store

---

### 19. No Debouncing on Search

**Location**: Search operations in EventsPage, potentially creating multiple API calls
- User typing in search creates immediate store updates
- Could benefit from debouncing to reduce API calls

---

### 20. Missing Environment Configuration

**Location**: `/home/user/utm-what2do/utm-what2do-frontend/src/api/index.js` (Line 4)
```javascript
baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
```

**Note**: Ensure `.env` files are properly configured for different environments

---

## SUMMARY TABLE

| Issue | Severity | File | Line(s) | Type |
|-------|----------|------|---------|------|
| Response data extraction mismatch | CRITICAL | auth.js | 5-15 | Data handling |
| Missing loading states in UI | CRITICAL | EventsPage.vue, etc. | 149+ | UX |
| Error states not displayed | CRITICAL | Multiple pages | Various | Error handling |
| Optimistic updates not reverted | CRITICAL | posts.js, clubs.js | 63-93 | State management |
| Inconsistent error message access | HIGH | api/index.js | 28-37 | Error handling |
| Missing error display in pages | HIGH | Multiple pages | Various | Error handling |
| Token header format | HIGH | api/index.js | 17 | Auth |
| Inconsistent data transformation | HIGH | posts.js | 71-80 | Data handling |
| Detail pages don't fetch on mount | HIGH | EventDetailPage.vue | 134 | Data loading |
| No refetch on route change | HIGH | Detail pages | Various | Navigation |
| Profile update silent failure | HIGH | ProfilePage.vue | 124-138 | UX |
| Mock data masks errors | MEDIUM | Multiple stores | Various | Error handling |
| No retry mechanism | MEDIUM | All error states | - | UX |
| API code check too strict | MEDIUM | api/index.js | 30 | API handling |
| Missing search debouncing | MEDIUM | EventsPage.vue | - | Performance |

---

## RECOMMENDATIONS

### Immediate Actions (Critical):
1. Fix response data extraction inconsistencies
2. Add loading state displays to all data-fetching pages
3. Add error message displays to all pages with error states
4. Fix optimistic update rollbacks
5. Verify authentication token header matches backend

### Short Term (High Priority):
1. Add retry mechanisms for failed requests
2. Implement proper error recovery
3. Add route parameter change watchers to detail pages
4. Standardize error structure
5. Add loading states to all user-waiting scenarios

### Long Term (Medium Priority):
1. Implement proper offline support (instead of silent fallback to mock data)
2. Add request debouncing for search
3. Consider implementing request cancellation
4. Add analytics/logging for error tracking
5. Add unit tests for error scenarios
6. Document API response formats

---

## Testing Recommendations

- Test all API failure scenarios
- Test loading states on slow networks
- Test error message displays
- Test navigation between detail pages
- Test authentication token expiration
- Test offline scenarios
- Verify token header format matches backend

