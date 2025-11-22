# Comprehensive API Mismatch Report
## Frontend vs Backend Comparison Analysis

**Report Date:** 2025-11-22  
**Project:** utm-what2do  
**Scope:** All API endpoints and data structures

---

## EXECUTIVE SUMMARY

This analysis found **23+ major mismatches** between frontend API implementations and backend endpoints:

- **12 Missing Backend Endpoints** - Frontend calls APIs that don't exist
- **5 Path/Routing Mismatches** - Frontend and backend use different URL patterns  
- **15+ Field Name Mismatches** - Response fields have different names or structures
- **3 HTTP Method Mismatches** - Frontend uses wrong HTTP verbs for operations
- **2 Key Design Conflicts** - Unclear requirements (like vs interested, join vs follow)

---

## PART 1: MISSING BACKEND ENDPOINTS

### Category: Events API (3 missing)

#### 1. GET /buildings/{buildingId}/events
- **Frontend Call**: `events.js:40` (eventsService.getEventsByBuilding)
- **Backend Status**: ❌ Does not exist
- **Current Implementation**: None
- **Replacement**: None (must create new endpoint)
- **Expected Response**: Array of EventCardVO for a specific building

#### 2. GET /clubs/{clubId}/events  
- **Frontend Call**: `events.js:44` (eventsService.getEventsByClub)
- **Backend Status**: ❌ Does not exist
- **Current Implementation**: None
- **Replacement**: None (must create new endpoint)
- **Expected Response**: Array of EventCardVO for a specific club

#### 3. DELETE /events/{id}/like - Unlike event
- **Frontend Call**: `events.js:34-36`
- **Backend Status**: ❌ No DELETE method for likes
- **Current Implementation**: Only POST /events/{id}/interested exists
- **Issue**: Backend uses "interested" concept, frontend expects "like/unlike"
- **Conflict Type**: Design conflict - need to clarify semantics

---

### Category: Posts API (3 missing)

#### 4. PUT /posts/{id} - Update post
- **Frontend Call**: `posts.js:19-21`
- **Backend Status**: ❌ No PUT endpoint
- **Current Implementation**: Only GET, POST, DELETE, PIN available
- **Decision Needed**: Should posts be updatable? If yes, implement PUT endpoint

#### 5. DELETE /posts/{id}/like - Unlike post
- **Frontend Call**: `posts.js:34-36` (unlikePost)
- **Backend Status**: ❌ No DELETE method for likes
- **Current Implementation**: Only POST /posts/{id}/like exists
- **Fix**: Add DELETE support to PostController or use POST with toggle parameter

#### 6. POST /posts/{id}/repost - Repost functionality
- **Frontend Call**: `posts.js:54-57`
- **Backend Status**: ❌ Does not exist
- **Current Implementation**: None
- **Field Tracking**: Backend already has repostsCount in PostVO, just needs endpoint

---

### Category: Comment Routes (3 wrong paths + misaligned endpoints)

#### 7-8. Comment API Paths Mismatch
| Operation | Frontend Path | Backend Path | Status |
|-----------|---------------|--------------|--------|
| Get Comments | GET /posts/{postId}/comments | GET /comments/post/{postId} | ❌ Path mismatch |
| Create Comment | POST /posts/{postId}/comments | POST /comments | ❌ Path mismatch |
| Delete Comment | DELETE /posts/{postId}/comments/{commentId} | DELETE /comments/{id} | ❌ Path mismatch |

**Frontend Code**: `posts.js:39-51`  
**Backend Code**: `CommentController.java:34, 68, 80`  
**Fix**: Update frontend to use `/comments/*` paths instead of `/posts/{postId}/comments/*`

---

### Category: Club Management API (4 missing)

#### 9. POST /clubs, PUT /clubs/{id}, DELETE /clubs/{id}
- **Frontend Calls**: `clubs.js:14-26`
- **Backend Status**: ❌ Only GET endpoints exist
- **Current Implementation**: 
  - GET /clubs (all clubs)
  - GET /clubs/{slug} (by slug)
  - GET /clubs/id/{id} (by ID)
- **Missing CRUD Operations**: Create, Update, Delete

#### 10. GET /clubs/{clubId}/members - Get club members
- **Frontend Call**: `clubs.js:29-31`
- **Backend Status**: ❌ Does not exist
- **Expected Response**: Page<ClubMemberVO> or List<ClubMemberVO>

