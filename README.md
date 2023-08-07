# H2Tool

A very stupid wrapper to access all the commands and tools supported by the _H2_ database (as of version 2.2.220).

## Build & Installation


Run the following command:

```bash
./gradlew installDist
```

This will generate a folder which you can to copy the compiled script with a launcher into
the `build/install` folder.

## Usage

To see which commands have been wrapped just run:

```bash
buld/install/bin/h2
```

It should generate the following output:

```text
The following commands are supported:

(1) backup
(2) console
(3) change-file-encryption
(4) convert-trace-file
(5) create-cluster
(6) delete-db-files
(7) gui-console
(8) recover
(9) run-script
(10) script
(11) server
(12) shell

To see help on any of them run: h2 <command> -help
for example:

h2 recover -help
```

> **NOTE**: These are just wrapped commands. Please consult the H2 website for details.

