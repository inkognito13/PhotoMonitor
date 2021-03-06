package su.tarasov.watchdir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class MonitorService {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private Configuration configuration;
    private boolean trace = false;
    private EventPool eventPool;

    private Logger logger = LoggerFactory.getLogger(MonitorService.class);
    /**
     * Creates a WatchService and registers the given directory
     */
    MonitorService(Configuration conf, EventPool eventPool) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.configuration = conf;
        this.eventPool = eventPool;
        Path base = Paths.get(configuration.BASE_FOLDER);

        if (configuration.recursive) {
            logger.debug("Scanning {} ", configuration.BASE_FOLDER);
            registerAll(base);
            logger.debug("Done scanning {}", configuration.BASE_FOLDER);
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

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        logger.debug("register: {}", dir);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                logger.debug("register {} as new folder", dir);
            } else {
                if (!dir.equals(prev)) {
                    logger.debug("update: {} -> {}", prev, dir);
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
                logger.error("WatchKey not recognized!!");
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

                // print out event
                logger.debug("{}: {}", event.kind().name(), child);

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

                eventPool.put(new Event(child, kind));
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {

                keys.remove(key);
                
                if (dir.toFile().exists()){
                    try {
                        registerAll(dir);    
                    }catch (IOException x){
                        
                    }
                }
                
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }


}
