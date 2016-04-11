package su.tarasov.watchdir;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @author Dmitry Tarasov
 *         Date: 04/10/2016
 *         Time: 23:42
 */
public class Event {
    private Path path;
    private WatchEvent.Kind kind;

    public Event(Path path, WatchEvent.Kind kind) {
        this.path = path;
        this.kind = kind;
    }

    public Event() {
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public WatchEvent.Kind getKind() {
        return kind;
    }

    public void setKind(WatchEvent.Kind kind) {
        this.kind = kind;
    }
}
