# App Flow - Kotlin Memo App

## User Journey

```
┌─────────────────────────────────────────────────────────────┐
│                        Launch App                            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                     MainActivity                             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  [SearchView - Search memos]                           │ │
│  └────────────────────────────────────────────────────────┘ │
│  ┌──────────────────┬─────────────────────────────────────┐ │
│  │   All Memos Tab  │      Folders Tab                    │ │
│  └──────────────────┴─────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  ┌──────────────────────────────────────────────┐     │ │
│  │  │  Memo 1                                       │     │ │
│  │  │  Title: Meeting Notes                        │     │ │
│  │  │  Content: Discussed project timeline...      │     │ │
│  │  │  [Folder Badge] 2024/01/15                   │     │ │
│  │  └──────────────────────────────────────────────┘     │ │
│  │  ┌──────────────────────────────────────────────┐     │ │
│  │  │  Memo 2                                       │     │ │
│  │  │  Title: Shopping List                        │     │ │
│  │  │  Content: Milk, Eggs, Bread...               │     │ │
│  │  └──────────────────────────────────────────────┘     │ │
│  └────────────────────────────────────────────────────────┘ │
│                                        [+] FAB              │
└──────────────────────┬──────────────────┬───────────────────┘
                       │                  │
        ┌──────────────┘                  └──────────────┐
        │ Click Memo                          Click FAB   │
        ▼                                                 ▼
┌─────────────────────────────────┐    ┌─────────────────────────────────┐
│   MemoEditActivity (Edit)       │    │   MemoEditActivity (New)        │
│  ┌────────────────────────────┐ │    │  ┌────────────────────────────┐ │
│  │ Title: [Meeting Notes]     │ │    │  │ Title: [Empty]             │ │
│  └────────────────────────────┘ │    │  └────────────────────────────┘ │
│  ┌────────────────────────────┐ │    │  ┌────────────────────────────┐ │
│  │ Content:                   │ │    │  │ Content:                   │ │
│  │ [Multi-line text...]       │ │    │  │ [Empty]                    │ │
│  │                            │ │    │  │                            │ │
│  └────────────────────────────┘ │    │  └────────────────────────────┘ │
│  Select Folder: [▼ Dropdown]    │    │  Select Folder: [▼ Dropdown]    │
│                                  │    │                                  │
│  [Save Button] [Delete Button]  │    │  [Save Button]                  │
└──────────────────────────────────┘    └──────────────────────────────────┘
        │                                         │
        │ Save                                    │ Save
        ▼                                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Room Database (SQLite)                          │
│  ┌────────────────┐         ┌──────────────────────┐       │
│  │  Memos Table   │────────▶│   Folders Table      │       │
│  │  - id          │  FK     │   - id               │       │
│  │  - title       │         │   - name             │       │
│  │  - content     │         │   - createdAt        │       │
│  │  - folderId    │         └──────────────────────┘       │
│  │  - createdAt   │                                         │
│  │  - updatedAt   │                                         │
│  └────────────────┘                                         │
└─────────────────────────────────────────────────────────────┘
```

## Folders Tab Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Folders Tab                               │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  ┌─────────────────────────────────────────────┐      │ │
│  │  │  Work     [Edit ✏️] [Delete 🗑️]             │      │ │
│  │  └─────────────────────────────────────────────┘      │ │
│  │  ┌─────────────────────────────────────────────┐      │ │
│  │  │  Personal [Edit ✏️] [Delete 🗑️]             │      │ │
│  │  └─────────────────────────────────────────────┘      │ │
│  │  ┌─────────────────────────────────────────────┐      │ │
│  │  │  Shopping [Edit ✏️] [Delete 🗑️]             │      │ │
│  │  └─────────────────────────────────────────────┘      │ │
│  └────────────────────────────────────────────────────────┘ │
│                                        [+] FAB              │
└──────────────────────┬──────────────────┬───────────────────┘
                       │                  │
        ┌──────────────┘                  └──────────────┐
        │ Click Edit                       Click FAB/New │
        ▼                                                 ▼
