/*
Copyright 2013-2016 Jason Leyba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.github.jsdossier;

import static com.github.jsdossier.Paths.normalizedAbsolutePath;
import static com.github.jsdossier.Paths.notHidden;
import static com.github.jsdossier.Paths.notIn;
import static com.github.jsdossier.Paths.toNormalizedAbsolutePath;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Verify.verify;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Sets.intersection;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.github.jsdossier.jscomp.Environment;
import com.google.auto.value.AutoValue;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.javascript.jscomp.ErrorFormat;
import com.google.javascript.jscomp.ErrorManager;
import com.google.javascript.jscomp.PrintStreamErrorReportGenerator;
import com.google.javascript.jscomp.SortingErrorManager;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.deps.DependencyInfo;
import com.google.javascript.jscomp.deps.DepsFileParser;
import com.google.javascript.jscomp.deps.DepsGenerator;
import com.google.javascript.jscomp.deps.Es6SortedDependencies;
import com.google.javascript.jscomp.deps.ModuleLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/** Describes the runtime configuration for the app. */
@AutoValue
abstract class Config {

  private static final Logger log = Logger.getLogger(Config.class.getName());

  Config() {}

  @Description(
    name = "closureLibraryDir",
    expandPaths = true,
    desc =
        "Path to the base directory of the Closure library (which must contain base.js"
            + " and deps.js). When this option is specified, Closure's deps.js and all of the files"
            + " specified by `closureDepsFile` will be parsed for calls to `goog.addDependency`. The"
            + " resulting map will be used to automatically expand the set of `sources` any time a"
            + " symbol is goog.require'd with the ile that goog.provides that symbol, along with all"
            + " of its transitive dependencies.\n"
            + "\n"
            + " For example, suppose you have one source file, `foo.js`:\n"
            + "\n"
            + "```js\n"
            + "goog.require('goog.array');\n"
            + "// ...\n"
            + "```\n"
            + "\n"
            + " and your configuration includes:\n"
            + "\n"
            + "```json\n"
            + "\"sources\": [\"foo.js\"],\n"
            + "\"closureLibraryDir\": \"closure/goog\"\n"
            + "```\n"
            + "\n"
            + " due to the dependencies of goog.array declared in closure/goog/deps.js, this is"
            + " equivalent to the following configuration:\n"
            + "\n"
            + "```json\n"
            + "\"sources\": [\n"
            + "    \"closure/goog/base.js\",\n"
            + "    \"closure/goog/debug/error.js\",\n"
            + "    \"closure/goog/string/string.js\",\n"
            + "    \"closure/goog/asserts/asserts.js\",\n"
            + "    \"closure/goog/array/array.js\",\n"
            + "    \"foo.js\"\n"
            + "]\n"
            + "```\n"
            + "\n"
            + " Notice specifying `closureLibraryDir` instructs Dossier to sort the input files so a"
            + " a file that goog.provides symbol X comes before any file that goog.requires X."
  )
  abstract Optional<Path> getClosureLibraryDir();

  @Description(
    name = "closureDepFiles",
    expandPaths = true,
    desc =
        "Path to a file to parse for calls to `goog.addDependency`. This option "
            + "requires also setting `closureLibraryDir`."
  )
  abstract ImmutableSet<Path> getClosureDepFiles();

  @Description(
    name = "sources",
    expandPaths = true,
    desc =
        "A list of .js files to extract API documentation from. If a glob pattern "
            + "is specified, every .js file under the current working directory matching that pattern"
            + " will be included. Specifying the path to a directory, `foo`, is the same as using "
            + "the glob pattern `foo/**.js`. The set of paths specified by this option *must* be "
            + "disjoint from those specified by `modules`."
  )
  abstract ImmutableSet<Path> getSources();

  @Description(
    name = "modules",
    expandPaths = true,
    desc =
        "A list of .js files to extract API documentation from. Each file will be "
            + "processed as a CommonJS module, with only its exported API included in the generated"
            + " output. If a glob pattern is specified, every .js file under the current directory "
            + "matching that pattern will be included. Specifying the path to a directory, `foo`, is"
            + " the same as the glob pattern `foo/**.js`. The set of paths specified by this option "
            + "*mut* be disjoint from those specified by `sources`."
  )
  abstract ImmutableSet<Path> getModules();

