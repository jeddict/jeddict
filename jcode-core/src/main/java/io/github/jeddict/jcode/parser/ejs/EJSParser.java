/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.jcode.parser.ejs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static io.github.jeddict.jcode.util.FileUtil.copy;
import static io.github.jeddict.jcode.util.FileUtil.loadResource;
import static io.github.jeddict.jcode.util.FileUtil.readString;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.ZipInputStream;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.openide.util.Exceptions;

public final class EJSParser {

    private ScriptEngine engine;

    private Character delimiter;

    private Map<String, String> importTemplate;

    private static String base;
    private static String ejs;

    private final List<Map<String, Object>> contexts = new ArrayList<>();

    private final ObjectMapper mapper = new ObjectMapper();

    private final StringBuilder scripts = new StringBuilder();

    private static final Set<String> SKIP_FILE_TYPE
            = new HashSet<>(asList("png", "jpeg", "jpg", "gif"));

    private static final String TEMPLATES = "io/github/jeddict/jcode/parser/ejs/resources/";

    private final static Jsonb JSONB = JsonbBuilder.create(
            new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
                @Override
                public boolean isVisible(Field field) {
                    return true;
                }

                @Override
                public boolean isVisible(Method method) {
                    return Modifier.isPublic(method.getModifiers());
                }
            })
    );

    static {
        Properties props = System.getProperties();
        props.setProperty("polyglot.js.nashorn-compat", "true");
        props.setProperty("polyglot.js.syntax-extensions", "true");
    }
    
    private ScriptEngine createEngine() {
        CompiledScript compiledScript;
        Bindings bindings;
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Graal.js");
        try {
            if (base == null) {
                base = readString(loadResource(TEMPLATES + "base.js"));
            }
            if (ejs == null) {
                ejs = readString(loadResource(TEMPLATES + "ejs.js"));
            }

            scriptEngine.eval(base);
            Compilable compilingEngine = (Compilable) scriptEngine;
            compiledScript = compilingEngine.compile(ejs);
            bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            compiledScript.eval(bindings);
            scriptEngine.eval(scripts.toString());

            for (Map<String, Object> context : contexts) {
                context.entrySet()
                        .forEach(entry -> {
                                Object value = entry.getValue();
                                if (value instanceof Collection || value instanceof Map) {
                                        value = toJson(scriptEngine, value);
                                }
                                bindings.put(entry.getKey(), value);
                        });
            }
        } catch (ScriptException ex) {
            Exceptions.printStackTrace(ex);
        }
        return scriptEngine;
    }

    private Object toJson(ScriptEngine scriptEngine, Object value) {
        try {
            return scriptEngine.eval("JSON.parse('" + mapper.writeValueAsString(value) + "')");
        } catch (JsonProcessingException | ScriptException ex) {
            Exceptions.printStackTrace(ex);
            throw new IllegalStateException("Error in converting to Json Object " + value, ex);
        }
    }

    public void addContext(Object context) {
        if (context != null) {
            try {
                addContext(mapper.convertValue(context, Map.class));
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void addContext(Map<String, Object> context) {
        contexts.add(context);
    }

    public String parse(String template) throws ScriptException {
        String result = null;
        try {
            if (engine == null) {
                engine = createEngine();
            }
            Object ejs = engine.eval("ejs");
            Invocable invocable = (Invocable) engine;
            Map<String, Object> options = new HashMap<>();
            options.put("filename", "template");
            if (importTemplate != null) {
                options.put("ext", importTemplate);
            }
            if (delimiter != null) {
                options.put("delimiter", delimiter);
            }

            result = (String) invocable.invokeMethod(ejs, "render", template, null, options);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }

    public String parse(Reader reader) throws ScriptException, IOException {
        String parsed;
        try (StringWriter writer = new StringWriter()) {
            copy(reader, writer);
            parsed = parse(writer.toString());
        }
        return parsed;
    }


    public void eval(String script) {
        scripts.append(script);
    }

    /**
     * @return the delimiter
     */
    public Character getDelimiter() {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(Character delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @return the importTemplate
     */
    public Map<String, String> getImportTemplate() {
        return importTemplate;
    }

    /**
     * @param importTemplate the importTemplate to set
     */
    public void setImportTemplate(Map<String, String> importTemplate) {
        this.importTemplate = importTemplate;
    }

    public static boolean isTextFile(String file) {
        return true;
    }

    public Consumer<FileTypeStream> getParserManager() {
        return getParserManager(null);
    }

    public Consumer<FileTypeStream> getParserManager(List<String> skipFile) {
        return (FileTypeStream fileType) -> {
            try {
                if (SKIP_FILE_TYPE.contains(fileType.getFileType())
                        || (skipFile != null && skipFile.contains(fileType.getFileName()))
                        || fileType.isSkipParsing()) {
                    copy(fileType.getInputStream(), fileType.getOutputStream());
                    if (!(fileType.getInputStream() instanceof ZipInputStream)) {
                        fileType.getInputStream().close();
                    }
                    fileType.getOutputStream().close();
                } else {
                    Reader reader = new BufferedReader(new InputStreamReader(fileType.getInputStream(), UTF_8));
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(fileType.getOutputStream(), UTF_8))) {
                        writer.write(parse(reader));
                        if (!(fileType.getInputStream() instanceof ZipInputStream)) {
                            reader.close();
                        }
                        writer.flush();
                    }
                }

            } catch (ScriptException | IOException ex) {
                Exceptions.printStackTrace(ex);
                System.out.println("Error in template : " + fileType.getFileName());
            }
        };
    }
}
