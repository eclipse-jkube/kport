package org.eclipse.jkube.kport.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.OS;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisabledOnOs(OS.WINDOWS)
@EnabledIfSystemProperty(named = "native.image.path", matches = ".+")
public class KubectlKportIT {

    @Test
    public void testNativeBinaryIsExecutable() {
        String nativeImagePath = System.getProperty("native.image.path");
        File nativeBinary = new File(nativeImagePath);
        assertTrue(nativeBinary.exists(), "Native binary should exist at: " + nativeImagePath);
        assertTrue(nativeBinary.canExecute(), "Native binary should be executable");
    }
}