  @Description(
    name = "sourcePrefix",
    desc =
        "A prefix to strip from every input file's path (source & module) when rendering source"
            + " paths. Notably, paths will be inserted into the source URL template after"
            + " this prefix has been removed. If this option is omitted, the closest common"
            + " ancestor for all input files will be used as the default."
  )
  abstract Optional<Path> getSourcePrefix();

  Path getSrcPrefix() {
    return getSourcePrefix().get();
  }

  @Description(
    name = "modulePrefix",
    desc =
        "A prefix to strip from every module's path when generating documentation."
            + " The specified path must be a directory that is an ancestor of every file specified "
            + "in `modules`. Note: if this option is omitted, the closest common ancestor for all "
            + "module files will be selected as the default."
  )
  abstract Optional<Path> getModulePrefix();

  @Description(
    name = "externs",
    expandPaths = true,
    desc =
        "A list of .js files to include as an extern file for the Closure compiler. "
            + "These  files are used to satisfy references to external types, but are excluded when "
            + "generating  API documentation."
  )
  abstract ImmutableSet<Path> getExterns();

  @Description(
    name = "externModules",
    expandPaths = true,
    desc =
        "A list of .js files to include as CommonJS extern module definitions. Each module may be"
            + " required in source by the file's base name, excluding the extension. For example,"
            + " 'extern/libfoo.js' would provide the extern definition for the import"
            + " `require('libfoo');`"
  )
  abstract ImmutableSet<Path> getExternModules();

  @Description(
    name = "excludes",
    expandPaths = true,
    desc =
        "A list of .js files to exclude from processing. If a directory is specified,"
            + " all of the .js files under that directory will be excluded. A glob pattern may also"
            + " be specified to exclude all of the paths under the current working directory that "
            + "match  the provided pattern."
  )
  abstract ImmutableSet<Path> getExcludes();

  @Description(
    name = "output",
    desc =
        "Path to the directory to write all generated documentation to. This field is"
            + " required."
  )
  abstract Path getOutput();

  @Description(
    name = "readme",
    desc =
        "Path to a README file to include as the main landing page for the generated "
            + "documentation. This file should use markdown syntax."
  )
  abstract Optional<Path> getReadme();

  @Description(
    name = "customPages",
    desc =
        "List of additional files to include in the generated documentation. Each page "
            + "is defined as a {name: string, path: string} object, where the name is what's "
            + "displayed in the navigation menu, and `path` is the path to the markdown file to use. "
            + "Files will be included in the order listed, after the standard navigation items."
  )
  abstract ImmutableSet<MarkdownPage> getCustomPages();

  @Description(name = "strict", desc = "Whether to run with all type checking flags enabled.")
  abstract boolean isStrict();

  @Description(
    name = "moduleNamingConvention",
    desc =
        "The module naming convention to use. If set to `NODE`, modules with a basename"
            + " of index.js will use the name of the parent directory"
            + " (e.g. \"foo/bar/index.js\" -> \"foo/bar/\"). Must be one of {ES6, NODE}; defaults to ES6"
  )
  abstract ModuleNamingConvention getModuleNamingConvention();

  @Description(
    name = "sourceUrlTemplate",
    desc =
        "Specifies a template from which to generate a HTTP(S) links to source files. Within this"
            + " template, the `%path%` and `%line%` tokens will be replaced with the linked"
            + " type's source file path and line number, respectively. Source paths will be"
            + " relative to the closest common ancestor of all input files.\n"
            + "\n"
            + " If this option is not specified, a rendered copy of each input file will be"
            + " included in the generated output."
  )
  abstract Optional<String> getSourceUrlTemplate();

  @Description(
    name = "typeFilters",
    desc =
        "List of regular expressions for types that should be excluded from generated "
            + "documentation, even if found in the type graph."
  )
  abstract ImmutableSet<Pattern> getTypeFilters();

