package us.kbase.scripts;

import static org.apache.commons.lang.StringUtils.stripEnd;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonschema2pojo.ContentResolver;
import com.googlecode.jsonschema2pojo.FragmentResolver;
import com.googlecode.jsonschema2pojo.Schema;
import com.googlecode.jsonschema2pojo.SchemaStore;

/**
 * This class was built based on com.googlecode.jsonschema2pojo.Schema source code
 * with some changes required for keeping schema files entirely in JVM memory.
 */
public class InMemorySchemaStore extends SchemaStore {
    private Map<URI, Schema> schemas = new HashMap<URI, Schema>();
    private FragmentResolver fragmentResolver = new FragmentResolver();
    private ContentResolver contentResolver = new ContentResolver();
    private static final ObjectMapper OBJECT_MAPPER = 
            new ObjectMapper().enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    public synchronized Schema create(URI id) {
        if (!schemas.containsKey(id)) {
            JsonNode content = contentResolver.resolve(removeFragment(id));
            if (id.toString().contains("#")) {
                content = fragmentResolver.resolve(content, '#' + substringAfter(id.toString(), "#"));
            }
            schemas.put(id, new InMemorySchema(id, content));
        }
        return schemas.get(id);
    }

    public void addSchema(URI id, InputStream is) {
        JsonNode content = resolveFromStream(id, is);
        schemas.put(id, new InMemorySchema(id, content));
    }
    
    public JsonNode resolveFromStream(URI id, InputStream is) {
        try {
            return OBJECT_MAPPER.readTree(is);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing document: " + id, e);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unrecognised URI, can't resolve this: " + id, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unrecognised URI, can't resolve this: " + id, e);
        }

    }

    private URI removeFragment(URI id) {
        return URI.create(substringBefore(id.toString(), "#"));
    }

    public Schema create(Schema parent, String path) {
        if (path.equals("#")) {
            return parent;
        }
        path = stripEnd(path, "#?&/");
        URI id = (parent == null || parent.getId() == null) ? URI.create(path) : parent.getId().resolve(path);
        return create(id);
    }

    public synchronized void clearCache() {
        schemas.clear();
    }

    public static class InMemorySchema extends Schema {
        public InMemorySchema(URI id, JsonNode content) {
            super(id,content);
        }
    }
}