#### 11. POST /clubs/{clubId}/join, DELETE /clubs/{clubId}/leave
- **Frontend Calls**: `clubs.js:34-41` (joinClub, leaveClub)
- **Backend Status**: ❌ Does not exist
- **Related Backend**: FollowController has POST /follows/clubs/{clubId} (different concept)
- **Design Conflict**: Join (membership) vs Follow (subscription) - which is it?

#### 12. GET /clubs/{clubId}/posts
- **Frontend Call**: `clubs.js:44-45`
- **Backend Status**: ❌ Does not exist
- **Expected Response**: Page<PostVO> for a specific club

#### 13. GET /clubs/{clubId}/events (duplicate #2 but from different API)
- **Frontend Call**: `clubs.js:49-51`
- **Backend Status**: ❌ Does not exist
- **Expected Response**: List<EventCardVO> for a specific club

---

### Category: Follows API (1 major conflict)

#### 14. POST /follows/{userId}, DELETE /follows/{userId}
- **Frontend Calls**: `follows.js:4-11`
- **Backend Status**: ❌ Wrong implementation
- **Current Implementation**: 
  - POST /follows/clubs/{clubId} (follows clubs, not users)
  - DELETE /follows/clubs/{clubId}
- **Design Conflict**: Frontend expects user-follows, backend implements club-follows
- **Decision Needed**: Should app support following users, or only following clubs?

---

### Category: Buildings API (1 missing)

#### 15. GET /buildings/{buildingId}/events
- **Frontend Call**: `buildings.js:14-16`
- **Backend Status**: ❌ Does not exist
- **Current Implementation**: 
  - GET /buildings (all buildings)
  - GET /buildings/{id} (single building)
  - GET /buildings/counts (bubble counts)
- **Expected Response**: Array of events in a specific building

---

## PART 2: ENDPOINT PATH MISMATCHES

### 2.1 Club Retrieval Path Mismatch

| Aspect | Frontend | Backend | Issue |
|--------|----------|---------|-------|
| Path Pattern | `/clubs/{id}` | `/clubs/{slug}` or `/clubs/id/{id}` | Ambiguous |
| API Call | `clubsService.getClubById(id)` | Routes expect slug OR /id/ prefix |
| Code Location | `clubs.js:9-11` | `ClubController.java:42, 52` |

**Example:**
```javascript
// Frontend code (clubs.js:10)
const response = await apiClient.get(`/clubs/${id}`);

// Backend routes
GET /clubs/{slug}        // Expects slug value
GET /clubs/id/{id}       // Expects numeric ID
```

**Fix**: Frontend should use `/clubs/id/{id}` OR backend should add `GET /clubs/{id}` alias

---

### 2.2 Follows API - User vs Club Mismatch

| Frontend | Backend | Status |
|----------|---------|--------|
| POST /follows/{userId} | POST /follows/clubs/{clubId} | ❌ Wrong resource |
| DELETE /follows/{userId} | DELETE /follows/clubs/{clubId} | ❌ Wrong resource |
| GET /follows/{userId}/check | GET /follows/clubs/{clubId}/check | ❌ Wrong parameter |
| GET /users/{userId}/followers | Not found | ❌ Missing |
| GET /users/{userId}/following | Not found | ❌ Missing |

**Design Issue**: App needs to clarify follow semantics

---

## PART 3: FIELD NAME AND STRUCTURE MISMATCHES

### 3.1 User Profile (UserInfoVO)

#### Frontend Expectations (from user.js store)
```javascript
// What frontend expects
{
  id,                // ✓ Backend has
  email,             // ✓ Backend has
  username,          // ✓ Backend has
  displayName,       // ✓ Backend has
  avatarUrl,         // ❌ Backend sends "avatar"
  coverUrl,          // ❌ Backend doesn't send (no cover image VO field)
  bio                // ✓ Backend has
}
```

#### Backend Response (UserInfoVO.java)
```java
private Long id;           // ✓ Correct
private String username;   // ✓ Correct
private String email;      // ✓ Correct
private String displayName;// ✓ Correct
private String avatar;     // ❌ Field named "avatar" not "avatarUrl"
private String bio;        // ✓ Correct
private String role;       // ✓ Extra field (OK)
private LocalDateTime createdAt; // ✓ Extra field (OK)
```