  @Description(
    name = "moduleFilters",
    desc =
        "List of regular expressions for modules that should be excluded from generated "
            + "documentation, even if found in the type graph. The provided expressions will be "
            + "to the _absolute_ path of the source file for each module."
  )
  abstract ImmutableSet<Pattern> getModuleFilters();

  @Description(
    name = "environment",
    desc =
        "The target environment for the analyzed scripts; dictates the default set of externs "
            + "and module naming convention. Must be one of {BROWSER, NODE}; defaults to ES6"
  )
  abstract Environment getEnvironment();

  abstract FileSystem getFileSystem();

  abstract Builder toBuilder();

  String toJson() {
    return new GsonBuilder()
        .registerTypeAdapter(Config.class, new ConfigMarshaller(getFileSystem()))
        .registerTypeAdapter(AutoValue_Config.class, new ConfigMarshaller(getFileSystem()))
        .registerTypeAdapter(MarkdownPage.class, new MarkdownPageSerializer())
        .setPrettyPrinting()
        .create()
        .toJson(this);
  }

  /** Returns whether the type with the given type should be excluded from documentation. */
  boolean isFilteredType(String name) {
    for (Pattern filter : getTypeFilters()) {
      if (filter.matcher(name).matches()) {
        return true;
      }
    }
    int index = name.lastIndexOf('.');
    return index != -1 && isFilteredType(name.substring(0, index));
  }

  /** Returns whether the given path should be excluded from documentation. */
  boolean isFilteredModule(Path path) {
    for (Pattern filter : getModuleFilters()) {
      if (filter.matcher(path.toAbsolutePath().normalize().toString()).matches()) {
        return true;
      }
    }
    return false;
  }

  public static Builder builder() {
    return new AutoValue_Config.Builder()
        .setClosureDepFiles(ImmutableSet.of())
        .setSources(ImmutableSet.of())
        .setModules(ImmutableSet.of())
        .setExterns(ImmutableSet.of())
        .setExternModules(ImmutableSet.of())
        .setExcludes(ImmutableSet.of())
        .setCustomPages(ImmutableSet.of())
        .setEnvironment(Environment.BROWSER)
        .setStrict(false)
        .setModuleNamingConvention(ModuleNamingConvention.ES6)
        .setTypeFilters(ImmutableSet.of())
        .setModuleFilters(ImmutableSet.of());
  }

  @AutoValue.Builder
  abstract static class Builder {
    public abstract Optional<Path> getClosureLibraryDir();

    public abstract Builder setClosureLibraryDir(Path path);

    public abstract ImmutableSet<Path> getClosureDepFiles();

    public abstract Builder setClosureDepFiles(ImmutableSet<Path> file);

    public abstract ImmutableSet<Path> getSources();

    public abstract Builder setSources(ImmutableSet<Path> paths);

    public abstract ImmutableSet<Path> getModules();

    public abstract Builder setModules(ImmutableSet<Path> paths);

    public abstract Optional<Path> getSourcePrefix();

    public abstract Builder setSourcePrefix(Path path);

    public abstract Builder setModulePrefix(Path path);

    public abstract ImmutableSet<Path> getExterns();

    public abstract Builder setExterns(ImmutableSet<Path> paths);

    public abstract ImmutableSet<Path> getExternModules();

    public abstract Builder setExternModules(ImmutableSet<Path> paths);

    public abstract ImmutableSet<Path> getExcludes();

    public abstract Builder setExcludes(ImmutableSet<Path> paths);

    public abstract Path getOutput();

    public abstract Builder setOutput(Path path);

    public abstract Optional<Path> getReadme();

    public abstract Builder setReadme(Optional<Path> path);

    public abstract Builder setReadme(Path path);

    public abstract ImmutableSet<MarkdownPage> getCustomPages();

    public abstract Builder setCustomPages(ImmutableSet<MarkdownPage> pages);

    public abstract Optional<String> getSourceUrlTemplate();

    public abstract Builder setSourceUrlTemplate(String template);

    public abstract Builder setStrict(boolean strict);

    public abstract Builder setModuleNamingConvention(ModuleNamingConvention convention);

    public abstract Builder setTypeFilters(ImmutableSet<Pattern> filters);

