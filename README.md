# Gitlet

A Git-like version-control system written in Java. Gitlet saves snapshots of
files in commits, keeps history, and supports branches, resets and merges,
all from the command line.

## Features

- **Commits and history**: stage files, commit snapshots, and view the history
  of the current branch or of the whole repository.
- **Content-addressable storage**: commits and file contents (blobs) are named by
  their SHA-1 hash, so identical file contents are stored only once.
- **Branches**: create, switch, and delete branches.
- **Reset and checkout**: restore a single file or the whole working directory
  from any commit. Abbreviated commit ids are supported.
- **Merge**: three-way merge using the latest common ancestor (split point) of
  the two branches, with conflict detection, conflict markers, and fast-forward
  handling.
- **Safety checks**: commands that would overwrite untracked files stop with an
  error instead of losing work.

## Commands

| Command | Description |
| --- | --- |
| `init` | Create a new Gitlet repository in the current directory. |
| `add [file]` | Stage a file for the next commit. |
| `commit [message]` | Save a snapshot of the tracked and staged files. |
| `rm [file]` | Unstage a file, or stage it for removal and delete it. |
| `log` | Show the history of the current branch (first parents only). |
| `global-log` | Show every commit ever made. |
| `find [message]` | Print the ids of all commits with this message. |
| `status` | Show branches, staged and removed files, unstaged modifications, and untracked files. |
| `checkout -- [file]` | Restore a file from the head commit. |
| `checkout [commit id] -- [file]` | Restore a file from a given commit. |
| `checkout [branch]` | Switch to a branch. |
| `branch [name]` | Create a new branch at the current commit. |
| `rm-branch [name]` | Delete a branch pointer. |
| `reset [commit id]` | Move the current branch to a commit and restore its files. |
| `merge [branch]` | Merge a branch into the current branch. |

### Example

```text
$ java gitlet.Main init
$ java gitlet.Main add hello.txt
$ java gitlet.Main commit "Add hello.txt"
$ java gitlet.Main branch feature
$ java gitlet.Main status
=== Branches ===
feature
*master

=== Staged Files ===

=== Removed Files ===

=== Modifications Not Staged For Commit ===

=== Untracked Files ===

```

When a merge has a conflict, the file is written with both versions:

```text
<<<<<<< HEAD
contents in the current branch
=======
contents in the given branch
>>>>>>>
```

## Getting Started

### Requirements

- Java 17 or newer
- Python 3 (only to run the integration tests)
- `make` (optional)

### Build

From the repository root:

```bash
make
```

or without `make`:

```bash
javac gitlet/*.java
```

### Run

Run Gitlet from the directory you want to track, with the repository root on
the classpath:

```bash
java -cp /path/to/Gitlet gitlet.Main init
java -cp /path/to/Gitlet gitlet.Main add notes.txt
java -cp /path/to/Gitlet gitlet.Main commit "First commit"
```

## Testing

### Integration tests

Each `.in` file in `testing/` runs a sequence of Gitlet commands in a fresh
directory and checks the output and the files.

```bash
make check
```

or without `make`:

```bash
cd testing
python3 tester.py samples/*.in student_tests/*.in
```

### Unit tests

JUnit 4 tests for each command are in `test/`. Run them from your IDE with
JUnit on the classpath.

> **Note:** the unit tests create and delete `.gitlet` and some text files in
> the directory they run in.

## Project Structure

```text
gitlet/
  Main.java                 command-line entry point, checks operands
  repository/               data and persistence
    Commit.java             commit object (message, time, parents, files)
    Blob.java               file contents stored by SHA-1 id
    StagingArea.java        files staged for addition and removal
    Branch.java             branch pointers and HEAD
    WorkingDirectory.java   reading and writing the user's files
    Path.java               locations inside .gitlet
  services/                 one class per command
    InitCommand.java  AddCommand.java  CommitCommand.java  RmCommand.java
    LogCommand.java   StatusCommand.java  CheckoutCommand.java
    BranchCommand.java  ResetCommand.java  MergeCommand.java
test/                       JUnit tests (one *CommandTest class per command)
testing/                    integration tests and tester
gitlet-design.md            design document
```

### Repository layout

```text
.gitlet/
  commits/<commit id>       serialized Commit
  blobs/<blob id>           file contents
  branches/<branch name>    commit id of the branch head
  head                      name of the current branch
  staging                   serialized staging area
```

See [gitlet-design.md](gitlet-design.md) for the full design: data
structures, algorithms, and persistence.

## Acknowledgments

Based on the Gitlet project from UC Berkeley's
[CS61B (Spring 2021)](https://sp21.datastructur.es/materials/proj/proj2/proj2).
`Utils.java`, `GitletException.java`, `DumpObj.java`, the Makefiles, and the
testing scripts in `testing/` come from the course skeleton.

## Author

**Mona Dewidar**: [GitHub](https://github.com/MonaDewidar)
