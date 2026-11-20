# Gitlet Design Document

**Name**: Mona Dewidar

## Classes and Data Structures

The code is split into two packages:
`gitlet.repository` holds the data and how it is saved, and
`gitlet.services` holds one class per command. `gitlet.Main` checks the
operands and calls the right command.

### Main

Reads `args`, checks the number of operands and that the `.gitlet` directory
exists, then calls the command. Every error is thrown as a `GitletException`,
and `Main` prints its message and exits with code 0.

### Commit (`gitlet.repository`)

#### Fields

1. `String message`: the commit message.
2. `Date TimeStamp`: the commit time (the epoch for the initial commit).
3. `String parent`: id of the first parent (`null` for the initial commit).
4. `String secParent`: id of the second parent (merge commits only, otherwise `null`).
5. `TreeMap<String, String> blobs`: file name -> blob id of every tracked file.

Parents are stored as ids, not as `Commit` objects, so serializing a commit
never serializes the commits behind it. The id of a commit is the SHA-1 of its
serialized bytes.

### Blob (`gitlet.repository`)

No fields. Saves file contents (as bytes) under `.gitlet/blobs/<id>`, where the
id is the SHA-1 of the contents. Equal contents have the same id, so they are
stored once.

### StagingArea (`gitlet.repository`)

#### Fields

1. `TreeMap<String, String> stagingFiles`: files staged for addition (file name -> blob id).
2. `TreeSet<String> stagingRemove`: files staged for removal.

A file is never in both at the same time. `TreeMap`/`TreeSet` keep the names
sorted for `status`.

### Branch (`gitlet.repository`)

No fields. Reads and writes the branch files (`.gitlet/branches/<name>`, each
holding a commit id) and the `HEAD` file (holding the current branch name).

### WorkingDirectory (`gitlet.repository`)

No fields. Reads, writes, deletes and lists the plain files in the current
working directory, and computes the blob id of a working file.

### Path (`gitlet.repository`)

Constants for every file and directory inside `.gitlet`.

### Commands (`gitlet.services`)

`InitCommand`, `AddCommand`, `CommitCommand`, `RmCommand`, `LogCommand`
(log, global-log, find, status), `CheckoutCommand`, `BranchCommand`
(branch, rm-branch), `ResetCommand`, `MergeCommand`.


## Algorithms

* **add**: compute the blob id of the working file. If it equals the id in the
  head commit, unstage the file (also from removal); otherwise save the blob and
  stage it.
* **commit**: copy the head commit's `blobs`, apply staged additions, drop staged
  removals, save the new commit, move the current branch to it, clear the
  staging area.
* **rm**: unstage the file if it is staged; if the head commit tracks it, stage
  it for removal and delete it from the working directory.
* **log**: follow first parents from the head commit to the initial commit.
  **global-log** / **find** go over every file in `.gitlet/commits`.
* **status**: branches, staged files and removed files come from the branch
  directory and the staging area. "Modifications Not Staged" compares the
  working files with the head commit and the staging area (all four cases in
  the spec). "Untracked" lists working files that are neither staged nor tracked,
  including files staged for removal and then created again.
* **Abbreviated ids** (checkout and reset): `Commit.findFullId` returns the first
  commit id that starts with the given prefix.
* **checkout branch / reset**: first, fail if a working file that the current
  commit does not track would be overwritten. Then write every file of the
  target commit, delete files tracked by the current commit but not the target,
  and clear the staging area. Checkout moves `HEAD`; reset moves the current
  branch.
* **merge**:
  1. Check for staged changes, a missing branch, and merging a branch with itself.
  2. Split point: BFS over both parents to collect every ancestor of the given
     branch head, then BFS (both parents) from the current head; the first
     commit found in that set is the latest common ancestor.
  3. If the split point is the given head, stop ("Given branch is an ancestor
     of the current branch."). If it is the current head, check out the given
     branch ("Current branch fast-forwarded.").
  4. For every file in the split point, current head and given head, decide:
     take the given version, remove the file, keep it, or mark a conflict.
  5. If any file to be written or deleted is untracked in the current commit,
     fail before changing anything.
  6. Apply the changes, write conflict files
     (`<<<<<<< HEAD` / current / `=======` / given / `>>>>>>>`, a deleted side
     is empty), stage everything, and commit with both heads as parents.
     Print "Encountered a merge conflict." if there was one.


## Persistence

```
.gitlet/
  commits/<commit id>     serialized Commit
  blobs/<blob id>         raw file contents
  branches/<branch name>  commit id of the branch head
  head                    name of the current branch
  staging                 serialized StagingArea
```

Every command loads only what it needs from these files, and writes back
whatever it changed before exiting.
