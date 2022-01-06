/*
 * Copyright (c) 2021, Adam Martinu. All rights reserved. Altering or
 * removing copyright notices or this file header is not allowed.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package dk.martinu.test;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import dk.martinu.kofi.*;
import dk.martinu.kofi.codecs.KofiCodec;
import dk.martinu.kofi.spi.DocumentFileReader;
import dk.martinu.kofi.spi.DocumentFileWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing write and read of {@link KofiCodec} with all property types in
 * {@link dk.martinu.kofi.properties}. This test consists of two subtests;
 * {@link WriteTest} and {@link ReadTest}. First properties are written, then
 * read, both in random order.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class KofiCodecTest {

    final Path path = Paths.get("KofiCodecTest.kofi");

    @AfterAll
    void cleanUp() {
        assertDoesNotThrow(() -> Files.deleteIfExists(path));
    }

    @DisplayName("B ReadTest")
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.Random.class)
    public class ReadTest {

        Document document;

        @Test
        void containsArray() {
            assertTrue(document.contains("mix"));
            assertTrue(document.contains("mix", JsonArray.class));
            assertEquals(new JsonArray("Hello", "World", true, 97), document.getArray("mix"));

            assertTrue(document.contains("abc", "numbers"));
            assertTrue(document.contains("abc", "numbers", JsonArray.class));
            assertEquals(new JsonArray(123, 567, 890), document.getArray("abc", "numbers"));
        }

        @Test
        void containsBoolean() {
            assertTrue(document.contains("bool"));
            assertTrue(document.contains("bool", Boolean.class));
            assertEquals(false, document.getBoolean("bool", true));
        }

        @Test
        void containsChar() {
            assertTrue(document.contains("char"));
            assertTrue(document.contains("char", Character.class));
            assertEquals('%', document.getChar("char", 'a'));
        }

        @Test
        void containsDouble() {
            assertTrue(document.contains("abc", "double"));
            assertTrue(document.contains("abc", "double", Double.class));
            assertEquals(123.567d, document.getDouble("abc", "double", 12d));
        }

        @Test
        void containsFloat() {
            assertTrue(document.contains("def", "float"));
            assertTrue(document.contains("def", "float", Float.class));
            assertEquals(.999F, document.getFloat("def", "float", 12F));
        }

        @Test
        void containsInt() {
            assertTrue(document.contains("abc", "int"));
            assertTrue(document.contains("abc", "int", Integer.class));
            assertEquals(4422, document.getInt("abc", "int", 12));

            assertNotNull(document.getPropertyComments("abc", "int"));
            assertEquals(1, document.getPropertyComments("abc", "int").size());
            assertEquals("This is an IntProperty with a value of 4422", document.getPropertyComments("abc", "int").get(0).text);

            assertTrue(document.contains("int2"));
            assertTrue(document.contains("int2", Integer.class));
            assertEquals(442211, document.getInt("int2", 12));

            assertNotNull(document.getPropertyComments("int2"));
            assertEquals(1, document.getPropertyComments("int2").size());
            assertEquals("This is an IntProperty with a value of 442211", document.getPropertyComments("int2").get(0).text);
        }

        @Test
        void containsLong() {
            assertTrue(document.contains("def", "long"));
            assertTrue(document.contains("def", "long", Long.class));
            assertEquals(111222333444L, document.getLong("def", "long", 12L));
        }

        @Test
        void containsString() {
            assertTrue(document.contains("abc", "string"));
            assertTrue(document.contains("abc", "string", String.class));
            assertEquals("Hello, World!", document.getString("abc", "string", "12"));

            assertNotNull(document.getPropertyComments("abc", "string"));
            assertEquals(2, document.getPropertyComments("abc", "string").size());
            assertEquals("This is a StringProperty", document.getPropertyComments("abc", "string").get(0).text);
            assertEquals("Its value is a common greeting in programming languages", document.getPropertyComments("abc", "string").get(1).text);
        }

        @Test
        void isNull() {
            assertTrue(document.contains("null"));
            assertTrue(document.contains("null", Object.class));
            assertNull(document.getValue("null", Object.class, new Object()));
            assertTrue(document.isNull("null"));

            assertTrue(document.contains("abc", "null"));
            assertTrue(document.contains("abc", "null", Object.class));
            assertNull(document.getValue("abc", "null", Object.class, new Object()));
            assertTrue(document.isNull("abc", "null"));
        }

        @BeforeAll
        void readDocument() {
            final DocumentFileReader reader = DocumentIO.getFileReader(path);
            assertTrue(reader instanceof KofiCodec);
            assertDoesNotThrow(() -> document = reader.readFile(path));

            try {
                System.out.println("READ DOCUMENT " + document);
                System.out.println(KofiCodec.provider().writeString(document));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @DisplayName("A WriteTest")
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.Random.class)
    public class WriteTest {

        Document document;

        @Test
        synchronized void addArray() {
            assertDoesNotThrow(() -> document.addArray("mix", new JsonArray("Hello", "World", true, 97)));
            assertTrue(document.contains("mix"));
            assertTrue(document.contains("mix", JsonArray.class));
            assertEquals(new JsonArray("Hello", "World", true, 97), document.getArray("mix"));

            assertDoesNotThrow(() -> document.addArray("abc", "numbers", new JsonArray(123, 567, 890)));
            assertTrue(document.contains("abc", "numbers"));
            assertTrue(document.contains("abc", "numbers", JsonArray.class));
            assertEquals(new JsonArray(123, 567, 890), document.getArray("abc", "numbers"));
        }

        @Test
        synchronized void addBoolean() {
            assertDoesNotThrow(() -> document.addBoolean("bool", false));
            assertTrue(document.contains("bool"));
            assertTrue(document.contains("bool", Boolean.class));
            assertEquals(false, document.getBoolean("bool", true));
        }

        @Test
        synchronized void addChar() {
            assertDoesNotThrow(() -> document.addChar("char", '%'));
            assertTrue(document.contains("char"));
            assertTrue(document.contains("char", Character.class));
            assertEquals('%', document.getChar("char", 'a'));
        }

        @Test
        synchronized void addDouble() {
            assertDoesNotThrow(() -> document.addDouble("abc", "double", 123.567d));
            assertTrue(document.contains("abc", "double"));
            assertTrue(document.contains("abc", "double", Double.class));
            assertEquals(123.567d, document.getDouble("abc", "double", 12d));
        }

        @Test
        synchronized void addFloat() {
            assertDoesNotThrow(() -> document.addFloat("def", "float", .999F));
            assertTrue(document.contains("def", "float"));
            assertTrue(document.contains("def", "float", Float.class));
            assertEquals(.999F, document.getFloat("def", "float", 12F));
        }

        @Test
        synchronized void addInt() {
            assertDoesNotThrow(() -> document.addInt("abc", "int", 4422));
            assertTrue(document.contains("abc", "int"));
            assertTrue(document.contains("abc", "int", Integer.class));
            assertEquals(4422, document.getInt("abc", "int", 12));

            assertDoesNotThrow(() -> document.addPropertyComments("abc", "int", List.of("This is an IntProperty with a value of 4422")));
            assertNotNull(document.getPropertyComments("abc", "int"));
            assertEquals(1, document.getPropertyComments("abc", "int").size());
            assertEquals("This is an IntProperty with a value of 4422", document.getPropertyComments("abc", "int").get(0).text);

            assertDoesNotThrow(() -> document.addInt("int2", 442211));
            assertTrue(document.contains("int2"));
            assertTrue(document.contains("int2", Integer.class));
            assertEquals(442211, document.getInt("int2", 12));

            assertDoesNotThrow(() -> document.addPropertyComments("int2", List.of("This is an IntProperty with a value of 442211")));
            assertNotNull(document.getPropertyComments("int2"));
            assertEquals(1, document.getPropertyComments("int2").size());
            assertEquals("This is an IntProperty with a value of 442211", document.getPropertyComments("int2").get(0).text);
        }

        @Test
        synchronized void addLong() {
            assertDoesNotThrow(() -> document.addLong("def", "long", 111222333444L));
            assertTrue(document.contains("def", "long"));
            assertTrue(document.contains("def", "long", Long.class));
            assertEquals(111222333444L, document.getLong("def", "long", 12L));
        }

        @Test
        synchronized void addNull() {
            assertDoesNotThrow(() -> document.addNull("null"));
            assertTrue(document.contains("null"));
            assertTrue(document.contains("null", Object.class));
            assertNull(document.getValue("null", Object.class, new Object()));

            assertDoesNotThrow(() -> document.addNull("abc", "null"));
            assertTrue(document.contains("abc", "null"));
            assertTrue(document.contains("abc", "null", Object.class));
            assertNull(document.getValue("abc", "null", Object.class, new Object()));
        }

        @Test
        synchronized void addString() {
            assertDoesNotThrow(() -> document.addString("abc", "string", "Hello, World!"));
            assertTrue(document.contains("abc", "string"));
            assertTrue(document.contains("abc", "string", String.class));
            assertEquals("Hello, World!", document.getString("abc", "string", "12"));

            assertDoesNotThrow(() -> document.addPropertyComments("abc", "string",
                    List.of("This is a StringProperty", "Its value is a common greeting in programming languages")));
            assertNotNull(document.getPropertyComments("abc", "string"));
            assertEquals(2, document.getPropertyComments("abc", "string").size());
            assertEquals("This is a StringProperty", document.getPropertyComments("abc", "string").get(0).text);
            assertEquals("Its value is a common greeting in programming languages", document.getPropertyComments("abc", "string").get(1).text);
        }

        @AfterAll
        synchronized void containsAll() {
            assertTrue(document.contains("bool"));
            assertTrue(document.contains("bool", Boolean.class));
            assertEquals(false, document.getBoolean("bool", true));

            assertTrue(document.contains("char"));
            assertTrue(document.contains("char", Character.class));
            assertEquals('%', document.getChar("char", 'a'));

            assertTrue(document.contains("int2"));
            assertTrue(document.contains("int2", Integer.class));
            assertEquals(442211, document.getInt("int2", 12));

            assertTrue(document.contains("null"));
            assertTrue(document.contains("null", Object.class));
            assertNull(document.getValue("null", Object.class, new Object()));

            assertTrue(document.contains("abc", "double"));
            assertTrue(document.contains("abc", "double", Double.class));
            assertEquals(123.567d, document.getDouble("abc", "double", 12d));

            assertTrue(document.contains("def", "float"));
            assertTrue(document.contains("def", "float", Float.class));
            assertEquals(.999F, document.getFloat("def", "float", 12F));

            assertTrue(document.contains("abc", "int"));
            assertTrue(document.contains("abc", "int", Integer.class));
            assertEquals(4422, document.getInt("abc", "int", 12));

            assertTrue(document.contains("def", "long"));
            assertTrue(document.contains("def", "long", Long.class));
            assertEquals(111222333444L, document.getLong("def", "long", 12L));

            assertTrue(document.contains("abc", "string"));
            assertTrue(document.contains("abc", "string", String.class));
            assertEquals("Hello, World!", document.getString("abc", "string", "12"));

            assertTrue(document.contains("abc", "null"));
            assertTrue(document.contains("abc", "null", Object.class));
            assertNull(document.getValue("abc", "null", Object.class, new Object()));
        }

        @BeforeAll
        void init() {
            document = new Document();
        }

        @AfterAll
        void writeDocument() {
            final DocumentFileWriter writer = DocumentIO.getFileWriter(path, document);
            assertTrue(writer instanceof KofiCodec);
            assertDoesNotThrow(() -> writer.writeFile(path, document));

            try {
                System.out.println("WRITE DOCUMENT " + document);
                System.out.println(KofiCodec.provider().writeString(document));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