    public abstract Builder setModuleFilters(ImmutableSet<Pattern> filters);

    public abstract FileSystem getFileSystem();

    public abstract Builder setFileSystem(FileSystem fs);

    public abstract Builder setEnvironment(Environment env);

    abstract Config autoBuild();

    private Builder duplicate() {
      return autoBuild().toBuilder();
    }

    private Builder normalize() {
      ImmutableSet<Path> excludes = getExcludes();

      if (!excludes.isEmpty()) {
        @SuppressWarnings("unchecked")
        Predicate<Path> filter = path -> notIn(excludes).test(path) && notHidden().test(path);

        setSources(getSources().stream().filter(filter).collect(toImmutableSet()));
        setModules(getModules().stream().filter(filter).collect(toImmutableSet()));
      }

      if (getClosureLibraryDir().isPresent()) {
        ImmutableSet<Path> depFiles =
            ImmutableSet.<Path>builder()
                .add(getClosureLibraryDir().get().resolve("deps.js"))
                .addAll(getClosureDepFiles())
                .build();

        try {
          setSources(processClosureSources(getSources(), depFiles, getClosureLibraryDir().get()));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      checkHasSourcesOrModules();
      checkInputFileSetsAreDisjoint();
      checkOutputPath();
      checkReadme();
      checkMarkdownPages();
      checkSourceUrlTemplate();
      checkSourcePrefix();
      checkInputsExist();

      return this;
    }

    public Config build() {
      // Ugly song and dance to ensure *this* builder's state is not changed as a result of
      // normalizing the configuration.
      return duplicate().normalize().autoBuild();
    }

    private void checkHasSourcesOrModules() {
      if (getSources().isEmpty() && getModules().isEmpty()) {
        throw new InvalidConfigurationException(
            "There must be at least one input module or source file");
      }
    }

    private void checkOutputPath() {
      Path output = getOutput();
      if (!isDirectory(output) && (exists(output) && !isZipFile(output))) {
        throw new InvalidConfigurationException(
            "Output path must be a directory or a zip file: %s", output);
      }
    }

    private void checkExists(Path path) {
      if (!exists(path)) {
        throw new InvalidConfigurationException("Input file does not exist: %s", path);
      }
    }

    private void checkInputsExist() {
      for (Path input : concat(getSources(), getModules(), getExterns(), getExternModules())) {
        checkExists(input);
      }
    }

    private void checkReadme() {
      if (getReadme().isPresent()) {
        checkExists(getReadme().get());
      }
    }

    private void checkMarkdownPages() {
      for (MarkdownPage page : getCustomPages()) {
        checkExists(page.getPath());
      }
    }

    private void checkInputFileSetsAreDisjoint() {
      checkDisjoint(getSources(), getExterns(), "sources", "externs");
      checkDisjoint(getSources(), getExternModules(), "sources", "extern modules");
      checkDisjoint(getSources(), getModules(), "sources", "modules");
      checkDisjoint(getModules(), getExterns(), "modules", "externs");
      checkDisjoint(getModules(), getExternModules(), "modules", "extern modules");
      checkDisjoint(getExterns(), getExternModules(), "externs", "extern modules");
    }

    private void checkSourceUrlTemplate() {
      if (getSourceUrlTemplate().isPresent()) {
        String template = getSourceUrlTemplate().get();
        if (!template.startsWith("http://") && !template.startsWith("https://")) {
          throw new InvalidConfigurationException(
              "Invalid URL template: must be a http or https URL: %s", template);
        }

        if (!template.contains("%path%")) {
          throw new InvalidConfigurationException(
              "Invalid URL template: must contain '%%path%%' and (optionally) '%%line%%': %s",
              template);
        }
      }
    }

    private void checkSourcePrefix() {
      if (getSourcePrefix().isPresent()) {
        Path prefix = getSourcePrefix().get();
        for (Path source : concat(getSources(), getModules())) {
          if (!source.startsWith(prefix)) {
            throw new InvalidConfigurationException(
                "Input file does not start with <%s>: %s", prefix, source);
          }
        }
      } else {
        setSourcePrefix(getSourcePrefixPath(getFileSystem(), getSources(), getModules()));
      }
    }

    private void checkDisjoint(
        ImmutableSet<Path> a, ImmutableSet<Path> b, String aName, String bName) {
      Set<Path> intersection = intersection(a, b);
      if (!intersection.isEmpty()) {
        throw new InvalidConfigurationException(
            "The %s and %s input sets must be disjoint; common files: %s",
            aName, bName, intersection);
      }
    }

    private static Path getSourcePrefixPath(
        FileSystem fileSystem, ImmutableSet<Path> sources, ImmutableSet<Path> modules) {
      Set<Path> allPaths = new HashSet<>();
      allPaths.addAll(sources);
      allPaths.addAll(modules);

      Path prefix = Paths.getCommonPrefix(fileSystem.getPath("").toAbsolutePath(), allPaths);
      if (sources.contains(prefix) || modules.contains(prefix)) {
        prefix = prefix.getParent();
      }
      return prefix;
    }
  }

  private static boolean isZipFile(Path path) {
    return path.toString().endsWith(".zip");
  }

  /** Loads a new runtime configuration from command line flags. */
  static Config fromFlags(Flags flags, FileSystem fileSystem) throws IOException {
    if (flags.config == null) {
      return Config.fromJson(flags.jsonConfig, fileSystem);
    } else {
      if (!flags.jsonConfig.entrySet().isEmpty()) {
        log.warning("A JSON configuration file was provided; ignoring flag-based configuration");
      }
      try (InputStream stream = newInputStream(flags.config)) {
        return Config.fromJson(stream, fileSystem);
      }
    }
  }

  @VisibleForTesting
  static Config fromJson(InputStream stream, FileSystem fileSystem) {
    return createGsonParser(fileSystem)
        .fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Config.class);
  }

