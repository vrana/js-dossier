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

import java.nio.file.Path;

/** Defines a markdown file that should be rendered as HTML with the generated documentation. */
public final class MarkdownPage {
  private final String name;
  private final Path path;

  MarkdownPage(String name, Path path) {
    this.name = name;
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public Path getPath() {
    return path;
  }
}