#### Issue Locations
- **User Store**: `user.js:105` assumes `avatarUrl` or `avatar`
- **Auth Service**: `auth.js:13` stores response in localStorage

#### Fix Required
```javascript
// Current (partially working)
this.avatar = userData.avatarUrl || userData.avatar || defaultAvatar;

// Should be
this.avatar = userData.avatar || defaultAvatar;

// AND handle missing coverUrl gracefully
this.coverImage = userData.coverImage || defaultCover; // Backend doesn't send
```

---

### 3.2 Event Response Structures

#### What Frontend Expects
```javascript
// From mock data in events.js store
{
  id,                    // ✓ Backend: id
  title,                 // ✓ Backend: title
  description,           // ✓ Backend: description
  longDescription,       // ❌ Backend doesn't have separate long desc
  date,                  // ❌ Backend has startTime/endTime (no "date" field)
  startTime,             // ✓ Backend: startTime
  endTime,               // ✓ Backend: endTime
  club,                  // ❌ Backend has "organizerName" not "club"
  clubId,                // ❌ Backend has "organizerId" not "clubId"
  tags,                  // ✓ Backend: tags (List<String>)
  location,              // ❌ Backend has "buildingName" not "location"
  locationId,            // ❌ Backend has "buildingId" not "locationId"
  coverImage,            // ❌ Backend has "coverImageUrl" not "coverImage"
  thumbnailUrl,          // ✓ Backend: thumbnailUrl
  registration: [],      // ❌ Backend has "registrationUrl" (string) not array
  organizer: {           // ❌ Backend sends flat structure, not nested
    name,                // Backend: organizerName
    description,         // Backend: organizerBio
    avatar,              // Backend: organizerLogo
    clubId,              // Backend: organizerId
    contact              // ❌ Backend doesn't have contact field
  }
}
```

#### Backend Response (EventCardVO / EventDetailVO)
```java
private Long id;
private String title;
private String slug;
private LocalDateTime startTime;
private LocalDateTime endTime;
private String buildingName;      // NOT "location"
private String room;
private String organizerName;     // NOT nested in "organizer"
private String organizerLogo;     // NOT "organizer.avatar"
private String thumbnailUrl;
private String coverImageUrl;     // NOT "coverImage"
private Integer interestedCount;
private List<String> tags;
private String status;
private LocalDateTime createdAt;
// EventDetailVO also has:
private Long buildingId;
private String buildingAddress;
private Long organizerId;
private String organizerType;
private String organizerBio;
private String registrationUrl;   // NOT registration array
private Integer capacity;
```

#### Code Locations
- Frontend store: `events.js:63-196` (store uses mockEvents)
- Backend response: `EventCardVO.java`, `EventDetailVO.java`

#### Fix Required
Transform backend response to frontend format:
```javascript
// In eventsService or store
const transformEventResponse = (data) => {
  return {
    ...data,
    // Rename fields
    location: data.buildingName,
    locationId: data.buildingId,
    coverImage: data.coverImageUrl,
    
    // Create nested organizer
    organizer: {
      name: data.organizerName,
      description: data.organizerBio,
      avatar: data.organizerLogo,
      clubId: data.organizerId,
      // contact - not provided by backend
    },
    
    // Handle registration (URL to array)
    registration: data.registrationUrl ? [data.registrationUrl] : [],
    
    // Create synthetic date field if needed
    date: new Date(data.startTime).toLocaleDateString()
  };
};
```

---

### 3.3 Post Response Structure

#### What Frontend Expects
```javascript
// From posts.js store usage
{
  id,                 // ✓ Backend: id
  content,            // ✓ Backend: content
  author: {           // ❌ Backend sends flat authorUserId/authorClubId
    id,               
    name,             
    avatar
  },
  likes,              // ❌ Backend has "likesCount" not "likes"
  comments,           // ❌ Backend has "commentsCount" not "comments"
  reposts,            // ❌ Backend has "repostsCount" not "reposts"
  liked,              // ❌ Frontend tracks locally (not in backend)
  pinned,             // ✓ Backend: pinned
  commentsThread,     // ❌ Backend doesn't return comments array
  createdAt,          // ✓ Backend: createdAt
  updatedAt           // ✓ Backend: updatedAt
}
```

