// Copyright 2013 Jason Leyba
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.github.jsdossier;

import static com.google.common.base.Preconditions.checkArgument;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Describes the runtime configuration for the app.
 */
class Flags {

  boolean displayHelp;
  Path config = null;

  private final FileSystem fileSystem;

  private Flags(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Option(
      name = "--help", aliases = "-h",
      usage = "Print this help message and exit.")
  private void setDisplayHelp(boolean help) {
    this.displayHelp = help;
  }

  @Option(
      name = "--config", aliases = "-c",
      required = true,
      usage = "Path to the JSON configuration file to use.")
  private void setConfigPath(String path) {
    config = fileSystem.getPath(path).toAbsolutePath().normalize();
    checkArgument(Files.exists(config), "Path does not exist: %s", config);
    checkArgument(Files.isReadable(config), "Path is not readable: %s", config);
  }

  /**
   * Parses the given command line flags, exiting the program if there are any errors or if usage
   * information was requested with the {@link #displayHelp --help} flag.
   */
  synchronized static Flags parse(String[] args, FileSystem fileSystem) {
    final Flags flags = new Flags(fileSystem);
    CmdLineParser parser = new CmdLineParser(flags);
    parser.setUsageWidth(79);

    boolean isConfigValid = true;
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      isConfigValid = false;
    }

    if (!isConfigValid || flags.displayHelp) {
      parser.printUsage(System.err);
      System.exit(1);
    }

    return flags;
  }
}