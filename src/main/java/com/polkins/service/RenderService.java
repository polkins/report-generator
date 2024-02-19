package com.polkins.service;

import com.polkins.enumeration.PrintFormat;

/**
 * Service for rendering documents by specified type f.e .pdf or other like .csv, .docx, .odt etc.
 */
interface RenderService {
    byte[] render(String templateId, String dataModel);

    PrintFormat getFormat();
}
