package com.ptob.demo.model;
import java.util.List;
public record ReferenceData(String project, String purpose, List<String> algorithms, List<String> endpoints) {}
