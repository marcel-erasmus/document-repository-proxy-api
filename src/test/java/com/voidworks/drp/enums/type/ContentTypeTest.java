package com.voidworks.drp.enums.type;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentTypeTest {

    @Test
    public void contentType_Instances() {
        Set<ContentType> contentTypes = Arrays.stream(ContentType.values()).collect(Collectors.toSet());

        assertEquals(19, contentTypes.size());
        assertTrue(contentTypes.containsAll(new HashSet<>(Arrays.asList(
                ContentType.CSV,
                ContentType.DOC,
                ContentType.DOC,
                ContentType.GIF,
                ContentType.JPG,
                ContentType.JPEG,
                ContentType.JSON,
                ContentType.PNG,
                ContentType.ODT,
                ContentType.PDF,
                ContentType.PPT,
                ContentType.PPT,
                ContentType.RTF,
                ContentType.TIF,
                ContentType.TIF,
                ContentType.TXT,
                ContentType.XLS,
                ContentType.XLS,
                ContentType.XML
        ))));
    }

    @Test
    public void getContentTypeByFileExtension_WhenGiven_FileExtensionPdf_Return_ContentTypePdf() {
        Optional<ContentType> contentType = ContentType.getContentTypeByFileExtension(".pdf");

        assertTrue(contentType.isPresent());
        assertEquals(ContentType.PDF, contentType.get());
    }

    @Test
    public void getContentTypeByFileExtension_WhenGiven_FileExtensionMp4_Return_None() {
        Optional<ContentType> contentType = ContentType.getContentTypeByFileExtension(".mp4");

        assertTrue(contentType.isEmpty());
    }

}
