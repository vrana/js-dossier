/*
 Copyright 2013-2015 Jason Leyba
 
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

package com.github.jsdossier.jscomp;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.javascript.rhino.JSDocInfo;

import java.nio.file.Path;

/**
 * Describes a JavaScript module.
 */
@AutoValue
public abstract class Module {

  /**
   * Returns a new builder.
   */
  public static Builder builder() {
    return new AutoValue_Module.Builder()
        .setExportedNames(ImmutableMap.<String, String>of())
        .setInternalVarDocs(ImmutableMap.<String, JSDocInfo>of());
  }

  // Package private to prevent extensions.
  Module() {}

  /**
   * Returns the ID used to reference this module in code after any transformations
   * applied by the compiler. For Closure module's, this will simply be the ID specified in the
   * "goog.module" declaration. For Node and ES6 modules, this will be derived by the compiler from
   * the module's file path.
   */
  public abstract String getId();

  /**
   * Returns the input file that defines this module.
   */
  public abstract Path getPath();

  /**
   * Returns which syntactic type of module this is.
   */
  public abstract Type getType();

  /**
   * Returns the file-level JSDoc for this module.
   */
  public abstract JsDoc getJsDoc();

  /**
   * Returns a map of exported names. Keys will be the exported symbol and values will be the
   * name of the exported value, as seen within the module. This map will only include exported
   * names:
   * <pre><code>
   *   function foo() {}
   *   exports.bar = foo;  // Will record (foo, bar)
   * </code></pre>
   */
  public abstract ImmutableMap<String, String> getExportedNames();

  /**
   * Returns the JSDoc for symbols defined within this module.
   */
  public abstract ImmutableMap<String, JSDocInfo> getInternalVarDocs();

  /**
   * The recognized module types.
   */
  public enum Type {
    /**
     * A module defined using the Closure library's "goog.module" syntax.
     */
    CLOSURE,

    /**
     * A standard ES6 module.
     */
    ES6,

    /**
     * CommonJS modules, as implemented for Node. Modules support "require(id)" syntax for imports.
     */
    NODE
  }

  @AutoValue.Builder
  public static abstract class Builder {
    public abstract Builder setId(String id);
    public abstract String getId();
    public abstract Builder setPath(Path path);
    public abstract Builder setType(Type type);
    public abstract Builder setJsDoc(JsDoc doc);
    public abstract Builder setExportedNames(ImmutableMap<String, String> names);
    public abstract Builder setInternalVarDocs(ImmutableMap<String, JSDocInfo> docs);
    public abstract Module build();
  }
}