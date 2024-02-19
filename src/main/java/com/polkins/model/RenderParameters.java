package com.polkins.model;

import com.polkins.enumeration.PrintFormat;

public record RenderParameters(String templateId, PrintFormat format, String dataModel) {
}