  private static Config fromJson(JsonElement json, FileSystem fileSystem) {
    return createGsonParser(fileSystem).fromJson(json, Config.class);
  }

  private static Gson createGsonParser(FileSystem fileSystem) {
    Path cwd = normalizedAbsolutePath(fileSystem, "");
    return new GsonBuilder()
        .registerTypeAdapter(Config.class, new ConfigMarshaller(fileSystem))
        .registerTypeAdapter(Path.class, new PathDeserializer(fileSystem))
        .registerTypeAdapter(PathSpec.class, new PathSpecDeserializer(cwd))
        .registerTypeAdapter(Pattern.class, new PatternDeserializer())
        .registerTypeAdapter(
            new TypeToken<Optional<Path>>() {}.getType(), new OptionalDeserializer<>(Path.class))
        .registerTypeAdapter(
            new TypeToken<Optional<String>>() {}.getType(),
            new OptionalDeserializer<>(String.class))
        .registerTypeAdapter(
            new TypeToken<ImmutableSet<MarkdownPage>>() {}.getType(),
            new ImmutableSetDeserializer<>(MarkdownPage.class))
        .registerTypeAdapter(
            new TypeToken<ImmutableSet<Path>>() {}.getType(),
            new ImmutableSetDeserializer<>(Path.class))
        .registerTypeAdapter(
            new TypeToken<ImmutableSet<Pattern>>() {}.getType(),
            new ImmutableSetDeserializer<>(Pattern.class))
        .create();
  }

