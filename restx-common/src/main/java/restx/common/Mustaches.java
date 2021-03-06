package restx.common;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.*;
import java.nio.file.Path;

import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;

/**
 * User: xavierhanin
 * Date: 3/31/13
 * Time: 6:36 PM
 */
public class Mustaches {
    public static Template compile(Class relativeTo, String name) {
        return compile(name, asCharSource(getResource(relativeTo, name), Charsets.UTF_8));
    }

    public static Template compile(String name) {
        return compile(name, asCharSource(getResource(name), Charsets.UTF_8));
    }

    public static Template compile(String name, CharSource charSource) {
        try (Reader reader = charSource.openBufferedStream()) {
            return Mustache.compiler().escapeHTML(false).compile(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String execute(Template mustache, Object scope) {
        return mustache.execute(scope);
    }

    public static void execute(Template mustache, Object scope, Path path) throws IOException {
        File file = path.toFile();
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException(String.format("can't generate file to `%s`: can't create directory `%s`",
                        file.getAbsolutePath(), file.getParentFile().getAbsolutePath()));
            }
        }
        try (FileWriter w = new FileWriter(file)) {
            mustache.execute(scope, w);
        }
    }
}
