package su.tarasov.watchdir;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;
import static su.tarasov.watchdir.Configuration.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private Configuration configuration;
    private boolean trace = false;
    private ThumbMaker thumbMaker;

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir(Configuration conf) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.configuration = conf;
        thumbMaker = new ThumbMaker(configuration.DCRAW_COMMAND);

        Path base = Paths.get(configuration.BASE_FOLDER);

        if (configuration.recursive) {
            System.out.format("Scanning %s ...\n", configuration.BASE_FOLDER);
            registerAll(base);
            System.out.println("Done.");
        } else {
            register(base);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private void handleSystemChangeEvent(Path file, WatchEvent.Kind kind) {
        if (kind.equals(ENTRY_CREATE)) {
            handleEntryCreate(file);
        } else if (kind.equals(ENTRY_DELETE)) {

        }
    }

    private void handleEntryCreate(Path file) {
        if (isFileRaw(file)) {
            System.out.format("File %s is Raw Photo file. Converting", file);
            Path convertedFile = convert(file);
            System.out.printf("Thumb %s is created", convertedFile);
        }
    }

    private Path convert(Path rawPhotoFile) {
        String original = rawPhotoFile.toString();
        String thumb = original.replace(configuration.BASE_FOLDER, configuration.THUMB_FOLDER)
                .replace(configuration.RAW_EXTENSION, configuration.THUMB_EXTENSION)
                .replace(configuration.RAW_EXTENSION.toLowerCase(), configuration.THUMB_EXTENSION);
        thumbMaker.createThumb(original, thumb);
        return Paths.get(thumb);
    }

    private static void upload(Path file) {

    }

    private boolean isFileRaw(Path file) {
        return (getFileExtension(file.toString()).equalsIgnoreCase(configuration.RAW_EXTENSION));
    }

    private static String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                handleSystemChangeEvent(child, kind);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (configuration.recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }


}
