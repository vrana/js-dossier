package com.github.jsdossier;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Tests for {@link Config}.
 */
@RunWith(JUnit4.class)
public class ConfigTest {

  @Test
  public void pathSpecResolvesToExpectedSetOfFiles() throws IOException {
    FileSystem fs = Jimfs.newFileSystem();
    Path baseDir = fs.getPath("/root");
    Path foo = baseDir.resolve("foo.js");
    Path bar = baseDir.resolve("a/bar.js");
    Path baz = baseDir.resolve("a/b/baz.js");
    Path quux = baseDir.resolve("a/b/c/quux.js");
    Path quot = baseDir.resolve("a/b/c/quot.js");

    Path otherDir = fs.getPath("/other");
    Path otherFooTest = otherDir.resolve("a/b/foo_test.js");
    Path otherBarTest = otherDir.resolve("a/bar_test.js");

    Files.createDirectories(quux.getParent());
    Files.createDirectories(otherFooTest.getParent());
    Files.createFile(foo);
    Files.createFile(bar);
    Files.createFile(baz);
    Files.createFile(quux);
    Files.createFile(quot);
    Files.createFile(otherFooTest);
    Files.createFile(otherBarTest);

    assertContentsAnyOrder(getPaths(baseDir, ""), foo, bar, baz, quux, quot);
    assertContentsAnyOrder(getPaths(baseDir, "foo.js"), foo);
    assertContentsAnyOrder(getPaths(baseDir, "a"), bar, baz, quux, quot);
    assertContentsAnyOrder(getPaths(baseDir, "a/b"), baz, quux, quot);
    assertContentsAnyOrder(getPaths(baseDir, "a/b/c"), quux, quot);

    assertContentsAnyOrder(getPaths(baseDir, "*.js"), foo);
    assertContentsAnyOrder(getPaths(baseDir, "a/b/*.js"), baz);
    assertContentsAnyOrder(getPaths(baseDir, "a/b/c/*.js"), quux, quot);
    assertContentsAnyOrder(getPaths(baseDir, "**.js"), foo, bar, baz, quux, quot);
    assertContentsAnyOrder(getPaths(baseDir, "a/**.js"), bar, baz, quux, quot);

    assertContentsAnyOrder(getPaths(baseDir, otherDir + "/a/*.js"), otherBarTest);
    assertContentsAnyOrder(getPaths(baseDir, otherDir + "/a/**_test.js"),
        otherBarTest, otherFooTest);
  }

  @SafeVarargs
  private static <T> void assertContentsAnyOrder(Iterable<T> iterable, T... expected) {
    assertEquals(ImmutableSet.copyOf(expected), ImmutableSet.copyOf(iterable));
  }

  private static List<Path> getPaths(Path baseDir, String spec) throws IOException {
    return new Config.PathSpec(baseDir, spec).resolve();
  }
}