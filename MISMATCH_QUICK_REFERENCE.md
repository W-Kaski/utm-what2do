# Quick Reference: API Mismatches Summary

## Critical Issues That Break Functionality

| Issue # | Area | Frontend | Backend | Priority | Effort |
|---------|------|----------|---------|----------|--------|
| 1 | Comments | GET /posts/{id}/comments | GET /comments/post/{id} | P1 | 5min |
| 2 | Comments | POST /posts/{id}/comments | POST /comments | P1 | 5min |
| 3 | Comments | DELETE /posts/{id}/comments/{id} | DELETE /comments/{id} | P1 | 5min |
| 4 | Clubs | GET /clubs/{id} | GET /clubs/id/{id} | P1 | 5min |
| 5 | Posts | likesCount field | likes expected | P1 | 30min |
| 6 | Events | buildingName field | location expected | P1 | 30min |
| 7 | User | avatar field | avatarUrl expected | P1 | 15min |
| 8 | Posts | DELETE /posts/{id}/like | No backend support | P1 | 30min |

## Missing Endpoints (Backend Must Implement)

| Endpoint | Purpose | Status | Effort |
|----------|---------|--------|--------|
| GET /clubs/{clubId}/events | List club events | Missing | 2h |
| GET /buildings/{buildingId}/events | List building events | Missing | 2h |
| GET /clubs/{clubId}/members | List club members | Missing | 2h |
| GET /clubs/{clubId}/posts | List club posts | Missing | 2h |
| POST /clubs/{clubId}/join | Join club | Missing | 1.5h |
| DELETE /clubs/{clubId}/leave | Leave club | Missing | 1.5h |
| DELETE /posts/{id}/like | Unlike post | Missing | 1h |
| DELETE /events/{id}/like | Unlike event | Missing | 1h |
| POST /posts/{id}/repost | Repost post | Missing | 1h |
| PUT /posts/{id} | Update post | Missing | 2h |

**Total Backend Effort**: ~17-18 hours

## Field Name Mappings Needed (Frontend)

### User Response
```
Backend: avatar          → Frontend: avatarUrl
Backend: (missing)       → Frontend: coverUrl (use default)
```

### Event Response
```
Backend: buildingName    → Frontend: location
Backend: buildingId      → Frontend: locationId
Backend: coverImageUrl   → Frontend: coverImage
Backend: organizerName   → Frontend: organizer.name
Backend: organizerBio    → Frontend: organizer.description
Backend: organizerLogo   → Frontend: organizer.avatar
Backend: organizerId     → Frontend: organizer.clubId
Backend: registrationUrl → Frontend: registration[] (wrap in array)
```

### Post Response
```
Backend: likesCount      → Frontend: likes
Backend: commentsCount   → Frontend: comments
Backend: repostsCount    → Frontend: reposts
Backend: (flat)          → Frontend: author{} (nest userId/username/avatar)
Backend: (missing)       → Frontend: liked (track locally)
Backend: (missing)       → Frontend: commentsThread[] (fetch separately)
```

### Comment Response
```
Backend: likesCount      → Frontend: likes
Backend: userId          → Frontend: author.id
Backend: username        → Frontend: author.name
Backend: avatarUrl       → Frontend: author.avatar
```

### Club Response
```
Backend: followersCount  → Frontend: membersCount (or change frontend semantics)
Backend: upcomingEvents  → Frontend: events (rename)
Backend: (missing)       → Frontend: category (use entity, not VO)
Backend: (missing)       → Frontend: isJoined (track locally)
```

## Design Conflicts Requiring Decision

### 1. Event Interaction Model
**Current Conflict**: Frontend uses "like/unlike", Backend uses "interested"
- **Option A**: Change to "interested" only (no toggle)
- **Option B**: Change to "like/unlike" (toggle support)
- **Option C**: Support both separately
**Recommendation**: Clarify with product team

### 2. Club Membership vs Following
**Current Conflict**: Frontend calls it "members", Backend calls it "followers"
- **Option A**: Rename backend field to membersCount
- **Option B**: Rename frontend to use followersCount
- **Option C**: Separate concepts (members + followers)
**Recommendation**: Clarify membership model in architecture

### 3. User Following
**Current Conflict**: Frontend expects user-to-user follows, Backend only has club follows
- **Option A**: Remove user follow feature
- **Option B**: Implement user follow endpoints
- **Option C**: Clarify that follows are only for clubs
**Recommendation**: Clarify social features scope

### 4. Post Edit Support
**Current Conflict**: Frontend calls PUT /posts/{id}, Backend doesn't support
- **Option A**: Remove edit feature (immutable posts)
- **Option B**: Implement PUT endpoint
- **Option C**: Only allow pin/unpin (current backend)
**Recommendation**: Clarify post lifecycle requirements

## Implementation Roadmap

### Phase 1: Quick Fixes (Frontend) - 2 hours
1. Update comment API paths (5 min)
2. Update club retrieval path (5 min)
3. Add field transformation functions (1.5 hours)
4. Test with actual backend responses (15 min)

### Phase 2: Backend Additions - 8-10 hours
1. Add DELETE /posts/{id}/like (1h)
2. Clarify event interaction semantics (0.5h + design)
3. Add collection endpoints (6-8h):
   - GET /clubs/{clubId}/events
   - GET /clubs/{clubId}/posts
   - GET /clubs/{clubId}/members
   - GET /buildings/{buildingId}/events
4. Add join/leave endpoints (3h)

### Phase 3: Design Decisions - 1-2 hours
1. Confirm event interaction model
2. Confirm club membership semantics
3. Confirm user follow requirements
4. Confirm post edit requirements
5. Document API contract

### Phase 4: Integration Testing - 2-3 hours
1. End-to-end test all flows
2. Verify all field mappings
3. Error handling verification
4. Performance check

**Total Estimated Effort**: 15-20 development hours + 2-3 hours planning/design

## Recommended Start
1. Priority 1: Fix comment paths and field mappings (unblocks most functionality)
2. Priority 2: Implement missing backend endpoints (enables features)
3. Priority 3: Resolve design conflicts (ensures long-term consistency)

