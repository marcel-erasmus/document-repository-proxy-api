package com.voidworks.drp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileUtilTest {

    @Test
    public void getFileExtension_WhenGiven_FilenameWithExtensionPdf_Return_Pdf() {
        String fileExtension = FileUtil.getFileExtension("test.pdf");

        assertEquals(".pdf", fileExtension);
    }

    @Test
    public void getFileExtension_WhenGiven_FilenameWithExtensionNone_Return_None() {
        String fileExtension = FileUtil.getFileExtension("test");

        assertEquals("", fileExtension);
    }

}