  private static ImmutableSet<Path> processClosureSources(
      Collection<Path> sources, ImmutableSet<Path> deps, Path closureBase) throws IOException {

    Collection<SourceFile> depsFiles = deps.stream().map(toSourceFile()).collect(toList());
    Collection<SourceFile> sourceFiles = sources.stream().map(toSourceFile()).collect(toList());

    ErrorManager errorManager =
        new SortingErrorManager(
            ImmutableSet.of(
                new PrintStreamErrorReportGenerator(
                    ErrorFormat.SOURCELESS.toFormatter(null, false), System.err, 1)));

    DepsGenerator generator =
        new DepsGenerator(
            depsFiles,
            sourceFiles,
            DepsGenerator.InclusionStrategy.ALWAYS,
            closureBase.toAbsolutePath().toString(),
            errorManager,
            ModuleLoader.EMPTY);

    String rawDeps = generator.computeDependencyCalls();
    errorManager.generateReport();
    if (rawDeps == null) {
      throw new RuntimeException("Encountered Closure dependency conflicts");
    }

    List<DependencyInfo> allDeps =
        new DepsFileParser(errorManager).parseFile("*generated-deps*", rawDeps);

    List<DependencyInfo> sourceDeps =
        allDeps.stream().filter(isInSources(sources, closureBase)).collect(toList());

    List<Path> sortedDeps =
        new Es6SortedDependencies<>(allDeps)
            .getSortedStrongDependenciesOf(sourceDeps)
            .stream()
            .map(toPath(closureBase))
            .collect(toList());

    return ImmutableSet.<Path>builder()
        // Always include Closure's base.js first.
        .add(closureBase.resolve("base.js"))
        .addAll(sortedDeps)
        .build();
  }

  private static Predicate<DependencyInfo> isInSources(
      final Collection<Path> sources, Path closureBaseDir) {
    final Function<DependencyInfo, Path> pathTransform = toPath(closureBaseDir);
    final Set<Path> sourcesSet = sources.stream().map(toNormalizedAbsolutePath()).collect(toSet());
    return input -> sourcesSet.contains(pathTransform.apply(input));
  }

  private static Function<DependencyInfo, Path> toPath(final Path closureBaseDir) {
    return input ->
        closureBaseDir.resolve(input.getPathRelativeToClosureBase()).normalize().toAbsolutePath();
  }