┌─────────────────────┐                  ┌─────────────────────┐
│  Edit Folder Dialog │                  │  New Folder Dialog  │
│  ┌────────────────┐ │                  │  ┌────────────────┐ │
│  │ Folder Name:   │ │                  │  │ Folder Name:   │ │
│  │ [Work]         │ │                  │  │ [Empty]        │ │
│  └────────────────┘ │                  │  └────────────────┘ │
│  [Save] [Cancel]    │                  │  [Save] [Cancel]    │
└─────────────────────┘                  └─────────────────────┘
```

## Search Flow

```
┌─────────────────────────────────────────────────────────────┐
│                     MainActivity                             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  SearchView: "meeting"                                 │ │
│  └────────────────────────────────────────────────────────┘ │
│         │                                                    │
│         │ User types                                         │
│         ▼                                                    │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Real-time search in Room Database                     │ │
│  │  SELECT * FROM memos                                   │ │
│  │  WHERE title LIKE '%meeting%'                          │ │
│  │  OR content LIKE '%meeting%'                           │ │
│  └────────────────────────────────────────────────────────┘ │
│         │                                                    │
│         │ LiveData updates                                  │
│         ▼                                                    │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Filtered Results:                                     │ │
│  │  ┌──────────────────────────────────────────────┐     │ │
│  │  │  Meeting Notes                               │     │ │
│  │  │  Discussed project timeline...               │     │ │
│  │  └──────────────────────────────────────────────┘     │ │
│  │  ┌──────────────────────────────────────────────┐     │ │
│  │  │  Team Meeting Agenda                         │     │ │
│  │  │  1. Status updates 2. Next steps...          │     │ │
│  │  └──────────────────────────────────────────────┘     │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Data Flow Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                         UI Layer                              │
│  ┌────────────┐  ┌──────────────┐  ┌─────────────────┐      │
│  │ MainActivity│  │MemoEditActivity│ │ FolderActivity  │      │
│  └──────┬─────┘  └──────┬───────┘  └────────┬────────┘      │
│         │                │                    │                │
│         │ observe()      │ observe()          │ observe()      │
│         ▼                ▼                    ▼                │
└─────────┼────────────────┼────────────────────┼───────────────┘
          │                │                    │
┌─────────┼────────────────┼────────────────────┼───────────────┐
│         │         ViewModel Layer             │                │
│  ┌──────▼──────┐                    ┌─────────▼───────┐       │
│  │ MemoViewModel│                    │ FolderViewModel │       │
│  │              │                    │                 │       │
│  │ - allMemos   │                    │ - allFolders    │       │
│  │ - insert()   │                    │ - insert()      │       │
│  │ - update()   │                    │ - update()      │       │
│  │ - delete()   │                    │ - delete()      │       │
│  │ - search()   │                    └─────────┬───────┘       │
│  └──────┬───────┘                              │               │
│         │                                      │               │
└─────────┼──────────────────────────────────────┼───────────────┘
          │                                      │
┌─────────┼──────────────────────────────────────┼───────────────┐
│         │        Repository Layer              │                │
│  ┌──────▼───────┐                    ┌─────────▼────────┐      │
│  │MemoRepository│                    │ FolderRepository │      │
│  │              │                    │                  │      │
│  │ Abstracts    │                    │ Abstracts        │      │
│  │ data source  │                    │ data source      │      │
│  └──────┬───────┘                    └─────────┬────────┘      │
│         │                                      │               │
└─────────┼──────────────────────────────────────┼───────────────┘
          │                                      │
┌─────────┼──────────────────────────────────────┼───────────────┐
│         │          DAO Layer                   │                │
│  ┌──────▼───┐                        ┌─────────▼─────┐         │
│  │ MemoDao  │                        │  FolderDao    │         │
│  │          │                        │               │         │
│  │ @Query   │                        │  @Query       │         │
│  │ @Insert  │                        │  @Insert      │         │
│  │ @Update  │                        │  @Update      │         │
│  │ @Delete  │                        │  @Delete      │         │
│  └──────┬───┘                        └───────┬───────┘         │
│         │                                    │                 │
└─────────┼────────────────────────────────────┼─────────────────┘
          │                                    │
          └──────────┬─────────────────────────┘
                     │
┌────────────────────▼──────────────────────────────────────────┐
│                    Room Database                               │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │                    SQLite Storage                         │ │
│  │  ┌─────────────┐              ┌──────────────┐          │ │
│  │  │ memos       │──────────────│  folders     │          │ │
│  │  │ table       │   Foreign Key│  table       │          │ │
│  │  └─────────────┘              └──────────────┘          │ │
│  └──────────────────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────────────────┘
```

## Key User Interactions

### 1. Creating a New Memo
1. User opens MainActivity
2. Clicks FAB (+) button on Memos tab
3. Opens MemoEditActivity with empty fields
4. Enters title and content
5. Optionally selects a folder from dropdown
6. Clicks Save button
7. MemoViewModel.insert() called
8. Room Database persists memo
9. LiveData updates automatically
10. Returns to MainActivity with updated list

### 2. Searching Memos
1. User types in SearchView
2. Real-time query to Room Database
3. SQL LIKE query on title and content
4. LiveData emits filtered results
5. RecyclerView updates instantly
6. Shows matching memos only

### 3. Organizing with Folders
1. Switch to Folders tab
2. Click FAB to create new folder
3. Enter folder name in dialog
4. Folder saved to database
5. When editing memo, select folder from dropdown
6. Memo associated with folder via foreign key
7. Cascade delete: deleting folder removes all contained memos

### 4. Editing/Deleting
1. Click on memo card
2. Opens MemoEditActivity with populated fields
3. Modify title/content/folder
4. Save updates existing memo (by ID)
5. Or click Delete for confirmation dialog
6. Deleted memo removed from database
7. LiveData updates UI automatically