#### Backend Response (PostVO)
```java
private Long id;
private Long authorUserId;       // NOT nested "author.id"
private Long authorClubId;       // Extra field
private String content;          // ✓
private Boolean pinned;          // ✓
private Integer likesCount;      // NOT "likes"
private Integer commentsCount;   // NOT "comments"
private Integer repostsCount;    // NOT "reposts"
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

#### Code Locations
- Frontend: `posts.js:85-96`, `stores/posts.js:64-82`
- Backend: `PostVO.java`

#### Fix Required
```javascript
// Transform backend to frontend format
const transformPostResponse = (post) => {
  return {
    ...post,
    // Rename count fields
    likes: post.likesCount,
    comments: post.commentsCount,
    reposts: post.repostsCount,
    
    // Build author object (need to fetch user info separately)
    author: {
      id: post.authorUserId,
      // name and avatar would need separate user fetch
    },
    
    // Frontend tracks locally
    liked: false,
    commentsThread: []
  };
};
```

---

### 3.4 Comment Structure Mismatch

#### What Frontend Expects
```javascript
// From posts.js store
{
  id,                 // ✓ Backend: id
  author: {           // ❌ Backend sends flat userId/username/avatarUrl
    id,
    name,
    avatar
  },
  content,            // ✓ Backend: content
  likes,              // ❌ Backend has "likesCount" not "likes"
  createdAt,          // ✓ Backend: createdAt (but as Date not LocalDateTime)
  replies: []         // ✓ Backend: replies
}
```

#### Backend Response (CommentVO)
```java
private Long id;
private Long postId;
private Long userId;              // NOT "author.id"
private String username;          // NOT "author.name"
private String avatarUrl;         // NOT "author.avatar"
private String content;           // ✓
private Long parentCommentId;     // ✓
private Integer likesCount;       // NOT "likes"
private Date createdAt;           // ✓ (as Date)
private List<CommentVO> replies;  // ✓
```

#### Code Locations
- Frontend: `stores/posts.js:91-96` (addComment)
- Backend: `CommentVO.java`

#### Fix Required
```javascript
const transformCommentResponse = (comment) => {
  return {
    ...comment,
    // Flatten author info
    author: {
      id: comment.userId,
      name: comment.username,
      avatar: comment.avatarUrl
    },
    // Rename field
    likes: comment.likesCount,
    // Remove flattened fields
    userId: undefined,
    username: undefined,
    avatarUrl: undefined,
    likesCount: undefined
  };
};
```

---

### 3.5 Club Structure Mismatch

#### What Frontend Expects
```javascript
// From clubs.js store
{
  id,                 // ✓ Backend: id
  name,               // ✓ Backend: name
  slug,               // ✓ Backend: slug
  bio,                // ✓ Backend: bio
  logoUrl,            // ✓ Backend: logoUrl
  coverImageUrl,      // ✓ Backend: coverImageUrl
  membersCount,       // ❌ Backend has "followersCount" not "membersCount"
  isJoined,           // ❌ Frontend local state (not in backend response)
  category,           // ❌ In Clubs entity, not in ClubDetailVO
  events: []          // ❌ ClubDetailVO has "upcomingEvents" not "events"
}
```

#### Backend Response (ClubDetailVO)
```java
private Long id;
private String name;
private String slug;
private String bio;
private String logoUrl;
private String coverImageUrl;
private String websiteUrl;
private String instagramUrl;
private String facebookUrl;
private String twitterUrl;
private String email;
private Integer followersCount;      // NOT "membersCount"
private LocalDateTime createdAt;
private List<EventCardVO> upcomingEvents; // NOT "events"
// ClubDetailVO doesn't have:
// - category (exists in Clubs entity)
// - isJoined (frontend local state)
```

#### Code Locations
- Frontend: `clubs.js:71-72` (membersCount), `stores/clubs.js:72`
- Backend: `ClubDetailVO.java:36`

#### Design Issue
**Semantic Conflict**: Is this a "members" system (users join) or "followers" system (users follow)?

Backend uses "followersCount" suggesting users follow clubs (like Twitter).  
Frontend uses "membersCount" suggesting users are members of clubs.

These are different concepts:
- **Follow**: Passive subscription, can follow anyone without approval
- **Member**: Active participation, usually requires joining/approval

#### Fix Options
1. **Change Frontend**: Use `followersCount` instead of `membersCount`
2. **Change Backend**: Rename `followersCount` to `membersCount`
3. **Clarify UI**: Decide if UX says "members" or "followers"

---

### 3.6 Response Wrapper Format

#### Backend Response Structure (ResultVO)
All backend endpoints wrap responses:
```json
{
  "code": 200,
  "msg": "success message",
  "data": {
    // actual response data
  }
}
```

#### Frontend Expectations
Most frontend code correctly handles this:
```javascript
// In stores
const response = await eventsService.getEvents(params);
if (response?.data) {  // ✓ Correctly accesses wrapped data
  this.events = response.data.records || response.data;
}
```

But verify specifically in `auth.js:11`:
```javascript
// auth.js login
if (response.data?.data?.token) {  // Checking for nested data
  localStorage.setItem('auth_token', response.data.data.token);
}
```

#### Status: ✓ Mostly OK but verify auth response structure

---

## PART 4: HTTP METHOD MISMATCHES

### Unlike Operations - No DELETE Support

#### Post Unlike
- **Frontend Code**: `posts.js:34-36`, `stores/posts.js:69`
- **Frontend Method**: `DELETE /posts/{id}/like`
- **Backend Method**: `POST /posts/{id}/like` (only)
- **Backend Missing**: DELETE endpoint to unlike
- **Workaround**: Implement toggle with POST (no DELETE needed)

#### Event Unlike  
- **Frontend Code**: `events.js:34-36`
- **Frontend Method**: `DELETE /events/{id}/like`
- **Backend Method**: `POST /events/{id}/interested` (different semantic)
- **Issue**: "Like" vs "Interested" are different concepts
- **Design Conflict**: Need to clarify event interaction model

---

## PART 5: KEY DESIGN DECISIONS NEEDED

### Decision 1: Event Interaction Model
Current State:
- **Frontend**: Treats events like posts with "like/unlike" semantics
- **Backend**: Has "interested" concept with no like/unlike

Question: Should events support:
- [ ] Like/Unlike (user shows interest, can toggle)
- [ ] Interested (user marks as interested, only increment)
- [ ] Both (separate concepts)

**Impact**: Affects endpoints, UI interactions, data model

### Decision 2: Club Membership Model
Current State:
- **Frontend**: Uses "members" and "join/leave" terminology
- **Backend**: Uses "followers" and "follow/unfollow" (only for users, not implemented)

Question: Are users:
- [ ] **Members** (active role, join/leave, contributions expected)
- [ ] **Followers** (passive subscriptions, follow/unfollow)
- [ ] **Both** (members have special rights, others can follow)

**Impact**: 
- Affects database schema (followship table)
- Changes UI terminology
- Affects permission/role system

### Decision 3: User vs Club Following
Current State:
- **Frontend**: Tries to follow users (follows.js)
- **Backend**: Only implements club following

Question: Should app support:
- [ ] User-to-user follows only
- [ ] Club follows only (current backend)
- [ ] Both (users can follow users AND clubs)

**Impact**: Required endpoints and social features

### Decision 4: Post Update Support
Current State:
- **Frontend**: Calls PUT /posts/{id}
- **Backend**: No PUT endpoint, only DELETE/PIN

Question: Should posts be:
- [ ] Immutable (no edit, only delete)
- [ ] Editable (add PUT endpoint)
- [ ] Partially editable (only pin status)

---

## PART 6: RECOMMENDED FIXES PRIORITY

### Priority 1: Critical Path Fixes (Required for basic functionality)

**Frontend:**
1. Update comment API paths (posts.js:39-51)
   - GET /posts/{postId}/comments → GET /comments/post/{postId}
   - POST /posts/{postId}/comments → POST /comments
   - DELETE /posts/{postId}/comments/{commentId} → DELETE /comments/{commentId}

2. Fix club getById path (clubs.js:10)
   - GET /clubs/{id} → GET /clubs/id/{id}

3. Transform API responses to match UI expectations
   - Add field mapping for Posts (likesCount → likes, etc.)
   - Add field mapping for Events (buildingName → location, etc.)
   - Add field mapping for Comments (flatten author structure)

**Backend:**
1. Add DELETE /posts/{id}/like support
2. Clarify event interaction (fix "interested" vs "like" semantic)

### Priority 2: Feature Completeness Fixes

**Backend Must Implement:**
1. GET /clubs/{clubId}/events
2. GET /buildings/{buildingId}/events
3. GET /clubs/{clubId}/members
4. GET /clubs/{clubId}/posts

**Frontend Must Update:**
1. Remove unsupported operations (POST /clubs, PUT /posts, etc.)
2. OR wait for backend implementation

### Priority 3: Design Resolution Needed

1. **Event Interaction**: Confirm like vs interested model
2. **Club Follow**: Confirm membership vs follower semantics
3. **User Follow**: Confirm if user-to-user following is needed
4. **Post Edit**: Confirm if posts should be editable

---

## IMPLEMENTATION CHECKLIST

### For Frontend Developers

- [ ] Update comment API paths in posts.js
- [ ] Update club retrieval path in clubs.js
- [ ] Add field transformation functions for:
  - [ ] Posts (counts → singular)
  - [ ] Events (building/organizer fields)
  - [ ] Comments (flatten author)
  - [ ] User (avatar field name)
- [ ] Test field mappings with actual backend responses
- [ ] Remove calls to unimplemented endpoints (or mark as TODO)
- [ ] Update error handling for missing backend features

### For Backend Developers  

- [ ] Implement DELETE /posts/{id}/like
- [ ] Resolve "interested" vs "like" semantic for events
- [ ] Implement missing collection endpoints:
  - [ ] GET /clubs/{clubId}/events
  - [ ] GET /buildings/{buildingId}/events
  - [ ] GET /clubs/{clubId}/posts
  - [ ] GET /clubs/{clubId}/members
- [ ] Clarify club membership vs following model
- [ ] Add support for DELETE /events/{id}/like (if using like/unlike)
- [ ] Document all response field names

### For Product/Architecture

- [ ] Decide on event interaction model (meeting/discussion needed)
- [ ] Decide on club membership vs following model
- [ ] Decide on user follow support
- [ ] Document API design decisions in architecture doc
- [ ] Create API contract documentation with clear field names

---

## FILES REFERENCED

### Frontend API Files
- `/home/user/utm-what2do/utm-what2do-frontend/src/api/auth.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/api/events.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/api/posts.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/api/clubs.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/api/follows.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/api/buildings.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/api/index.js`

### Frontend Store Files
- `/home/user/utm-what2do/utm-what2do-frontend/src/stores/user.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/stores/events.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/stores/posts.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/stores/clubs.js`
- `/home/user/utm-what2do/utm-what2do-frontend/src/stores/buildings.js`

### Backend Controller Files
- `/home/user/utm-what2do/utm-what2do-backend/src/main/java/com/utm/what2do/controller/UserController.java`
- `/home/user/utm-what2do/utm-what2do-backend/src/main/java/com/utm/what2do/controller/EventController.java`
- `/home/user/utm-what2do/utm-what2do-backend/src/main/java/com/utm/what2do/controller/PostController.java`
- `/home/user/utm-what2do/utm-what2do-backend/src/main/java/com/utm/what2do/controller/CommentController.java`
- `/home/user/utm-what2do/utm-what2do-backend/src/main/java/com/utm/what2do/controller/ClubController.java`
- `/home/user/utm-what2do/utm-what2do-backend/src/main/java/com/utm/what2do/controller/FollowController.java`
- `/home/user/utm-what2do/utm-what2do-backend/src/main/java/com/utm/what2do/controller/FavoriteController.java`
- `/home/user/utm-what2do/utm-what2do-backend/src/main/java/com/utm/what2do/controller/MapController.java`

### Backend VO/DTO Files
- User: `UserInfoVO.java`, `UserLoginDTO.java`, `UserRegisterDTO.java`
- Events: `EventCardVO.java`, `EventDetailVO.java`, `EventCreateDTO.java`
- Posts: `PostVO.java`, `PostCreateDTO.java`
- Comments: `CommentVO.java`, `CommentCreateDTO.java`
- Clubs: `ClubDetailVO.java`, `Clubs.java` (entity)
- Buildings: `BuildingCountVO.java`, `Buildings.java` (entity)

---

## CONCLUSION

The API mismatch analysis reveals significant disconnects between frontend expectations and backend implementation. Most issues can be resolved by:

1. **Quick wins** (< 1 hour): Update API paths in frontend for comments
2. **Data mapping** (2-4 hours): Add field transformation layers
3. **Backend implementation** (4-8 hours): Implement missing endpoints
4. **Design alignment** (1-2 hours): Resolve key architectural conflicts

**Estimated Total Effort**: 10-18 development hours + 2-3 hours for product/design decisions

Recommend tackling Priority 1 fixes first to unblock development, then addressing Priority 2 and 3 in parallel.