  private static Function<Path, SourceFile> toSourceFile() {
    return input -> {
      try {
        String content = new String(readAllBytes(input), UTF_8);
        return SourceFile.fromCode(input.toString(), content);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  static String getOptionsText(boolean includeHeader) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    if (includeHeader) {
      pw.println("__Configuration Options__");
      pw.println();
    }

    Iterable<Description> descriptions =
        Arrays.stream(Config.class.getDeclaredMethods())
            .map(m -> m == null ? null : m.getAnnotation(Description.class))
            .filter(desc -> desc != null)
            .sorted((a, b) -> a.name().compareTo(b.name()))
            .collect(toList());

    for (Description description : descriptions) {
      String str = " * `" + description.name() + "` " + description.desc().trim();
      boolean isFirst = true;
      for (String line : Splitter.on('\n').split(str)) {
        if (isFirst) {
          printLine(pw, line);
          isFirst = false;
        } else {
          printLine(pw, "   " + line);
        }
      }
      pw.println();
    }
    pw.println();
    return sw.toString();
  }

  private static void printLine(PrintWriter pw, String line) {
    if (line.length() <= 79) {
      pw.println(line.replaceAll("\\s+$", ""));
    } else {
      int index = 79;
      while (line.charAt(index) != ' ') {
        index -= 1;
      }
      while (line.charAt(index) == '.'
          && index + 1 < line.length()
          && line.charAt(index + 1) != ' ') {
        index -= 1;
      }
      pw.println(line.substring(0, index));
      printLine(pw, "   " + line.substring(index));
    }
  }

  @VisibleForTesting
  static final class PathSpec {
    private final Path baseDir;
    private final String spec;

    PathSpec(Path baseDir, String spec) {
      this.baseDir = baseDir;
      this.spec = spec;
    }

    List<Path> resolve() throws IOException {
      Path path = baseDir.resolve(spec).normalize();
      if (isDirectory(path)) {
        return collectFiles(path, "**.js");
      }

      if (exists(path)) {
        return ImmutableList.of(path);
      }

      return collectFiles(firstNonNull(path.getParent(), baseDir), path.getFileName().toString());
    }

    List<Path> collectFiles(final Path baseDir, String glob) throws IOException {
      final PathMatcher matcher = baseDir.getFileSystem().getPathMatcher("glob:" + glob);
      final List<Path> files = new ArrayList<>();
      Files.walkFileTree(
          baseDir,
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              if (matcher.matches(baseDir.relativize(file))) {
                files.add(file);
              }
              return FileVisitResult.CONTINUE;
            }
          });
      return ImmutableList.copyOf(files);
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface Description {
    String name();

    String desc();

    boolean expandPaths() default false;
  }

  private static class ConfigMarshaller
      implements JsonDeserializer<Config>, JsonSerializer<Config> {

    private final FileSystem fs;

    private ConfigMarshaller(FileSystem fs) {
      this.fs = fs;
    }

    @Override
    public JsonElement serialize(Config src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      for (Method method : Config.class.getDeclaredMethods()) {
        Description description = method.getAnnotation(Description.class);
        if (description == null) {
          continue;
        }

        try {
          Object value = method.invoke(src);
          if (value instanceof Optional) {
            Optional<?> opt = (Optional) value;
            if (!opt.isPresent()) {
              continue;
            }
            value = opt.get();
          }

          json.add(description.name(), serialize(value, context));
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
      return json;
    }

    private JsonElement serialize(Object value, JsonSerializationContext context) {
      if (value instanceof ImmutableSet) {
        JsonArray array = new JsonArray();
        for (Object element : ((ImmutableSet<?>) value)) {
          array.add(serialize(element, context));
        }
        return array;
      } else if (value instanceof Path || value instanceof Pattern) {
        return new JsonPrimitive(value.toString());
      }
      return context.serialize(value);
    }

    @Override
    public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      Config.Builder config = Config.builder().setFileSystem(fs);

      JsonObject jsonObject = json.getAsJsonObject();
      for (Map.Entry<Description, AccessorSetterPair> entry :
          getAccessorSetterPairs(config.getClass()).entrySet()) {
        if (!jsonObject.has(entry.getKey().name())) {
          continue;
        }

        Description description = entry.getKey();
        Type genericType = entry.getValue().getter().getGenericReturnType();
        Object value;
        Type pathSetType = new TypeToken<ImmutableSet<Path>>() {}.getType();
        if (genericType.equals(pathSetType)) {
          ImmutablePathSetDeserializer deserializer =
              new ImmutablePathSetDeserializer(description.expandPaths());
          value =
              deserializer.deserialize(jsonObject.get(description.name()), pathSetType, context);

        } else {
          value = context.deserialize(jsonObject.get(description.name()), genericType);
        }

        Method setter = entry.getValue().setter();
        if (value instanceof Optional
            && setter.getParameterTypes().length > 0
            && !Optional.class.equals(setter.getParameterTypes()[0])) {
          value = ((Optional<?>) value).get();
        }

        try {
          entry.getValue().setter().invoke(config, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new JsonParseException(e);
        }
      }
      return config.build();
    }
  }

  private static Map<Description, AccessorSetterPair> getAccessorSetterPairs(
      Class<? extends Config.Builder> builderClass) {
    Map<String, Method> setters = new HashMap<>();
    for (Method method : builderClass.getDeclaredMethods()) {
      if (method.getName().startsWith("set")) {
        setters.put(method.getName(), method);
      }
    }

    Map<Description, AccessorSetterPair> pairs = new HashMap<>();
    for (Method getter : Config.class.getDeclaredMethods()) {
      Description description = getter.getAnnotation(Description.class);
      if (description == null) {
        continue;
      }

      String setterName;
      if (getter.getName().startsWith("get")) {
        setterName = "set" + getter.getName().substring("get".length());
      } else {
        verify(getter.getName().startsWith("is"));
        setterName = "set" + getter.getName().substring("is".length());
      }

      Method setter = setters.get(setterName);
      verify(setter != null, "failed to resolve setter for %s", getter.getName());
      verify(
          setter.getParameterTypes().length == 1,
          "expected setter to accept one parameter for %s",
          getter.getName());
      pairs.put(description, new AutoValue_Config_AccessorSetterPair(getter, setter));
    }
    return pairs;
  }

  @AutoValue
  abstract static class AccessorSetterPair {
    abstract Method getter();

    abstract Method setter();
  }

  private static class MarkdownPageSerializer implements JsonSerializer<MarkdownPage> {

    @Override
    public JsonElement serialize(
        MarkdownPage src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.addProperty("name", src.getName());
      json.addProperty("path", src.getPath().toString());
      return json;
    }
  }

  private static class ImmutablePathSetDeserializer
      implements JsonDeserializer<ImmutableSet<Path>> {
    private final boolean expandPaths;

    private ImmutablePathSetDeserializer(boolean expandPaths) {
      this.expandPaths = expandPaths;
    }

    @Override
    public ImmutableSet<Path> deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      Set<Path> paths = new HashSet<>();
      if (expandPaths) {
        Type type = new TypeToken<List<PathSpec>>() {}.getType();
        List<PathSpec> specs = context.deserialize(json, type);
        for (PathSpec spec : specs) {
          if (spec == null) {
            continue;
          }
          try {
            List<Path> resolved = spec.resolve();
            paths.addAll(resolved);
          } catch (IOException e) {
            throw new JsonParseException(e);
          }
        }
      } else {
        Type type = new TypeToken<List<Path>>() {}.getType();
        List<Path> list = context.deserialize(json, type);
        paths.addAll(list);
      }
      return ImmutableSet.copyOf(paths);
    }
  }

  private static class ImmutableSetDeserializer<T> implements JsonDeserializer<ImmutableSet<T>> {
    private final Class<T> componentType;

    private ImmutableSetDeserializer(Class<T> componentType) {
      this.componentType = componentType;
    }

    @Override
    public ImmutableSet<T> deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      if (json.isJsonNull()) {
        return ImmutableSet.of();
      }
      List<T> items = new ArrayList<>();
      JsonArray array = json.getAsJsonArray();
      for (int i = 0; i < array.size(); i++) {
        JsonElement element = array.get(i);
        if (element.isJsonNull()) {
          if (i == array.size() - 1) {
            break;
          }
          throw new JsonParseException("null element in array at index " + i);
        }
        T item = context.deserialize(array.get(i), componentType);
        items.add(item);
      }
      return ImmutableSet.copyOf(items);
    }
  }

  private static class OptionalDeserializer<T> implements JsonDeserializer<Optional<T>> {

    private final Class<T> componentType;

    private OptionalDeserializer(Class<T> componentType) {
      this.componentType = componentType;
    }

    @Override
    public Optional<T> deserialize(
        JsonElement jsonElement, Type type, JsonDeserializationContext context)
        throws JsonParseException {
      if (jsonElement.isJsonNull()) {
        return Optional.empty();
      }
      T value = context.deserialize(jsonElement, componentType);
      return Optional.ofNullable(value);
    }
  }

  private static class PathDeserializer implements JsonDeserializer<Path> {

    private final FileSystem fileSystem;

    PathDeserializer(FileSystem fileSystem) {
      this.fileSystem = fileSystem;
    }

    @Override
    public Path deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
        throws JsonParseException {
      return normalizedAbsolutePath(fileSystem, jsonElement.getAsString());
    }
  }

  private static class PathSpecDeserializer implements JsonDeserializer<PathSpec> {

    private final Path baseDir;

    PathSpecDeserializer(Path baseDir) {
      this.baseDir = baseDir;
    }

    @Override
    public PathSpec deserialize(
        JsonElement jsonElement, Type type, JsonDeserializationContext context)
        throws JsonParseException {
      return new PathSpec(baseDir, jsonElement.getAsString());
    }
  }

  private static class PatternDeserializer implements JsonDeserializer<Pattern> {

    @Override
    public Pattern deserialize(
        JsonElement jsonElement, Type type, JsonDeserializationContext context)
        throws JsonParseException {
      return Pattern.compile(jsonElement.getAsString());
    }
  }

  public static void main(String[] args) throws IOException {
    URL headUrl = Resources.getResource(Config.class, "resources/ReadmeHead.md");
    URL tailUrl = Resources.getResource(Config.class, "resources/ReadmeTail.md");
    String output =
        Resources.toString(headUrl, UTF_8)
            + getOptionsText(true)
            + Resources.toString(tailUrl, UTF_8);

    if (args.length > 0) {
      Path path = FileSystems.getDefault().getPath(args[0]);
      write(path, output.getBytes(UTF_8));
    } else {
      System.err.println(output);
    }
  }
}
