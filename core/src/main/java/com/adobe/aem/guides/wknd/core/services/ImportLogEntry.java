package com.adobe.aem.guides.wknd.core.services;

public class ImportLogEntry {
   private String rowId;
   private String status;
    private String message;
    private String path; 

    // constructors, getters, and setters
    public ImportLogEntry(String rowId, String status, String message, String path) {
        this.rowId = rowId;
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public String getRowId() {
        return rowId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
    
}
